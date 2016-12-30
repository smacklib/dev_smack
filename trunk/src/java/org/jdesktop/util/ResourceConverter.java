
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */
package org.jdesktop.util;

/**
 * A base class for converting arbitrary types to and from Strings, as well as
 * a registry of ResourceConverter implementations.
 * <p>
 * The <tt>supportsType</tt> method defines what types a ResourceConverter supports.
 * By default it returns true for classes that are equal to the constructor's
 * <tt>type</tt> argument.  The <tt>parseType</tt> methods converts a string
 * the ResourceConverter's supported type, and the <tt>toString</tt> does the
 * inverse, it converts a supported type to a String.  Concrete ResourceConverter
 * subclasses must override <tt>parseType()</tt> and, in most cases, the
 * <tt>toString</tt> method as well.
 * <p>
 * This class maintains a registry of ResourceConverters.
 * The <tt>forType</tt> method returns the first ResourceConverter that
 * supports a particular type, new ResourceConverters can be added with
 * <tt>register()</tt>.  A small set of generic ResourceConverters are
 * registered by default.  They support the following types:
 * <ul>
 * <li><tt>Boolean</tt></li>
 * <li><tt>Integer</tt></li>
 * <li><tt>Float</tt></li>
 * <li><tt>Double</tt></li>
 * <li><tt>Long</tt></li>
 * <li><tt>Short</tt></li>
 * <li><tt>Byte</tt></li>
 * <li><tt>MessageFormat</tt></li>
 * <li><tt>URL</tt></li>
 * <li><tt>URI</tt></li>
 * </ul>
 * <p>
 * The Boolean ResourceConverter returns true for "true", "on", "yes",
 * false otherwise.  The other primitive type ResourceConverters rely on
 * the corresponding static parse<i>Type</i> method,
 * e.g. <tt>Integer.parseInt()</tt>.  The MessageFormat
 * ResourceConverter just creates MessageFormat object with the string
 * as its constructor argument.  The URL/URI converters just apply
 * the corresponding constructor to the resource string.
 *
 * @author Hans Muller (Hans.Muller@Sun.COM)
 * @see ResourceMap
 */
public abstract class ResourceConverter
{
    protected final Class<?> type;

    /**
     * Convert string to object
     * @param s the string to be parsed
     * @param r the {@code ResourceMap}
     * @return the object which was created from the string
     * @throws org.jdesktop.application.ResourceConverter.ResourceConverterException
     */
    public abstract Object parseString(String s, ResourceMap r)
            throws Exception;

    /**
     *
     * @param type
     */
    protected ResourceConverter(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("null type");
        }
        this.type = type;
    }

    protected Class<?> getType() {
        return type;
    }

    /**
     * Checks whether {@code testType} can be converted with this converter.
     * @param testType The type to test.
     * @return {@code true} if {@code testType} can be converted with this converter.
     * @deprecated Not longer used.
     */
    @Deprecated
    public boolean supportsType(Class<?> testType) {
        return type.equals(testType);
    }
}
