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
        TimeProbe heatup = new TimeProbe().start();
        Thread.sleep( 10 );
        heatup.stop();

        int TIME = 500;
        TimeProbe tp = new TimeProbe().start();
        Thread.sleep( TIME );
        tp.stop();
        assertTrue( tp.duration() >= TIME );
        // One percent error seems generous.
        assertTrue( tp.duration() < (TIME + (TIME/100)) );
    }
}
