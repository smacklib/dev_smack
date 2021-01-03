package org.smack.util.converters;

import org.smack.util.resource.ResourceConverterExtension;
import org.smack.util.resource.ResourceConverterRegistry;

/**
 * Installs ResourceConverters for primitive types.
 *
 * @version $Revision$
 * @author Michael Binz
 */
public class PrimitivesBundle extends ResourceConverterExtension
{
    @Override
    public void extendTypeMap( ResourceConverterRegistry registry )
    {
        registry.put( boolean.class, new BooleanStringConverter() );
        registry.put( Boolean.class, new BooleanStringConverter() );
        registry.put( byte.class, new ByteStringConverter() );
        registry.put( Byte.class, new ByteStringConverter() );
        registry.put( short.class, new ShortStringConverter() );
        registry.put( Short.class, new ShortStringConverter() );
        registry.put( int.class, new IntegerStringConverter() );
        registry.put( Integer.class, new IntegerStringConverter() );
        registry.put( long.class, new LongStringConverter() );
        registry.put( Long.class, new LongStringConverter() );

        registry.put( float.class, new FloatStringConverter() );
        registry.put( Float.class, new FloatStringConverter() );
        registry.put( double.class, new DoubleStringConverter() );
        registry.put( Double.class, new DoubleStringConverter() );

        // char ...
    }
}
