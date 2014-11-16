/* $Id$
 *
 * Utilities
 *
 * Released under Gnu Public License
 * Copyright (c) 2008 Michael G. Binz
 */
package de.s.mack.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.filechooser.FileFilter;



/**
 * A library of utility routines for file handling.
 *
 * @version $Rev$s
 * @author Michael Binz
 */
public class FileUtils
{
    /**
     * Instances cannot be created.
     */
    private FileUtils()
    {
        throw new AssertionError();
    }



    /**
     * The logger for this class.
     */
    private static final Logger _log = Logger.getLogger(
        FileUtils.class.getName() );



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
        Vector<File> fs = new Vector<File>();

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
     * Silently closes the passed {@link Closeable}.  In case the close
     * operation fails the exception is written into the log.
     *
     * @param f The {@link Closeable} to close.  If {@code null} is passed
     *          this operation does nothing.
     * @return Always a typed {@code null}.  This can be used to reset the
     * reference to the passed {@link Closeable}.
     */
    public static <T extends Closeable> T forceClose( T f )
    {
        if ( f != null )
        {
            try
            {
                f.close();
            }
            catch ( IOException e )
            {
                _log.log( Level.INFO, "forceClose", e );
            }
        }

        return null;
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
       Vector<File> collector = new Vector<File>( files.length );

       for ( File f : files )
       {
           if ( filter.accept( f ) )
               collector.add( f );
       }

       return collector.toArray( new File[ collector.size() ] );
   }
}
