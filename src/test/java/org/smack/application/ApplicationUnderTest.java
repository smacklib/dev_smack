package org.smack.application;

import java.io.File;

/**
 * Tests type transformations.
 *
 * Do not add property annotations on this class, since some tests
 * rely on the fact that none exist.
 */
public class ApplicationUnderTest
    extends CliApplication
{
    public enum Day {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY,
        THURSDAY, FRIDAY, SATURDAY
    }

    @Command
    private void cmdShort( short i )
    {
        out( "%s:%d%n", currentCommand(), i );
    }

    /**
     * Place this here.  cmdByte must be sorted first.
     * @param i Test parameter.
     */
    @Command
    private void cmdByte( byte i )
    {
        out( "%s:%d%n", currentCommand(), i );
    }

    @Command
    private void cmdInt( int i )
    {
        out( "%s:%d%n", currentCommand(), i );
    }

    @Command
    private void cmdLong( long i )
    {
        out( "%s:%d%n", currentCommand(), i );
    }

    @Command
    private void cmdFloat( float i )
    {
        out( "%s:%s%n", currentCommand(), i );
    }

    @Command
    private void cmdDouble( double i )
    {
        out( "%s:%s%n", currentCommand(), i );
    }

    @Command
    private void cmdFile( File i )
    {
        out( "%s:%s%n", currentCommand(), i.getPath() );
    }

    @Command
    private void cmdString( String i )
    {
        out( "%s:%s%n", currentCommand(), i );
    }

    @Command
    private void cmdBoolean( boolean i )
    {
        out( "%s:%s%n", currentCommand(), i );
    }

    @Command
    private void cmdEnum( Day i )
    {
        out( "%s:%s%n", currentCommand(), i );
    }

    public static void main( String[] argv )
    {
        launch( ApplicationUnderTest::new, argv );
    }
}
