package org.smack.application;

import java.io.IOException;

import org.junit.Test;
import org.smack.application.CallableModule.Command;

public class CliApplicationTest2
{
    static class UnderTest
    {
        @Command
        public void add( int a, int b )
        {
            System.out.format( "%s%n", a + b );
        }
        @Command
        public void add( int a, int b, int c )
        {
            System.out.format( "%s%n", a + b + c );
        }
        public static void main( String[] argv )
        {
            CallableModule.launch( UnderTest::new, argv );
        }
    }

    @Test
    public void testHelp() throws IOException
    {
         CliApplicationTest.execCli(
             UnderTest::main,
             new String[0],
             null,
             new String[]
             {
                 "UnderTest",
                 "The following commands are supported:",
                 "add: int, int",
                 "add: int, int, int"
             }
         );
    }

    @Test
    public void testadd2() throws IOException
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

//    @Test
//    public void testArgListNotUnique() throws IOException
//    {
//        final var err =
//                new ArrayList<String>();
//        final var out =
//                new ArrayList<String>();
//
//        execCli(
//                out,
//                err,
//                ApplicationUnderTestOverload::main,
//                "cmdoverload 1 2 3 4".split( " " ) );
//
//        assertEquals( 0, out.size() );
//
//        List<String> expectedLines = Arrays.asList( new String[] {
//                "Parameter count does not match. Available alternatives:",
//                "cmdOverload",
//                "cmdOverload: String",
//                "cmdOverload: String, String",
//                "cmdOverload: String, String, String",
//                StringUtil.EMPTY_STRING
//        } );
//
//        List<String> receivedLines =
//                err;
//
//        assertEquals(
//                expectedLines,
//                receivedLines );
//    }
//
//    private void testType(
//            String command,
//            String argument,
//            String expectedCommand,
//            String expectedArg )
//    {
//        final var err =
//                new ArrayList<String>();
//        final var out =
//                new ArrayList<String>();
//
//        execCli( out, err,
//                ApplicationUnderTest::main,
//                command,
//                argument );
//
//        assertEquals(
//                0,
//                err.size() );
//
//        String expected =
//                String.format( "%s:%s",
//                        expectedCommand,
//                        expectedArg );
//
//        assertEquals(
//                1,
//                out.size() );
//        assertEquals(
//                expected,
//                out.get( 0 ) );
//    }
//
//    private void testType( String command, String expectedCommand )
//    {
//        var dummy = "0";
//
//        testType(
//                command,
//                dummy,
//                expectedCommand,
//                dummy );
//    }
//
//    @Test
//    public void testNameUpperLowerCase()
//    {
//        var act = "cmdByte";
//
//        testType( act, act );
//        testType( act.toLowerCase(), act );
//        testType( act.toUpperCase(), act );
//    }
//
//    @Test
//    public void testUnknownCommand()
//    {
//        final var err =
//                new ArrayList<String>();
//        final var out =
//                new ArrayList<String>();
//        final var badName =
//                "unknown-command";
//
//        execCli( out, err,
//                ApplicationUnderTest::main,
//                badName );
//
//        assertEquals(
//                0,
//                out.size() );
//        assertEquals(
//                1,
//                err.size() );
//
//        String expected =
//                String.format( "Unknown command '%s'.",
//                        badName );
//
//        assertEquals(
//                expected,
//                err.get( 0 ) );
//    }
}
