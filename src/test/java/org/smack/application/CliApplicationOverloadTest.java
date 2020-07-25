package org.smack.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.Test;
import org.smack.util.StringUtil;

public class CliApplicationOverloadTest
{
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
