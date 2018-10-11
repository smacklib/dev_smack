/* $Id$
 *
 * Utilities
 *
 * Released under Gnu Public License
 * Copyright (c) 2008 Michael G. Binz
 */
package org.jdesktop.smack.util;

import java.io.Closeable;
import java.io.File;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.filechooser.FileFilter;

import org.jdesktop.util.StringUtil;

/**
 * A library of utility routines for file handling.
 *
 * @version $Rev$s
 * @author Michael Binz
 */
public final class FileUtils
{
    /**
     * The logger for this class.
     */
    private static final Logger LOG = Logger.getLogger(
            FileUtils.class.getSimpleName() );

    /**
     * Resolves directories in the passed file list. That is, normal file
     * entries in the returned list are returned unmodified in the result. If a
     * directory is contained in the passed file list, then this is replaced
     * recursively by its contained files.
     *
     * @param files The file list including directories.
     * @return A list of files. No directories are part of this list.
     */
    public static File[] resolveDirectories( File[] files )
    {
        Vector<File> fs = new Vector<>();

        for ( File file : files )
        {
            if ( file.isDirectory() )
            {
                File[] dirContents = file.listFiles();
                if ( dirContents != null && dirContents.length > 0 )
                {
                    for ( File c : resolveDirectories( dirContents ) )
                        fs.add( c );
                }
            }
            else
            {
                fs.add( file );
            }
        }

        return fs.toArray( new File[fs.size()] );
    }

    /**
     * Silently closes the passed closeable.  In case the close
     * operation fails, the exception is written into the log.
     *
     * @param closeable The object to close.  If {@code null} is passed
     *          this operation does nothing.
     */
    public static void forceClose( Closeable closeable )
    {
        if ( closeable == null )
            return;

        try
        {
            closeable.close();
        }
        catch (Exception e)
        {
            LOG.log( Level.FINE, e.getMessage(), e);
        }
    }

    /**
     * Filters a list of files.
     *
     * @param files The files to filter.
     * @param filter The filter to use.
     * @return The files that have been accepted by the filter.
     */
    public static File[] filterFiles( File[] files, FileFilter filter )
    {
        Vector<File> collector = new Vector<>( files.length );

        for ( File f : files )
        {
            if ( filter.accept( f ) )
                collector.add( f );
        }

        return collector.toArray( new File[ collector.size() ] );
    }

    private final static char SUFFIX_SEPARATOR = '.';

    /**
     * Get the passed file's suffix.
     * @param file A file.
     * @return The passed file's suffix not including the separator.
     */
    public static String getSuffix( File file )
    {
        String filename = file.getName();

        int dotidx = filename.lastIndexOf( SUFFIX_SEPARATOR );

        if (dotidx == -1 )
            return StringUtil.EMPTY_STRING;

        return filename.substring( dotidx+1 );
    }

    /**
     * Replace the suffix in a filename.
     * @param filename The file name.
     * @param newSuffix A new suffix without a separator character.
     * @return The extended file name.
     */
    public static String replaceSuffix( String filename, String newSuffix )
    {
        int dotidx = filename.lastIndexOf( SUFFIX_SEPARATOR );

        if (dotidx == -1 )
            return filename + newSuffix;

        return filename.substring( 0, dotidx ) + SUFFIX_SEPARATOR + newSuffix;
    }

    /**
     * Replace the suffix in a file name.
     * @param filename A file.
     * @param newSuffix The new suffix without a separator character.
     * @return The extended file name.
     */
    public static File replaceSuffix( File filename, String newSuffix )
    {
        return new File(
                filename.getParent(),
                replaceSuffix(
                        filename.getName(),
                        newSuffix)
                 );
    }

    /**
     * Instances cannot be created.
     */
    private FileUtils()
    {
        throw new AssertionError();
    }
}
