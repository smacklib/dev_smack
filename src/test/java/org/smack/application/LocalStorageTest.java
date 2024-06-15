/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2020-21 Michael G. Binz
 */
package org.smack.application;

import java.io.IOException;

import org.junit.jupiter.api.Test;


public class LocalStorageTest
{
    @Test
    public void plain() throws IOException
    {
        var ls = new LocalStorage( "smacktest", getClass().getSimpleName() );
        System.out.println( ls.getDirectory() );
    }
}
