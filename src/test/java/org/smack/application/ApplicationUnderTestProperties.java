package org.smack.application;

import java.util.logging.Level;

public class ApplicationUnderTestProperties extends CliApplication
{
    enum Ducks { TICK, TRICK, TRACK };

    @Property
    public boolean booleanProperty;
    @Property
    public byte byteProperty;
    @Property
    public short shortProperty;
    @Property
    public int intProperty;
    @Property
    public long longProperty;
    @Property
    public float floatProperty;
    @Property
    public double doubleProperty;
    @SuppressWarnings("exports")
    @Property( name = "duck", description = "One of the nephews names." )
    public Ducks duckProperty = Ducks.TRACK;
    @SuppressWarnings("exports")
    @Property( name = "level" )
    public Level levelProperty = Level.ALL;

    @Command
    public void c_booleanProperty()
    {
        out( "%s%n", booleanProperty );
    }

    @Command
    public void c_levelProperty()
    {
        out( "%s%n", levelProperty );
    }

    @Command
    public void setIntExplicit( int value )
    {
        intProperty = value;
        out( "%s%n", intProperty );
    }

    @Command
    public void hello()
    {
        out( "booleanProperty=%s%n", booleanProperty );
        out( "byteProperty=%d%n", byteProperty );
        out( "shortProperty=%d%n", shortProperty );
        out( "intProperty=%d%n", intProperty );
        out( "longProperty=%d%n", longProperty );
        out( "floatProperty=%f%n", floatProperty );
        out( "doubleProperty=%f%n", doubleProperty );
        out( "duckProperty=%s%n", duckProperty );
        out( "levelProperty=%s%n", levelProperty );
    }

    public static void main( String[] argv )
    {
        addConverter( Level.class,
                s->{
                    switch ( s )
                    {
                    case "INFO":
                        return Level.INFO;
                    case "WARNING":
                        return Level.WARNING;
                    default:
                        return Level.OFF;
                    }
                });

        launch( ApplicationUnderTestProperties::new, argv );
    }
}
