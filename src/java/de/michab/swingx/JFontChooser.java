/************************************************************
 * Copyright 2004-2005,2007-2008 Masahiko SAWAI All Rights Reserved.
 ************************************************************/
package de.michab.swingx;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;

/**
 * The <code>JFontChooser</code> class is a swing component for font selection. This class has
 * <code>JFileChooser</code> like APIs. The following code pops up a font chooser dialog.
 *
 * <pre>
 *   JFontChooser fontChooser = new JFontChooser();
 *   int result = fontChooser.showDialog(parent);
 *   if (result == JFontChooser.OK_OPTION)
 *   {
 *      Font font = fontChooser.getSelectedFont();
 *      System.out.println("Selected Font : " + font);
 * }
 *
 * <pre>
 *
 * @version $Rev$
 * @author Masahiko SAWAI
 */
@SuppressWarnings("serial")
public class JFontChooser extends JComponent {
    // class variables
    /**
     * Return value from <code>showDialog()</code>.
     *
     * @see #showDialog
     **/
    public static final int       OK_OPTION                 = 0;
    /**
     * Return value from <code>showDialog()</code>.
     *
     * @see #showDialog
     **/
    public static final int       CANCEL_OPTION             = 1;
    /**
     * Return value from <code>showDialog()</code>.
     *
     * @see #showDialog
     **/
    public static final int       ERROR_OPTION              = -1;
    private static final Font     DEFAULT_SELECTED_FONT     = new Font("Serif", Font.PLAIN, 12);
    private static final Font     DEFAULT_FONT              = new Font("Dialog", Font.PLAIN, 10);
    private static final int[]    FONT_STYLE_CODES          = {Font.PLAIN, Font.BOLD, Font.ITALIC,
        Font.BOLD | Font.ITALIC                             };
    private static final String[] DEFAULT_FONT_SIZE_STRINGS = {"8", "9", "10", "11", "12", "14", "16", "18", "20",
        "22", "24", "26", "28", "36", "48", "72",           };

    // instance variables
    protected int                 dialogResultValue         = JFontChooser.ERROR_OPTION;

    private String[]              fontStyleNames            = null;
    private String[]              fontFamilyNames           = null;
    private String[]              fontSizeStrings           = null;
    private JTextField            fontFamilyTextField       = null;
    private JTextField            fontStyleTextField        = null;
    private JTextField            fontSizeTextField         = null;
    private JList                 fontNameList              = null;
    private JList                 fontStyleList             = null;
    private JList                 fontSizeList              = null;
    private JPanel                fontNamePanel             = null;
    private JPanel                fontStylePanel            = null;
    private JPanel                fontSizePanel             = null;
    private JPanel                samplePanel               = null;
    private JTextField            sampleText                = null;

    /**
     * Constructs a <code>JFontChooser</code> object.
     **/
    public JFontChooser() {
        this(JFontChooser.DEFAULT_FONT_SIZE_STRINGS);
    }

    /**
     * Constructs a <code>JFontChooser</code> object using the given font size array.
     *
     * @param fontSizeStrings
     *            the array of font size string.
     **/
    public JFontChooser(String[] fontSizeStrings) {
        if (fontSizeStrings == null) {
            fontSizeStrings = JFontChooser.DEFAULT_FONT_SIZE_STRINGS;
        }
        this.fontSizeStrings = fontSizeStrings;

        JPanel selectPanel = new JPanel();
        selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.X_AXIS));
        selectPanel.add(getFontFamilyPanel());
        selectPanel.add(getFontStylePanel());
        selectPanel.add(getFontSizePanel());

        JPanel contentsPanel = new JPanel();
        contentsPanel.setLayout(new GridLayout(2, 1));
        contentsPanel.add(selectPanel, BorderLayout.NORTH);
        contentsPanel.add(getSamplePanel(), BorderLayout.CENTER);

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(contentsPanel);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setSelectedFont(JFontChooser.DEFAULT_SELECTED_FONT);
    }

    public JTextField getFontFamilyTextField() {
        if (this.fontFamilyTextField == null) {
            this.fontFamilyTextField = new JTextField();
            this.fontFamilyTextField.addFocusListener(new TextFieldFocusHandlerForTextSelection(
                this.fontFamilyTextField));
            this.fontFamilyTextField.addKeyListener(new TextFieldKeyHandlerForListSelectionUpDown(
                getFontFamilyList()));
            this.fontFamilyTextField.getDocument().addDocumentListener(
                new ListSearchTextFieldDocumentHandler(getFontFamilyList()));
            this.fontFamilyTextField.setFont(JFontChooser.DEFAULT_FONT);

        }
        return this.fontFamilyTextField;
    }

    public JTextField getFontStyleTextField() {
        if (this.fontStyleTextField == null) {
            this.fontStyleTextField = new JTextField();
            this.fontStyleTextField.addFocusListener(new TextFieldFocusHandlerForTextSelection(
                this.fontStyleTextField));
            this.fontStyleTextField.addKeyListener(new TextFieldKeyHandlerForListSelectionUpDown(
                getFontStyleList()));
            this.fontStyleTextField.getDocument().addDocumentListener(
                new ListSearchTextFieldDocumentHandler(getFontStyleList()));
            this.fontStyleTextField.setFont(JFontChooser.DEFAULT_FONT);
        }
        return this.fontStyleTextField;
    }

    public JTextField getFontSizeTextField() {
        if (this.fontSizeTextField == null) {
            this.fontSizeTextField = new JTextField();
            this.fontSizeTextField.addFocusListener(new TextFieldFocusHandlerForTextSelection(
                this.fontSizeTextField));
            this.fontSizeTextField
                .addKeyListener(new TextFieldKeyHandlerForListSelectionUpDown(getFontSizeList()));
            this.fontSizeTextField.getDocument().addDocumentListener(
                new ListSearchTextFieldDocumentHandler(getFontSizeList()));
            this.fontSizeTextField.setFont(JFontChooser.DEFAULT_FONT);
        }
        return this.fontSizeTextField;
    }

    public JList getFontFamilyList() {
        if (this.fontNameList == null) {
            this.fontNameList = new JList(getFontFamilies());
            this.fontNameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.fontNameList.addListSelectionListener(new ListSelectionHandler(getFontFamilyTextField()));
            this.fontNameList.setSelectedIndex(0);
            this.fontNameList.setFont(JFontChooser.DEFAULT_FONT);
            this.fontNameList.setFocusable(false);
        }
        return this.fontNameList;
    }

    public JList getFontStyleList() {
        if (this.fontStyleList == null) {
            this.fontStyleList = new JList(getFontStyleNames());
            this.fontStyleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.fontStyleList.addListSelectionListener(new ListSelectionHandler(getFontStyleTextField()));
            this.fontStyleList.setSelectedIndex(0);
            this.fontStyleList.setFont(JFontChooser.DEFAULT_FONT);
            this.fontStyleList.setFocusable(false);
        }
        return this.fontStyleList;
    }

    public JList getFontSizeList() {
        if (this.fontSizeList == null) {
            this.fontSizeList = new JList(this.fontSizeStrings);
            this.fontSizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.fontSizeList.addListSelectionListener(new ListSelectionHandler(getFontSizeTextField()));
            this.fontSizeList.setSelectedIndex(0);
            this.fontSizeList.setFont(JFontChooser.DEFAULT_FONT);
            this.fontSizeList.setFocusable(false);
        }
        return this.fontSizeList;
    }

    /**
     * Get the family name of the selected font.
     *
     * @return the font family of the selected font.
     * @see #setSelectedFontFamily
     **/
    public String getSelectedFontFamily() {
        String fontName = (String) getFontFamilyList().getSelectedValue();
        return fontName;
    }

    /**
     * Get the style of the selected font.
     *
     * @return the style of the selected font. <code>Font.PLAIN</code>, <code>Font.BOLD</code>,
     *         <code>Font.ITALIC</code>, <code>Font.BOLD|Font.ITALIC</code>
     * @see java.awt.Font#PLAIN
     * @see java.awt.Font#BOLD
     * @see java.awt.Font#ITALIC
     * @see #setSelectedFontStyle
     **/
    public int getSelectedFontStyle() {
        int index = getFontStyleList().getSelectedIndex();
        return JFontChooser.FONT_STYLE_CODES[index];
    }

    /**
     * Get the size of the selected font.
     *
     * @return the size of the selected font
     * @see #setSelectedFontSize
     **/
    public int getSelectedFontSize() {
        int fontSize = 1;
        String fontSizeString = getFontSizeTextField().getText();
        while (true) {
            try {
                fontSize = Integer.parseInt(fontSizeString);
                break;
            }
            catch (NumberFormatException e) {
                fontSizeString = (String) getFontSizeList().getSelectedValue();
                getFontSizeTextField().setText(fontSizeString);
            }
        }

        return fontSize;
    }

    /**
     * Get the selected font.
     *
     * @return the selected font
     * @see #setSelectedFont
     * @see java.awt.Font
     **/
    public Font getSelectedFont() {
        Font font = new Font(getSelectedFontFamily(), getSelectedFontStyle(), getSelectedFontSize());
        return font;
    }

    /**
     * Set the family name of the selected font.
     *
     * @param name
     *            the family name of the selected font.
     * @see getSelectedFontFamily
     **/
    public void setSelectedFontFamily(String name) {
        String[] names = getFontFamilies();
        for (int i = 0; i < names.length; i++) {
            if (names[i].toLowerCase().equals(name.toLowerCase())) {
                getFontFamilyList().setSelectedIndex(i);
                break;
            }
        }
        updateSampleFont();
    }

    /**
     * Set the style of the selected font.
     *
     * @param style
     *            the size of the selected font. <code>Font.PLAIN</code>, <code>Font.BOLD</code>,
     *            <code>Font.ITALIC</code>, or <code>Font.BOLD|Font.ITALIC</code>.
     * @see java.awt.Font#PLAIN
     * @see java.awt.Font#BOLD
     * @see java.awt.Font#ITALIC
     * @see #getSelectedFontStyle
     **/
    public void setSelectedFontStyle(int style) {
        for (int i = 0; i < JFontChooser.FONT_STYLE_CODES.length; i++) {
            if (JFontChooser.FONT_STYLE_CODES[i] == style) {
                getFontStyleList().setSelectedIndex(i);
                break;
            }
        }
        updateSampleFont();
    }

    /**
     * Set the size of the selected font.
     *
     * @param size
     *            the size of the selected font
     * @see #getSelectedFontSize
     **/
    public void setSelectedFontSize(int size) {
        String sizeString = String.valueOf(size);
        for (int i = 0; i < this.fontSizeStrings.length; i++) {
            if (this.fontSizeStrings[i].equals(sizeString)) {
                getFontSizeList().setSelectedIndex(i);
                break;
            }
        }
        getFontSizeTextField().setText(sizeString);
        updateSampleFont();
    }

    /**
     * Set the selected font.
     *
     * @param font
     *            the selected font
     * @see #getSelectedFont
     * @see java.awt.Font
     **/
    public void setSelectedFont(Font font) {
        setSelectedFontFamily(font.getFamily());
        setSelectedFontStyle(font.getStyle());
        setSelectedFontSize(font.getSize());
    }

    public String getVersionString() {
        return ("Version");
    }

    /**
     * Show font selection dialog.
     *
     * @param parent
     *            Dialog's Parent component.
     * @return OK_OPTION, CANCEL_OPTION or ERROR_OPTION
     * @see #OK_OPTION
     * @see #CANCEL_OPTION
     * @see #ERROR_OPTION
     **/
    public int showDialog(Component parent) {
        this.dialogResultValue = JFontChooser.ERROR_OPTION;
        JDialog dialog = createDialog(parent);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                JFontChooser.this.dialogResultValue = JFontChooser.CANCEL_OPTION;
            }
        });

        dialog.setVisible(true);
        dialog.dispose();
        dialog = null;

        return this.dialogResultValue;
    }

    protected class ListSelectionHandler implements ListSelectionListener {
        private JTextComponent textComponent;

        ListSelectionHandler(JTextComponent textComponent) {
            this.textComponent = textComponent;
        }

        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting() == false) {
                JList list = (JList) e.getSource();
                String selectedValue = (String) list.getSelectedValue();

                String oldValue = this.textComponent.getText();
                this.textComponent.setText(selectedValue);
                if (!oldValue.equalsIgnoreCase(selectedValue)) {
                    this.textComponent.selectAll();
                    this.textComponent.requestFocus();
                }

                updateSampleFont();
            }
        }
    }

    protected class TextFieldFocusHandlerForTextSelection extends FocusAdapter {
        private JTextComponent textComponent;

        public TextFieldFocusHandlerForTextSelection(JTextComponent textComponent) {
            this.textComponent = textComponent;
        }

        public void focusGained(FocusEvent e) {
            this.textComponent.selectAll();
        }

        public void focusLost(FocusEvent e) {
            this.textComponent.select(0, 0);
            updateSampleFont();
        }
    }

    protected class TextFieldKeyHandlerForListSelectionUpDown extends KeyAdapter {
        private JList targetList;

        public TextFieldKeyHandlerForListSelectionUpDown(JList list) {
            this.targetList = list;
        }

        public void keyPressed(KeyEvent e) {
            int i = this.targetList.getSelectedIndex();
            switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                i = this.targetList.getSelectedIndex() - 1;
                if (i < 0) {
                    i = 0;
                }
                this.targetList.setSelectedIndex(i);
                break;
            case KeyEvent.VK_DOWN:
                int listSize = this.targetList.getModel().getSize();
                i = this.targetList.getSelectedIndex() + 1;
                if (i >= listSize) {
                    i = listSize - 1;
                }
                this.targetList.setSelectedIndex(i);
                break;
            default:
                break;
            }
        }
    }

    protected class ListSearchTextFieldDocumentHandler implements DocumentListener {
        JList targetList;

        public ListSearchTextFieldDocumentHandler(JList targetList) {
            this.targetList = targetList;
        }

        public void insertUpdate(DocumentEvent e) {
            update(e);
        }

        public void removeUpdate(DocumentEvent e) {
            update(e);
        }

        public void changedUpdate(DocumentEvent e) {
            update(e);
        }

        private void update(DocumentEvent event) {
            String newValue = "";
            try {
                Document doc = event.getDocument();
                newValue = doc.getText(0, doc.getLength());
            }
            catch (BadLocationException e) {
                e.printStackTrace();
            }

            if (newValue.length() > 0) {
                int index = this.targetList.getNextMatch(newValue, 0, Position.Bias.Forward);
                if (index < 0) {
                    index = 0;
                }
                this.targetList.ensureIndexIsVisible(index);

                String matchedName = this.targetList.getModel().getElementAt(index).toString();
                if (newValue.equalsIgnoreCase(matchedName)) {
                    if (index != this.targetList.getSelectedIndex()) {
                        SwingUtilities.invokeLater(new ListSelector(index));
                    }
                }
            }
        }

        public class ListSelector implements Runnable {
            private int index;

            public ListSelector(int index) {
                this.index = index;
            }

            public void run() {
                ListSearchTextFieldDocumentHandler.this.targetList.setSelectedIndex(this.index);
            }
        }
    }

    protected class DialogOKAction extends AbstractAction {
        protected static final String ACTION_NAME = "OK";
        private JDialog               dialog;

        protected DialogOKAction(JDialog dialog) {
            this.dialog = dialog;
            putValue(Action.DEFAULT, DialogOKAction.ACTION_NAME);
            putValue(Action.ACTION_COMMAND_KEY, DialogOKAction.ACTION_NAME);
            putValue(Action.NAME, (DialogOKAction.ACTION_NAME));
        }

        public void actionPerformed(ActionEvent e) {
            JFontChooser.this.dialogResultValue = JFontChooser.OK_OPTION;
            this.dialog.setVisible(false);
        }
    }

    protected class DialogCancelAction extends AbstractAction {
        protected static final String ACTION_NAME = "Cancel";
        private JDialog               dialog;

        protected DialogCancelAction(JDialog dialog) {
            this.dialog = dialog;
            putValue(Action.DEFAULT, DialogCancelAction.ACTION_NAME);
            putValue(Action.ACTION_COMMAND_KEY, DialogCancelAction.ACTION_NAME);
            putValue(Action.NAME, (DialogCancelAction.ACTION_NAME));
        }

        public void actionPerformed(ActionEvent e) {
            JFontChooser.this.dialogResultValue = JFontChooser.CANCEL_OPTION;
            this.dialog.setVisible(false);
        }
    }

    protected JDialog createDialog(Component parent) {
        Frame frame = parent instanceof Frame ? (Frame) parent : (Frame) SwingUtilities.getAncestorOfClass(
            Frame.class, parent);
        JDialog dialog = new JDialog(frame, ("Select Font"), true);

        Action okAction = new DialogOKAction(dialog);
        Action cancelAction = new DialogCancelAction(dialog);

        JButton okButton = new JButton(okAction);
        okButton.setFont(JFontChooser.DEFAULT_FONT);
        JButton cancelButton = new JButton(cancelAction);
        cancelButton.setFont(JFontChooser.DEFAULT_FONT);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(2, 1));
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 10));

        ActionMap actionMap = buttonsPanel.getActionMap();
        actionMap.put(cancelAction.getValue(Action.DEFAULT), cancelAction);
        actionMap.put(okAction.getValue(Action.DEFAULT), okAction);
        InputMap inputMap = buttonsPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), cancelAction.getValue(Action.DEFAULT));
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), okAction.getValue(Action.DEFAULT));

        JPanel dialogEastPanel = new JPanel();
        dialogEastPanel.setLayout(new BorderLayout());
        dialogEastPanel.add(buttonsPanel, BorderLayout.NORTH);

        dialog.getContentPane().add(this, BorderLayout.CENTER);
        dialog.getContentPane().add(dialogEastPanel, BorderLayout.EAST);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        return dialog;
    }

    protected void updateSampleFont() {
        Font font = getSelectedFont();
        getSampleTextField().setFont(font);
    }

    protected JPanel getFontFamilyPanel() {
        if (this.fontNamePanel == null) {
            this.fontNamePanel = new JPanel();
            this.fontNamePanel.setLayout(new BorderLayout());
            this.fontNamePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            this.fontNamePanel.setPreferredSize(new Dimension(180, 130));

            JScrollPane scrollPane = new JScrollPane(getFontFamilyList());
            scrollPane.getVerticalScrollBar().setFocusable(false);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            JPanel p = new JPanel();
            p.setLayout(new BorderLayout());
            p.add(getFontFamilyTextField(), BorderLayout.NORTH);
            p.add(scrollPane, BorderLayout.CENTER);

            JLabel label = new JLabel(("Font Name"));
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setHorizontalTextPosition(SwingConstants.LEFT);
            label.setLabelFor(getFontFamilyTextField());
            label.setDisplayedMnemonic('F');

            this.fontNamePanel.add(label, BorderLayout.NORTH);
            this.fontNamePanel.add(p, BorderLayout.CENTER);

        }
        return this.fontNamePanel;
    }

    protected JPanel getFontStylePanel() {
        if (this.fontStylePanel == null) {
            this.fontStylePanel = new JPanel();
            this.fontStylePanel.setLayout(new BorderLayout());
            this.fontStylePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            this.fontStylePanel.setPreferredSize(new Dimension(140, 130));

            JScrollPane scrollPane = new JScrollPane(getFontStyleList());
            scrollPane.getVerticalScrollBar().setFocusable(false);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            JPanel p = new JPanel();
            p.setLayout(new BorderLayout());
            p.add(getFontStyleTextField(), BorderLayout.NORTH);
            p.add(scrollPane, BorderLayout.CENTER);

            JLabel label = new JLabel(("Font Style"));
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setHorizontalTextPosition(SwingConstants.LEFT);
            label.setLabelFor(getFontStyleTextField());
            label.setDisplayedMnemonic('Y');

            this.fontStylePanel.add(label, BorderLayout.NORTH);
            this.fontStylePanel.add(p, BorderLayout.CENTER);
        }
        return this.fontStylePanel;
    }

    protected JPanel getFontSizePanel() {
        if (this.fontSizePanel == null) {
            this.fontSizePanel = new JPanel();
            this.fontSizePanel.setLayout(new BorderLayout());
            this.fontSizePanel.setPreferredSize(new Dimension(70, 130));
            this.fontSizePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JScrollPane scrollPane = new JScrollPane(getFontSizeList());
            scrollPane.getVerticalScrollBar().setFocusable(false);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            JPanel p = new JPanel();
            p.setLayout(new BorderLayout());
            p.add(getFontSizeTextField(), BorderLayout.NORTH);
            p.add(scrollPane, BorderLayout.CENTER);

            JLabel label = new JLabel(("Font Size"));
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setHorizontalTextPosition(SwingConstants.LEFT);
            label.setLabelFor(getFontSizeTextField());
            label.setDisplayedMnemonic('S');

            this.fontSizePanel.add(label, BorderLayout.NORTH);
            this.fontSizePanel.add(p, BorderLayout.CENTER);
        }
        return this.fontSizePanel;
    }

    protected JPanel getSamplePanel() {
        if (this.samplePanel == null) {
            Border titledBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), ("Sample"));
            Border empty = BorderFactory.createEmptyBorder(5, 10, 10, 10);
            Border border = BorderFactory.createCompoundBorder(titledBorder, empty);

            this.samplePanel = new JPanel();
            this.samplePanel.setLayout(new BorderLayout());
            this.samplePanel.setBorder(border);

            this.samplePanel.add(getSampleTextField(), BorderLayout.CENTER);
        }
        return this.samplePanel;
    }

    protected JTextField getSampleTextField() {
        if (this.sampleText == null) {
            Border lowered = BorderFactory.createLoweredBevelBorder();

            this.sampleText = new JTextField(("AaBbYyZz"));
            this.sampleText.setBorder(lowered);
            this.sampleText.setPreferredSize(new Dimension(300, 100));
        }
        return this.sampleText;
    }

    protected String[] getFontFamilies() {
        if (this.fontFamilyNames == null) {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            this.fontFamilyNames = env.getAvailableFontFamilyNames();
        }
        return this.fontFamilyNames;
    }

    protected String[] getFontStyleNames() {
        if (this.fontStyleNames == null) {
            int i = 0;
            this.fontStyleNames = new String[4];
            this.fontStyleNames[i++] = ("Plain");
            this.fontStyleNames[i++] = ("Bold");
            this.fontStyleNames[i++] = ("Italic");
            this.fontStyleNames[i++] = ("BoldItalic");
        }
        return this.fontStyleNames;
    }
}
