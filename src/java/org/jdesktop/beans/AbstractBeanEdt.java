package org.jdesktop.beans;

import java.beans.PropertyChangeSupport;

import javax.swing.event.SwingPropertyChangeSupport;

/**
 * An encapsulation of the PropertyChangeSupport methods based on
 * java.beans.PropertyChangeSupport.  PropertyChangeListeners are fired
 * on the event dispatching thread.
 *
 * <p>
 * Note: this class is only public because the so-called "fix"
 * for javadoc bug
 * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4780441">4780441</a>
 * still fails to correctly document public methods inherited from a package
 * private class.
 */
public class AbstractBeanEdt extends AbstractBean
{
    public AbstractBeanEdt() {
    }

    @Override
    protected PropertyChangeSupport createPcs()
    {
        return new SwingPropertyChangeSupport(this, true);
    }
}
