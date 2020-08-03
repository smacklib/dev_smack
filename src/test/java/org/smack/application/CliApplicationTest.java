package org.smack.application;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;
import org.smack.util.FileUtil;
import org.smack.util.StringUtil;

public class CliApplicationTest
{
    @Test
    public void testHelp() throws IOException
    {
        PrintStream originalErrOut =
                System.err;
        ByteArrayOutputStream errOs =
                new ByteArrayOutputStream();
        System.setErr( new PrintStream( errOs ) );

        PrintStream originalOut =
                System.out;
        ByteArrayOutputStream outOs =
                new ByteArrayOutputStream();
        System.setOut( new PrintStream( outOs ) );

        ApplicationUnderTest.main( new String[0] );

        System.err.flush();
        System.setErr( originalErrOut );
        System.out.flush();
        System.setOut( originalOut );

        String expectedString =
                "ApplicationUnderTest\n" +
                        "\n" +
                        "The following commands are supported:\n" +
                        "\n" +
                        "cmdBoolean: boolean\n" +
                        "cmdByte: byte\n" +
                        "cmdDouble: double\n" +
                        "cmdEnum: [FRIDAY, MONDAY, SATURDAY, SUNDAY, THURSDAY, TUESDAY, WEDNESDAY]\n" +
                        "cmdFile: File\n" +
                        "cmdFloat: float\n" +
                        "cmdInt: int\n" +
                        "cmdLong: long\n" +
                        "cmdShort: short\n" +
                        "cmdString: String\n";
        List<String> expectedLines =
                FileUtil.readLines(
                        new StringReader( expectedString ) );
        List<String> receivedLines =
                FileUtil.readLines(
                        new StringReader( errOs.toString() ) );

        assertEquals(
                expectedLines,
                receivedLines );
    }

    @Test
    public void testHelpDefault() throws IOException
    {
        PrintStream originalErrOut =
                System.err;
        ByteArrayOutputStream errOs =
                new ByteArrayOutputStream();
        System.setErr( new PrintStream( errOs ) );

        PrintStream originalOut =
                System.out;
        ByteArrayOutputStream outOs =
                new ByteArrayOutputStream();
        System.setOut( new PrintStream( outOs ) );

        ApplicationUnderTestDefault.main( new String[0] );

        System.err.flush();
        System.setErr( originalErrOut );
        System.out.flush();
        System.setOut( originalOut );

        String expectedString =
                "ApplicationUnderTestDefault\n" +
                        "\n" +
                        "The following commands are supported:\n" +
                        "\n" +
                        "*\n" +
                        "*: String\n" +
                        "*: String, String\n";
        List<String> expectedLines =
                FileUtil.readLines(
                        new StringReader( expectedString ) );
        List<String> receivedLines =
                FileUtil.readLines(
                        new StringReader( errOs.toString() ) );

        assertEquals(
                expectedLines,
                receivedLines );
    }

    @Test
    public void testArgListNotUnique() throws IOException
    {
        PrintStream originalErrOut =
                System.err;
        ByteArrayOutputStream errOs =
                new ByteArrayOutputStream();
        System.setErr( new PrintStream( errOs ) );

        PrintStream originalOut =
                System.out;
        ByteArrayOutputStream outOs =
                new ByteArrayOutputStream();
        System.setOut( new PrintStream( outOs ) );

        ApplicationUnderTestOverload.main(
                "cmdoverload 1 2 3 4".split( " " ) );

        System.err.flush();
        System.setErr( originalErrOut );
        System.out.flush();
        System.setOut( originalOut );

        String expectedString =
                "Parameter count does not match. Available alternatives:\n" +
                "cmdOverload\n" +
                "cmdOverload: String\n" +
                "cmdOverload: String, String\n" +
                "cmdOverload: String, String, String\n" +
                "\n";
        List<String> expectedLines =
                FileUtil.readLines(
                        new StringReader( expectedString ) );
        List<String> receivedLines =
                FileUtil.readLines(
                        new StringReader( errOs.toString() ) );

        assertEquals(
                expectedLines,
                receivedLines );
    }

    private void testType(
            String command,
            String argument,
            String expectedCommand,
            String expectedArg )
    {
        PrintStream originalErrOut =
                System.err;
        ByteArrayOutputStream errOs =
                new ByteArrayOutputStream();
        System.setErr( new PrintStream( errOs ) );

        PrintStream originalOut =
                System.out;
        ByteArrayOutputStream outOs =
                new ByteArrayOutputStream();
        System.setOut( new PrintStream( outOs ) );

        ApplicationUnderTest.main( new String[]{ command, argument } );

        System.err.flush();
        System.setErr( originalErrOut );
        System.out.flush();
        System.setOut( originalOut );

        assertEquals(
                StringUtil.EMPTY_STRING,
                errOs.toString() );

        String expected =
                String.format( "%s:%s\n",
                        expectedCommand,
                        expectedArg );
        String outOss =
                outOs.toString();

        assertEquals(
                expected,
                outOss );
    }

    private void testType( String command, String expectedCommand )
    {
        var dummy = "0";

        testType(
                command,
                dummy,
                expectedCommand,
                dummy );
    }

    @Test
    public void testNameUpperLowerCase()
    {
        var act = "cmdByte";

        testType( act, act );
        testType( "cmdbyte", act );
        testType( "CMDBYTE", act );
    }
}
