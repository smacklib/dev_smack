package org.smack.application;

/**
 * Tests type transformations.
 */
public class ApplicationUnderTestOverload
    extends CliApplication
{
    @Command
    public void cmdOverload()
    {
        out( "%s:%s\n", currentCommand(), 0 );
    }
    @Command
    public void cmdOverload( String n0 )
    {
        out( "%s:%s\n", currentCommand(), 1 );
    }
    @Command
    public void cmdOverload( String n0, String n1 )
    {
        out( "%s:%s\n", currentCommand(), 2 );
    }
    @Command
    public void cmdOverload( String n0, String n1, String n2 )
    {
        out( "%s:%s\n", currentCommand(), 3 );
    }

    public static void main( String[] argv )
    {
        launch( ApplicationUnderTestOverload::new, argv );
    }
}
