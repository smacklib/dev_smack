/**
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2021 Michael G. Binz
 */
package org.smack.util.converters;

/**
 * A plug-in interface used to register type converters.
 *
 * @author Michael Binz
 */
public abstract class StringConverterExtension
{
    public abstract void extendTypeMap( StringConverter registry );
}
