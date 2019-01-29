import org.jdesktop.util.converters.BooleanStringConverter;
import org.jdesktop.util.converters.FxImageConverter;

/**
 * $Id$
 */
module framework.smack {
	requires java.desktop;
	requires java.jnlp;
	requires java.logging;
	requires java.prefs;
	requires javafx.base;
	requires javafx.controls;

	uses org.jdesktop.util.ResourceConverter;
	uses org.jdesktop.util.ResourceConverterExtension;

    provides org.jdesktop.util.ResourceConverter with
        BooleanStringConverter,
        org.jdesktop.util.converters.ByteStringConverter,
        FxImageConverter;

	exports org.jdesktop.application;
	exports org.jdesktop.beans;
	exports org.jdesktop.smack.util;
    exports org.jdesktop.util;
    exports org.jdesktop.util.converters;
    exports org.smack.fx;
    exports org.smack.util;
}
