/* $Id$
 *
 * Common.
 *
 * Released under Gnu Public License
 * Copyright © 2020 Michael G. Binz
 */
package org.smack;

import java.io.File;

import org.jdesktop.application.CliApplication;
import org.smack.util.XmlUtil;

/**
 *
 * @author michab66
 */
public final class Cli extends CliApplication
{
    //
    private Cli()
    {
    }

    @Command
    public void xsl(
            @Named File stylesheet,
            @Named File xml ) throws Exception
    {
        out( "%s\n", XmlUtil.transform( stylesheet, xml ) );
    }

    public static void main( String[] argv )
    {
        launch( Cli::new, argv );
    }
}
