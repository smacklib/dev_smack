/* $Id: b90f6625a6174c3806f3084876f0ba99ce867b56 $
 *
 * Copyright © 2003-2015 Michael G. Binz
 */
package org.jdesktop.smack.util;

import java.util.Stack;

/**
 * Manages a number of shutdown procedures.  The difference to the raw usage of
 * <code>Runtime.addShutdownHook()</code> is that this happens in LIFO order.
 *
 * @see  java.lang.Runtime#addShutdownHook(Thread)
 * @version $Rev$
 */
public final class ShutdownManager
{
    // We deliberately do not offer a remove() method.

    /**
     * Add a shutdown procedure to be run on application termination.  The
     * added shutdown procedures will be invoked in reverse order of
     * addition.
     *
     * @param shutdownProcedure The shutdown procedure.
     */
    public static void add( Runnable shutdownProcedure )
    {
        if ( _theInstance == null )
            _theInstance = new ShutdownManager();

        _shutdownRunnables.push( shutdownProcedure );
    }

    /**
     * Creates an instance and registers the real shutdown listener.
     */
    private ShutdownManager()
    {
        Thread shutdown = new Thread(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        performShutdown();
                    }
                },
                getClass().getSimpleName() );

        Runtime.getRuntime().addShutdownHook( shutdown );
    }

    /**
     * Performs the actual shutdown.
     */
    private static void performShutdown()
    {
        while ( ! _shutdownRunnables.empty() )
            _shutdownRunnables.pop().run();
    }

    /**
     * The singleton instance of this class.
     */
    private static ShutdownManager _theInstance;

    /**
     * The internal container used to hold the shutdown runnables.
     */
    private static final Stack<Runnable> _shutdownRunnables =
            new Stack<Runnable>();
}
