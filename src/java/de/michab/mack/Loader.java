/* $Id$
 *
 * Michael's Application Construction Kit (MACK)
 *
 * Released under Gnu Public License
 * Copyright © 2008 Michael G. Binz
 */
package de.michab.mack;



/**
 *
 * @param <T>
 * @version $Rev$
 * @author Michael Binz
 */
public interface Loader<T>
{
    void load( T item );
}
