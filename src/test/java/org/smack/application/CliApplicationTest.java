package org.smack.application;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;
import org.smack.util.FileUtil;
import org.smack.util.StringUtil;

public class CliApplicationTest
{
    static void execCli(
            List<String> out,
            List<String> err,
            Consumer<String[]> cliApplicationMain,
            String ... argv  )
    {
        StringBuilder outb =
                out == null ? null : new StringBuilder();
        StringBuilder errb =
                err == null ? null : new StringBuilder();

        execCli(
                outb,
                errb,
                cliApplicationMain,
                argv );

        if ( out != null )
        {
            var outx = outb.toString();
            if ( out != null && StringUtil.hasContent( outx ) )
                for ( String c : outx.split( StringUtil.EOL ) )
                    out.add( c );
        }
        if ( err != null )
        {
            var errx = errb.toString();
            if ( StringUtil.hasContent( errx ) )
                for ( String c : errx.split( StringUtil.EOL ) )
                    err.add( c );
        }
    }

    /**
     * Get results as strings holding all lines.
     *
     * @param out
     * @param err
     * @param cliApplicationMain
     * @param argv
     */
    static void execCli(
            StringBuilder out,
            StringBuilder err,
            Consumer<String[]> cliApplicationMain,
            String ... argv  )
    {
        PrintStream originalErr =
                System.err;
        ByteArrayOutputStream errOs =
                new ByteArrayOutputStream();
        System.setErr( new PrintStream( errOs ) );

        PrintStream originalOut =
                System.out;
        ByteArrayOutputStream outOs =
                new ByteArrayOutputStream();
        System.setOut( new PrintStream( outOs ) );

        cliApplicationMain.accept( argv );

        System.err.flush();
        System.setErr( originalErr );
        System.out.flush();
        System.setOut( originalOut );

        if ( out != null )
            out.append( outOs.toString() );
        if ( err != null )
            err.append( errOs.toString() );
    }

    @Test
    public void testHelp() throws IOException
    {
        final var err =
                new StringBuilder();
        final var out =
                new StringBuilder();

        execCli(
                out,
                err,
                ApplicationUnderTest::main,
                new String[0] );

        assertEquals( 0, out.length() );

        String expectedString =
                "ApplicationUnderTest\n" +
                        "The following commands are supported:\n" +
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
                        new StringReader( err.toString() ) );

        assertEquals(
                expectedLines,
                receivedLines );
    }

    @Test
    public void testHelpDefault() throws IOException
    {
        final var err =
                new StringBuilder();
        final var out =
                new StringBuilder();

        execCli(
                out,
                err,
                ApplicationUnderTestDefault::main,
                new String[0] );

        assertEquals( 0, out.length() );

        String expectedString =
                "ApplicationUnderTestDefault\n" +
                        "The following commands are supported:\n" +
                        "*\n" +
                        "*: String\n" +
                        "*: String, String\n";
        List<String> expectedLines =
                FileUtil.readLines(
                        new StringReader( expectedString ) );
        List<String> receivedLines =
                FileUtil.readLines(
                        new StringReader( err.toString() ) );

        assertEquals(
                expectedLines,
                receivedLines );
    }

    @Test
    public void testArgListNotUnique() throws IOException
    {
        final var err =
                new StringBuilder();
        final var out =
                new StringBuilder();

        assertEquals( 0, out.length() );

        execCli(
                out,
                err,
                ApplicationUnderTestOverload::main,
                "cmdoverload 1 2 3 4".split( " " ) );

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
                        new StringReader( err.toString() ) );

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
        final var err =
                new StringBuilder();
        final var out =
                new StringBuilder();

        execCli( out, err,
                ApplicationUnderTest::main,
                command,
                argument );

        assertEquals(
                StringUtil.EMPTY_STRING,
                err.toString() );

        String expected =
                String.format( "%s:%s%n",
                        expectedCommand,
                        expectedArg );

        assertEquals(
                expected,
                out.toString() );
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


    @Test
    public void testUnknownCommand()
    {
        final var err =
                new ArrayList<String>();
        final var out =
                new ArrayList<String>();
        final var badName =
                "unknown-command";

        execCli( out, err,
                ApplicationUnderTest::main,
                badName );

        assertEquals(
                0,
                out.size() );
        assertEquals(
                1,
                err.size() );

        String expected =
                String.format( "Unknown command '%s'.",
                        badName );

        assertEquals(
                expected,
                err.get( 0 ) );
    }
}
