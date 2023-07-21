package org.smack.util;

import static org.junit.Assert.assertEquals;
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

        var duration = tp.duration();

        assertTrue(
                String.format( "duration %d less than TIME %d", duration, TIME ),
                duration >= TIME );
        // One percent error seems generous.

        int MAX_DURATION = (TIME + (TIME/100));

        System.out.println( MAX_DURATION - tp.duration() );
        assertTrue( tp.duration() <= MAX_DURATION );
    }

    @Test
    public void repeatedStop() throws Exception
    {
        var tp = new TimeProbe().start();
        Thread.sleep( 100 );
        tp.stop();
        var t1 = tp.duration();
        Thread.sleep( 100 );
        tp.stop();
        var t2 = tp.duration();

        assertEquals( t1, t2 );
    }
}
