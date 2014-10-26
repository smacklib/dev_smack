/* $Id: DropTableListener.java 691 2013-12-22 11:06:09Z michab $
 *
 * Michael's Application Construction Kit (MACK)
 *
 * Released under Gnu Public License
 * Copyright © 2008-2010 Michael G. Binz
 */
package de.michab.mack;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTable;

import de.michab.util.Transformer;



/**
 * <p>A drag and drop listener that can be added to a table.  An example
 * is</p><pre>
 *  JTable table = new JTable();
 *  new DropTarget( table, new DropTableListener() );
 *  ...
 * </pre><p>
 * Note that a drop table listener is stateless, i.e. a single instance
 * can be added to more than one table.</p>
 *
 * <p>A drop table listener checks whether the dropped data can be transformed
 * to the addressed column class and if so accepts the drop, performs
 * the transformation and sets the new cell value.</p>
 *
 * @version $Rev: 691 $
 * @author Michael Binz
 */
public class DropTableListener implements DropTargetListener
{
    /*
     * The logger for this class.
     */
    private static final Logger _log =
        Logger.getLogger( DropTableListener.class.getName() );



    /**
     * Create a drop table listener
     */
    public DropTableListener()
    {
    }



    /*
     * Inherit javadoc.
     */
    public void dragEnter( DropTargetDragEvent dtde )
    {
    }



    /*
     * Inherit javadoc.
     */
    public void drop( DropTargetDropEvent dtde )
    {
        // Used on dnd protocol completion in 'finally' below.
        boolean status = false;

        try
        {
            JTable src = getSource( dtde );

            // First we accept the drop to get the dnd protocol into the
            // needed state for getting the data.
            dtde.acceptDrop( DnDConstants.ACTION_COPY_OR_MOVE );

            File file = getTransferFile( dtde.getTransferable() );

            int col = src.columnAtPoint( dtde.getLocation() );
            int row = src.rowAtPoint( dtde.getLocation() );
            if ( col < 0 )
                return;

            Class<?> c = src.getColumnClass( col );

            Object o = Transformer.transform( file, c );

            if ( o != null )
            {
                src.setValueAt( o, row, col );
                src.repaint();
            }

            // Everything went fine.
            status = true;
        }
        // Catch potential IO exceptions and keep dnd protocol in sync.
        catch ( Exception e )
        {
            _log.log( Level.WARNING, e.getLocalizedMessage(), e );
            dtde.rejectDrop();
        }
        // And again: Last step in dnd protocol. After that we are ready to
        // accept the next drop.
        finally
        {
            dtde.dropComplete( status );
        }
    }



    /**
     *
     * @param x
     * @return
     */
    private File getTransferFile( Transferable x )
    {
        File[] result;

        try
        {
            // Now get the transferred data and be as defensive as possible.
            // We expect a java.util.List of java.io.Files.

            List<File> fileList = (List<File>)
                x.getTransferData( DataFlavor.javaFileListFlavor );
            result = fileList.toArray( new File[fileList.size()] );
        }
        catch ( Exception  e )
        {
            _log.log( Level.FINE, e.getLocalizedMessage(), e );
            return null;
        }
        return result[0];
    }



    @Override
    public void dragExit( DropTargetEvent dte )
    {
    }



    @Override
    public void dragOver( DropTargetDragEvent dtde )
    {
        JTable src = getSource( dtde );

        Point p = dtde.getLocation();
        int col = src.columnAtPoint( p );
        int row = src.rowAtPoint( p );

        src.changeSelection( row, col, false, false );

        if ( Transformer.canConvert(
                File.class,
                src.getColumnClass( col ) ) )
        {
            dtde.acceptDrag( DnDConstants.ACTION_COPY_OR_MOVE );
        }
        else
        {
            dtde.rejectDrag();
        }
    }



    @Override
    public void dropActionChanged( DropTargetDragEvent dtde )
    {
    }



    /**
     * Get the source component of the passed event.
     *
     * @param dte The drag and drop event.
     * @return The source JTable.  If this was no JTable, then null is
     *         returned.
     */
    private static JTable getSource( DropTargetEvent dte )
    {
        try
        {
            return (JTable)dte.getDropTargetContext().getComponent();
        }
        catch ( Exception e )
        {
            _log.log(
                    Level.FINE,
                    e.getLocalizedMessage(),
                    e );

            return null;
        }
    }
}
