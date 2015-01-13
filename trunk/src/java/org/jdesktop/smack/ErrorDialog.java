/* $Id$
 *
 * Michael's Application Construction Kit (MACK)
 *
 * Released under Gnu Public License
 * Copyright (c) 2002 Michael G. Binz
 */
package org.jdesktop.smack;

import java.awt.Component;
import java.text.MessageFormat;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.smack.util.StringUtils;




/**
 * Represents a singular error dialog that can be loaded with a resource
 * file.
 *
 * @version $Revision$
 * @author michab
 * @deprecated Use JXErrorPane instead.
 */
@Deprecated
public class ErrorDialog
{
    // TODO(michab) Resource handling must be switched to
    // ResourceMaps.  This seems to be from the past.
  // TODO We have to add a possibility to pass an exception and display its
  //       stack backtrace (and if only I need it for debugging.)
  /**
   * The logger for this class.
   */
  private final static Logger _log =
    Logger.getLogger( ErrorDialog.class.getName() );

    /**
     * Forbid creation.
     */
    private ErrorDialog()
    {
        throw new AssertionError();
    }



    /**
     * Shows the passed throwable in a dialog.
     *
     * @param parent The parent component for the dialog.
     * @param e The throwable to display.
     */
    static public void show( Component parent, Throwable e )
    {
        String message = e.getLocalizedMessage();

        if ( StringUtils.hasContent( message, true ) )
            message = StringUtils.ensureFirstCharacterUppercase( message.trim() );
        else
            message = e.getClass().getSimpleName();

        JOptionPane.showMessageDialog(
                parent,
                message,
                ERROR_DIALOG_TITLE,
                JOptionPane.ERROR_MESSAGE );
    }



    /**
     * Shows the passed throwable in a dialog.
     *
     * @param e The throwable to display.
     */
    static public void show( Throwable e )
    {
        show( null, e );
    }



  /**
   * Brings up an error dialog on the passed parent component.  Dialog text is
   * created from the passed resource string and with the formatted arguments.
   *
   * @param parent The parent component for the dialog.  Pass
   *               <code>null</code> if there's no parent component.
   * @param key The resource key.
   * @param args Parameter list to be formatted into the error message.
   */
  static public void show( Component parent,
                           String key,
                           Object... args )
  {
      ResourceMap rm = Application.getInstance().getContext().getResourceMap();

      String theMessage =
          rm.getString( key );
      if ( theMessage == null )
      {
          _log.warning( "Key not defined in Application resource map: " + key );
          theMessage = key;
      }
      theMessage =
          MessageFormat.format( theMessage, args );

      JOptionPane.showMessageDialog(
              parent,
              theMessage,
              ERROR_DIALOG_TITLE,
              JOptionPane.ERROR_MESSAGE );
  }

  /**
   * Creates a message from the passed exception.
   *
   * @param e The exception to create the message for.
   * @return The message.
   */
  static private Object createMessage( Exception e )
  {
    // Then transform the exception stack backtrace into a string...
    java.io.StringWriter sw = new java.io.StringWriter();
    java.io.PrintWriter pw = new java.io.PrintWriter( sw );
    e.printStackTrace( pw );
    pw.flush();
    sw.flush();

    return sw.toString();
  }



  /**
   * Title to be used for the dialog.  Initial value is the resource key used
   * to resolve the title.  Successful resolution replaces the initial value.
   */
  private static String ERROR_DIALOG_TITLE = "ERROR_DIALOG_TITLE";


  /**
   *
   */
  private static String _errorTabTitle = "Error";
  private static String _detailTabTitle = "Details";
}
