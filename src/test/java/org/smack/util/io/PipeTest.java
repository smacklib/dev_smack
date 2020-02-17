package org.smack.util.io;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class PipeTest
{
    private void singleChar( Pipe p ) throws IOException
    {
        for ( int toWrite = 0 ; toWrite < 100 ; toWrite++ )
        {
            var w = p.getWriteEnd();
            w.write( toWrite );
            var r = p.getReadEnd();
            assertEquals( toWrite, r.read() );
        }
    }

    @Test
    public void testPipeNio() throws IOException
    {
        singleChar( new NioPipe() );
    }
    @Test
    public void testPipePower() throws IOException
    {
        singleChar( new PowerPipe() );
    }
    @Test
    public void testPipeNormal() throws IOException
    {
        singleChar( new SimplePipe() );
    }
}
