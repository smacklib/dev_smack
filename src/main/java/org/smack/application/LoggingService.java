/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2017-2022 Michael G. Binz
 */
package org.smack.application;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.LogManager;

import org.smack.util.JavaUtil;
import org.smack.util.StringUtil;
import org.smack.util.resource.ResourceUtil;

/**
 * Configures java util logging.
 *
 * @author michab66
 */
class LoggingService
{
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
        _logDir =
                init( ac.getHome(), _applicationId );
    }

    LoggingService( File applicationHome, String applicationId )
    {
        JavaUtil.Assert(
                StringUtil.hasContent( applicationId ),
                "application.id not set.");

        _applicationId =
                applicationId;
        _logDir =
                init( applicationHome, _applicationId );
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
    private static File createLogDir( File applicationHome )
    {
        try
        {
            var logDir = new File(
                    applicationHome,
                    "log/" );

            if ( logDir.exists() && ! logDir.isDirectory() )
                Files.delete( logDir.toPath() );
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
        // The exception is if the new entry is 'smack.logfile.path' then this
        // is replaced by a dynamically assembled value.
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
    private static File init( File applicationHome, String applicationId )
    {
        try
        {
            return initImpl(
                    createLogDir( applicationHome ),
                    applicationId );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}
