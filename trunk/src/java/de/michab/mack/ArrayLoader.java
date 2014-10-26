/* $Id: ArrayLoader.java 280 2010-04-18 08:57:17Z Michael $
 *
 * Michael's Application Construction Kit (MACK)
 *
 * Released under Gnu Public License
 * Copyright (c) 2008 Michael G. Binz
 */
package de.michab.mack;



/**
 *
 * @param <T>
 * @version $Rev: 280 $
 * @author Michael Binz
 */
public interface ArrayLoader<T>
{
    void load( T[] item );
}
