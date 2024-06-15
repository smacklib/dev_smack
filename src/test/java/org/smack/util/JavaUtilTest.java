/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2019-2022 Michael G. Binz
 */
package org.smack.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import org.junit.jupiter.api.Test;

public class JavaUtilTest
{
    @Test
    public void assertPlain()
    {
        assertThrows(
                RuntimeException.class,
                () -> { JavaUtil.Assert( false ); } );
    }

    @Test
    public void assertMessage()
    {
        var ex = assertThrows(
                Exception.class,
                () -> { JavaUtil.Assert( false, "Donald" ); } );

        assertEquals( "Donald", ex.getMessage() );
    }

    @Test
    public void assertFormatted()
    {
        var ex = assertThrows(
                Exception.class,
                () -> { JavaUtil.Assert( false, "%s%d", "Donald", 313  ); } );

        assertEquals( "Donald313", ex.getMessage() );
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

    @Test
    public void execTestEmptyCommand() throws Exception
    {
        assertThrows(
                RuntimeException.class,
                () -> {
                    List<String> out = new ArrayList<String>();
                    List<String> err = new ArrayList<String>();

                    JavaUtil.exec( out, err ); } );
    }

    @Test
    public void execTestUnknownCommand() throws Exception
    {
        assertThrows(
                RuntimeException.class,
                () -> {
                    List<String> out = new ArrayList<String>();
                    List<String> err = new ArrayList<String>();

                    JavaUtil.exec( out, err, "_non_valid_executable_" );
                } );
    }
}
