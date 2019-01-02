
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */
package org.jdesktop.util;

import java.util.Objects;

/**
 * A base class for converting arbitrary types to and from Strings.
 *
 * @author Hans Muller (Hans.Muller@Sun.COM)
 * @see ResourceMap
 */
public abstract class ResourceConverter
{
    private final Class<?> _type;

    /**
     * Convert string to object
     * @param s the string to be parsed
     * @param r the {@code ResourceMap}
     * @return the object which was created from the string
     * @throws Exception If conversion fails.
     */
    public abstract Object parseString(String s, ResourceMap r)
            throws Exception;

    /**
     * @param type Create a converter for the passed type.
     */
    protected ResourceConverter(Class<?> type) {
        this._type = Objects.requireNonNull( type );
    }

    /**
     * @return The type this converter can handle.
     */
    public Class<?> getType() {
        return _type;
    }
}
