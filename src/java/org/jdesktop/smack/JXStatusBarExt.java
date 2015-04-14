/* $Id$
 *
 * Mack.
 *
 * Released under Gnu Public License
 * Copyright © 2012 Michael G. Binz
 */
package org.jdesktop.smack;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.Timer;

import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.smack.util.StringUtils;




/**
 * Strategy:  Three areas:
 *  - Message : Displays messages, possibly temporary.
 *  (Optional: A persistent message can be shadowed by a temporary
 *  message. A history of messages is kept.)
 *  - Center area: Can be used for temporary display of components, normally
 *  ones that are alive displaying progress or short term messages to the user.
 *  - Application area: Right part of the bar: Used by the application to install
 *  additional components, like a button for a debug screen or a version number
 *  display. This area can handle Components as well as Actions. Adding to this
 *  area is by default from right to left. It is possible to add WEST or EAST
 *  explicitly.

 * @version $Rev$
 * @author Michael Binz
 */
public class JXStatusBarExt extends JPanel
{
    private final JToolBar _right = new JToolBar();

    private final Insets zeroInsets =
        new Insets( 0, 0, 0, 0 );

    private final JLabel messageLabel = new JLabel();
//    private final JProgressBar progressBar;
//    private final JLabel statusAnimationLabel;

    private Timer messageTimer;
//    private final Timer busyIconTimer;
    @Resource
    private Icon idleIcon;
    @Resource
    private final Icon[] busyIcons = new Icon[15];
    @Resource
    private int busyAnimationRate;
    private int busyIconIndex = 0;



    /**
     * Create an instance.
     */
    public JXStatusBarExt()
    {
        super( new BorderLayout() );

        // Display the Application version on startup.
        messageLabel.setText( Application.getInstance().getVersion() );
        add( messageLabel, BorderLayout.LINE_START );

        _right.setFloatable( false );
        add( _right, BorderLayout.LINE_END );
    }

//    /**
//     * Constructs a panel that displays messages/progress/state properties of
//     * the {@code taskMonitor's} foreground task.
//     *
//     * @param taskMonitor
//     *            the {@code TaskMonitor} whose {@code PropertyChangeEvents}
//     *            {@code this StatusBar} will track.
//     */
//    public JXStatusBarExt( TaskMonitor taskMonitor )
//    {
//        super( new GridBagLayout() );
//        setBorder( new EmptyBorder( 2, 0, 6, 0 ) ); // top, left, bottom, right
//        messageLabel = new JLabel();
//        progressBar = new JProgressBar( 0, 100 );
//        statusAnimationLabel = new JLabel();
//
//        Application.getResourceManager().injectResources( this );
//
//        busyIconTimer = new Timer( busyAnimationRate, new UpdateBusyIcon() );
//        progressBar.setEnabled( false );
//        statusAnimationLabel.setIcon( idleIcon );
//
//        GridBagConstraints c = new GridBagConstraints();
//        initGridBagConstraints( c );
//        c.gridwidth = GridBagConstraints.REMAINDER;
//        c.fill = GridBagConstraints.HORIZONTAL;
//        c.weightx = 1.0;
//        add( new JSeparator(), c );
//
//        initGridBagConstraints( c );
//        c.insets = new Insets( 6, 6, 0, 3 ); // top, left, bottom, right;
//        c.weightx = 1.0;
//        c.fill = GridBagConstraints.HORIZONTAL;
//        add( messageLabel, c );
//
//        initGridBagConstraints( c );
//        c.insets = new Insets( 6, 3, 0, 3 ); // top, left, bottom, right;
//        add( progressBar, c );
//
//        initGridBagConstraints( c );
//        c.insets = new Insets( 6, 3, 0, 6 ); // top, left, bottom, right;
//        add( statusAnimationLabel, c );
//
//        if ( taskMonitor != null )
//            taskMonitor.addPropertyChangeListener( this );
//    }



    /**
     * Display the passed message.
     *
     * @param message The message to display.
     */
    public void setMessage( String message )
    {
        if ( StringUtils.isEmpty( message ) )
            // Set a space to ensure we keep our height.
            message = " ";

        messageLabel.setText( message );
    }

    /**
     * Display the passed message for a certain duration.  After the duration
     * passed, the status bar message is cleared.
     *
     * @param message The message to display.
     * @param displayDuration The time in milliseconds the message is to be
     * displayed.
     */
    public void setMessage( String message, int displayDuration )
    {
        if ( message == null )
            throw new NullPointerException( "message == null" );
        if ( displayDuration <= 0 )
            throw new IllegalArgumentException( "displayDuration <= 0" );

        messageLabel.setText( message );

        if ( messageTimer == null )
        {
            messageTimer = new Timer( 0, new ClearOldMessage() );
            messageTimer.setRepeats( false );
        }

        messageTimer.setInitialDelay( displayDuration );
        messageTimer.restart();
    }

    /**
     * The component currently displayed in the center of the status bar.
     * If no component is displayed this is null.
     */
    private Component _centerComponent = null;

    /**
     * Add the a component in the center of the status bar.
     *
     * @param component The component to add. null removes the center component.
     */
    public void addCenter( Component component )
    {
        if ( component == null && _centerComponent != null )
            remove( _centerComponent );
        else
            add( component, BorderLayout.CENTER );

        _centerComponent = component;
    }

    public void addRight( Component component )
    {
        _right.add( component );

    }

    public void addRight( Component component, String linePosition )
    {
        if ( BorderLayout.LINE_END.equals( linePosition ) )
        {
            addRight( component );
            return;
        }
    }

    public void addRight( Action component )
    {

    }

    public void addRight( Action component, int linePosition )
    {

    }

    private class ClearOldMessage implements ActionListener
    {
        @Override
        public void actionPerformed( ActionEvent e )
        {
            messageLabel.setText( " " );
        }
    }

    public void showBusyAnimation()
    {
//        if ( !busyIconTimer.isRunning() )
//        {
//            statusAnimationLabel.setIcon( busyIcons[0] );
//            busyIconIndex = 0;
//            busyIconTimer.start();
//        }
    }


    public void stopBusyAnimation()
    {
//        busyIconTimer.stop();
//        statusAnimationLabel.setIcon( idleIcon );
    }

//    /**
//     * The TaskMonitor (constructor arg) tracks a "foreground" task; this method
//     * is called each time a foreground task property changes.
//     */
//    @Override
//    public void propertyChange( PropertyChangeEvent e )
//    {
//        String propertyName = e.getPropertyName();
//        if ( "started".equals( propertyName ) )
//        {
//            showBusyAnimation();
//            progressBar.setEnabled( true );
//            progressBar.setIndeterminate( true );
//        }
//        else if ( "done".equals( propertyName ) )
//        {
//            stopBusyAnimation();
//            progressBar.setIndeterminate( false );
//            progressBar.setEnabled( false );
//            progressBar.setValue( 0 );
//        }
//        else if ( "message".equals( propertyName ) )
//        {
//            String text = (String) (e.getNewValue());
//            setMessage( text );
//        }
//        else if ( "progress".equals( propertyName ) )
//        {
//            int value = (Integer) (e.getNewValue());
//            progressBar.setEnabled( true );
//            progressBar.setIndeterminate( false );
//            progressBar.setValue( value );
//        }
//    }
}
