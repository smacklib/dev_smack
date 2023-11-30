/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2019-2022 Michael G. Binz
 */
package org.smack.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import org.junit.Test;

public class JavaUtilTest
{
    @Test( expected = RuntimeException.class )
    public void assertPlain()
    {
        JavaUtil.Assert( false );
        fail();
    }

    @Test
    public void assertMessage()
    {
        try
        {
            JavaUtil.Assert( false, "Donald" );
            fail();
        }
        catch ( Exception e )
        {
            assertEquals( "Donald", e.getMessage() );
        }
    }

    @Test
    public void assertFormatted()
    {
        try
        {
            JavaUtil.Assert( false, "%s%d", "Donald", 313  );
            fail();
        }
        catch ( Exception e )
        {
            assertEquals( "Donald313", e.getMessage() );
        }
    }

    @Test
    public void fmtX()
    {
        Exception e = JavaUtil.fmtX( "%s%s", "mic", "binz" );
        assertEquals( "micbinz", e.getMessage() );
    }
    @Test
    public void fmtXC()
    {
        Throwable cause = new Throwable( "bah" );
        Exception e = JavaUtil.fmtX( cause, "%s%s", "mic", "binz" );
        assertEquals( "micbinz", e.getMessage() );
        assertEquals( cause, e.getCause() );
    }

    @Test
    public void makeTest()
    {
        JButton b = JavaUtil.make(
                () -> {
                    var result = new JButton();
                    result.setText( "text" );
                    result.setName( "name" );
                    return result;
                });

        assertEquals( "text", b.getText() );
        assertEquals( "name", b.getName() );
    }

    @Test
    public void execTestOutErr_err_content() throws Exception
    {
        List<String> out = new ArrayList<String>();
        List<String> err = new ArrayList<String>();

        var rc = JavaUtil.exec( out, err, "java" );

        assertEquals( 1, rc );
        assertEquals( 0, out.size() );
        assertTrue( err.size() > 0 );
    }

    @Test
// Fails in Eclipse on Mac.
//  @Ignore
    public void execTestOutErr_out_content() throws Exception
    {
        List<String> out = new ArrayList<String>();
        List<String> err = new ArrayList<String>();

        var rc = JavaUtil.exec( out, err, "java", "--version" );

        assertEquals( 0, rc );
        assertEquals( 0, err.size() );
        assertTrue( out.size() > 0 );
    }

    @Test
    public void execTestNullErr_err_content() throws Exception
    {
        List<String> err = new ArrayList<String>();

        var rc = JavaUtil.exec( null, err, "java" );

        assertEquals( 1, rc );
        assertTrue( err.size() > 0 );
    }

    @Test
    public void execTestNullNull() throws Exception
    {
        var rc = JavaUtil.exec( null, null, "java" );

        assertEquals( 1, rc );
    }

    @Test
 // Fails in Eclipse on Mac.
//  @Ignore
    public void execTestOutNull_out_content() throws Exception
    {
        List<String> out = new ArrayList<String>();

        var rc = JavaUtil.exec( out, null, "java", "--version" );

        assertEquals( 0, rc );
        assertTrue( out.size() > 0 );
    }

    @Test( expected = RuntimeException.class )
    public void execTestEmptyCommand() throws Exception
    {
        List<String> out = new ArrayList<String>();
        List<String> err = new ArrayList<String>();

        JavaUtil.exec( out, err );
        fail();
    }

    @Test( expected = RuntimeException.class )
    public void execTestUnknownCommand() throws Exception
    {
        List<String> out = new ArrayList<String>();
        List<String> err = new ArrayList<String>();

        JavaUtil.exec( out, err, "_non_valid_executable_" );
        fail();
    }
}
