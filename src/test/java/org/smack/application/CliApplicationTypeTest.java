package org.smack.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;
import org.smack.util.StringUtil;

public class CliApplicationTypeTest
{
    private String hex( Number n )
    {
        return String.format(
                "0x%x",
                n.longValue() );
    }
    private String dec( Number n )
    {
        return StringUtil.EMPTY_STRING +
                n.longValue();
    }

    private void TestType( String command, String argument, String exp )
    {
        final var err =
                new ArrayList<String>();
        final var out =
                new ArrayList<String>();

        CliApplicationTest.execCli(
                out,
                err,
                ApplicationUnderTest::main,
                command, argument );

        assertEquals(
                0,
                err.size() );

        String expected =
                String.format( "%s:%s", command, exp );

        assertEquals(
                1,
                out.size() );
        assertEquals(
                expected,
                out.get( 0 ) );
    }

    private void TestType( String command, String argument )
    {
        TestType( command, argument, argument );
    }

    @Test
    public void TestTypeByte()
    {
        var cmd = "cmdByte";
        TestType( cmd, dec( Byte.MAX_VALUE ) );
        TestType( cmd, dec( Byte.MIN_VALUE ) );
        TestType(
                cmd,
                hex( Byte.MAX_VALUE ),
                dec( Byte.MAX_VALUE ));
    }

    @Test
    public void TestTypeShort()
    {
        var cmd = "cmdShort";
        TestType( cmd, dec( Short.MAX_VALUE ) );
        TestType( cmd, dec( Short.MIN_VALUE ) );
        TestType(
                cmd,
                hex( Short.MAX_VALUE ),
                dec( Short.MAX_VALUE ));
    }

    @Test
    public void TestTypeInteger()
    {
        var cmd = "cmdInt";
        TestType( cmd, dec( Integer.MAX_VALUE ) );
        TestType( cmd, dec( Integer.MIN_VALUE ) );
        TestType(
                cmd,
                hex( Integer.MAX_VALUE ),
                dec( Integer.MAX_VALUE ));
    }

    @Test
    public void TestTypeLong()
    {
        var cmd = "cmdLong";
        TestType( cmd, dec( Long.MAX_VALUE ) );
        TestType( cmd, dec( Long.MIN_VALUE ) );
        TestType(
                cmd,
                hex( Long.MAX_VALUE ),
                dec( Long.MAX_VALUE ));
    }

    @Test
    public void TestTypeFloat()
    {
        TestType( "cmdFloat", "" + (float)Math.PI );
    }

    @Test
    public void TestTypeDouble()
    {
        TestType( "cmdDouble", "" + Math.E );
    }

    @Test
    public void TestTypeBoolean()
    {
        TestType( "cmdBoolean", "true" );
    }

    @Test
    public void TestTypeEnum()
    {
        var EXPECTED = "FRIDAY";

        TestType( "cmdEnum", "FRIDAY", EXPECTED );
    }

    @Test
    public void TestTypeFile() throws Exception
    {
        File tmpFile = File.createTempFile( "tmp", "bah" );
        tmpFile.createNewFile();
        tmpFile.deleteOnExit();

        try {
            assertTrue( tmpFile.exists() );

            final var err =
                    new ArrayList<String>();
            final var out =
                    new ArrayList<String>();

            CliApplicationTest.execCli(
                    out,
                    err,
                    ApplicationUnderTest::main,
                    "cmdFile",
                    tmpFile.getPath() );

            assertTrue( err.isEmpty() );

            String expected =
                    "cmdFile:" +
                            tmpFile.getPath();

            assertEquals(
                    1,
                    out.size() );
            assertEquals(
                    expected,
                    out.get( 0 ) );
        }
        finally
        {
            tmpFile.delete();
        }
    }

    @Test
    public void TestUnknownType() throws Exception
    {
        final var err =
                new ArrayList<String>();
        final var out =
                new ArrayList<String>();

        CliApplicationTest.execCli(
                out,
                err,
                ApplicationUnderTest::main,
                new String[0] );

        assertEquals( 0, out.size() );
        assertTrue( err.size() > 0 );
    }
}
