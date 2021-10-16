package org.smack.application;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

import org.junit.Test;
import org.smack.util.Disposer;
import org.smack.util.JavaUtil;
import org.smack.util.StringUtil;
import org.smack.util.io.Redirect;

public class CliApplicationTest
{
    static final String[] EMPTY_STRING_ARRAY = new String[0];
    static final String[] IGNORE = new String[] { "ignore" };

    static String[] s( String ... strings )
    {
        return strings;
    }

    /**
     * An operation that supports cli testing.
     *
     * @param cliApplicationMain The main operation to call.
     * @param argv The parameters to pass.
     * @param out The expected lines on stdout. Pass {@link #IGNORE} if this is
     * to be ignored.
     * @param err The expected lines on stderr. Pass {@link #IGNORE} if this is
     * to be ignored.
     */
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

            if ( outRedir != null && out != IGNORE )
                assertEquals( Arrays.asList( out ), outRedir.content() );

            if ( errRedir != null && err != IGNORE )
                assertEquals( Arrays.asList( err ), errRedir.content() );
        }
    }

    static public class UnderTestDeprecated extends CliApplication
    {
        @Command
        public void add( int a, int b )
        {
            out( "%s%n", a + b );
        }
        public static void main( String[] argv )
        {
            launch( UnderTestDeprecated.class, argv );
        }
    }


    @Test
    public void testHelpDeprecated() throws IOException
    {
        execCli( UnderTestDeprecated::main,
            EMPTY_STRING_ARRAY,
            EMPTY_STRING_ARRAY,
            s(
                "UnderTestDeprecated",
                "The following commands are supported:",
                "add: int, int" )
            );
    }

    @Test
    public void testHelp() throws IOException
    {
        execCli( ApplicationUnderTest::main,
            EMPTY_STRING_ARRAY,
            EMPTY_STRING_ARRAY,
            s(
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
                "cmdString: String" )
            );
    }

    @Test
    public void testHelpDefault() throws IOException
    {
        execCli(
            ApplicationUnderTestDefault::main,
            CliApplicationTest.EMPTY_STRING_ARRAY,
            CliApplicationTest.EMPTY_STRING_ARRAY,
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
        execCli(
                ApplicationUnderTestOverload::main,
                "cmdoverload 1 2 3 4".split( " " ),
                EMPTY_STRING_ARRAY,
                s (
                "Parameter count does not match. Available alternatives:",
                "cmdOverload",
                "cmdOverload: String",
                "cmdOverload: String, String",
                "cmdOverload: String, String, String",
                StringUtil.EMPTY_STRING
        ) );
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
                s( badName ),
                EMPTY_STRING_ARRAY,
                s(
                String.format( "Unknown command '%s'.",
                        badName ) )
        );
    }
}
