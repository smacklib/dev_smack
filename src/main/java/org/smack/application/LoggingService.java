/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2017-21 Michael G. Binz
 */
package org.smack.application;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.smack.util.FileUtil;
import org.smack.util.JavaUtil;
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

    private final String _groupId;

    private final String _applicationId;

    private final File _logDir;

    LoggingService( ApplicationContext ac )
    {
        Objects.requireNonNull( ac );

        _applicationId =
                ac.getId();
        JavaUtil.Assert(
                StringUtil.hasContent( _applicationId ),
                "Application.id not set.");

        var vendorId =
                ac.getVendorId();
        _groupId =
                StringUtil.hasContent( vendorId ) ? vendorId : _applicationId;
        _logDir =
                init( _groupId, _applicationId );
    }

    LoggingService( String groupId, String applicationId )
    {
        JavaUtil.Assert(
                StringUtil.hasContent( groupId ),
                "group.id not set.");
        JavaUtil.Assert(
                StringUtil.hasContent( applicationId ),
                "application.id not set.");

        _groupId =
                groupId;
        _applicationId =
                applicationId;
        _logDir =
                init( _groupId, _applicationId );
    }

    File getLogDir()
    {
        return _logDir;
    }

    /**
     * Get the log directory for the passed application id.  This is
     * $HOME/.appid/log/.
     *
     * @param groupId An group id.
     * @return The respective log directory.  This directory
     * is created by this call if it does not exist.
     */
    private static File createLogDir( String groupId )
    {
        try
        {
            // Check if the logging directory exists.
            var logDir = new File(
                    FileUtil.getUserHome() + "/." + groupId + "/log/" );

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
     * @param applicationId An application id.
     * @param logDir The log directory.
     * @return The logDir as passed.
     * @throws Exception
     */
    private static File initImpl(
            File logDir,
            String applicationId ) throws Exception
    {
        // Replaces all entries from the configuration by the new entries.
        // The exception is if the new entry is 'smack.logfile.path' then that gets
        // replaced by a dynamically assembled value.
        Function<String, BiFunction<String,String,String>> consume =
                (k) -> {
                    return (o,n) -> {
                        if ( ! "smack.logfile.path".equals( n ) )
                            return n;
                        return new File( logDir, applicationId + "_%g.log" ).toString();
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

        return logDir;
    }

    /**
     * Initialize logging.
     *
     * @param groupId A group id.
     * @param applicationId An application id.
     * @throws Exception
     */
    private static File init( String groupId, String applicationId )
    {
        try
        {
            return initImpl(
                    createLogDir( groupId ),
                    applicationId );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}
