package org.smack.application;

public class TestAppProperties extends CliApplication
{
    @Property
    public boolean booleanProperty;

    @Command
    public void hello()
    {
        out( "booleanProperty=%s%n", booleanProperty );
    }

    public static void main( String[] argv )
    {
        launch( TestAppProperties::new, argv );
    }
}
