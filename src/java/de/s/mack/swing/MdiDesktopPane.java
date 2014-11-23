/* $Id$
 *
 * Common.
 *
 * Released under Gnu Public License
 * Copyright © 2011 Michael G. Binz
 */
package de.s.mack.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

/**
 * A special desktop pane, providing the ability to tile the existing child
 * windows and helping with some bright placement of new child frames. Tries to
 * be as similar as possible to what is usually implemented in Windows for MDI
 * support.
 *
 * @version $Rev$
 * @author micbinz
 */
@SuppressWarnings("serial")
public class MdiDesktopPane extends JDesktopPane
{
    private static Logger LOG = Logger.getLogger( MdiDesktopPane.class.getName() );

    // TODO sorting window array
    // TODO calculating cascade offset
    /**
     *
     */
    private static final int TILE_HORIZONTALLY = 0;

    /**
     *
     */
    private static final int TILE_VERTICALLY = 1;

    /**
     * The last rectangle returned by initWindowPosition().
     */
    private final Rectangle _windowPosition = new Rectangle();

    /**
     * Maps menu items to their associated child windows.
     */
    private final HashMap<JMenuItem, JInternalFrame> _mi2frameMap =
        new HashMap<JMenuItem, JInternalFrame>();

    /**
     * The button group used to manage the selected window in the window menu.
     */
    private final ButtonGroup _group = new ButtonGroup();

    /**
     * Defines the maximum number of windows represented as menu items in the
     * windows menu. If there are more windows, a selection dialog is added.
     */
    private static final int showWindowsMenuCount = 10;

    /**
     * The window menu. This is completely managed by the MdiFrame.
     */
    private final JPopupMenu _windowMenu = new JPopupMenu( "Window" );



    /**
     * Create an instance.
     */
    public MdiDesktopPane()
    {
        JMenuItem tile = new JMenuItem( getNamedAction( "actCascade" ) );
        _windowMenu.add( tile );

        tile = new JMenuItem( getNamedAction( "actTileVertically" ) );
        _windowMenu.add( tile );

        tile = new JMenuItem( getNamedAction( "actTileHorizontally" ) );
        _windowMenu.add( tile );

        // The static entries in the windows menu are delimited by a separator.
        // Everything below that separator will be managed automatically.
        _windowMenu.addSeparator();
        setComponentPopupMenu( _windowMenu );
    }

    /**
     * Add an mdi child to the frame. There's no way back, after adding the
     * frame takes over responsibility for the lifecycle of the child.
     */
    private void add( JInternalFrame child )
    {
        super.add( child );

        JMenuItem mi = new JRadioButtonMenuItem( child.getTitle() );
        mi.addActionListener( childsMenuItemListener );
        child.putClientProperty( CP_MENUITEM, mi );
        _mi2frameMap.put( mi, child );
        _group.add( mi );

        // Bookkeeping.
        int mapSize = _mi2frameMap.size();

        if ( mapSize < showWindowsMenuCount )
            _windowMenu.add( mi );
        else if ( mapSize == showWindowsMenuCount )
            _windowMenu.add( getWindowsDialogMenuItem() );

        _windowMenu.setEnabled( true );
    }



    @Action
    public void actTileHorizontally( ActionEvent ae )
    {
        // TODO(micbinz) document...
        tile( TILE_VERTICALLY );
    }
    @Action
    public void actTileVertically( ActionEvent ae )
    {
        // TODO(micbinz) document...
        tile( TILE_HORIZONTALLY );
    }
    @Action
    public void actCascade( ActionEvent ae )
    {
        cascade();
    }

    /**
     * Tile the contained internal frames horizontally or vertically as defined
     * by the passed constant.
     */
    public void tile( int how )
    {
        Dimension availableDesktop = arrangeIcons();

        JInternalFrame[] children = getOpenFrames();

        // Check if there are really child windows to arrange. Iconified child
        // windows have been handled in arrangeIcons().
        if ( children.length < 1 )
            return;

        // Switch off the maximized status of all windows since the look and feel
        // does not handle setDimension() on maximized frames properly.
        for ( JInternalFrame c : children )
            unMaximizeFrame( c );

        int numOfRows = (int)Math.sqrt( children.length );
        int childsPerRow = children.length / numOfRows;
        int remaining = children.length % numOfRows;

        if ( how == TILE_VERTICALLY )
            tileVertically( children, numOfRows, childsPerRow, remaining,
                    availableDesktop );
        else
            tileHorizontally( children, numOfRows, childsPerRow, remaining,
                    availableDesktop );
    }

    /**
     *
     */
    private Dimension arrangeIcons()
    {
        Point currentPosition = null;

        JInternalFrame[] childs = getAllFrames();

        for ( int i = 0; i < childs.length; i++ )
        {
            if ( childs[i].isIcon() )
            {
                JComponent icon = childs[i].getDesktopIcon();

                // Init the current placement position.
                if ( currentPosition == null )
                    currentPosition = new Point( 0, getHeight()
                            - icon.getHeight() );

                // If we are not in the leftmost position and
                // the icon won't fit on this line...
                if ( currentPosition.x > 0
                        && currentPosition.x + icon.getWidth() > getWidth() )
                {
                    // ...we go one line up and to the left.
                    currentPosition.x = 0;
                    currentPosition.y -= icon.getHeight();
                }

                // We are ready to set the icon location.
                icon.setLocation( currentPosition );

                // At last set the position for the next item to the right of
                // this one.
                currentPosition.x += icon.getWidth();
            }
        }

        // If the current position is valid and any icon has been placed...
        if ( currentPosition != null && currentPosition.x > 0 )
            // ...return the remaining free space for further tiling.
            return new Dimension( getWidth(), currentPosition.y );
        else
            // In the other case all real estate may be used.
            return new Dimension( getWidth(), getHeight() );
    }

    /**
     * Implements cascading the child windows.
     */
    public void cascade()
    {
        Dimension availableDesktop = arrangeIcons();

        // Init our window position.
        _windowPosition.setLocation( 0, 0 );

        JInternalFrame[] childs = getOpenFrames();

        int cascadeOffset = calculateCascadeOffset( childs );

        // Childs will be created at 4/5 of our width.
        int childWidth = (4 * availableDesktop.width) / 5;

        // Calculate the number of childs we can position for one run...
        int childsPerRun = (availableDesktop.width - childWidth)
                / cascadeOffset;
        // ...and fix the width so we use all of the available width.
        childWidth += (availableDesktop.width - childWidth) % cascadeOffset;

        // Now calculate the height.
        int childHeight = availableDesktop.height
                - (childsPerRun * cascadeOffset);

        for ( int i = 0; i < childs.length; i++ )
        {
            // Check if we can create a window given that width at the current
            // position...
            if ( (_windowPosition.x + childWidth) > availableDesktop.width )
                // ...and reset the current position to the upper left corner if
                // not.
                _windowPosition.setLocation( 0, 0 );

            // Seems fine so far. Set the height of the new window to the
            // maximum possible for the current y position.
            _windowPosition.setSize( childWidth, childHeight );

            // Set our result on the frame.
            childs[i].setBounds( _windowPosition );
            childs[i].toFront();

            // Translate the current position for the next call.
            _windowPosition.translate( cascadeOffset, cascadeOffset );
        }
    }

    /**
     * Implements vertical tiling.
     */
    private void tileHorizontally( JInternalFrame[] childs, int numOfRows,
            int childsPerRow, int remaining, Dimension availableDesktop )
    {
        int childWidth = availableDesktop.width / numOfRows;

        int childIdx = 0;

        // For each row...
        for ( int i = 0; i < numOfRows; i++ )
        {
            // ...calculate the number of children in this row...
            int childsInThisRow = childsPerRow + (i < remaining ? 1 : 0);
            // ...and each child's height...
            int childHeight = availableDesktop.height / childsInThisRow;
            // ...and for each child in this row...
            for ( int j = 0; j < childsInThisRow; j++ )
            {
                // ...calculate the position.
                childs[childIdx++].setBounds( i * childWidth, j * childHeight,
                        childWidth, childHeight );
            }
        }
    }

    /**
     * Implements horizontal tiling.
     */
    private void tileVertically( JInternalFrame[] childs, int numOfRows,
            int childsPerRow, int remaining, Dimension availableDesktop )
    {
        int childHeight = availableDesktop.height / numOfRows;

        int childIdx = 0;

        // For each row...
        for ( int i = 0; i < numOfRows; i++ )
        {
            // ...calculate the number of childs in this row...
            int childsInThisRow = childsPerRow + (i < remaining ? 1 : 0);
            // ...and each child's height...
            int childWidth = availableDesktop.width / childsInThisRow;
            // ...and for each child in this row...
            for ( int j = 0; j < childsInThisRow; j++ )
            {
                // ...calculate the position.
                childs[childIdx++].setBounds( j * childWidth, i * childHeight,
                        childWidth, childHeight );
            }
        }
    }

    /**
     *
     */
    private int calculateCascadeOffset( JInternalFrame[] childs )
    {
        // The following code isn't working yet.
        /*
         * Insets insets = new Insets( 0, 0, 0, 0 ); int result = 1;
         *
         * for ( int i = 0 ; i < childs.length ; i++ ) { int currentTop =
         * childs[i].getContentPane().getY(); //Insets( insets )).top;
         * System.err.println(currentTop); //Insets( insets )); if ( currentTop
         * < result ) result = currentTop; }
         *
         * if ( result == 0 ) result = 1; System.err.println(
         * "calculateCascadeOffset: " + result ); return result;
         */
        return 30;
    }

    /**
     * Returns an array of open -- that means 'not iconified' -- frames.
     */
    private JInternalFrame[] getOpenFrames()
    {
        Vector<JInternalFrame> openChildren = new Vector<JInternalFrame>();

        for ( JInternalFrame c : getAllFrames() )
        {
            if ( c.isVisible() && !c.isIcon() )
                openChildren.add( c );
        }

        JInternalFrame[] result =
                openChildren.toArray( new JInternalFrame[ openChildren.size() ] );

        Arrays.sort( result, ifComparator );

        return result;
    }

    /**
     * Sorts internal frames according to their title.
     */
    private final Comparator<JInternalFrame> ifComparator = new Comparator<JInternalFrame>()
    {
        @Override
        public int compare( JInternalFrame o1, JInternalFrame o2 )
        {
            return o1.getTitle().compareTo( o2.getTitle() );
        }
    };

    /**
     *
     */
    private final ActionListener windowsDialogListener = new ActionListener()
    {
        @Override
        public void actionPerformed( ActionEvent dummy )
        {
            WindowsDialog.showFor( MdiDesktopPane.this );
        }
    };

    /**
     *
     */
    private JMenuItem windowsDialogMenuItem = null;

    /**
     *
     */
    private JMenuItem getWindowsDialogMenuItem()
    {
        if ( windowsDialogMenuItem == null )
        {
            windowsDialogMenuItem = new JMenuItem( "Windows..." );
            windowsDialogMenuItem.addActionListener( windowsDialogListener );
        }

        return windowsDialogMenuItem;
    }

    /**
     * Listens for a child window's MenuItem. On selection the child window gets
     * deiconified if it was iconified and activated.
     */
    private final ActionListener childsMenuItemListener = new ActionListener()
    {
        @Override
        public void actionPerformed( ActionEvent event )
        {
            JInternalFrame child = _mi2frameMap.get( event.getSource() );

            activate( child );
        }
    };

    /**
     * Sets the focus to a specific child window. If the child is iconified it
     * gets deiconified.
     */
    public void activate( JInternalFrame child )
    {
        try
        {
            if ( child.isIcon() )
                child.setIcon( false );

            child.setSelected( true );
        }
        catch ( Throwable e )
        {
            System.err.println( "activate() failed." );
        }
    }

    /**
     * Swing client property key.
     */
    private static final String CP_MENUITEM = "mdiCpMenuitem";

    /**
     *
     */
    private void remove( JInternalFrame child )
    {
        LOG.fine( "removeChild" );

        JRadioButtonMenuItem mi = (JRadioButtonMenuItem)child.getClientProperty( CP_MENUITEM );
        _mi2frameMap.remove( mi );

        if ( mi == null )
            throw new RuntimeException( "Child didn't exist: " + child );

        // TODO: don't know why this is not working. Maybe related to the
        // DO_NOTHING_ON_CLOSE problem in JInternalFrames.
        // desktop.remove( child );

        _group.remove( mi );

        // If the menu item was actually in the menu remove it, but remember if
        // it really was there.
        boolean wasInMenu = -1 !=_windowMenu.getComponentIndex( mi );
        if ( wasInMenu )
            _windowMenu.remove( mi );

        // If the special dialog action is in our windows menu...
        if ( -1 != _windowMenu.getComponentIndex( getWindowsDialogMenuItem() ) )
        {
            // ...remove it.
            _windowMenu.remove( getWindowsDialogMenuItem() );

            // If the window we closed was contained in the menu...
            if ( wasInMenu )
            {
                // ...look for a component *not* contained in the menu...
                for ( JInternalFrame c : _mi2frameMap.values() )
                {
                    JMenuItem tmi = (JMenuItem)c.getClientProperty( CP_MENUITEM );

                    if ( -1 == _windowMenu.getComponentIndex( tmi ) )
                    {
                        // ...and put it there.
                        _windowMenu.add( tmi );
                        break;
                    }
                }
            }
        }

        // As long as we have more children than we show in the menu keep the
        // windows dialog menu item.
        if ( (_mi2frameMap.size()) >= showWindowsMenuCount )
            _windowMenu.add( getWindowsDialogMenuItem() );

        // If we have no child frames disable the window menu.
        _windowMenu.setEnabled( _mi2frameMap.size() > 0 );
    }



    /**
     *
     */
    private final InternalFrameAdapter childListener = new InternalFrameAdapter()
    {
        @Override
        public void internalFrameActivated( InternalFrameEvent e )
        {
            LOG.fine( "InternalFrameListener: Activated" );
            // Set our attribute...
            JInternalFrame activeChild = (JInternalFrame)e.getSource();
            // ...and activate the window's menu item.
            JRadioButtonMenuItem mi = (JRadioButtonMenuItem)
                activeChild.getClientProperty( CP_MENUITEM );
            if ( mi != null )
                _group.setSelected( mi.getModel(), true );
        }

        /**
         *
         */
        @Override
        public void internalFrameClosing( InternalFrameEvent e )
        {
            // TODO add a system for the InternalFrame to take part in the
            // decision whether it can be closed or not.
            LOG.fine( "InternalFrameListener: Closing" );
            JInternalFrame internalFrame = (JInternalFrame)e.getSource();

            int closeOperation =
                    internalFrame.getDefaultCloseOperation();

            if ( closeOperation == WindowConstants.DO_NOTHING_ON_CLOSE )
                ;
            else if ( closeOperation == WindowConstants.HIDE_ON_CLOSE )
                internalFrame.setVisible( false );
            else if ( closeOperation == WindowConstants.DISPOSE_ON_CLOSE )
            {
                internalFrame.dispose();
                remove( internalFrame );
            }
        }

        /**
         *
         */
        @Override
        public void internalFrameOpened( InternalFrameEvent e )
        {
            LOG.fine( "InternalFrameListener: Opened" );
            add( (JInternalFrame)e.getSource() );
        }
    };



    /**
     * Responsible for listening to property changes of contained child windows.
     * Currently only keeps the frames menu item text in sync with its title.
     */
    private final PropertyChangeListener internalFramePropertyChangeListener =
      new PropertyChangeListener()
    {
      @Override
    public void propertyChange(PropertyChangeEvent evt)
      {
        if ( evt.getPropertyName() == JInternalFrame.TITLE_PROPERTY )
        {
          JInternalFrame changedFrame = (JInternalFrame)evt.getSource();
          JMenuItem menuItemToChange = (JMenuItem)
              changedFrame.getClientProperty( CP_MENUITEM );

          if ( null == menuItemToChange )
          {
            System.err.println( "internalFramePropertyChangeListener: no menuItem found." );
            return;
          }
          if ( ! menuItemToChange.getText().equals( changedFrame.getTitle() ) )
          {
            menuItemToChange.setText( changedFrame.getTitle() );
          }
        }
      }
    };


    /**
     * Registers a new ChildWindow.  On registration child windows are set
     * to an invisible state.  To display the child window call setVisible( true )
     * on the returned instance.  This call triggers registration -- nothing more
     * has to be done.
     *
     * @param child The new child window to register.
     */
    public JInternalFrame registerChild( JInternalFrame child )
    {
        // Set the child to invisible state.
        child.setVisible( false );
        // Add our internal management listener.
        child.addInternalFrameListener( childListener );
        child.addPropertyChangeListener( internalFramePropertyChangeListener );

        child.setInheritsPopupMenu( true );

        return child;
    }

    /**
     * Get the action corresponding to the passed name. The action is entered in the component's
     * action map.
     */
    private javax.swing.Action getNamedAction( String actionName )
    {
        javax.swing.Action result = Application.getInstance().getContext().getActionMap(this).get( actionName );

        if ( result != null && getActionMap().get( actionName ) == null )
            getActionMap().put( actionName, result );

        return result;
    }

    /**
     * Switches a frame into the unmaximized state.
     *
     * @param frame The frame to change.
     */
    private void unMaximizeFrame( JInternalFrame frame )
    {
        if ( frame.isMaximum() )
        {
            try
            {
                frame.setMaximum( false );
            }
            catch ( PropertyVetoException e )
            {
                return;
            }
        }
    }
}
