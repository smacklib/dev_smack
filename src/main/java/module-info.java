/*
 * $Id$
 * Released under Gnu Public License
 * Copyright © 2019 Michael G. Binz
 */
module framework.smack {
    requires transitive java.desktop;
    requires java.logging;
    requires java.prefs;

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
        org.jdesktop.util.converters.IconStringConverter,
        org.jdesktop.util.converters.ImageStringConverter,
        org.jdesktop.util.converters.Point2dStringConverter,
        org.jdesktop.util.converters.PointStringConverter;

    uses org.smack.util.resource.ResourceConverter;
    uses org.smack.util.resource.ResourceConverterExtension;

    // All converters have to be registered here.  The ServiceLoader
    // uses this list. The ServiceLoader configuration file is not
    // needed with the module system.
    provides org.smack.util.resource.ResourceConverterExtension with
        org.smack.util.converters.PrimitivesBundle;
    provides org.smack.util.resource.ResourceConverter with
        org.smack.util.converters.StringStringConverter,
        org.smack.util.converters.StringStringBuilderConverter,
        org.smack.util.converters.StringArrayRc,
        org.smack.util.converters.UrlStringConverter,
        org.smack.util.converters.UriStringConverter,
        org.smack.util.converters.ColorStringConverter,
        org.smack.util.converters.DimensionStringConverter,
        org.smack.util.converters.FontStringConverter,
        org.smack.util.converters.IconStringConverter,
        org.smack.util.converters.ImageStringConverter,
        org.smack.util.converters.Point2dStringConverter,
        org.smack.util.converters.PointStringConverter;

    exports org.jdesktop.application;
    exports org.jdesktop.util;
    exports org.jdesktop.util.converters;
    exports org.smack.application;
    exports org.smack.util;
    exports org.smack.util.collections;
    exports org.smack.util.resource;
    exports org.smack.util.io;
    exports org.smack.util.xml;

    // Needed for testing.
    opens org.smack.util.resource;
    opens org.smack;
}
