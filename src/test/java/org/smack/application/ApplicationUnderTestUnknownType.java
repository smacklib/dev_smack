package org.smack.application;

import javax.swing.JFrame;

/**
 * Tests type transformations.
 */
public class ApplicationUnderTestUnknownType
    extends CliApplication
{
    /**
     * Must fail, no mapper.
     * @param i Parameter
     */
    @Command
    private void cmdJFrame( JFrame i )
    {
        out( "%s( %d )", currentCommand(), i );
    }

    public static void main( String[] argv )
    {
        launch( ApplicationUnderTestUnknownType::new, argv );
    }
}
