/* $Id$
 *
 * Common.
 *
 * Released under Gnu Public License
 * Copyright © 2003-15 Michael G. Binz
 */
package org.smack.fx;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.smack.util.StringUtil;
import org.smack.util.io.OutputStreamForwarder;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

/**
 * A console ui component. Connects stream oriented in- and output to a
 * text component.
 *
 * Experimental.
 *
 * @author Michael Binz
 */
public final class FxConsole extends BorderPane
{
    private final Logger LOG = Logger.getLogger( getClass().getName() );

    // TODO(micbinz) History prefix search. Oh, that seems so easy and fine
    // but is unimplementable?!

    /**
     * The maximum text length a console is allowed to handle.  Adding more text results in
     * the console discarding the oldest text.
     */
    private final static int MAX_TEXT_LENGTH = 250000;

    /**
     * The stream that receives input from the console. This is passed in by the client.
     */
    private OutputStream _outPipe  = null;

    /**
     * The console's output stream that is connected to the text area.
     */
    private final OutputStream _out = new OutputStream()
    {

        @Override
        public void write( int b ) throws IOException
        {
            write( new byte[] { (byte)(b & 0xff) } );
        }

        @Override
        public void write( byte b[] ) throws IOException
        {
            threadSavePrint( new String( b ) );
        }

        @Override
        public void write( byte[] b, int off, int len ) throws IOException
        {
            threadSavePrint( new String( b, off, len  ) );
        }
    };

    /**
     * Get a reference to the output stream.
     *
     * @return A reference to the output stream.
     */
    public OutputStream getOut() {
        return _out;
    }

    /**
     * The document position where the editable content starts.
     */
    private int _cmdStart = 0;

    private static final int  maximumHistoryLength = 100;

    /**
     * The command line history.  The last line is the newest line in the
     *  history.
     */
    private final Vector<String> _history = new Vector<>();

    /**
     *
     */
    private String _startedLine;

    /**
     *
     */
    private int _histLine = 0;

    /**
     * The text component used.
     */
    private final TextArea _text;

    /**
     * True if a console should display an entered CR, false
     * otherwise.  Default is false.
     */
    private final boolean _showCr;

    /**
     * Creates a console.
     */
    public FxConsole() {
        this( false );
    }

    /**
     * Creates a console. Allows to select whether carriage
     * returns are displayed in the console window as they are entered.
     *
     * @param showCr If true then entered carriage returns are shown in the
     * console window. Otherwise carriage returns are not displayed.
     */
    public FxConsole( boolean showCr ) {

        _showCr = showCr;

        _text = new TextArea() {

            @Override
            public void cut() {
                // If not in the edit area map the cut action to copy.
                if ( _text.getCaretPosition() < _cmdStart) {
                    super.copy();
                }
                else {
                    super.cut();
                }
            }

            @Override
            public void paste() {
                forceCaretIntoEditArea();
                super.paste();
            }
        };

        _text.setWrapText( true );
        _text.setEditable( false );

        _text.setFont(
                new Font("Monospaced", 12));

        _text.setOnKeyTyped( this::keyTyped );
//        _text.setOnKeyReleased( this::keyReleased );
        _text.setOnKeyPressed( this::keyPressed );

        setCenter( _text );
    }

    /**
     *
     */
    private void keyTyped(KeyEvent e) {
        String characterString = e.getCharacter();

        if ( StringUtil.isEmpty( characterString ) )
            return;

        char character = characterString.charAt( 0 );

        // We do not handle any control characters below space here.
        if ( character < ' ' )
            return;
        // 127 is DEL, the only control character above the space character.
        if ( character == 127 )
            return;

        // A plain vanilla character. Insert it ...
        forceCaretIntoEditArea();
    }

    /**
     *
     */
    private void keyPressed(KeyEvent e) {

        switch (e.getCode()) {
        case ENTER:
            if (e.getEventType() == KeyEvent.KEY_PRESSED) {
                enter();
                resetEditArea();
                _text.positionCaret(_cmdStart);
            }
            e.consume();
            break;

        case UP:
            if (e.getEventType() == KeyEvent.KEY_PRESSED) {
                historyUp();
            }
            e.consume();
            break;

        case DOWN:
            if (e.getEventType() == KeyEvent.KEY_PRESSED) {
                historyDown();
            }
            e.consume();
            break;

        case DELETE:
            if (_text.getCaretPosition() < _cmdStart) {
                e.consume();
            }
            break;

        case LEFT:
        case BACK_SPACE:
            if ((_text.getCaretPosition() <= _cmdStart)) {
                e.consume();
            }
            break;
            /*
             * case ( KeyEvent.VK_RIGHT ): forceCaretMoveToStart(); break;
             */

        case HOME:
            _text.positionCaret(_cmdStart);
            e.consume();
            break;

        default:
            break;
        }
    }

    /**
     * Creates a new edit area.
     */
    private void resetEditArea() {
        _cmdStart = _text.getText().length();
    }

    /**
     * Appends the passed text to the console display.
     */
    private void append(String string) {

        StringBuilder d =
                new StringBuilder( _text.getText() );

        int documentLength = d.length();

        // Check if we reached our maximum buffer size and release data if this
        // is the case.
        if (documentLength + string.length() > FxConsole.MAX_TEXT_LENGTH) {
            d.delete(
                    0,
                    // Under extreme circumstances it may be the case that the
                    // passed strings are larger than the entire buffer, for
                    // example if the buffer is defined relatively small and a
                    // lot of data is sent.
                    Math.min( string.length(), documentLength ) );
        }

        d.insert( d.length(), string );

        _text.setText( d.toString() );
    }

    /**
     *
     */
    private void replaceEditArea(String s) {
        _text.selectRange(_cmdStart, _text.getText().length());
        _text.replaceSelection(s);
        _text.positionCaret(_text.getText().length());
    }

    /**
     * In case the caret is to the left of the current command area moves the caret to the end of
     * the command area. If the caret is inside the current command area its position is not
     * changed.
     */
    private void forceCaretIntoEditArea() {
        if (_text.getCaretPosition() < _cmdStart) {
            _text.positionCaret(_text.getText().length());
        }
    }

    /**
     * Add a command line to the history.  The strategy is to not keep any
     * duplicate lines in the history and to add recent lines always to the
     * recent history.  This means that rarely used entries move towards
     * the old end of the history and are thrown out first.
     *
     * @param commandLine The command line to put into the history.
     */
    private void addToHistory(String commandLine) {

        // We do not keep empty lines in our history.
        if (commandLine.isEmpty()) {
            return;
        }

        _history.remove( commandLine );

        if ( _history.size() >= FxConsole.maximumHistoryLength ) {
            _history.remove(0);
        }

        _history.addElement(commandLine);
    }

    /**
     * The user pressed enter after editing.
     */
    private void enter() {

        String confirmedLine = getEditAreaContents().trim();

        addToHistory(confirmedLine);

        this._histLine = 0;

        postLine(confirmedLine + "\n");

        if ( _showCr )
            try
        {
                getOut().write( '\n' );
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Returns the edited line.
     *
     * @return The line edited, if this is empty then the empty string is returned. {@code null} is
     *         never returned.
     */
    private String getEditAreaContents() {
        return _text.getText(
                _cmdStart,
                _text.getText().length() );
    }

    /**
     * Processes a cursor up key.
     */
    private void historyUp()
    {
        // TODO Implementation is a mess. Cleanup.

        if (_history.size() == 0) {
            return;
        }

        if (_histLine == 0) {
            _startedLine = getEditAreaContents();
        }

        int historyLine = _histLine;

        while (historyLine < _history.size()) {
            historyLine++;
            if ( _history.get( _history.size() - historyLine ).startsWith( _startedLine ) )
            {
                _histLine = historyLine;
                showHistoryLine();
                break;
            }
        }
    }

    /**
     * Processes a cursor down key.
     */
    private void historyDown()
    {
        // TODO Implementation is a mess. Cleanup.

        if (_histLine == 0)
            return;

        int historyLine = _histLine;

        while ( historyLine > 0 ) {
            historyLine--;
            if ( historyLine == 0 ||
                    _history.get( _history.size() - historyLine ).startsWith( _startedLine ) )
            {
                _histLine = historyLine;
                showHistoryLine();
                break;
            }
        }
    }

    /**
     *
     */
    private void showHistoryLine()
    {
        // TODO Implementation is a mess. Cleanup.

        String showline;
        if (_histLine == 0) {
            showline = _startedLine;
        }
        else {
            showline = _history.elementAt(_history.size() - _histLine);
        }

        replaceEditArea(showline);
        forceCaretIntoEditArea();
        _text.positionCaret(_text.getText().length());
    }

    /**
     * Write the passed line to the console's output listener.
     */
    private void postLine(String line) {

        if (this._outPipe == null) {
            return;
        }

        try {
            _outPipe.write(line.getBytes());
        }
        catch (IOException e) {
            // Signal to the user that we cannot accept further input.
            //            _text.setBackground( Color.LIGHT_GRAY );
            _text.setEditable( false );
            _outPipe = null;
            LOG.log( Level.WARNING, "Console pipe broken...", e );
        }
    }

    /**
     * Set an OutputStream that receives the lines entered by the user.
     *
     * @param os
     *            The output stream receiving lines entered by the user.
     */
    public void setInputReceiver(OutputStream os) {
        _outPipe = os == null ?
                null :
                    new OutputStreamForwarder( os, 10 );

        // Set back to default background color.
        if ( _outPipe != null )
        {
            //            _text.setBackground( new JTextArea().getBackground() );
            _text.setEditable( true );
        }
        else
        {
            //            _text.setBackground( _toolbar.getBackground() );
            _text.setEditable( false );
        }
    }

    /**
     * A buffer used to carry the bytes across the border between non-edt and edt threads.
     * Since the edt event queue is processed very slow this buffer is used to collect incoming
     * data while the event queue has not processed the insert operation.
     * Used only in {@link #threadSavePrint(String)}.
     */
    private final StringBuffer _crossEdtBuffer = new StringBuffer();

    /**
     * Output the passed string to the console.
     *
     * @param string The string to print. The passed string buffer is locked
     * and modified.
     */
    private void printTakeover( StringBuffer string ) {

        if ( ! javafx.application.Platform.isFxApplicationThread() )
            throw new InternalError( "Not on EDT." );

        int caretPosition;

        if ( _text.getSelection().getLength() != 0 )
            caretPosition = -1;
        else
            caretPosition = _text.getCaretPosition();

        // Atomically read and reset the data to display.
        synchronized ( string )
        {
            append(string.toString());
            string.setLength( 0 );
        }

        resetEditArea();

        if ( caretPosition < 0 )
            ;
        else if ( lockedProperty.get() )
            _text.positionCaret( caretPosition );
        else
            _text.positionCaret( _cmdStart );
    }

    /**
     * Append to the console from a thread different from the EDT.
     *
     * @param string The text to append.
     */
    private synchronized void threadSavePrint( String string ) {

        boolean edtNotifyNeeded = true;

        synchronized ( _crossEdtBuffer )
        {
            // If our edt buffer was empty, we have to send a notification to
            // JavaFx.
            // If it was not empty, Fx is already notified.
            edtNotifyNeeded = _crossEdtBuffer.length() == 0;
            _crossEdtBuffer.append( string );
        }

        // If no Fx notification is needed...
        if ( ! edtNotifyNeeded )
        {
            // ...we're done.
            return;
        }

        // Notify Fx of the new data.
        Platform.runLater( () ->
        printTakeover( _crossEdtBuffer ) );
    }

    /**
     * If true then scroll lock is active.
     */
    public final SimpleBooleanProperty lockedProperty =
            new SimpleBooleanProperty( this, "locked", false );
    {
        lockedProperty.addListener( (a,b,c) -> setLocked( c ) );
    }

    /**
     * Sets the scroll lock status.
     *
     * @param what The scroll lock status. {@code true} is scroll lock active.
     */
    public void setLocked( boolean what )
    {
        // Clear the selection if the lock status changed.
        _text.deselect();

        // If not longer locked navigate to the end of the text.
        if ( !what )
            _text.positionCaret( _cmdStart );
    }
}
