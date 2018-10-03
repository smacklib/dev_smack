package org.jdesktop.application.converters;

import java.awt.Event;
import java.awt.Toolkit;
import java.util.regex.Pattern;

import javax.swing.KeyStroke;

import org.jdesktop.application.ResourceConverter;
import org.jdesktop.application.ResourceMap;

public class KeyStrokeStringConverter extends ResourceConverter {
    private static final String KEYWORD_SHORTCUT = "shortcut";
    private static final String KEYWORD_META = "meta";
    private static final String KEYWORD_CONTROL = "control";

    private static final String REPLACE = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ==
        Event.META_MASK ? KEYWORD_META : KEYWORD_CONTROL;
    private static final Pattern PATTERN = Pattern.compile(KEYWORD_SHORTCUT);

    public KeyStrokeStringConverter() {
        super(KeyStroke.class);
    }

    @Override
    public Object parseString(String s, ResourceMap ignore) {
        if (s.contains(KEYWORD_SHORTCUT)) {
//            int k = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
            s = PATTERN.matcher(s).replaceFirst(REPLACE);
        }
        return KeyStroke.getKeyStroke(s);
    }
}
