/* $Id$
 *
 * Unpublished work.
 * Copyright © 2015 Michael G. Binz
 */
package org.jdesktop.application;

import java.awt.event.ActionEvent;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceManager;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.util.AppHelper;
import org.jdesktop.swingx.action.AbstractActionExt;

/**
 * An action taking a method reference as the action delegate.
 * (Smack candidate.)
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class Action8 extends AbstractActionExt
{
    public interface AmVoid
    {
        void p();
    }

    public interface AmAe
    {
        void p( ActionEvent e );
    }

    private final AmAe _amAe;
    private final AmVoid _amVoid;

    /**
     * Create an instance.
     *
     * @param action A reference to a method 'method( ActionEvent )'.
     */
    public Action8( AmAe action )
    {
        _amAe = action;
        _amVoid = null;
    }

    /**
     * Create an instance.
     *
     * @param action A reference to a method 'method( ActionEvent )'.
     */
    public Action8( AmVoid action )
    {
        _amAe = null;
        _amVoid = action;
    }

    @Override
    public void actionPerformed( ActionEvent e )
    {
        if ( _amVoid != null )
            _amVoid.p();
        else
            _amAe.p( e );
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
    public Action8 inject( Application app, Class<?> context, String key )
    {
        ResourceMap map = AppHelper.getResourceMap(
                app,
                context );

        ResourceManager rm = AppHelper.getResourceManager( app );

        key += ".Action";

        rm.injectProperties( context, key, map );

        return this;
    }

    /**
     * Generated for Action8.java.
     */
    private static final long serialVersionUID = -7056521329585878347L;
}
