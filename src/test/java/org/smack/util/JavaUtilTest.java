package org.smack.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JavaUtilTest
{
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
}
