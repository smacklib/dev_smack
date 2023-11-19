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
        public void add( String[] rest )
        {
            System.out.format(
                    "%s%n",
                    StringUtil.concatenate( ".", rest ) );
        }

        @Command( name = "total" )
        public void add( int[] rest )
        {
            int total = 0;

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
                 "total: int...",
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
                 "313"
             },
             null
         );
    }

    @Test
    public void testTotal() throws IOException
    {
         CliApplicationTest.execCli(
             UnderTestVariadic::main,
             new String[] {
                 "total",
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
}
