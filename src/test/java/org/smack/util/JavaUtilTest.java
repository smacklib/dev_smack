package org.smack.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
}
