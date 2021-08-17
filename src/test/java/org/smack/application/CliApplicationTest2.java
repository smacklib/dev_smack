package org.smack.application;

import java.io.IOException;

import org.junit.Test;
import org.smack.application.CliApplication.Command;
import org.smack.application.CliApplication.Property;
import org.smack.util.StringUtil;

public class CliApplicationTest2
{
    static class UnderTest
    {
        @Property
        public String nameProperty;

        @Command
        public void add( int a, int b )
        {
            System.out.format( "%s%n", a + b );
        }
        @Command( name = "plus3" )
        public void add( int a, int b, int c )
        {
            System.out.format( "%s%n", a + b + c );
        }
        @Command
        public void property()
        {
            System.out.format( "%s%n", nameProperty );
        }
        public static void main( String[] argv )
        {
            CliApplication.launch( UnderTest::new, argv );
        }
    }

    @Test
    public void testHelp() throws IOException
    {
         CliApplicationTest.execCli(
             UnderTest::main,
             CliApplicationTest.EMPTY_STRING_ARRAY,
             CliApplicationTest.EMPTY_STRING_ARRAY,
             new String[]
             {
                 "UnderTest",
                 "The following commands are supported:",
                 "add: int, int",
                 "plus3: int, int, int",
                 "property",
                 StringUtil.EMPTY_STRING,
                 "Properties:",
                 "-nameProperty=(String)"
             }
         );
    }

    @Test
    public void testAdd2() throws IOException
    {
         CliApplicationTest.execCli(
             UnderTest::main,
             new String[] {
                 "add",
                 "300",
                 "13"
             },
             new String[]
             {
                 "313"
             },
             null
         );
    }

    @Test
    public void testAdd3() throws IOException
    {
         CliApplicationTest.execCli(
             UnderTest::main,
             new String[] {
                 "plus3",
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
    public void testProperty() throws IOException
    {
         CliApplicationTest.execCli(
             UnderTest::main,
             new String[] {
                 "property",
                 "-nameProperty=Donald"
             },
             new String[]
             {
                 "Donald"
             },
             null
         );
    }
}
