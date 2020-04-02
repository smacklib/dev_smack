/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */
package org.jdesktop.application;

import java.awt.Rectangle;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.ExceptionListener;
import java.beans.Expression;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jdesktop.beans.AbstractBeanEdt;
import org.jdesktop.util.PlatformType;
import org.smack.util.StringUtil;

/**
 * Access to per application, per user, local file storage. The
 * shared instance can be received by calling
 * {@link Application#getApplicationService(Class)}.
 *
 * @version $Rev$
 * @author Michael Binz
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public final class LocalStorage extends AbstractBeanEdt
{
    private LocalIO localIO = null;

    private final File unspecifiedFile = new File("unspecified");

    private File directory = unspecifiedFile;

    /**
     * Create an instance.
     * @param a
     */
    LocalStorage()
    {
        ApplicationInfo a =
                org.jdesktop.util.ServiceManager.getApplicationService( ApplicationInfo.class );

        String vendorId = a.getVendorId();
        String applicationId = a.getId();

        if ( StringUtil.isEmpty( vendorId ) )
            throw new IllegalArgumentException("Empty vendorId");
        if ( StringUtil.isEmpty( applicationId ) )
            throw new IllegalArgumentException("Empty applicationId");

        _vendorId =
                vendorId.trim();
        _applicationId =
                applicationId.trim();
    }

    private void checkFileName(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("null fileName");
        }
    }

    /**
     * Opens an input stream to read from the entry
     * specified by the {@code name} parameter.
     * If the named entry cannot be opened for reading
     * then a {@code IOException} is thrown.
     *
     * @param fileName  the storage-dependent name
     * @return an {@code InputStream} object
     * @throws IOException if the specified name is invalid,
     *                     or an input stream cannot be opened
     * @deprecated Use {@link #openInputFile(File)}
     */
    @Deprecated
    public InputStream openInputFile(String fileName) throws IOException {
        checkFileName(fileName);
        return getLocalIO().openInputFile(fileName);
    }

    /**
     * Opens an input stream to read from the entry
     * specified by the {@code name} parameter.
     * If the named entry cannot be opened for reading
     * then a {@code IOException} is thrown.
     *
     * @param file  the storage-dependent name
     * @return an {@code InputStream} object
     * @throws IOException if the specified name is invalid,
     *                     or an input stream cannot be opened
     */
    public InputStream openInputFile( File file ) throws IOException {
        return openInputFile( file.getPath() );
    }

    /**
     * Opens an output stream to write to the entry
     * specified by the {@code name} parameter.
     * If the named entry cannot be opened for writing
     * then a {@code IOException} is thrown.
     * If the named entry does not exist it can be created.
     * The entry will be recreated if already exists.
     *
     * @param fileName  the storage-dependent name
     * @return an {@code OutputStream} object
     * @throws IOException if the specified name is invalid,
     *                     or an output stream cannot be opened
     * @deprecated Use {@link #openInputFile(File)}
     */
    @Deprecated
    public OutputStream openOutputFile(final String fileName) throws IOException {
        return openOutputFile(fileName, false);
    }

    /**
     * Opens an output stream to write to the entry
     * specified by the {@code name} parameter.
     * If the named entry cannot be opened for writing
     * then a {@code IOException} is thrown.
     * If the named entry does not exist it can be created.
     * The entry will be recreated if already exists.
     *
     * @param file The storage-dependent name
     * @return an {@code OutputStream} object
     * @throws IOException if the specified name is invalid,
     *                     or an output stream cannot be opened
     */
    public OutputStream openOutputFile( File file ) throws IOException {
        return openOutputFile(file.getPath(), false);
    }

    /**
     * Opens an output stream to write to the entry
     * specified by the {@code name} parameter.
     * If the named entry cannot be opened for writing
     * then a {@code IOException} is thrown.
     * If the named entry does not exist it can be created.
     * You can decide whether data will be appended via append parameter.
     *
     * @param file The storage-dependent name
     * @param append If <code>true</code>, then bytes will be written
     * to the end of the output entry rather than the beginning
     * @return an {@code OutputStream} object
     * @throws IOException if the specified name is invalid,
     * or an output stream cannot be opened
     */
    public OutputStream openOutputFile(File file, boolean append) throws IOException {
        return openOutputFile( file.getPath(), append );
    }

    /**
     * Opens an output stream to write to the entry
     * specified by the {@code name} parameter.
     * If the named entry cannot be opened for writing
     * then a {@code IOException} is thrown.
     * If the named entry does not exist it can be created.
     * You can decide whether data will be appended via append parameter.
     *
     * @param fileName  the storage-dependent name
     * @param append if <code>true</code>, then bytes will be written
     *                   to the end of the output entry rather than the beginning
     * @return an {@code OutputStream} object
     * @deprecated Use {@link #openOutputFile(File, boolean)}
     * @throws IOException if the specified name is invalid,
     *                     or an output stream cannot be opened
     */
    @Deprecated
    public OutputStream openOutputFile(String fileName, boolean append) throws IOException {
        checkFileName(fileName);
        return getLocalIO().openOutputFile(fileName, append);
    }

    /**
     * Deletes the entry specified by the {@code name} parameter.
     *
     * @deprecated Use {@link #deleteFile(File)}
     * @param fileName  the storage-dependent name
     * @throws IOException if the specified name is invalid,
     *                     or an internal entry cannot be deleted
     */
    @Deprecated
    public boolean deleteFile(String fileName) throws IOException {
        checkFileName(fileName);
        return getLocalIO().deleteFile(fileName);
    }

    /**
     * Deletes the entry specified by the {@code name} parameter.
     *
     * @param file The storage-dependent name.
     * @throws IOException if the specified name is invalid,
     * or an internal entry cannot be deleted
     */
    public boolean deleteFile( File file ) throws IOException {
        // TODO(michab) Fix return code AND exception.
        return deleteFile( file.getPath() );
    }

    /**
     * If an exception occurs in the XMLEncoder/Decoder, we want
     * to throw an IOException.  The exceptionThrow listener method
     * doesn't throw a checked exception so we just set a flag
     * here and check it when the encode/decode operation finishes
     */
    private static class AbortExceptionListener implements ExceptionListener {

        public Exception exception = null;

        @Override
        public void exceptionThrown(Exception e) {
            if (exception == null) {
                exception = e;
            }
        }
    }

    private static boolean persistenceDelegatesInitialized = false;

    /**
     * Saves the {@code bean} to the local storage
     * @param bean the object ot be saved
     * @param fileName the targen file name
     * @throws IOException
     */
    public void save(Object bean, final String fileName) throws IOException {
        AbortExceptionListener el = new AbortExceptionListener();
        XMLEncoder e = null;
        /* Buffer the XMLEncoder's output so that decoding errors don't
         * cause us to trash the current version of the specified file.
         */
        ByteArrayOutputStream bst = new ByteArrayOutputStream();
        try {
            e = new XMLEncoder(bst);
            if (!persistenceDelegatesInitialized) {
                e.setPersistenceDelegate(Rectangle.class, new RectanglePD());
                persistenceDelegatesInitialized = true;
            }
            e.setExceptionListener(el);
            e.writeObject(bean);
        } finally {
            if (e != null) {
                e.close();
            }
        }
        if (el.exception != null) {
            throw new IOException("save failed \"" + fileName + "\"", el.exception);
        }
        OutputStream ost = null;
        try {
            ost = openOutputFile(fileName);
            ost.write(bst.toByteArray());
        } finally {
            if (ost != null) {
                ost.close();
            }
        }
    }

    /**
     * Loads the bean from the local storage
     * @param fileName name of the file to be read from
     * @return loaded object
     * @throws IOException
     */
    public Object load(String fileName) throws IOException {
        InputStream ist;
        try {
            ist = openInputFile(fileName);
        } catch (IOException e) {
            return null;
        }
        AbortExceptionListener el = new AbortExceptionListener();
        XMLDecoder d = null;
        try {
            d = new XMLDecoder(ist);
            d.setExceptionListener(el);
            Object bean = d.readObject();
            if (el.exception != null) {
                throw new IOException("load failed \"" + fileName + "\"", el.exception);
            }
            return bean;
        } finally {
            if (d != null) {
                d.close();
            }
        }
    }


    private final String _vendorId;
    private final String _applicationId;

    /**
     * Returns the directory where the local storage is located
     * @return the directory where the local storage is located
     */
    private File getDirectory() {
        if (directory == unspecifiedFile) {
            directory = null;
            String userHome = null;
            try {
                userHome = System.getProperty("user.home");
            } catch (SecurityException ignore) {
            }
            if (userHome != null) {
                final PlatformType osId = PlatformType.getPlatform();
                if (osId == PlatformType.WINDOWS) {
                    File appDataDir = null;
                    try {
                        String appDataEV = System.getenv("APPDATA");
                        if ((appDataEV != null) && (appDataEV.length() > 0)) {
                            appDataDir = new File(appDataEV);
                        }
                    } catch (SecurityException ignore) {
                    }
                    if ((appDataDir != null) && appDataDir.isDirectory()) {
                        // ${APPDATA}\{vendorId}\${applicationId}
                        String path = _vendorId + "\\" + _applicationId + "\\";
                        directory = new File(appDataDir, path);
                    } else {
                        // ${userHome}\Application Data\${vendorId}\${applicationId}
                        String path = "Application Data\\" + _vendorId + "\\" + _applicationId + "\\";
                        directory = new File(userHome, path);
                    }
                } else if (osId == PlatformType.OS_X) {
                    // ${userHome}/Library/Application Support/${applicationId}
                    String path = "Library/Application Support/" + _applicationId + "/";
                    directory = new File(userHome, path);
                } else {
                    // ${userHome}/.${applicationId}/
                    String path = "." + _applicationId + "/";
                    directory = new File(userHome, path);
                }
            }
        }
        return directory;
    }

//    /**
//     * Sets the location of the local storage
//     * @param directory the location of the local storage
//     */
//    public void setDirectory(File directory) {
//        File oldValue = this.directory;
//        this.directory = directory;
//        firePropertyChange("directory", oldValue, this.directory);
//    }

    /* There are some (old) Java classes that aren't proper beans.  Rectangle
     * is one of these.  When running within the secure sandbox, writing a
     * Rectangle with XMLEncoder causes a security exception because
     * DefaultPersistenceDelegate calls Field.setAccessible(true) to gain
     * access to private fields.  This is a workaround for that problem.
     * A bug has been filed, see JDK bug ID 4741757
     */
    private static class RectanglePD extends DefaultPersistenceDelegate {

        public RectanglePD() {
            super(new String[]{"x", "y", "width", "height"});
        }

        @Override
        protected Expression instantiate(Object oldInstance, Encoder out) {
            Rectangle oldR = (Rectangle) oldInstance;
            Object[] constructorArgs = new Object[]{
                    oldR.x, oldR.y, oldR.width, oldR.height
            };
            return new Expression(oldInstance, oldInstance.getClass(), "new", constructorArgs);
        }
    }

    private synchronized LocalIO getLocalIO() {
        if (localIO == null) {
            localIO = new LocalFileIO();
        }

        return localIO;
    }

    private abstract class LocalIO {

        /**
         * Opens an input stream to read from the entry
         * specified by the {@code name} parameter.
         * If the named entry cannot be opened for reading
         * then a {@code IOException} is thrown.
         *
         * @param fileName  the storage-dependent name
         * @return an {@code InputStream} object
         * @throws IOException if the specified name is invalid,
         *                     or an input stream cannot be opened
         */
        public abstract InputStream openInputFile(String fileName) throws IOException;

        /**
         * Opens an output stream to write to the entry
         * specified by the {@code name} parameter.
         * If the named entry cannot be opened for writing
         * then a {@code IOException} is thrown.
         * If the named entry does not exist it can be created.
         * You can decide whether data will be appended via append parameter.
         *
         * @param fileName  the storage-dependent name
         * @param append if <code>true</code>, then bytes will be written
         *                   to the end of the output entry rather than the beginning
         * @return an {@code OutputStream} object
         * @throws IOException if the specified name is invalid,
         *                     or an output stream cannot be opened
         */
        public abstract OutputStream openOutputFile(final String fileName, boolean append) throws IOException;

        /**
         * Deletes the entry specified by the {@code name} parameter.
         *
         * @param fileName  the storage-dependent name
         * @throws IOException if the specified name is invalid,
         *                     or an internal entry cannot be deleted
         */
        public abstract boolean deleteFile(String fileName) throws IOException;
    }

    private final class LocalFileIO extends LocalIO {

        @Override
        public InputStream openInputFile(String fileName) throws IOException {
            File path = getFile(fileName);
            try {
                return new BufferedInputStream(new FileInputStream(path));
            } catch (IOException e) {
                throw new IOException("couldn't open input file \"" + fileName + "\"", e);
            }
        }

        @Override
        public OutputStream openOutputFile(String name, boolean append) throws IOException {
            try {
                File file = getFile(name);
                File dir = file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs()) {
                    throw new IOException("couldn't create directory " + dir);
                }
                return new BufferedOutputStream(new FileOutputStream(file, append));
            }
            catch (SecurityException exception) {
                throw new IOException("could not write to entry: " + name, exception);
            }
        }

        @Override
        public boolean deleteFile(String fileName) throws IOException {
            File path = new File(getDirectory(), fileName);
            return path.delete();
        }

        private File getFile(String name) throws IOException {
            if (name == null) {
                throw new IOException("name is not set");
            }
            return new File(getDirectory(), name);
        }
    }
}
