package org.smack.application;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;
import org.smack.util.Disposer;
import org.smack.util.JavaUtil;
import org.smack.util.StringUtil;
import org.smack.util.io.Redirect;

public class CliApplicationTest
{
    static void execCli(
            List<String> out,
            List<String> err,
            Consumer<String[]> cliApplicationMain,
            String ... argv  )
    {
        try ( Disposer d = new Disposer() )
        {
            final var errRedir = err != null ?
                    d.register( new Redirect( Redirect.StdStream.err ) ) :
                        null;
            final var outRedir = out != null ?
                    d.register( new Redirect( Redirect.StdStream.out ) ) :
                        null;

            cliApplicationMain.accept( argv );

            if ( out != null )
                out.addAll( outRedir.content() );
            if ( err != null )
                err.addAll( errRedir.content() );
        }
    }

    static void execCli(
            Consumer<String[]> cliApplicationMain,
            String[] argv,
            String[] out,
            String[] err
            )
    {
        try ( Disposer d = new Disposer() )
        {
            @SuppressWarnings("resource")
            final var errRedir = ! JavaUtil.isEmptyArray( err ) ?
                    d.register( new Redirect( Redirect.StdStream.err ) ) :
                        null;
            @SuppressWarnings("resource")
            final var outRedir = ! JavaUtil.isEmptyArray( out ) ?
                    d.register( new Redirect( Redirect.StdStream.out ) ) :
                        null;

            cliApplicationMain.accept( argv );

            if ( outRedir != null )
                assertEquals( Arrays.asList( out ), outRedir.content() );

            if ( errRedir != null )
                assertEquals( Arrays.asList( err ), errRedir.content() );
        }
    }

    @Test
    public void testHelp() throws IOException
    {
        execCli( ApplicationUnderTest::main,
            new String[0],
            null,
            new String[] {
                "ApplicationUnderTest",
                "The following commands are supported:",
                "cmdBoolean: boolean",
                "cmdByte: byte",
                "cmdDouble: double",
                "cmdEnum: [FRIDAY, MONDAY, SATURDAY, SUNDAY, THURSDAY, TUESDAY, WEDNESDAY]",
                "cmdFile: File",
                "cmdFloat: float",
                "cmdInt: int",
                "cmdLong: long",
                "cmdShort: short",
                "cmdString: String" }
            );
    }

    @Test
    public void testHelpDefault() throws IOException
    {
        execCli(
            ApplicationUnderTestDefault::main,
            new String[0],
            null,
            new String[] {
                "ApplicationUnderTestDefault",
                "The following commands are supported:",
                "*",
                "*: String",
                "*: String, String"
            }
        );
    }

    @Test
    public void testArgListNotUnique() throws IOException
    {
        final var err =
                new ArrayList<String>();
        final var out =
                new ArrayList<String>();

        execCli(
                out,
                err,
                ApplicationUnderTestOverload::main,
                "cmdoverload 1 2 3 4".split( " " ) );

        assertEquals( 0, out.size() );

        List<String> expectedLines = Arrays.asList( new String[] {
                "Parameter count does not match. Available alternatives:",
                "cmdOverload",
                "cmdOverload: String",
                "cmdOverload: String, String",
                "cmdOverload: String, String, String",
                StringUtil.EMPTY_STRING
        } );

        List<String> receivedLines =
                err;

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
        String expected =
                String.format( "%s:%s",
                        expectedCommand,
                        expectedArg );

        execCli( ApplicationUnderTest::main,
                new String[] { command, argument },
                new String[] { expected },
                null );
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
        testType( act.toLowerCase(), act );
        testType( act.toUpperCase(), act );
    }

    @Test
    public void testUnknownCommand()
    {
        final var badName =
                "unknown-command";

        execCli( ApplicationUnderTest::main,
                new String[] { badName },
                null,
                new String[] {
                String.format( "Unknown command '%s'.",
                        badName ) }
        );
    }
}
