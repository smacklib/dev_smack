/* $Id$
 *
 * Lichen CosExplorer / CosNaming
 *
 * Released under Gnu Public License
 * Copyright © 2003 Michael G. Binz
 */
package de.michab.util;

import java.util.Stack;



/**
 * Manages a number of shutdown procedures.  The difference to the raw usage of
 * <code>Runtime.addShutdownHook()</code> is that this happens in LIFO order.
 *
 * @see java.awt.Runtime#addShutdownHook()
 * @version $Rev$
 */
public final class ShutdownManager
{
  // We deliberately do not offer a remove() method.



  /**
   * <p>Add a shutdown procedure to be run on application termination.  The
   * added shutdown procedures will be invoked in reverse order of
   * addition.</p>
   *
   * @param shutdownProcedure The shutdown procedure.
   */
  public static void add( Runnable shutdownProcedure )
  {
    if ( _theInstance == null )
      _theInstance = new ShutdownManager();

    _shutdownRunnables.push( shutdownProcedure );
  }



  /**
   * Creates an instance and registers the real shutdown listener.
   */
  private ShutdownManager()
  {
    Thread shutdown = new Thread(
      new Runnable()
      {
        public void run()
        {
          performShutdown();
        }
      },
      getClass().getSimpleName() );

    Runtime.getRuntime().addShutdownHook( shutdown );
  }



  /**
   * Performs the actual shutdown.
   */
  private static void performShutdown()
  {
    while ( ! _shutdownRunnables.empty() )
      _shutdownRunnables.pop().run();
  }



  /**
   * The singleton instance of this class.
   */
  private static ShutdownManager _theInstance;



  /**
   * The internal container used to hold the shutdown runnables.
   */
  private static final Stack<Runnable> _shutdownRunnables =
      new Stack<Runnable>();
}
