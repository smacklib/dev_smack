/* $Id$
 *
 * Application framework
 *
 * Unpublished work.
 * Copyright (c) 1999 Michael G. Binz
 */

package org.jdesktop.smack.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.View;
import org.jdesktop.swingx.JXToolbar;

/**
 * Class for testing MdiFrames.
 */
public class TestMdiFrame extends SingleFrameApplication
{
    /**
     * Test main. Creates an MdiFrame and adds a button for creating child
     * frames.
     */
    public static void main( String[] argv )
    {
        Application.launch( TestMdiFrame.class, argv );
    }

    @Override
    protected void startup()
    {
        View v = getMainView();
//        v.
//        final JFrame mdiFrame = new JFrame();
//        mdiFrame.setSize( 800, 400 );
//        mdiFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final MdiDesktopPane desktop = new MdiDesktopPane();
        final JToolBar toolBar = new JXToolbar("");

//        mdiFrame.getContentPane().add( toolBar, BorderLayout.SOUTH );
//        mdiFrame.add( desktop, BorderLayout.CENTER );

        // Create a new JInternalFrame
        JButton createButton = new JButton( "Create" );
        createButton.addActionListener( new ActionListener()
        {
            private int unique = 0;

            @Override
            public void actionPerformed( ActionEvent e )
            {
                System.err.println( "create performed." );
                JInternalFrame child = new JInternalFrame( "Test " + unique++,
                        true, true, true, true );
                child.setDefaultCloseOperation( JInternalFrame.DO_NOTHING_ON_CLOSE );
                desktop.registerChild( child );
                child.setVisible( true );
            }
        } );

        toolBar.add( createButton );

        // Change the title of the active internal frame
        JButton changeTitleButton = new JButton( "Change Title..." );
        changeTitleButton.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                JInternalFrame child = desktop.getSelectedFrame();
                if ( child == null )
                    return;

                String newTitle = (String)JOptionPane.showInputDialog(
                        getMainFrame(),
                        "Enter new title", // Dlg title
                        "Change frame title", JOptionPane.QUESTION_MESSAGE,
                        null, null, child.getTitle() );
                if ( newTitle != null && newTitle.length() > 0 )
                    child.setTitle( newTitle );
            }
        } );

        toolBar.add( changeTitleButton );

        v.setToolBar( toolBar );
        v.setComponent( desktop );

        show( v );
        //v.

        //mdiFrame.setVisible( true );
    }
}
