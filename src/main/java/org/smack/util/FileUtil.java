/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2008-21 Michael G. Binz
 */
package org.smack.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.filechooser.FileFilter;

/**
 * Utility routines for file handling.
 *
 * @author Michael Binz
 */
public final class FileUtil
{
    /**
     * The logger for this class.
     */
    private static final Logger LOG = Logger.getLogger(
            FileUtil.class.getName() );

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
     * Silently closes the passed Closeable.  In case the close
     * operation fails, the exception is written into the log.
     *
     * @param closeable The object to close.
     */
    public static void forceClose( Closeable closeable )
    {
        JavaUtil.force( closeable::close );
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
                        newSuffix) );
    }

    /**
     * Read all lines from a reader into a list.  Closes the reader.
     *
     * @param in The reader to use.
     * @return The lines read without EOL characters.
     * @throws IOException In case of an error.
     */
    public static List<String> readLines( Reader in ) throws IOException
    {
        try ( BufferedReader din = new BufferedReader( in ) )
        {
            return din.lines().collect( Collectors.toList() );
        }
        catch ( UncheckedIOException e )
        {
            throw e.getCause();
        }
    }

    /**
     * Read all lines from an stream into a list. Closes the stream.
     *
     * @param in The reader to use.
     * @return The lines read without EOL characters.
     * @throws IOException In case of an error.
     */
    public static List<String> readLines( InputStream in ) throws IOException
    {
        return readLines(
                new InputStreamReader( in ) );
    }

    /**
     * Read all lines from a file into a list.
     *
     * @param in The file to use.
     * @return The lines read without EOL characters.
     * @throws IOException In case of an error.
     */
    public static List<String> readLines( File in ) throws IOException
    {
        return Files.lines( in.toPath() ).collect( Collectors.toList() );
    }

    /**
     * Deletes recursively.
     *
     * @param dir The file to delete. A directory is deleted recursively.
     * @return true if successful.
     */
    public static boolean delete( File dir )
    {
        if ( ! dir.exists() )
            return true;

        try
        {
            Files.walk( dir.toPath() )
                .sorted( Comparator.reverseOrder() )
                .map( Path::toFile )
                .forEach( File::delete );
        }
        catch ( Exception e )
        {
            LOG.log(
                    Level.WARNING,
                    "Failed to delete: " + dir.getPath(),
                    e );
            return false;
        }

        return ! dir.exists();
    }

    /**
     * @return The user's home directory.
     */
    public static File getUserHome()
    {
        var home = System.getProperty("user.home");
        if ( home == null )
            throw new AssertionError( "No 'user.home'." );

        File result = new File( home );
        if ( !result.exists() )
            throw new AssertionError( "'user.home' does not exist." );
        if ( !result.isDirectory() )
            throw new AssertionError( "'user.home' not a directory." );

        return result;
    }

    /**
     * Instances cannot be created.
     */
    private FileUtil()
    {
        throw new AssertionError();
    }
}
