/* $Id$
 *
 * Mack.
 *
 * Released under Gnu Public License
 * Copyright © 2010 Michael G. Binz
 */

package de.s.mack;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.bsaf192.application.Application;
import org.bsaf192.application.ResourceMap;
import org.jdesktop.swingx.action.ActionContainerFactory;

/**
 * An extended toolbar that offers a popup menu allowing to select whether
 * the buttons show only an icon or an icon and text.
 *
 * @version $Rev$
 * @author Michael Binz
 */
@SuppressWarnings("serial")
public class MackToolbar extends JToolBar
{
    private final JCheckBoxMenuItem _showLabels =
        new JCheckBoxMenuItem();



    /**
     * Flags whether text is shown or not.  Note that this
     * initially needs to be set to false since otherwise
     * a swingx behavior -- creating buttons with text set
     * to empty -- manifests itself in a toolbar that
     * should show text, but does not do so.
     */
    private boolean _showText = false;



    /**
     * Used to create the buttons.  May be null.
     */
    private ActionContainerFactory _acf = null;



    /**
     * The default constructor.
     */
    public MackToolbar( ActionContainerFactory acf )
    {
        _acf = acf;
        init();
    }



    /**
     * Create a named toolbar.
     *
     * @param componentName The toolbar's component name.
     */
    public MackToolbar( String name )
    {
        super( name );
        init();
    }



    /**
     * Should be used in the future.
     *
     * @param acf
     * @param name
     */
    public MackToolbar( ActionContainerFactory acf, String name )
    {
        super( name );
        _acf = acf;
        init();
    }



    /**
     * Internal initialization after ctor call.
     */
    private void init()
    {
        // Resolve the resource annotations.
        _showLabels.setName( "showLabelsCheckbox" );
        ResourceMap rm =
            Application.getResourceManager().getResourceMap( this.getClass() );
        rm.injectComponent( _showLabels );

        // Init the state.
        _showLabels.setSelected( _showText );

        // Wire the menu item.
        _showLabels.addItemListener( new ItemListener() {
            @Override
            public void itemStateChanged( ItemEvent e )
            {
                assert e.getSource() == _showLabels;

                setShowButtonText(
                        e.getStateChange() == ItemEvent.SELECTED );
            }
        } );

        // Create and register the popup menu.
        JPopupMenu pp = new JPopupMenu();
        pp.add( _showLabels );
        this.setComponentPopupMenu( pp );
    }



    /**
     * Allows to switch on or off toolbar button label display.
     *
     * @param doShow {@code true} switches label display on.
     */
    public void setShowButtonText( boolean doShow )
    {
        if ( _showText == doShow )
            return;

        _showText = doShow;
        _showLabels.setSelected( _showText );

        for ( AbstractButton c : getButtons() )
        {
            c.setHideActionText( hideText( c ) );
        }

        firePropertyChange( "showButtonText", !_showText, _showText );
    }



    /**
     * Whether the toolbar button labels are shown.
     *
     * @return {@code true} if the button labels are shown.
     */
    public boolean getShowButtonText()
    {
        return _showText;
    }



    /**
     * Get the buttons on this toolbar.
     *
     * @return A list of buttons.
     */
    private Set<AbstractButton> getButtons()
    {
        Set<AbstractButton> result = new HashSet<AbstractButton>();

        for ( int i = getComponentCount(); i >= 0; i-- )
        {
            Component c = getComponentAtIndex( i );
            if (c instanceof AbstractButton)
                result.add( (AbstractButton)c );
        }

        return result;
    }



    /**
     *  Note that this returns null if a component different from a
     *  jbutton was added.
     */
    @Override
    public JButton add( Action a )
    {
        JComponent toAdd = null;

        // Check if this is a MackAction.
        MackAction ma = MackAction.toMackAction( a );

        // Check if this is a MackAction with a special component.
        if ( ma != null && ma.getToolbarComponent() != null )
        {
            toAdd = ma.getToolbarComponent();
        }
        // Check if we've a component factory.
        else if ( _acf != null )
        {
            toAdd = _acf.createButton( a );
        }
        // Create the component using the toolbar.
        else
        {
            JButton button = createActionComponent( a );
            // This is required for vanilla non-mack actions.
            button.setAction( a );
            toAdd = button;
        }

        assert toAdd != null;

        // Add the component.
        add( toAdd );

        // If we created a JButton, return it...
        if ( toAdd instanceof JButton )
            return (JButton)toAdd;
        // ...if not return null.
        return null;
    }



    /**
     * Make all added JButtons to follow the _showText property.  This covers
     * buttons from {@link #add(Action)} as well as from
     * {@link #add(Component)}.
     */
    @Override
    protected void addImpl( Component comp, Object constraints, int index )
    {
        super.addImpl( comp, constraints, index );

        if ( comp instanceof AbstractButton )
        {
            adjustButton( (AbstractButton)comp );
        }
    }



    /**
     * This is responsible to set the required common attributes
     * of a button on the toolbar that needs to be respected by every
     * button.
     *
     * @param button The button to adjust.
     */
    private void adjustButton( AbstractButton button )
    {
        button.setInheritsPopupMenu( true );
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalTextPosition(JButton.BOTTOM);
        button.setHideActionText( hideText( button ) );
    }



    /**
     * Compute the value of the component's hideText property.
     * Takes into account that for example text cannot be hidden
     * if there's no icon.
     *
     * @param ab The button to adjust.
     * @return The value of the hide text property.
     */
    private boolean hideText( AbstractButton ab )
    {
        // Hide text only if there's an icon.
        return
            _showText == false &&
            ab.getIcon() != null;
    }
}
