/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2020-21 Michael G. Binz
 */
package org.smack.application;

import java.io.IOException;
import java.util.logging.Level;

import org.junit.Test;


public class CliApplicationPropertyTest
{
    public static class AutProperties extends CliApplication
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

            launch( AutProperties::new, argv );
        }
    }

    private String[] s( String ... strings )
    {
        return strings;
    }

    @Test
    public void testIntExplicit() throws IOException
    {
        CliApplicationTest.execCli(
                AutProperties::main,
                s(
                        "setIntExplicit",
                        "-313",
                        "-intProperty=1"),
                new String[] { "-313" },
                CliApplicationTest.EMPTY_STRING_ARRAY );
    }

    @Test
    public void testBooleanSet() throws IOException
    {
        CliApplicationTest.execCli(
                AutProperties::main,
                s( "c_booleanProperty",
                        "-booleanProperty=true"),
                s("true"),
                CliApplicationTest.EMPTY_STRING_ARRAY );
    }

    @Test
    public void testBooleanUnset() throws IOException
    {
        CliApplicationTest.execCli(
                AutProperties::main,
                s( "c_booleanProperty"),
                s("false"),
                CliApplicationTest.EMPTY_STRING_ARRAY );
    }

    @Test
    public void testLevelSet() throws IOException
    {
        CliApplicationTest.execCli(
                AutProperties::main,
                s( "c_levelProperty",
                "-level=WARNING"),
                s( "WARNING" ),
                // Ignore duplicate resource converter warnings.
                CliApplicationTest.IGNORE );
    }

    @Test
    public void testLevelUnset() throws IOException
    {
        CliApplicationTest.execCli(
                AutProperties::main,
                s( "c_levelProperty" ),
                s( "ALL" ),
                CliApplicationTest.EMPTY_STRING_ARRAY );
    }
}
