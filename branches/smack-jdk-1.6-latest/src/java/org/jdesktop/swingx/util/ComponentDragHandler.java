package org.jdesktop.swingx.util;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

/**
 * An event handler that allows to drag a Component using the mouse.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class ComponentDragHandler extends MouseAdapter
{
    /**
     * Valid if a drag is ongoing. Holds the position where
     * the mouse has grabbed the component. If no drag is
     * ongoing this is null.
     */
    private Point _componentDelta;

    /**
     * The component we are linked against. Used to implement
     * the dispose operation.
     */
    private final Component _component;

    /**
     * Creates an instance that is already properly linked against the
     * passed {@link Component}.
     *
     * @param component The component the new instance is linked to.
     */
    public ComponentDragHandler( Component component )
    {
        _component = component;
        _component.addMouseListener( this );
        _component.addMouseMotionListener( this );
    }

    /**
     * Removes the component links.
     */
    public void dispose()
    {
        _component.removeMouseListener( this );
        _component.removeMouseMotionListener( this );
    }

    @Override
    public void mousePressed( MouseEvent e )
    {
        _componentDelta = new Point(
                - e.getX(),
                - e.getY() );
    }

    @Override
    public void mouseReleased( MouseEvent e )
    {
        _componentDelta = null;
    }

    @Override
    public void mouseDragged( MouseEvent e )
    {
        if ( _componentDelta == null )
            return;

        Point p = e.getLocationOnScreen();

        SwingUtilities.convertPointFromScreen(
                p,
                e.getComponent().getParent() );

        p.translate(
                _componentDelta.x,
                _componentDelta.y );

        e.getComponent().setLocation( p );
    }
}
