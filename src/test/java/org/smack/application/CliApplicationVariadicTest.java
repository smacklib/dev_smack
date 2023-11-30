/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2016-21 Michael G. Binz
 */
package org.smack.application;

import java.io.IOException;

import org.junit.Test;
import org.smack.application.CliApplication.Command;
import org.smack.util.StringUtil;

public class CliApplicationVariadicTest
{
    static class UnderTestVariadic
    {
        @Command( name = "concatenate" )
        public void add( String ... rest )
        {
            System.out.format(
                    "%s%n",
                    StringUtil.concatenate( ".", rest ) );
        }

        @Command( name = "totalIntegers" )
        public void add( int ... rest )
        {
            int total = 0;

            for ( var c : rest )
                total += c;

            System.out.format( "%s%n", total );
        }

        @Command( name = "totalBytes" )
        public void add( byte ... rest )
        {
            byte total = 0;

            for ( var c : rest )
                total += c;

            System.out.format( "%s%n", total );
        }

        @Command( name = "totalShorts" )
        public void add( short ... rest )
        {
            short total = 0;

            for ( var c : rest )
                total += c;

            System.out.format( "%s%n", total );
        }

        @Command( name = "totalLongs" )
        public void add( long ... rest )
        {
            long total = 0;

            for ( var c : rest )
                total += c;

            System.out.format( "%s%n", total );
        }

        @Command( name = "totalFloats" )
        public void add( float ... rest )
        {
            float total = 0;

            for ( var c : rest )
                total += c;

            System.out.format( "%s%n", total );
        }

        @Command( name = "totalDoubles" )
        public void add( double ... rest )
        {
            double total = 0;

            for ( var c : rest )
                total += c;

            System.out.format( "%s%n", total );
        }

        public static void main( String[] argv )
        {
            CliApplication.launch( UnderTestVariadic::new, argv );
        }
    }

    @Test
    public void testHelp() throws IOException
    {
         CliApplicationTest.execCli(
             UnderTestVariadic::main,
             CliApplicationTest.EMPTY_STRING_ARRAY,
             CliApplicationTest.EMPTY_STRING_ARRAY,
             new String[]
             {
                 "UnderTestVariadic",
                 "The following commands are supported:",
                 "concatenate: String...",
                 "totalBytes: byte...",
                 "totalDoubles: double...",
                 "totalFloats: float...",
                 "totalIntegers: int...",
                 "totalLongs: long...",
                 "totalShorts: short...",
             }
         );
    }

    @Test
    public void testConcatenate() throws IOException
    {
         CliApplicationTest.execCli(
             UnderTestVariadic::main,
             new String[] {
                 "concatenate",
                 "3",
                 "1",
                 "3"
             },
             new String[]
             {
                 "3.1.3"
             },
             null
         );
    }

    @Test
    public void testTotalBytes() throws IOException
    {
        CliApplicationTest.execCli(
                UnderTestVariadic::main,
                new String[] {
                        "totalBytes",
                        "5",
                        "1",
                        "2"
                },
                new String[]
                {
                        "8"
                },
                null
        );
    }

    @Test
    public void testTotalShorts() throws IOException
    {
        CliApplicationTest.execCli(
                UnderTestVariadic::main,
                new String[] {
                        "totalShorts",
                        "3",
                        "10",
                        "300"
                },
                new String[]
                {
                        "313"
                },
                null
        );
    }

    @Test
    public void testTotalIntegers() throws IOException
    {
        CliApplicationTest.execCli(
                UnderTestVariadic::main,
                new String[] {
                        "totalIntegers",
                        "300",
                        "10",
                        "3"
                },
                new String[]
                        {
                                "313"
                        },
                        null
                );
    }

    @Test
    public void testTotalLongs() throws IOException
    {
        CliApplicationTest.execCli(
                UnderTestVariadic::main,
                new String[] {
                        "totalLongs",
                        "" + (Integer.MAX_VALUE * 2),
                        "" + (Integer.MAX_VALUE * 2),
                        "" + (Integer.MAX_VALUE * 2),
                },
                new String[]
                        {
                                "" + (Integer.MAX_VALUE * 6),
                        },
                        null
                );
    }

    @Test
    public void testTotalFloats() throws IOException
    {
        CliApplicationTest.execCli(
                UnderTestVariadic::main,
                new String[] {
                        "totalFloats",
                        "0.25",
                        "0.75",
                        "0.25",
                },
                new String[]
                        {
                                "1.25",
                        },
                        null
                );
    }

    @Test
    public void testTotalDoubles() throws IOException
    {
        CliApplicationTest.execCli(
                UnderTestVariadic::main,
                new String[] {
                        "totalFloats",
                        "0.25",
                        "0.75",
                        "0.25",
                },
                new String[]
                        {
                                "1.25",
                        },
                        null
                );
    }
}
