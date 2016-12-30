package org.jdesktop.util.converters;

import org.jdesktop.smack.util.StringUtils;
import org.jdesktop.util.ResourceConverter;
import org.jdesktop.util.ResourceMap;

/**
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class StringStringConverter extends ResourceConverter {

    public StringStringConverter() {
        super(String.class);
    }

    @Override
    public Object parseString( String s, ResourceMap r )
    {
        String[] quotedParts = StringUtils.splitQuoted( s );

        return StringUtils.concatenate(
                StringUtils.EMPTY_STRING,
                quotedParts );
    }
}
