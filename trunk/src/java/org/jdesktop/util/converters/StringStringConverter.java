/* $Id$
 *
 * Utilities
 *
 * Released under Gnu Public License
 * Copyright © 2017 Michael G. Binz
 */
package org.jdesktop.util.converters;

import org.jdesktop.util.ResourceConverter;
import org.jdesktop.util.ResourceMap;
import org.jdesktop.util.StringUtil;

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
    public Object parseString( String s, ResourceMap notUsed )
    {
        // TODO(michab) Goal here is to unquote the string.
        // Code below is the poor-man's-solution.
        // TODO a StringUtil.unquote is needed.
        String[] quotedParts = StringUtil.splitQuoted( s );

        return StringUtil.concatenate(
                " ",
                quotedParts );
    }
}
