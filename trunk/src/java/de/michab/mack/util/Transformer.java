/* $Id$
 *
 * Michael's Application Construction Kit (MACK)
 *
 * Released under Gnu Public License
 * Copyright © 2008 Michael G. Binz
 */
package de.michab.mack.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * A class capable of transforming types.
 *
 * @param <T> The transformation target type.
 * @param <F> The transformation source type.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class Transformer<T, F>
{
    /*
     * The logger for this class.
     */
    private static final Logger _log = Logger.getLogger( Transformer.class
            .getName() );

    /**
     * The target type constructor;
     */
    private final Constructor<T> _ctor;



    /**
     * Creates a transformer for the passed types.
     *
     * @param targetClass
     * @param from
     * @throws IllegalArgumentException
     */
    public Transformer(Class<T> targetClass, Class<F> from)
    {
        if ( from.equals( targetClass ) )
        {
          _ctor = null;
          return;
        }

        try
        {
            _ctor = targetClass.getConstructor( from );
        }
        catch ( NoSuchMethodException e )
        {
            throw new IllegalArgumentException( e );
        }
    }



    /**
     * Performs a transformation.
     *
     * @param f The object to transform.
     * @return The transformation result.
     */
    public T transform( F f )
    {
        try
        {
            return transformX( f );
        }
        catch ( Exception e )
        {
            unexpectedException( e, f );
            return null;
        }
    }



    /**
     * Performs a transformation.
     *
     * @param f The object to transform.
     * @return The transformation result.
     * @throws Exception if the transformation failed.
     */
    @SuppressWarnings("unchecked")
    public T transformX( F f )
        throws Exception
    {
        if ( _ctor == null )
          return (T)f;

        try
        {
            return _ctor.newInstance( f );
        }
        catch ( InvocationTargetException e )
        {
            Throwable cause = e.getCause();

            if ( cause instanceof Exception )
                throw (Exception)cause;

            throw e;
        }
    }



    /**
     * Transform an array.
     *
     * @param fa The array to transform.
     * @return The transformed array.
     */
    public T[] transform( F[] fa )
    {
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(
            _ctor.getDeclaringClass(),
            fa.length );

        for ( int i = 0 ; i < result.length ; i++ )
        {
            result[i] = transform( fa[i] );
        }

        return result;
    }



    /**
     *
     * @param e
     * @param o
     */
    private static void unexpectedException( Throwable e, Object o )
    {
        _log.log( Level.SEVERE, e.getLocalizedMessage() + ":" + o, e );
    }



    /**
     * Checks whether automatic conversion from fromClass to targetClass is
     * possible.
     *
     * @param fromClass The class to convert from.
     * @param targetClass The class to convert to.
     * @return True if a conversion seems possible.
     */
    public static boolean canConvert(
        Class<?> fromClass,
        Class<?> targetClass )
    {
        try
        {
            targetClass.getConstructor( fromClass );
            return true;
        }
        catch ( NoSuchMethodException e )
        {
            // This does not need to be handled.  If the ctor
            // does not exist, this means 'not convertible'.
        }

        return false;
    }



    /**
     * Transforms the passed value to the passed target class.
     * Expects that a constructor for the target class is available accepting
     * a parameter of the value's type.
     *
     * @param value The value to transform.
     * @param targetClass The class to create an instance of.
     * @return The transformed value.
     * @throws Exception An unspecified exception, if the transformation
     *         was not possible.
     */
    @SuppressWarnings("unchecked")
    public static <T> T transform( Object value, Class<T> targetClass )
       throws Exception
    {
        if ( value == null )
            return null;

        Class<?> sourceType = value.getClass();

        // Check whether the types are directly assignable.
        if ( targetClass.isAssignableFrom( sourceType ) )
            return (T)value;

        Constructor<T> ctor = targetClass.getConstructor(
            sourceType );

        return ctor.newInstance(
            value );
    }
}
