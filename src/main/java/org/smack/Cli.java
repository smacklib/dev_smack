/* $Id$
 *
 * Common.
 *
 * Released under Gnu Public License
 * Copyright © 2020 Michael G. Binz
 */
package org.smack;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import org.smack.application.CliApplication;
import org.smack.application.CliApplication.Named;
import org.smack.util.StringUtil;
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

    public static void main( String[] argv )
    {
        launch( Cli::new, argv );
    }
}