/* $Id$
 *
 * Utilities
 *
 * Released under Gnu Public License
 * Copyright © 2017 Michael G. Binz
 */
package org.smack.util.converters;

import org.smack.util.resource.ResourceConverter;
import org.smack.util.resource.ResourceMap;

/**
 * The trivial converter.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class StringStringConverter extends ResourceConverter
{
    // Remark: Do not perform unquoting here.  This was once the
    // case and made the converter pretty inconvenient to use
    // in cases where quotes are actually needed. (Remember that
    // backslash in resource files is line continuation and
    // cannot be used for escape purposes.)

    // Think if this converter is needed at all or if better the
    // conversion engine should detect that no conversion is needed.
    public StringStringConverter() {
        super(String.class);
    }

    @Override
    public Object parseString( String s, ResourceMap notUsed )
    {
        return s;
    }
}
