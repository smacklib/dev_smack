/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2017-21 Michael G. Binz
 */
package org.smack.application;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.smack.util.FileUtil;
import org.smack.util.StringUtil;
import org.smack.util.resource.ResourceUtil;

/**
 * Configures java util logging.
 *
 * @author michab66
 */
public class LoggingService
{
    private static final Logger LOG = Logger.getLogger(
            LoggingService.class.getName() );

    /**
     * Get the log directory for the passed application id.  This is
     * $HOME/.appid/log/.
     *
     * @param appid An application id.
     * @return The respective log directory.  This directory
     * is created by this call if it does not exist.
     */
    private static File getLogDir( String appid )
    {
        try
        {
            // Check if the logging directory exists.
            var logDir = new File(
                    FileUtil.getUserHome() + "/." + appid + "/log/" );

            if ( !logDir.exists() )
                Files.createDirectories( logDir.toPath() );

            return logDir;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Initialize logging for the passed group and application id.
     *
     * @param groupId A group id.
     * @param applicationId An application id.
     * @throws Exception
     */
    private static void initImpl( String groupId, String applicationId ) throws Exception
    {
        // Replaces all entries from the configuration by the new entries.
        // The exception is if the new entry is 'smack.logfile.path' then that gets
        // replaced by a dynamically assembled value.
        Function<String, BiFunction<String,String,String>> consume =
            (k) -> {
                return (o,n) -> {
                    if ( ! "smack.logfile.path".equals( n ) )
                        return n;
                    return new File( getLogDir( groupId ), applicationId + "_%g.log" ).toString();
                };
            };

        var lm = LogManager.getLogManager();

        lm.reset();
        lm.updateConfiguration(
                new ByteArrayInputStream(
                        ResourceUtil.loadResource(
                                LoggingService.class,
                                "log.props" ) ),
                consume );
    }

    /**
     * Initialize logging for the passed group and application id.
     *
     * @param groupId A group id.
     * @param applicationId An application id.
     * @throws Exception
     */
    private static void init( String groupId, String applicationId )
    {
        try
        {
            initImpl( groupId, applicationId );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    /**
     * Initialize logging for the passed application id.
     *
     * @param applicationId An application id.
     * @throws Exception In case of an error.
     */
    public static void init( String applicationId )
    {
        init( applicationId, applicationId );
    }

    /**
     * Initialize logging for the passed application id.
     *
     * @param applicationId An application id.
     * @throws Exception In case of an error.
     */
    public static void init( ApplicationContext ac )
    {
        var id =
            ac.getId();
        var vi =
            ac.getVendorId();
        init(
            StringUtil.hasContent( vi ) ? vi : id,
            id );
    }

    public static void main( String[] args ) throws Exception
    {
        init( "mmt" );

        LOG.info( "info2" );
        LOG.warning( "warning2" );
        LOG.severe( "severe2" );
    }
}
