/*
 * $Id$
 * Released under Gnu Public License
 * Copyright © 2019 Michael G. Binz
 */
module framework.smack {
    requires transitive java.desktop;
    requires java.logging;
    requires java.prefs;
    requires transitive javafx.base;
    requires javafx.controls;
    requires transitive javafx.graphics;

    uses org.jdesktop.util.ResourceConverter;
    uses org.jdesktop.util.ResourceConverterExtension;

    // All converters have to be registered here.  The ServiceLoader
    // uses this list. The ServiceLoader configuration file is not
    // needed with the module system.
    provides org.jdesktop.util.ResourceConverterExtension with
        org.jdesktop.util.converters.PrimitivesBundle;
    provides org.jdesktop.util.ResourceConverter with
        org.jdesktop.util.converters.StringStringConverter,
        org.jdesktop.util.converters.StringStringBuilderConverter,
        org.jdesktop.util.converters.StringArrayRc,
        org.jdesktop.util.converters.UrlStringConverter,
        org.jdesktop.util.converters.UriStringConverter,
        org.jdesktop.util.converters.ColorStringConverter,
        org.jdesktop.util.converters.DimensionStringConverter,
        org.jdesktop.util.converters.FontStringConverter,
        org.jdesktop.util.converters.FxImageConverter,
        org.jdesktop.util.converters.IconStringConverter,
        org.jdesktop.util.converters.ImageStringConverter,
        org.smack.fx.converters.KeyCombinationConverter,
        org.jdesktop.util.converters.Point2dStringConverter,
        org.jdesktop.util.converters.PointStringConverter;

    exports org.jdesktop.application;
    exports org.jdesktop.beans;
    exports org.jdesktop.util;
    exports org.jdesktop.util.converters;
    exports org.smack.fx;
    exports org.smack.util;
    exports org.smack.util.collections;
}
