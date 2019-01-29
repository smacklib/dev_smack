/* $Id$
 *
 * Released under Gnu Public License
 * Copyright © 2011-17 Michael G. Binz
 */
package org.jdesktop.beans;

import java.util.HashSet;
import java.util.WeakHashMap;

import org.jdesktop.application.ApplicationProperties;
import org.jdesktop.util.OneToN;
import org.jdesktop.util.ServiceManager;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.adapter.JavaBeanObjectProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder;
import javafx.util.StringConverter;

/**
 * Links a bound property on a source object to a property on
 * a target object.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class PropertyLink
{
    private static final OneToN<Object, Object, HashSet<Object>> _holder =
            new OneToN<>( WeakHashMap::new, HashSet::new );
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
            _holder.putValue(
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

    /**
     * Persist the passed property.
     *
     * @param p The property to persist.
     * @param c A converter.
     * @return The passed property with additional persistence bindings.
     */
    public static <T,P extends ObjectProperty<T> >
        P persist(
                P p,
                StringConverter<T> c )
    {
        ApplicationProperties a = ServiceManager.getApplicationService(
                ApplicationProperties.class );

        // Read the initial value from persistence.
        T initialValue = c.fromString(
                a.get( p.getBean().getClass(), p.getName(), null ) );

        // If an initial value was set in persistence ...
        if ( initialValue != null )
        {
            // ... we set it on  the property.
            p.set( initialValue );
        }
        // If we found nothing in persistence, but the property
        // has a value ...
        else if ( p.get() != null )
        {
            // ... we update persistence.
            a.put(
                    p.getBean().getClass(),
                    p.getName(),
                    c.toString( p.get() ) );
        }

        p.addListener( (observable,o,n) ->
        {
            // Record all property changes in persistence.
            a.put(
                    p.getBean().getClass(),
                    p.getName(),
                    c.toString( n ) );
        } );

        return p;
    }

    /**
     * Persist the passed property.
     *
     * @param property The property to persist.
     * @param converter A converter.
     * @param key A key used to lookup the property value in the persistence
     * layer. A good value is the name of the property attribute.
     * @return The passed property with additional persistence bindings.
     */
    public static <T,P extends Property<T> >
        P persist(
                P property,
                StringConverter<T> converter,
                String key )
    {
        ApplicationProperties a = ServiceManager.getApplicationService(
                ApplicationProperties.class );

        // Read the initial value from persistence.
        T initialValue = converter.fromString(
                a.get( property.getBean().getClass(), key, null ) );

        // If an initial value was set in persistence ...
        if ( initialValue != null )
        {
            // ... we set it on  the property.
            property.setValue( initialValue );
        }
        // If we found nothing in persistence, but the property
        // has a value ...
        else if ( property.getValue() != null )
        {
            // ... we update persistence.
            a.put(
                    property.getBean().getClass(),
                    key,
                    converter.toString( property.getValue() ) );
        }

        property.addListener( (observable,o,n) ->
        {
            // Record all property changes in persistence.
            a.put(
                    property.getBean().getClass(),
                    key,
                    converter.toString( n ) );
        } );

        return property;
    }

    /**
     * The trivial string converter.
     */
    public static final StringConverter<String> STRING_STRING_CONVERTER =
            new StringConverter<>()
    {

        @Override
        public String toString( String object )
        {
            return object;
        }

        @Override
        public String fromString( String string )
        {
            return string;
        }
    };
}
