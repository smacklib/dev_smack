package org.jdesktop.util.converters;

import java.lang.reflect.Array;

import org.jdesktop.util.ResourceConverter;
import org.jdesktop.util.ResourceMap;
import org.smack.util.StringUtil;

/**
 * Converts string arrays, handles quoting.
 *
 * @version $Revision$
 * @author Michael Binz
 */
public final class StringArrayRc extends ResourceConverter
{
    public StringArrayRc()
    {
        super( String[].class );

        if ( ! getType().isArray() )
            throw new IllegalArgumentException();
    }

    @Override
    public Object parseString( String s, ResourceMap r )
            throws Exception
    {
        String[] split = StringUtil.splitQuoted( s );

        Object result = Array.newInstance(
                getType().getComponentType(), split.length );

        int idx = 0;
        for ( String c : split )
        {
            Array.set(
                    result,
                    idx++,
                    c );
        }

        return result;
    }
}
