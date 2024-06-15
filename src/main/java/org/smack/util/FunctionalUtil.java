/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2022-2024 Michael G. Binz
 */
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

    /**
     * Represents a function that accepts an arguments and produces a result.
     *
     * <p>This is a <a href="package-summary.html">functional interface</a>
     * whose functional method is {@link #apply(Object, Object)}.
     *
     * @param <T> the type of the first argument to the function
     * @param <R> the type of the result of the function
     */
    @FunctionalInterface
    public interface FunctionX<T, R, X extends Exception> {

        /**
         * Applies this function to the given arguments.
         *
         * @param t the function argument
         * @return the function result
         */
        R apply(T t)
            throws X;
    }

    @FunctionalInterface
    public interface BiFunctionX<T1, T2, R, X extends Exception> {

        /**
         * Applies this function to the given arguments.
         *
         * @param t the first function argument
         * @param t the second function argument
         * @return the function result
         */
        R apply(T1 t1, T2 t2)
            throws X;
    }

    @FunctionalInterface
    public interface SupplierX<T,X extends Exception>
    {
        T get() throws X;
    }
}
