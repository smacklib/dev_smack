package org.smack.util;

import static org.junit.Assert.assertEquals;

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
}
