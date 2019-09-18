/* $Id$
 *
 * Common.
 *
 * Released under Gnu Public License
 * Copyright © 2011 Michael G. Binz
 */
package org.jdesktop.smack;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.text.JTextComponent;



/**
 * A focus listener that can be added to a text component to select
 * the text content if the component receives the focus.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class TextSelectResponder implements FocusListener
{
    /**
     * The single instance.
     */
    public static final TextSelectResponder INSTANCE = new TextSelectResponder();



    /**
     * Create an instance.
     */
    private TextSelectResponder()
    {
    }



    /**
     * Perform selectAll() if the source of the event is a JTextComponent.
     */
    @Override
    public void focusGained( FocusEvent e )
    {
        Object source = e.getSource();

        if ( source instanceof JTextComponent )
        {
            ((JTextComponent)source).selectAll();
        }
    }



    /**
     * Ignored
     */
    @Override
    public void focusLost( FocusEvent e )
    {
        // Ignored.
    }
}
