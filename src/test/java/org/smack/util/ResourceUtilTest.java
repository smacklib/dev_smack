package org.smack.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class ResourceUtilTest
{
    @Test
    public void testLoadResource() throws Exception
    {
        var resource =
                ResourceUtil.loadResource( getClass(), "micbinz.ascii" );
        String micbinz =
                new String( resource, StandardCharsets.US_ASCII );
        assertEquals(
                "micbinz",
                micbinz );
    }
    @Test
    public void testLoadResourceBadName() throws Exception
    {
        try
        {
            ResourceUtil.loadResource( getClass(), "micbinz.no" );
            fail( "Must not reach this." );
        }
        catch (Exception ignore) {
        }
    }
}
