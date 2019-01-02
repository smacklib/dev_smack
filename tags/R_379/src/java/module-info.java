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

	exports org.jdesktop.application;
	exports org.jdesktop.beans;
	exports org.jdesktop.smack.util;
    exports org.jdesktop.util;
    exports org.jdesktop.util.converters;
    exports org.smack.fx;
    exports org.smack.util;
}
