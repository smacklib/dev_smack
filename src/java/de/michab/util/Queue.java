/* $Id: Queue.java 41 2008-10-26 11:22:08Z Michael $
 *
 * Utilities
 *
 * Released under Gnu Public License
 * Copyright (c) 1998 Michael G. Binz
 */
package de.michab.util;

import java.util.ArrayList;
import java.util.List;



/**
 * <p>A queue of objects used for transient inter-thread communication between
 * a single producer and a single consumer.  Queues are used for
 * first-in-first-out communication between two entities, where the entity that
 * puts objects on the queue is called the producer and the entity reading the
 * queue is called the consumer.</p><p>
 *
 * Using the <code>get()</code> method the consumer is blocked in case the
 * queue is empty.  For non-blocking read access to the queue use the
 * <code>getNoBlock()</code> method.</p><p>
 *
 * All kinds of <code>java.lang.Object</code>s can be communicated across the
 * queue including <code>null</code> and exceptions.  For prioritised
 * exceptions the <code>raise()</code> method is available.  This method allows
 * the queue producer to put an exception object on the queue that is thrown on
 * the next <code>get()</code> operation, even in case there are currently
 * other objects waiting on the queue.</p>
 *
 * @author Michael G. Binz
 */
public class Queue <T>
{
  // TODO: check if it possible to remove the exception mechanism without loss
  // of required functionality.  Would simplify a lot.

  /**
   * This represents the implementation we delegate the queue's responsibilites
   * to.
   */
  private final List<T> _objects = new ArrayList<T>();



  /**
   * Used for exception queueing.
   */
  private List<Exception> _exceptions = null;



  /**
   * Create an instance.
   */
  public Queue()
  {
  }



  /**
   * Check if an exception is pending.
   *
   * @return <code>true</code> if the producer put an exception on the queue.
   */
  private boolean exceptionRaised()
  {
    return
      _exceptions != null &&
      ! _exceptions.isEmpty();
  }



  /**
   * Removes and returns the first registered exception from the internal
   * vector.
   *
   * @return The exception that has been put onto the queue.
   */
  private Exception getException()
  {
    return _exceptions.remove( 0 );
  }



  /**
   * Get one object from the <code>Queue</code>.  If the <code>Queue</code> is
   * empty this operation blocks.  In case the producer placed an exception on
   * the queue this will be thrown.
   *
   * @return An object from the <code>Queue</code>.
   * @exception Exception A user defined exception placed on the queue by a
   *            call to <code>raise()</code>.
   * @see Queue#raise
   */
  synchronized public T get()
    throws Exception
  {
    while ( true )
    {
      if ( exceptionRaised() )
        throw getException();
      else if ( ! _objects.isEmpty() )
        return _objects.remove( 0 );

      wait();
    }
  }



  /**
   * Get one object from the <code>Queue</code> without blocking.  In case the
   * <code>Queue</code> is empty the object passed as an argument into this
   * method call will be returned as the result.  This is needed since the
   * <code>Queue</code> does not put any restrictions on the objects that are
   * transferred -- even <code>null</code> references are allowed.  As a result
   * of this the client has to decide which object can be used as a
   * <code>Queue</code>-empty marker.<p>
   * The following is a coding sample demonstrating the use of the
   * <code>defaultAnswer</code> argument:
   * <p><blockquote><pre>
   *   Object queueEmpty = new Object();
   *
   *   Object result = theQueue.getNoBlock( queueEmpty );
   *   if ( result == queueEmpty )
   *   {
   *     // The queue was empty.
   *   }
   *   else
   *   {
   *     // The queue returned an object.
   *   }
   * </pre></blockquote>
   *
   * @param queueEmptyAnswer The object to return in case the
   *        <code>Queue</code> is empty.<p>
   *        Note that it must be ensured that it is not possible that this
   *        object can be communicated as a regular object on the
   *        <code>Queue</code>.
   * @return An object from the <code>Queue</code> or the object passed in as
   *         an argument.
   * @exception Exception A user defined exception placed on the queue by a
   *            call to <code>raise()</code>.
   * @see Queue#get
   * @see Queue#raise
   */
  synchronized public T getNoBlock( T queueEmptyAnswer )
    throws Exception
  {
    if ( exceptionRaised() )
      throw getException();
    else if ( ! _objects.isEmpty() )
      return _objects.remove( 0 );
    else
      return queueEmptyAnswer;
  }



  /**
   * Put an element on this queue.  This operation will never block.
   *
   * @param object The element to put onto the queue.
   */
  public synchronized void put( T object )
  {
    // Add the new element to the queue...
    _objects.add( object );
    // ...and unlock *one* waiting consumer.
    notify();
  }



  /**
   * Submit an exception to be thrown on the next call of <code>get()</code> or
   * <code>getNoBlock()</code>.  Note that there is no synchronisation with the
   * queue's contents, the exception gets thrown on the next queue get
   * operation even if there are objects on the queue.
   *
   * @param e The exception to be sent to the consumer.
   * @see Queue#get()
   * @see Queue#getNoBlock( Object )
   */
  public synchronized void raise( Exception e )
  {
    // Init our attribute on demand.
    if ( null == _exceptions )
      _exceptions = new ArrayList<Exception>();

    _exceptions.add( e );

    notify();
  }
}
