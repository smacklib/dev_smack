package org.jdesktop.application;

public class ApplicationUnderTest
    extends CliApplication
{
    @Command
    private void cmdInt( int i )
    {
        out( "cmdInt( %d )", i);
    }

    public static void main( String[] argv )
    {
        launch( ApplicationUnderTest.class, argv );
    }
}
