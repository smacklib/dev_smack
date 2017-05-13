/* $Id$
 *
 * Released under Gnu Public License
 * Copyright © 2011-17 Michael G. Binz
 */
package org.jdesktop.beans;

import javafx.beans.property.adapter.JavaBeanObjectProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder;

/**
 * Links a bound property on a source object to a property on
 * a target object.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class PropertyLink
{
    private static final WeakHolder _holder =
            new WeakHolder();
    private final ReadOnlyJavaBeanObjectProperty<?>
        _sourceProperty;
    private final JavaBeanObjectProperty<Object>
        _targetProperty;

    /**
     * Creates a property update link between the source and target.
     * The property is expected to exist on both objects.
     *
     * @param source The source object. Changes on this are propagated to the
     * target object.
     * @param propName The name of source and target property.
     * @param target The target object.
     * @deprecated Use static {@link #bind(Object, String, Object)}
     */
    @Deprecated
    public PropertyLink(
            Object source,
            String propName,
            Object target )
    {
        this( source, propName, target, propName );
    }

    /**
     * Creates a property update link between the source and target.
     *
     * @param source The source object. Changes on this are propagated to the
     * target object.
     * @param propSrcName The name of the source property.
     * @param target The target object.
     * @param propTgtName The name of the target property.
     * @deprecated Use static {@link #bind(Object, String, Object, String)}
     */
    @Deprecated
    public PropertyLink(
            Object source,
            String propSrcName,
            Object target,
            String propTgtName )
    {
        // Would be cool if we could match the types of source and target
        // but this is currently not part of the FX properties API.
        // Anyway, we fail in both cases at runtime.
        try
        {
            // TODO: Create the property in this special way to prevent
            // type warnings. I think the typing of JBOPB is plainly wrong.
            JavaBeanObjectPropertyBuilder<Object> tgtBld =
                    new JavaBeanObjectPropertyBuilder<>();
            tgtBld
                .name( propTgtName )
                .bean( target );
            _targetProperty =
                    tgtBld.build();
            _sourceProperty =
                    ReadOnlyJavaBeanObjectPropertyBuilder.create()
                        .name( propSrcName )
                        .bean( source )
                        .build();
            _holder.put(
                    source,
                    _targetProperty );
            _targetProperty.bind(
                    _sourceProperty );
        }
        catch ( NoSuchMethodException e )
        {
            throw new IllegalArgumentException( e );
        }
    }

    /**
     * Allows to manually trigger a property update.
     *
     * @return The PropertyLink for chained calls.
     */
    public PropertyLink update()
    {
        _sourceProperty.fireValueChangedEvent();
        return this;
    }

    /**
     * Remove the internal listener registrations.  This is only needed if the
     * linked beans have a different life cycle.
     */
    public void dispose()
    {
        _sourceProperty.dispose();
        _targetProperty.dispose();
    }

    public static PropertyLink bind(
            Object source,
            String propSrcName,
            Object target,
            String propTgtName )
    {
        return new PropertyLink( source, propSrcName, target, propTgtName );
    }
    public static PropertyLink bind(
            Object source,
            String propSrcName,
            Object target )
    {
        return new PropertyLink( source, propSrcName, target, propSrcName );
    }
}
