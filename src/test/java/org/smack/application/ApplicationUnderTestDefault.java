package org.smack.application;

/**
 * Tests type transformations.
 */
public class ApplicationUnderTestDefault
    extends CliApplication
{
    @Command( name = "*" )
    public void defaultCmd()
    {
        out( "%s:%s\n", currentCommand(), 0 );
    }
    @Command( name = "*" )
    public void defaultCmd( String n0 )
    {
        out( "%s:%s\n", currentCommand(), 1 );
    }
    @Command( name = "*" )
    public void defaultCmd( String n0, String n1 )
    {
        out( "%s:%s\n", currentCommand(), 2 );
    }

    public static void main( String[] argv )
    {
        launch( ApplicationUnderTestDefault::new, argv );
    }
}
