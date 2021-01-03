package org.smack.util.converters;

import org.jdesktop.util.ResourceConverter;
import org.jdesktop.util.ResourceMap;

/**
 * A ResourceConverter that takes the passed string as a classname.
 * Checks whether the class can be loaded and is assignable to
 * the target type.  If this is the case creates and returns an instance
 * of the configured class using the default constructor.
 */
public class ClassStringConverter extends ResourceConverter
{
    public ClassStringConverter( Class<?> type )
    {
        super( type );
    }

    @Override
    public Object parseString( String s, ResourceMap r ) throws Exception
    {
        Class<?> resultClass = Class.forName( s );

        if ( ! getType().isAssignableFrom( resultClass ) )
            throw new ClassCastException( getType().getName() + " is not assignable from " + resultClass.getName() );

        return resultClass.getDeclaredConstructor().newInstance();
    }
}

