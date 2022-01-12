package org.smack;

import org.smack.util.JavaUtil;

public class DisposeHook implements AutoCloseable
{
    public interface CallableX
    {
        void call()
            throws Exception;
    }

    private final CallableX _closeOperation;

    public DisposeHook( CallableX callable )
    {
        _closeOperation = callable;
    }

    @Override
    public void close()
    {
        JavaUtil.force( _closeOperation::call );
    }
}
