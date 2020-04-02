/* $Id$
 *
 * Unpublished work.
 * Copyright © 2015-20 Michael G. Binz
 */
package org.smack.fx;

import java.util.function.Consumer;
import java.util.logging.Logger;

import org.jdesktop.util.ResourceManager;
import org.jdesktop.util.ResourceMap;
import org.jdesktop.util.ServiceManager;
import org.smack.util.StringUtil;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;

/**
* An action taking a method reference as the action delegate.
*
* @version $Rev$
* @author Michael Binz
*/
public class ActionFx
{
    private static final Logger LOG =
            Logger.getLogger( ActionFx.class.getName() );

    private final Consumer<ActionEvent> _consumer;

    public final SimpleBooleanProperty enabledProperty =
            new SimpleBooleanProperty( this, "enabled", true );

    /**
     * Create an instance.
     *
     * @param action The ActionEvent consumer to call.
     */
    public ActionFx( Consumer<ActionEvent> action )
    {
        _consumer =
                action;
    }

    /**
     * Create an instance.
     *
     * @param action A reference to a method 'void method()'.
     */
    public ActionFx( Runnable action )
    {
        this( (s)->action.run() );
    }

    public void actionPerformed( ActionEvent e )
    {
        if ( enabledProperty.get() )
            _consumer.accept( e );
        else
            LOG.warning( "ActionFx disabled. Ignore." );
    }

    /**
     * Injects the action properties of the action's class.
     *
     * @param context The home class of the action that is used to access the
     * resources.
     * @param key A prefix used to identify the resource keys used for property
     * injection.
     * @return A reference to the action for call chaining.
     */
    public ActionFx inject( Class<?> context, String key )
    {
        final ResourceManager rm =
                ServiceManager.getApplicationService( ResourceManager.class );
        final ResourceMap map =
                rm.getResourceMap( context );
        rm.injectProperties(
                this,
                key + ".Action",
                map );
        return this;
    }

    /**
     * Injects this action's JavaBean properties. The property values are
     * resolved in the ResourceMap of the passed context class.
     *
     * @param context The home class of the action that is used to access the
     * resources.
     * @param key A prefix used to identify the resource keys used for property
     * injection.
     * @return A reference to the action for call chaining.
     */
    public ActionFx init( Class<?> context, String key )
    {
        final org.jdesktop.util.ResourceManager rm =
                ServiceManager.getApplicationService( org.jdesktop.util.ResourceManager.class );
        final org.jdesktop.util.ResourceMap map =
                rm.getResourceMap( context );

        final String resourceKey = String.format(
                "%s.%s.Action",
                context.getSimpleName(),
                key );

        rm.injectProperties(
                this,
                resourceKey,
                map );
        return this;
    }

    private String _text;

    /**
     * @return The text associated with the action.  This may be placed in
     * a button. If no text is set returns the empty string, never null.
     */
    public String getText()
    {
        if ( _text == null )
            return StringUtil.EMPTY_STRING;

        return _text;
    }

    public void setText( String text )
    {
        this._text = text;
    }

    private Image _image;

    public Image getImage()
    {
        return _image;
    }

    public void setImage( Image image )
    {
        _image = image;
    }

    private KeyCombination _accelerator;

    public void setAccelerator( KeyCombination accelerator )
    {
        _accelerator = accelerator;
    }

    public KeyCombination getAccelerator()
    {
        return _accelerator;
    }
}
