/* $Id$
 *
 * UI general
 *
 * Released under Gnu Public License
 * Copyright (c) 2008 Michael G. Binz
 */
package de.michab.swingx;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;





/**
 * Fits a table column's width to its content.
 *
 * @version $Rev$
 * @author Michael Binz
 * @deprecated Use JXTable instead.
 */
public class TableColumnWidthHandler implements MouseListener
{
  /**
   *
   */
  private static int MARGIN = 2;



  /**
   * Sets the preferred width of the visible column specified by vColIndex. The
   * column.
   *
   * Will be just wide enough to show the column head and the widest cell in the
   * column. margin pixels are added to the left and right (resulting in an
   * additional width of 2*margin pixels).
   */
  private void packColumn(JTable table, int vColIndex)
  {
    // Disable auto resizing
    table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

    DefaultTableColumnModel colModel = (DefaultTableColumnModel)
      table.getColumnModel();
    TableColumn col =
      colModel.getColumn( vColIndex );
    int width = 0;

    // Get width of column header
    TableCellRenderer renderer =
      col.getHeaderRenderer();
    if ( renderer == null )
    {
      renderer = table.getTableHeader().getDefaultRenderer();
    }

    Component comp = renderer.getTableCellRendererComponent(
        table,
        col.getHeaderValue(),
        false,
        false,
        0,
        0 );
    width = comp.getPreferredSize().width;

    // Get maximum width of column data
    for ( int r = 0 ; r < table.getRowCount() ; r++ )
    {
      renderer =
        table.getCellRenderer( r, vColIndex );
      comp =
        renderer.getTableCellRendererComponent(
            table,
            table.getValueAt( r, vColIndex ),
            false,
            false,
            r,
            vColIndex );
      width = Math.max(
          width,
          comp.getPreferredSize().width );
    }

    // Add margin
    width += 2 * MARGIN;

    // Set the width
    col.setPreferredWidth( width );
  }



  public void mouseClicked(MouseEvent e)
  {
    // If this is not button one, leave.
    if ( e.getButton() != MouseEvent.BUTTON1 )
      return;
    // If this is not a double click, leave.
    if ( e.getClickCount() != 2 )
      return;

    // If this is not a horizontal resize, leave.  Note that
    // we detect the horizontal resize based on the cursor
    // that is active.  Not sure whether that is solid, but
    // it works.
    JTableHeader th = (JTableHeader) e.getSource();
    Cursor c = th.getCursor();
    if ( !(c.getType() == Cursor.E_RESIZE_CURSOR || c.getType() == Cursor.W_RESIZE_CURSOR) )
      return;

    // If this is not a valid column, leave.
    int column = th.columnAtPoint( e.getPoint() );
    if ( column < 0 )
      return;

    if ( column > 0 )
    {
      Rectangle rect = th.getHeaderRect( column );
      int mouse = e.getPoint().x - rect.x;

      // If we are to the right side of a column header, we resize the
      // column to the left.
      if ( mouse < (rect.width / 2) )
        column--;
    }
    // Finally resize the column.
    packColumn( th.getTable(), column );
  }



  public void mouseEntered(MouseEvent e)
  {
  }



  public void mouseExited(MouseEvent e)
  {
  }



  public void mousePressed(MouseEvent e)
  {
  }



  public void mouseReleased(MouseEvent e)
  {
  }
}
