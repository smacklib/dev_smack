/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2022-2024 Michael G. Binz
 */
package org.smack.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.beans.BeanProperty;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;
import org.smack.application.CliApplication.Named;
import org.smack.util.resource.ResourceManager.Resource;

public class ReflectionUtilTest
{
    static class TestClass
    {
        private final int _value;

        public TestClass( int value )
        {
            _value = value;
        }

        public int getValue()
        {
            return _value;
        }
    }

    private static final int ZERO = 0;
    private static final int SEVEN = 7;

    @Test
    public void getConstructorTestPrimitive() throws Exception
    {
        var ctr = ReflectionUtil.getConstructor(
                TestClass.class,
                Integer.TYPE );

        assertNotNull( ctr );

        var result = ctr.newInstance(
                Integer.valueOf( SEVEN ) );
        assertEquals( SEVEN, result.getValue() );
    }

    @Test
    public void getConstructorTest() throws Exception
    {
        var ctr = ReflectionUtil.getConstructor(
                TestClass.class,
                Integer.class );

        assertNull( ctr );
    }

    @Test
    public void getConstructorTestFail() throws Exception
    {
        var ctr = ReflectionUtil.getConstructor(
                TestClass.class,
                Long.TYPE );

        assertNull( ctr );
    }

    @Test
    public void getMethodTest() throws Exception
    {
        var obj = ReflectionUtil.getMethod(
                TestClass.class,
                "getValue" );

        assertNotNull( obj );
    }

    @Test
    public void getMethodTestFail() throws Exception
    {
        var obj = ReflectionUtil.getMethod(
                TestClass.class,
                "_getValue" );

        assertNull( obj );
    }

    @Test
    public void makeArrayTest()
    {
        {
            String[] seven =
                    ReflectionUtil.makeArray( String.class, SEVEN );
            assertEquals( SEVEN, seven.length );

            for ( int i = 0 ; i < seven.length ; i++ )
                assertNull( seven[i] );
        }
        {
            String[] zero =
                    ReflectionUtil.makeArray( String.class, ZERO );
            assertEquals( ZERO, zero.length );
        }

        try
        {
            ReflectionUtil.makeArray( String.class, -1 );
            fail();
        }
        catch ( Exception expected )
        {
        }

        // Test not-allowed primitives.
        try
        {
            ReflectionUtil.makeArray( int.class, SEVEN );
            fail();
        }
        catch ( Exception expected )
        {
        }
    }

    org.smack.application.CliApplication c;

    @Named
    static class AnnotationClass
    {
        // Note that the deprecations below are part of the test!
        @Resource( dflt = "private_i" )
        @Deprecated
        private String private_i;

        @Resource( dflt = "public_i" )
        @Deprecated
        public String public_i;

        public void operation1()
        {
        }

        @BeanProperty(description = "operation2")
        public void operation2()
        {
        }

        @BeanProperty(description = "operation3")
        private void operation3()
        {
        }

        @BeanProperty(description = "operation4")
        static public void operation4()
        {
        }
    }

    @Test
    public void testBeanPropertyAll() throws Exception
    {
        final int[] count = {0};

        ReflectionUtil.processAnnotation(
                BeanProperty.class,
                AnnotationClass.class::getDeclaredMethods,
                (a, b) -> {
                    assertEquals( a.getName(), b.description() );
                    count[0]++;
                    });

        assertEquals( 3, count[0] );
    }

    @Test
    public void testBeanPropertyPublic() throws Exception
    {
        final int[] count = {0};

        ReflectionUtil.processAnnotation(
                BeanProperty.class,
                AnnotationClass.class::getDeclaredMethods,
                (t) -> {
                    return Modifier.isPublic( t.getModifiers() ); },
                (a, b) -> {
                    assertEquals( a.getName(), b.description() );
                    count[0]++;
                    });

        assertEquals( 2, count[0] );
    }

    @Test
    public void testField_Resource() throws Exception
    {
        final int[] count = {0};

        ReflectionUtil.processAnnotation(
                Resource.class,
                AnnotationClass.class::getDeclaredFields,
                (t) -> {
                    return Modifier.isPrivate( t.getModifiers() ); },
                (a, b) -> {
                    assertEquals( a.getName(), b.dflt() );
                    count[0]++;
                    });

        assertEquals( 1, count[0] );
    }

    @Test
    public void testField_Resource_all() throws Exception
    {
        final int[] count = {0};

        ReflectionUtil.processAnnotation(
                Resource.class,
                AnnotationClass.class::getDeclaredFields,
                (a, b) -> {
                    assertEquals( a.getName(), b.dflt() );
                    count[0]++;
                    });

        assertEquals( 2, count[0] );
    }

    @Test
    public void testGetInheritanceList() throws Exception
    {
        var il = ReflectionUtil.getInheritanceList( Short.class );
        assertEquals( 3, il.size() );
        assertEquals( Short.class, il.get(0) );
        assertEquals( Number.class, il.get(1) );
        assertEquals( Object.class, il.get(2) );
    }
}
