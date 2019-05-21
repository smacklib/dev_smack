package org.jdesktop.application;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class CliApplicationTest
{
    @Test
    public void TestHelp()
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

        String expected =
                "ApplicationUnderTest\n" +
                "\n" +
                "The following commands are supported:\n" +
                "\n" +
                "cmdInt: int\n\r\n";
        String errOss =
                errOs.toString();

        System.out.printf( "%d %d\n", expected.length(), errOss.length() );
        assertEquals(
                expected,
                errOss );
    }
}
