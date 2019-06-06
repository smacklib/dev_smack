package org.jdesktop.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.jdesktop.util.StringUtil;
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
                        "cmdBoolean: boolean\n" +
                        "cmdByte: byte\n" +
                        "cmdDouble: double\n" +
                        "cmdFile: File\n" +
                        "cmdFloat: float\n" +
                        "cmdInt: int\n" +
                        "cmdLong: long\n" +
                        "cmdShort: short\n" +
                        "cmdString: String\n\r\n";
        String errOss =
                errOs.toString();

        assertEquals(
                expected,
                errOss );
    }

    @Test
    public void TestTypeBoolean()
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

        ApplicationUnderTest.main( new String[]{ "cmdBoolean", "true" } );

        System.err.flush();
        System.setErr( originalErrOut );
        System.out.flush();
        System.setOut( originalOut );

        assertTrue( StringUtil.isEmpty( errOs.toString() ) );

        String expected =
                "cmdBoolean:true\n";
        String outOss =
                outOs.toString();

        assertEquals(
                expected,
                outOss );
    }
    @Test
    public void TestTypeFile() throws Exception
    {
        PrintStream originalErrOut =
                System.err;
        ByteArrayOutputStream errOs =
                new ByteArrayOutputStream();
        System.setErr( new PrintStream( errOs ) );

        File tmpFile = File.createTempFile( "tmp", "bah" );
        tmpFile.createNewFile();

        try {
            PrintStream originalOut =
                    System.out;
            ByteArrayOutputStream outOs =
                    new ByteArrayOutputStream();
            System.setOut( new PrintStream( outOs ) );

            assertTrue( tmpFile.exists() );

            ApplicationUnderTest.main( new String[]{ "cmdFile", tmpFile.getPath() } );

            System.err.flush();
            System.setErr( originalErrOut );
            System.out.flush();
            System.setOut( originalOut );

            assertTrue( StringUtil.isEmpty( errOs.toString() ) );

            String expected =
                    "cmdFile:" +
                            tmpFile.getPath() +
                            "\n";
            String outOss =
                    outOs.toString();

            assertEquals(
                    expected,
                    outOss );
            assertTrue( tmpFile.exists() );
        }
        finally
        {
            tmpFile.delete();
        }
    }

    @Test
    public void TestUnknownType() throws Exception
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

        assertTrue( StringUtil.hasContent( errOs.toString() ) );
    }
}
