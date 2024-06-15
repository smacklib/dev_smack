/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2020-21 Michael G. Binz
 */
package org.smack.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smack.util.FileUtil;
import org.smack.util.ServiceManager;

public class ApplicationInfoTest
{
    @BeforeEach
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

        var homedir = ai.getHome();
        assertNotNull( homedir );
        assertTrue( homedir.isDirectory() );
        assertTrue( homedir.canWrite() );

        var appDir = new File(
                FileUtil.getUserHome(),
                "." + ai.getId() );
        assertEquals( appDir, homedir );

        var dummyFile = new File( homedir, getClass().getSimpleName() );
        dummyFile.createNewFile();
        try ( FileWriter fw = new FileWriter( dummyFile ) )
        {
            fw.write( ai.getTitle() );
            fw.flush();
        }

        FileUtil.delete( homedir );
    }

    /**
     * Instantiation via ServiceManager as normal application
     * service must fail.
     */
    @Test
    public void fromServiceManager()
    {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    ServiceManager.getApplicationService( ApplicationInfo.class );
                } );
    }
}
