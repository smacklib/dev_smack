package org.smack.util.resource;

import java.util.function.Function;

class DynamicResourceConverter<T> extends ResourceConverter
{
    private final Function<String, T> _function;

    DynamicResourceConverter( Class<T> cl, Function<String,T> f )
    {
        super( cl );

        _function = f;
    }

    @Override
    public Object parseString( String s, ResourceMap r ) throws Exception
    {
        return _function.apply( s );
    }
}