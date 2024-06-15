/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2020-21 Michael G. Binz
 */
package org.smack.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Test file utilities.
 */
public class FormattedExTest
{
    @Test
    public void msg() throws Exception
    {
        try {
            throw new FormattedEx( "313" );
        }
        catch ( FormattedEx e )
        {
            assertEquals( "313", e.getMessage() );
            assertNull( e.getCause() );
        }
    }

    @Test
    public void msgFmt() throws Exception
    {
        try {
            throw new FormattedEx( "3%d3", 1 );
        }
        catch ( FormattedEx e )
        {
            assertEquals( "313", e.getMessage() );
            assertNull( e.getCause() );
        }
    }

    @Test
    public void msgCause() throws Exception
    {
        var cause = new Exception( "cause" );

        try {
            throw new FormattedEx( cause, "313" );
        }
        catch ( FormattedEx e )
        {
            assertEquals( "313", e.getMessage() );
            assertEquals( cause, e.getCause() );
        }
    }

    @Test
    public void msgFmtCause() throws Exception
    {
        var cause = new Exception( "cause" );

        try {
            throw new FormattedEx( cause, "3%d3", 1 );
        }
        catch ( FormattedEx e )
        {
            assertEquals( "313", e.getMessage() );
            assertEquals( cause, e.getCause() );
        }
    }
}
