/* $Id$
 *
 * Mack
 *
 * Released under Gnu Public License
 * Copyright © 2010 Michael G. Binz
 */
package org.jdesktop.smack;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.jdesktop.smack.util.StringUtils;




/**
 * A notifier that allows to link the enabled property of a target component
 * against a text component in a way that the text content controls whether the
 * target is enabled.  The default implementation enables the target as soon as
 * the text component contains a non-null, and after trimming, a non-empty
 * string.
 *
 * <p>Create an instance of this class for each necessary connection.  It is
 * allowed to create several connection for a single target when the enabled
 * state of the target depends on more than one sources.</p>
 *
 * <p>The validation behavior can be adjusted by overriding
 * {@link #isValidContent(String)}.</p>
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class TextContentNotifier
{
    /**
     * A reference to the source component.
     */
    private final JTextComponent _source;



    /**
     * The handler for property changes.
     */
    private ConstrainedBooleanEnabled<Boolean> _targetHandler;



    /**
     * Create an instance that is linked between source and target.
     *
     * @param source The source component.
     * @param target The target component.
     */
    @SuppressWarnings("unchecked")
    public TextContentNotifier( JTextComponent source, JComponent target )
    {
        PropertyProxy<Boolean> _targetProperty =
            new PropertyProxy<Boolean>( ENABLED_PROP_NAME, target );

        _source = source;

        // Check if we are already cooperating with the target.
        _targetHandler = (ConstrainedBooleanEnabled<Boolean>)
            target.getClientProperty( TextContentNotifier.class );

        if ( _targetHandler == null )
        {
            // No, so start cooperation.
            _targetHandler =
                new ConstrainedBooleanEnabled<Boolean>( _targetProperty );

            target.putClientProperty(
                    TextContentNotifier.class,
                    _targetHandler );
        }

        linkSource( source );
    }



    /**
     * Create an instance that is linked between source and target.
     *
     * @param source The source component.
     * @param target The target component.
     */
    @SuppressWarnings("unchecked")
    public TextContentNotifier( JTextComponent source, Action target )
    {
        PropertyProxy<Boolean> _targetProperty =
            new PropertyProxy<Boolean>( ENABLED_PROP_NAME, target );

        _source = source;

        // Check if we are already cooperating with the target.
        _targetHandler = (ConstrainedBooleanEnabled<Boolean>)
            target.getValue( TextContentNotifier.class.getName() );

        if ( _targetHandler == null )
        {
            // No, so start cooperation.
            _targetHandler =
                new ConstrainedBooleanEnabled<Boolean>( _targetProperty );

            target.putValue(
                TextContentNotifier.class.getName(),
                _targetHandler );
        }

        linkSource( source );
    }



    private void linkSource( JTextComponent source )
    {
        _targetHandler.addVetoableChangeListener(
                ENABLED_PROP_NAME,
                _changeListener );

        // Register with the component's document model.
        source.getDocument().addDocumentListener( _documentListener );

        // Initialize the target status.
        handleChange( source.getDocument() );
    }



    /**
     * Check if the passed content qualifies as valid.  This default
     * implementation accepts non-null, non-empty strings as valid.
     *
     * @param pContent The content to qualify.
     * @return True if the content is valid and the targets should be enabled.
     */
    protected boolean isValidContent( String pContent )
    {
        return StringUtils.hasContent( pContent, true );
    }



    /**
     * Access to the source component.  Can be used to modify the component
     * view in the {@link #isValidContent(String)} operation.
     *
     * @return The source component.
     */
    protected JTextComponent getSource()
    {
        return _source;
    }



    /**
     * Checks if the passed document should enable the target component.
     *
     * @param d The document to check.
     * @return {@code true} if the target should enable.
     */
    private Boolean shouldEnable( Document d )
    {
        String content = null;

        try
        {
            content = d.getText( 0, d.getLength() );
        }
        catch ( BadLocationException e )
        {
            content = StringUtils.EMPTY_STRING;
        }

        Boolean result = Boolean.valueOf( isValidContent( content ) );

        return result;
    }



    /**
     * Synchronizes the enabled state of the target according to the content
     * of the passed document.
     *
     * @param d The document whose content should be checked.
     */
    private void handleChange( Document d )
    {
        try
        {
            _targetHandler.set( shouldEnable( d ) );
        }
        catch ( PropertyVetoException e )
        {
            // Change was not accepted by another component.
        }
    }



    /**
     * The document listener for the source component.
     */
    private final DocumentListener _documentListener = new DocumentListener()
    {
        @Override
        public void insertUpdate( DocumentEvent e )
        {
            handleChange( e.getDocument() );
        }



        @Override
        public void removeUpdate( DocumentEvent e )
        {
            handleChange( e.getDocument() );
        }



        @Override
        public void changedUpdate( DocumentEvent e )
        {
            handleChange( e.getDocument() );
        }
    };



    /**
     * The change listener that is linked on the shared, per-target, bean.
     *
     * @see ConstrainedBooleanEnabled
     */
    private final VetoableChangeListener _changeListener = new VetoableChangeListener()
    {
        @Override
        public void vetoableChange( PropertyChangeEvent evt )
                throws PropertyVetoException
        {
            // We are only interested in enabled property changes.
            if ( ! ENABLED_PROP_NAME.equals( evt.getPropertyName() ) )
                return;

            // We never veto a change to not enabled.
            if ( ! Boolean.TRUE.equals( evt.getNewValue() ) )
                return;

            if ( Boolean.FALSE.equals( shouldEnable( _source.getDocument() ) ) )
                throw new PropertyVetoException( null, evt );
        }
    };



    /**
     * One instance of this type is placed in the client properties
     * of each target component.  It acts as a wrapper for the target
     * adding a constrained enabled Java Bean property.
     */
    @SuppressWarnings("serial")
    private static class ConstrainedBooleanEnabled<T>
        extends VetoableChangeSupport
    {
        private final PropertyProxy<T> _target;

        /**
         * Create an instance for the given target.
         */
        public ConstrainedBooleanEnabled( PropertyProxy<T> target )
        {
            super( target );
            _target = target;
        }

        public void set( T what )
            throws PropertyVetoException
        {
            T old = _target.get();
            fireVetoableChange( _target.getName(), old, what );
            _target.set( what );
        }
    }



    /**
     * Allows to set a JavaBean property in a relatively simple way.  An
     * instance if this class represents the property on the target object
     * in a (runtime) type-safe way.
     */
    private static class PropertyProxy<T>
    {
        private final PropertyDescriptor _targetProperty;
        private final Object _targetObject;

        /**
         * Create an instance of the proxy and ensures that the property is
         * actually available on the target.  'Available' means that the
         * class of the target object follows the Java Beans conventions.
         *
         * @param propName The name of the property.
         * @param target The target object. Null not allowed.
         */
        public PropertyProxy( String propName, Object target )
        {
            try
            {
                _targetProperty = new PropertyDescriptor( propName, target.getClass() );

                if ( _targetProperty.getReadMethod() == null )
                    throw new IllegalArgumentException( "target has no read method" );
                if ( _targetProperty.getWriteMethod() == null )
                    throw new IllegalArgumentException( "target has no write method" );

                _targetObject = target;
            }
            catch ( IntrospectionException e )
            {
                throw new IllegalArgumentException();
            }
        }



        /**
         * Get the property's value.
         *
         * @return The property's value.
         */
        @SuppressWarnings("unchecked")
        T get()
        {
            try
            {
                return (T)_targetProperty.getReadMethod().invoke( _targetObject );
            }
            catch ( InvocationTargetException e )
            {
                throw new RuntimeException( "Getter failed", e.getCause() );
            }
            catch ( Exception e )
            {
                throw new RuntimeException( "Getter not callable", e);
            }
        }
        /**
         * Set the property's value.
         *
         * @param value The value to set.
         */
        void set( T value )
        {
            try
            {
                _targetProperty.getWriteMethod().invoke( _targetObject, value );
            }
            catch ( InvocationTargetException e )
            {
                throw new RuntimeException( "Setter failed", e.getCause() );
            }
            catch ( Exception e )
            {
                throw new RuntimeException( "Setter not callable", e);
            }
        }
        /**
         * Get the property's name.
         *
         * @return The property's name.
         */
        String getName()
        {
            return _targetProperty.getName();
        }
    }



    /**
     * Unlink the listener connections.
     */
    public void dispose()
    {
        _targetHandler.removeVetoableChangeListener(
                ENABLED_PROP_NAME,
                _changeListener );

        _source.getDocument().removeDocumentListener(
                _documentListener );
    }



    /**
     * The name of the enabled property.
     */
    private final static String ENABLED_PROP_NAME = "enabled";
//
//
//
//
//    public static void main( String[] argv )
//    {
//        JFrame main = new JFrame("Test");
//        JTextField t1 = new JTextField();
//        JTextField t2 = new JTextField("Gimme float...");
//        JTextField t3 = new JTextField();
//        JTextField t4 = new JTextField();
//        JButton b = new JButton( "Huh" );
//
//        new TextContentNotifier( t1, b );
//        new TextContentNotifier( t2, b )
//        {
//            @Override
//            protected boolean isValidContent(String pContent)
//            {
//                try
//                {
//                    Float.parseFloat( pContent );
//                    return true;
//                }
//                catch ( NumberFormatException e )
//                {
//                    return false;
//                }
//            };
//        };
//
//        new TextContentNotifier( t3, b );
//        new TextContentNotifier( t4, b );
//
//        Box c = new Box( BoxLayout.Y_AXIS );
//
//        c.add(  t1 );
//        c.add(  t2 );
//        c.add(  t3 );
//        c.add(  t4 );
//        c.add(  b );
//
//        main.setContentPane( c );
//        main.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
//
//        main.pack();
//        main.setVisible( true );
//    }
}
