/* $Id: 16a5448c0c3f5eeca48c84dcd5004e3a0977003a $
 *
 * Utilities
 *
 * Released under Gnu Public License
 * Copyright © 2017 Michael G. Binz
 */
package org.jdesktop.util.converters;

import org.jdesktop.util.ResourceConverter;
import org.jdesktop.util.ResourceMap;

/**
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class StringStringBuilderConverter extends ResourceConverter {

    public StringStringBuilderConverter() {
        super(StringBuilder.class);
    }

    @Override
    public Object parseString( String s, ResourceMap notUsed )
    {
        return new StringBuilder( s );
    }
}
