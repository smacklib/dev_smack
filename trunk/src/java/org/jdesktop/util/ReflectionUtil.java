/* $Id$
 *
 * Common.
 *
 * Released under Gnu Public License
 * Copyright © 2010 Michael G. Binz
 */

package org.jdesktop.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.util.Pair;

/**
 * Reflection helpers.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public final class ReflectionUtil
{
    /**
     * The logger for this class.
     */
    private static final Logger LOG = Logger.getLogger(
        ReflectionUtil.class.getName() );

    /**
     * Forbid instantiation.
     */
    private ReflectionUtil()
    {
        throw new AssertionError();
    }

    /**
     * Get a named method from the passed class.
     *
     * @param pClass The class used to lookup the method.
     * @param name The name of the method.
     * @param parameterTypes The parameter types.
     * @return A reference to the method or {@code null} if the method was not
     * found.
     */
    public static Method getMethod(
            Class<?> pClass,
            String name,
            Class<?> ... parameterTypes )
    {
        try
        {
            return pClass.getMethod( name, parameterTypes );
        }
        catch ( Exception e )
        {
            LOG.log( Level.FINE, e.getClass().getSimpleName(), e );
            return null;
        }
    }

    /**
     * Get a constructor from the passed class.  Returns {@code null} if the
     * constructor is not found or not accessible.
     *
     * @param <T> The result type.
     * @param pClass The class used to lookup the method.
     * @param parameterTypes The parameter types.
     * @return A reference to the constructor or {@code null} if the constructor
     * was not found.
     */
    public static <T> Constructor<T> getConstructor(
            Class<T> pClass,
            Class<?> ... parameterTypes )
    {
        try
        {
            return pClass.getConstructor( parameterTypes );
        }
        catch ( Exception e )
        {
            LOG.log( Level.FINE, e.getClass().getSimpleName(), e );
            return null;
        }
    }

    /**
     * Find the first constructor matching the passed types.
     *
     * @param <T> The result type.
     * @param constructors The constructors to select from.
     * @param classes The types to match.
     * @return A constructor or null if no constructor matched.
     */
    public static <T> Constructor<T> matchConstructorTypes(
            Constructor<T>[] constructors,
            Class<?> ... classes  )
    {
        for ( Constructor<T> c : constructors )
        {
            if ( areTypesAssignable( c.getParameterTypes(), classes ) )
                return c;
        }

        return null;
    }

    /**
     * Find the first constructor matching the passed types.
     *
     * @param <T> The result type.
     * @param constructors The constructors to select from.
     * @param classes The types to match.
     * @return A constructor or null if no constructor matched.
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> matchConstructorArguments(
            Constructor<?>[] constructors,
            Object ... classes  )
    {
        Class<?>[] types = new Class<?>[ classes.length ];

        for ( int i = 0 ; i < types.length ; i++ )
        {
            types[i] = classes[i] == null ?
                    null :
                    classes[i].getClass();
        }

        for ( Constructor<?> c : constructors )
        {
            if ( areTypesAssignable( c.getParameterTypes(), types ) )
                return (Constructor<T>)c;
        }

        return null;
    }

    /**
     * Check if the passed type arrays are assignment-compatible.
     *
     * @param assignmentTargets Left side.
     * @param toAssign Right side. Null slots are always assignable.
     * @return true if assignment compatible.
     */
    private static boolean areTypesAssignable(
            Class<?>[] assignmentTargets,
            Class<?>[] toAssign )
    {
        if ( assignmentTargets.length != toAssign.length )
            return false;

        for ( int i = 0 ; i < assignmentTargets.length ; i++ )
        {
            // Primitives cannot accept a null parameter.
            if ( assignmentTargets[i].isPrimitive() && toAssign[i] == null )
                return false;

            // Null is matching always.
            boolean assignable =  toAssign == null ?
                    true :
                    assignmentTargets[i].isAssignableFrom( toAssign[i] );

            if ( ! assignable )
                return false;
        }

        return true;
    }

    /**
     * Normalizes class instances that represent primitive types
     * to their non-primitive counterparts.  If for example the class
     * {@code Integer.TYPE} is passed, then the class {@code Integer.class}
     * is returned.  For non-primitive classes the passed class is returned.
     *
     * @param cl The class reference to normalize.
     * @return The normalized class.
     */
    public static Class<?> normalizePrimitives( Class<?> cl )
    {
        if ( ! cl.isPrimitive() )
            return cl;

        if ( Boolean.TYPE == cl )
            return Boolean.class;
        else if ( Byte.TYPE == cl )
            return Byte.class;
        else if ( Short.TYPE == cl )
            return Short.class;
        else if ( Character.TYPE == cl )
            return Character.class;
        else if ( Integer.TYPE == cl )
            return Integer.class;
        else if ( Long.TYPE == cl )
            return Long.class;
        else if ( Float.TYPE == cl )
            return Float.class;
        else if ( Double.TYPE == cl )
            return Double.class;
        else if ( Void.TYPE == cl )
            return Void.class;

        throw new IllegalArgumentException( cl.getName() );
    }

    /**
     * Invoke the passed method.  Simplifies error handling to throwing
     * an untagged exception in every error case.  Note that this includes
     * exceptions that are internally thrown by the called operation.
     *
     * @param target The target object.
     * @param method The method to call.
     * @param parameters The method's parameters.
     * @return The method's result.
     * @throws IllegalArgumentException All exceptions are thrown as untagged
     * exceptions.
     */
    public static Object invokeQuiet(
           Method method,
           Object target,
           Object... parameters )
    {
        try
        {
            return method.invoke( target, parameters );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( e );
        }
    }

    /**
     * Invoke the passed method.  Simplifies error handling to throwing
     * an untagged exception in case of an reflection error. Exceptions
     * thrown by the called operation are re-thrown.
     *
     * @param target The target object.
     * @param method The method to call.
     * @param parameters The method's parameters.
     * @return The method's result.
     * @throws Exception If the called operation threw an exception.  This
     * is the unwrapped InvocationTargetException.
     */
    public static Object invoke(
            Method method,
            Object target,
            Object... parameters )
        throws Exception
    {
        try
        {
            return method.invoke( target, parameters );
        }
        catch ( InvocationTargetException e )
        {
            Throwable cause = e.getCause();

            try
            {
                throw (Exception)cause;
            }
            catch ( ClassCastException ee )
            {
                LOG.log( Level.WARNING, "ITE wrapped throwable.", cause );
                throw new RuntimeException( cause );
            }
        }
        catch ( IllegalArgumentException e )
        {
            throw e;
        }
        catch ( IllegalAccessException e )
        {
            throw new IllegalArgumentException( e );
        }
    }



    /**
     * Clone the passed array.  Allows to clone only parts of the
     * original array.
     *
     * @param <T> The array's component type.
     * @param original The array to clone.
     * @param startIdx The start index in the original array.
     * @param length The length of the target array.
     * @return A newly allocated array with the array elements initialised
     * to the content of the original array.
     */
    public static <T> T[] cloneArray( T[] original, int startIdx, int length )
    {
        if ( startIdx < 0 )
            throw new ArrayIndexOutOfBoundsException( "startIdx < 0" );
        if ( startIdx + length > original.length )
            throw new IllegalArgumentException(
                    "startIdx + length > original.length" );

        Class<?> componentType = original.getClass().getComponentType();

        @SuppressWarnings("unchecked")
        T[] result = (T[])Array.newInstance( componentType, length );

        System.arraycopy( original, startIdx, result, 0, length );

        return result;
    }



    /**
     * Clone the passed array.
     *
     * @param <T> The array's component type.
     * @param original The array to clone.
     * @return A newly allocated array with the array elements initialised
     * to the content of the original array.
     */
    public static <T> T[] cloneArray( T[] original )
    {
        return cloneArray( original, 0, original.length );
    }



    /**
     * Return the list of super classes for the passed class.
     *
     * @param c The start class.
     * @return The list of super classes. The first element in the
     * returned list is the passed class, the second its super class
     * and so on.
     */
    public static List<Class<?>> getInheritanceList( Class<?> c )
    {
        if ( c == null )
            throw new NullPointerException();

        List<Class<?>> result = new ArrayList<>();

        for (
            Class<?> current = c ;
            current != null ;
            current = current.getSuperclass() )
        {
            result.add( current );
        }

        return result;
    }

    /**
     * Get all annotated fields defined on a class.
     *
     * @param<T> An annotation type.
     * @param source The class to search.
     * @param annotation The annotation to look up.
     * @param modifiers The required modifiers.
     * @return Pairs of fields and field annotations.
     * The empty list if no annotation was found.
     */
    public static <T extends Annotation>
    List<Pair<Field,T>> getAnnotatedFields(
            Class<?> source,
            Class<T> annotation,
            int modifiers )
    {
        List<Pair<Field,T>> result = new ArrayList<>();

        for ( Field c : source.getDeclaredFields() )
        {
            // Check for exact match of requested modifiers.
            if ( modifiers != 0 && (c.getModifiers() & modifiers) != modifiers )
                continue;

            T a = c.getAnnotation( annotation );

            if ( a == null )
                continue;

            result.add( new Pair<>( c, a ) );
        }

        return result;
    }

    /**
     * Get all annotated fields defined on a class.
     *
     * @param<T> An annotation type.
     * @param source The class to search.
     * @param annotation The annotation to look up.
     * @return Pairs of fields and field annotations.
     * The empty list if no annotation was found.
     */
    public static <T extends Annotation>
    List<Pair<Field,T>> getAnnotatedFields(
            Class<?> source,
            Class<T> annotation )
    {
        return getAnnotatedFields( source, annotation, 0 );
    }

    /**
     * Get all annotated fields defined on a class.
     *
     * @param<T> An annotation type.
     * @param source The class to search.
     * @param annotation The annotation to look up.
     * @param modifiers The required modifiers.
     * @return Pairs of methods and method annotations.
     * The empty list if no annotation was found.
     */
    public static <T extends Annotation>
    List<Pair<Method,T>> getAnnotatedMethods(
            Class<?> source,
            Class<T> annotation,
            int modifiers )
    {
        List<Pair<Method,T>> result = new ArrayList<>();

        for ( Method c : source.getDeclaredMethods() )
        {
            // Check for exact match of requested modifiers.
            if ( modifiers != 0 && (c.getModifiers() & modifiers) != modifiers )
                continue;

            T a = c.getAnnotation( annotation );

            if ( a == null )
                continue;

            result.add( new Pair<>( c, a ) );
        }

        return result;
    }

    /**
     * Get all annotated fields defined on a class.
     *
     * @param<T> An annotation type.
     * @param source The class to search.
     * @param annotation The annotation to look up.
     * @return Pairs of methods and method annotations.
     * The empty list if no annotation was found.
     */
    public static <T extends Annotation>
    List<Pair<Method,T>> getAnnotatedMethods(
            Class<?> source,
            Class<T> annotation )
    {
        return getAnnotatedMethods( source, annotation, 0 );
    }

    /**
     * Create an instance of the passed class using the default
     * constructor.
     *
     * @param<T> The result type.
     * @param clazz The class to instantiate.
     * @return The instance or {@code null} if the constructor failed.
     */
    public static <T> T createInstance( Class<T> clazz )
    {
        try
        {
            return createInstanceX( clazz );
        }
        catch ( Exception e )
        {
            LOG.log( Level.INFO, e.getMessage(), e );
        }

        return null;
    }

    /**
     * Create an instance of the passed class using the default
     * constructor.
     *
     * @param <T> The result type.
     * @param clazz The class to instantiate.
     * @return The instance.
     * @throws Exception If the instance could not be created.
     */
    public static <T> T createInstanceX( Class<T> clazz )
            throws Exception
    {
        Constructor<T> ctor =
                clazz.getDeclaredConstructor();

        if (!ctor.isAccessible()) {
            try {
                ctor.setAccessible(true);
            } catch (SecurityException ignore) {
                // ctor.newInstance() will throw an IllegalAccessException
            }
        }

        return ctor.newInstance();
    }

    /**
     * Create an array.
     * @param c The new array's component type.  Note that a primitive type
     * is not allowed.
     * @param length The new array's length in range [0..INT_MAX].
     * @return The newly allocated array.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] makeArray( Class<T> c, int length )
    {
        Objects.requireNonNull(
                c );
        if ( c.isPrimitive() )
            throw new IllegalArgumentException(
                    "Primitive type is not allowed." );

        return (T[])Array.newInstance( c, length );
    }

    /**
     * @param e The exception to unwrap.
     * @return If the passed throwable is an {@link InvocationTargetException}
     * then throws the cause of this, otherwise returns the passed throwable
     * unmodified.
     */
    public static Throwable unwrap( Throwable e )
    {
        if ( e instanceof InvocationTargetException )
            return e.getCause();
        return e;
    }
}
