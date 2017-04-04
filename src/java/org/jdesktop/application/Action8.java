/* $Id$
 *
 * Unpublished work.
 * Copyright © 2015 Michael G. Binz
 */
package org.jdesktop.application;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.util.ServiceManager;

/**
* An action taking a method reference as the action delegate.
*
* @version $Rev$
* @author Michael Binz
*/
public class Action8 extends AbstractActionExt
{
    private final Consumer<ActionEvent> _consumer;

    /**
     * Create an instance.
     *
     * @param action A reference to a method 'void method( ActionEvent )'.
     */
    public Action8( Consumer<ActionEvent> action )
    {
        _consumer = action;
    }

    /**
     * Create an instance.
     *
     * @param action A reference to a method 'void method()'.
     */
    public Action8( Runnable action )
    {
        _consumer = (s) -> action.run();
    }

    @Override
    public void actionPerformed( ActionEvent e )
    {
        _consumer.accept( e );
    }

    /**
     * Injects the action properties of the action's class, i.e.
     * {@link AbstractActionExt}.
     *
     * @param app The application reference.
     * @param context The home class of the action that is used to access the
     * resources.
     * @param key A prefix used to identify the resource keys used for property
     * injection.
     * @return A reference to the action for call chaining.
     */
    public Action8 inject( Class<?> context, String key )
    {
        ResourceManager rm =
                ServiceManager.getApplicationService( ResourceManager.class );
        ResourceMap map =
                rm.getResourceMap( context );
        rm.injectProperties(
                this,
                key + ".Action",
                map );
        return this;
    }

    /**
     * Generated for Action8.java.
     */
    private static final long serialVersionUID = -7056521329585878347L;
}
