package org.jdesktop.smack;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.logging.Logger;

/**
 * Allows to adjust the font size of a linked component using the mouse wheel.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public final class FontResizer
{
    private static Logger LOG = Logger.getLogger( FontResizer.class.getName() );

    private final int KEY_STROKE = KeyEvent.VK_CONTROL;

    private final static float minSize = 5;
    private final static float maxSize = 30;

    private final Component _component;

    /**
     * Create an instance.
     *
     * @param component The component receiving the font size updates.
     */
    public FontResizer( Component component )
    {
        _component = component;

        _component.addKeyListener( _keyListener );
    }

    private final KeyListener _keyListener = new KeyListener()
    {
        private boolean __addedFlag = false;

        @Override
        public void keyTyped( KeyEvent e )
        {
        }

        @Override
        public void keyReleased( KeyEvent e )
        {
            if ( e.getKeyCode() == KEY_STROKE && __addedFlag == true )
            {
                LOG.info( "Remove mwl." );
                _component.removeMouseWheelListener( _mouseWheelListener );
                __addedFlag = false;
            }
        }

        @Override
        public void keyPressed( KeyEvent e )
        {
            if ( e.getKeyCode() == KEY_STROKE && __addedFlag == false )
            {
                LOG.info( "Add mwl." );
                _component.addMouseWheelListener( _mouseWheelListener );
                __addedFlag = true;
            }
        }
    };

    /**
     * Decouple the FontResizer from its target component.
     */
    public void dispose()
    {
        _component.removeMouseWheelListener( _mouseWheelListener );
        _component.removeKeyListener( _keyListener );
    }

    /**
     * The listener processing mouse wheel events.
     */
    private final MouseWheelListener _mouseWheelListener = new MouseWheelListener()
    {
        @Override
        public void mouseWheelMoved( MouseWheelEvent e )
        {
            // If control is not pressed, we leave.
            if ( e.getModifiersEx() != InputEvent.CTRL_DOWN_MASK )
                return;

            if ( _component != e.getSource() )
            {
                LOG.warning( "Unexepected event source type: " + e.getSource() );
                return;
            }

            e.consume();

            float fontsize =
                    _component.getFont().getSize2D();

            int direction =
                    e.getWheelRotation();

            // Scrolling down and lower limit reached.
            if ( direction < 0 && fontsize <= minSize )
                return;
            // Scrolling up and upper limit is reached.
            else if ( direction > 0 && fontsize >= maxSize )
                return;

            float step = direction * fontsize * 0.1f;

            fontsize += step;

            // Keep size inside bounds.
            if ( fontsize >= maxSize )
                fontsize = maxSize;
            else if ( fontsize <= minSize )
                fontsize = minSize;

            fontsize = Math.round( fontsize );

            LOG.info( "fontsize=" + fontsize );

            _component.setFont(
                    _component.getFont().deriveFont( fontsize ) );
        }
    };
}
