package org.smack.application;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.smack.util.StringUtil;

public class CliApplicationTypeTest
{
    static String[] s( String ... strings )
    {
        return strings;
    }

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
        CliApplicationTest.execCli(
                ApplicationUnderTest::main,
                s( command, argument ),
                s( String.format( "%s:%s", command, exp ) ),
                CliApplicationTest.EMPTY_STRING_ARRAY );
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
        TestType( "cmdBoolean", "false" );
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

            CliApplicationTest.execCli(
                    ApplicationUnderTest::main,
                    s( "cmdFile", tmpFile.getPath() ),
                    s( "cmdFile:" +
                            tmpFile.getPath() ),
                    CliApplicationTest.EMPTY_STRING_ARRAY );
        }
        finally
        {
            tmpFile.delete();
        }
    }
}
