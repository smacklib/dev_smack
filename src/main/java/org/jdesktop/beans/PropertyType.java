/* $Id$
 *
 * Laboratory.
 *
 * Released under Gnu Public License
 * Copyright © 2015 Michael G. Binz
 */
package org.jdesktop.beans;

/**
 * Interface for JavaBean property operations.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public interface PropertyType<T,B>
{
    /**
     * Get the property's type.
     *
     * @return The property's type.
     */
    Class<T> getType();

    /**
     * Get the property's value.
     *
     * @return The property's value.
     */
    T get();

    /**
     * Set the property's value.
     *
     * @param value The value to set.
     */
    void set( T value );

    /**
     * Get the property name.
     *
     * @return The property name.
     */
    String getName();

    /**
     * Get the property's home Bean instance.
     *
     * @return The property's home Bean instance.
     */
    B getBean();
}
