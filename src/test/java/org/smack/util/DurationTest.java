package org.smack.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DurationTest
{
    @Test
    public void testSec()
    {
        assertEquals( 1000, Duration.MS_SEC );
    }
    @Test
    public void testDuration1000()
    {
        var d1000 = new Duration( Duration.MS_SEC );

        assertEquals( Duration.MS_SEC, d1000.getDurationMs() );
        assertEquals( 0, d1000.getDays() );
        assertEquals( 1, d1000.getSeconds() );
        assertEquals( 0, d1000.getMinutes() );
        assertEquals( 0, d1000.getMilliseconds() );
    }
    @Test
    public void testDurationDay()
    {
        var d1000 = new Duration( Duration.MS_DAY );

        assertEquals( Duration.MS_DAY, d1000.getDurationMs() );
        assertEquals( 1, d1000.getDays() );
        assertEquals( 0, d1000.getSeconds() );
        assertEquals( 0, d1000.getMinutes() );
        assertEquals( 0, d1000.getMilliseconds() );
    }
}
