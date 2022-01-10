/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2020-21 Michael G. Binz
 */
package org.smack.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.smack.util.FileUtil;
import org.smack.util.ServiceManager;

public class ApplicationInfoTest
{
    @Before
    public void setup()
    {
        ServiceManager.clear();
    }

    @Test
    public void plain() throws IOException
    {
        var ai = new ApplicationInfo( TestApplication.class );

        assertEquals( "UsbView", ai.getTitle() );
        assertEquals( "UsbViewId", ai.getId() );
        assertEquals( "1", ai.getVersion() );
        assertNotNull( ai.getIcon() );
        assertEquals( 128, ai.getIcon().getHeight( null ) );
        assertEquals( "Michael Binz", ai.getVendor() );
        assertEquals( "micbinz", ai.getVendorId() );
    }

    @Test
    public void plainNoIcon() throws IOException
    {
        var ai = new ApplicationInfo( TestApplication2.class );

        assertEquals( "UsbView2", ai.getTitle() );
        assertEquals( "UsbViewId2", ai.getId() );
        assertEquals( "1", ai.getVersion() );
        assertNull( ai.getIcon() );
        assertEquals( "Michael Binz", ai.getVendor() );
        assertEquals( "micbinz", ai.getVendorId() );
    }

    @Test
    public void homeDir() throws IOException
    {
        var ai = new ApplicationInfo( TestApplication2.class );
        assertEquals( "UsbView2", ai.getTitle() );

        var homedir = ai.getHomeDir();
        assertNotNull( homedir );
        assertTrue( homedir.isDirectory() );
        assertTrue( homedir.canWrite() );

        assertTrue( FileUtil.delete( homedir ) );
    }

    /**
     * Instantiation via ServiceManager as normal application
     * service must fail.
     */
    @Test( expected = IllegalStateException.class )
    public void fromServiceManager()
    {
        ServiceManager.getApplicationService( ApplicationInfo.class );
    }
}
