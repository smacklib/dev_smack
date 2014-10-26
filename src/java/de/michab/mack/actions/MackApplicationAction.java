/* $Id: MackApplicationAction.java 766 2014-05-03 15:27:15Z michab $
 *
 * Mack II
 *
 * Released under Gnu Public License
 * Copyright © 2010-12 Michael G. Binz
 */
package de.michab.mack.actions;

import de.michab.mack.MackAction;



/**
 * Mack's common base class for all application wide actions.
 *
 * @version $Rev: 766 $
 * @author Michael Binz
 */
abstract class MackApplicationAction
    extends
        MackAction
{
    private static final long serialVersionUID = 3076593949273352017L;



    /**
     * The supported application actions.
     */
    public static enum AppActionKey {
      appOpen,
      appExit,
      appSave,
      appSaveAs,
      appPrint,

      appUndo,
      appRedo,
      appCut,
      appCopy,
      appPaste,

      // This is for the application preferences.
      appOption,

      appAbout };



    /**
     * Creates an instance.
     */
    MackApplicationAction( AppActionKey id )
    {
        super( id.toString() );
    }
}
