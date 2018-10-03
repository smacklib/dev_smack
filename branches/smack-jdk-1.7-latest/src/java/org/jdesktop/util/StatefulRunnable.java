/* $Id$
 *
 * Released under Gnu Public License
 * Copyright © 2008 Michael G. Binz
 */
package org.jdesktop.util;

/**
 * A runnable that keeps a state object.
 *
 * @version $Rev$
 * @author Michael
 */
public abstract class StatefulRunnable<T> implements Runnable
{
    private final T _state;

    public StatefulRunnable( T state )
    {
        _state = state;
    }

    /**
     * Access the state.
     */
    protected T getState()
    {
        return _state;
    }

    /**
     * Entered if the Runnable is started.
     *
     * @param state The state set in the constructor.
     * @see Runnable#run()
     */
    protected abstract void run( T state );

    /**
     * Calls {@link #run(Object)}.
     */
    @Override
    public void run()
    {
        run( getState() );
    }
}
