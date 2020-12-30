package org.smack.util;

public class FunctionalUtil
{
    public interface ConsumerTX<T,X extends Exception>
    {
        void accept( T t )
            throws X;
    }

    public interface ConsumerX<T> extends ConsumerTX<T, Exception>
    {
        @Override
        void accept( T t )
                throws Exception;
    }
}
