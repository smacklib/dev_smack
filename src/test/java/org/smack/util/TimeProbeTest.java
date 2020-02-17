package org.smack.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TimeProbeTest
{
    @Test
    public void simple()
    {
        TimeProbe tp = new TimeProbe();
        assertFalse( tp.isRunning() );

    }
    @Test
    public void simple2() throws Exception
    {
        TimeProbe tp = new TimeProbe().start();
        Thread.sleep( 1000 );
        tp.stop();
        assertTrue( tp.duration() >= 1000 );
        assertTrue( tp.duration() < 1004 );

    }
}
