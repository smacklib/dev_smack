/* $Id$
 *
 * Common.
 *
 * Released under Gnu Public License
 * Copyright © 2020 Michael G. Binz
 */
package org.smack;

import java.awt.Image;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import org.smack.application.CliApplication;
import org.smack.application.CliApplication.Named;
import org.smack.util.JavaUtil;
import org.smack.util.ServiceManager;
import org.smack.util.StringUtil;
import org.smack.util.resource.ResourceManager;
import org.smack.util.resource.ResourceManager.Resource;
import org.smack.util.xml.XmlUtil;

/**
 * A collection of cli utilities based on Smack.
 *
 * @author michab66
 */
@Named( description="A collection of cli utilities based on Smack." )
public final class Cli extends CliApplication
{
    private Cli()
    {
    }

    @Command( shortDescription =
            "Tranforms xml using stylesheet.  Writes the result to stdout." )
    public void xsl(
            @Named( value="stylesheet" ) File stylesheet,
            @Named( value="xml-file") File xml ) throws Exception
    {
        out( "%s\n", XmlUtil.transform( stylesheet, xml ) );
    }

    @Command( shortDescription =
            "Tranforms xml using stylesheet.  "
            + "Writes the result to target-file.  "
            + "If target-file exists, it is overwritten." )
    public void xsl(
            @Named( value="stylesheet" ) File stylesheet,
            @Named( value="xml-file") File xml,
            @Named( value="target-file") String target
            ) throws Exception
    {
        try ( Writer w = new FileWriter( target ) )
        {
            w.write( XmlUtil.transform( stylesheet, xml ) );
            w.write( StringUtil.EOL );
        }
    }

    @Resource
    private Image image;

    @Command
    public void test()
    {
        var rm =
                ServiceManager.getApplicationService( ResourceManager.class );
        rm.injectResources( this );
        JavaUtil.Assert( image != null );
    }

    public static void main( String[] argv )
    {
        launch( Cli::new, argv );
    }
}
