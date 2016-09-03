package org.jdesktop.util;

/**
 *
 * @author Michael
 *
 * @param <T>
 */
public abstract class StatefulRunnable<T> implements Runnable
{
    private final T _state;

    public StatefulRunnable( T state )
    {
        _state = state;
    }

    protected T getState()
    {
        return _state;
    }

    abstract void run( T state );

    @Override
    public void run()
    {
        run( getState() );
    }
}
