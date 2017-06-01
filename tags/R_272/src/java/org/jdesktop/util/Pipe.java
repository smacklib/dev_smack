/* $Id$
 *
 * Common.
 *
 * Released under Gnu Public License
 * Copyright © 2012 Michael G. Binz
 */
package org.jdesktop.util;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A simple pipe.  Write to the write end, read from the read end.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public interface Pipe extends AutoCloseable
{
    /**
     * Get the Pipe's write end.
     *
     * @return The write end.
     */
    OutputStream getWriteEnd();

    /**
     * Get the pipes read end.
     *
     * @return The read end.
     */
    InputStream getReadEnd();

    /**
     * Close the pipe.
     */
    @Override
    void close();
}
