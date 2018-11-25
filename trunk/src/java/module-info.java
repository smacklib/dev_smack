/**
 * $Id$
 */
module framework.smack {
	requires java.desktop;
	requires java.jnlp;
	requires java.logging;
	requires java.prefs;
	requires javafx.base;

	uses org.jdesktop.util.ResourceConverter;
	uses org.jdesktop.util.ResourceConverterExtension;

	exports org.jdesktop.application;
	exports org.jdesktop.beans;
	exports org.jdesktop.smack.util;
	exports org.jdesktop.util;
}
