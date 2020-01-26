package org.jdesktop.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.smack.util.StringUtil;

public class CliApplicationTest
{
    private static List<String> readLines( Reader in ) throws IOException
    {
        BufferedReader din =
                new BufferedReader( in );

        ArrayList<String> result = new ArrayList<>();

        while ( true )
        {
            String c = din.readLine();

            if ( c == null )
                break;

            result.add( c );
        }

        return result;
    }

    @Test
    public void TestHelp() throws IOException
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
                        "cmdString: String\n\n";
        List<String> expectedLines =
                readLines(
                        new StringReader( expectedString ) );
        List<String> receivedLines =
                readLines(
                        new StringReader( errOs.toString() ) );

        assertEquals(
                expectedLines,
                receivedLines );
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
    public void TestTypeEnum()
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

        ApplicationUnderTest.main( new String[]{ "cmdEnum", "Friday" } );

        System.err.flush();
        System.setErr( originalErrOut );
        System.out.flush();
        System.setOut( originalOut );

        assertTrue( StringUtil.isEmpty( errOs.toString() ) );

        String expected =
                "cmdEnum:FRIDAY\n";
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
