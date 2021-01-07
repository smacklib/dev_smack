/* $Id$
 *
 * Copyright © 2014 Michael G. Binz
 */
package org.smack.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

import org.smack.util.JavaUtil;
import org.smack.util.StringUtil;

/**
 * Resource Bundle helpers.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class ResourceUtil
{
    /**
     * Unchecked exception thrown when resource lookup
     * fails, for example because string conversion fails.  This is
     * not a missing resource exception.  If a resource isn't defined
     * for a particular key, no exception is thrown.
     */
    @SuppressWarnings("serial")
    private static class LookupException extends RuntimeException {
        /**
         * Constructs an instance of this class with some useful information
         * about the failure.
         *
         * @param msg the detail message
         * @param type the type of the resource
         * @param key the name of the resource
         */
        public LookupException(String msg, String key, Class<?> type) {
            super(String.format("%s: resource %s, type %s", msg, key, type));
        }
    }

    /**
     * Generated. Fixes the fact that {@link ResourceBundle#getBaseBundleName()}
     * returns null in modular applications.  This bites us when injecting
     * resources. The class is used in {@link ResourceUtil#getClassResources(Class)}
     * as the result.
     */
    static class NamedResourceBundle extends ResourceBundle
    {
        private final String _name;

        private final URL _url;

        public NamedResourceBundle( URL url, String name, ResourceBundle parent )
        {
            this.parent = parent;
            this._name = name;
            _url = url;
        }

        @Override
        public String getBaseBundleName()
        {
            var bbn = super.getBaseBundleName();

            if ( StringUtil.hasContent( bbn ) )
                return bbn;

            return _name;
        }

        public URL getUrl()
        {
            return _url;
        }

        @Override
        protected Object handleGetObject( String key )
        {
            return null;
        }

        @Override
        public Enumeration<String> getKeys()
        {
            return parent.getKeys();
        }
    }

    /**
     * Populates the passed Map with the preprocessed values from the passed
     * resource bundle.
     *
     * @param bundle The resource bundle whose entries are processed.
     * @return The requested resource bundle or {@code null} if the bundle
     * did not exist.
     */
    static Map<String, String> preprocessResourceBundleN(
            NamedResourceBundle bundle )
    {
        try
        {
            Map<String, String> result = new HashMap<>();

            // Preprocessing is currently limited to a single
            // resource bundle. A broader context may be
            // pretty confusing. michab.

            var urlPrefix = bundle.getUrl().toExternalForm();

            var lastSlash = urlPrefix.lastIndexOf( '/' );
            if ( lastSlash > 0 )
                urlPrefix = urlPrefix.substring( 0, lastSlash+1 );

            JavaUtil.Assert( urlPrefix.endsWith( "/" ) );

            for ( String key : bundle.keySet() )
            {
                var value = bundle.getString( key );

                if ( value.startsWith( "@" ) )
                {
                    value =
                            urlPrefix +
                            value.substring( 1 );
                }
                else
                {
                    value = evaluateStringExpression(
                            value,
                            bundle );
                }

                result.put(
                        key,
                        value );
            }

            return result;
        }
        catch ( MissingResourceException ignore )
        {
            return null;
        }
    }

    /**
     * Evaluates a string expression in the context of a passed environment used
     * to look up token definitions.
     *
     * Given the following resources:
     *
     * <pre><code>
     * hello = Hello
     * world = World
     * place = ${world}
     * </code></pre>
     *
     * The value of evaluateStringExpression("${hello} ${place}") would be
     * "Hello World". The value of ${null} is null.
     *
     * @param expr The expression to evaluate.
     * @param env The resource bundle to use for token look-up.
     * @return The evaluated expression.
     */
    private static String evaluateStringExpression(
            String expr,
            ResourceBundle env )
    {
        if ( !expr.contains( "${" ) )
            return expr;

        if ( expr.trim().equals( "${null}" ) )
            return null;

        StringBuilder result = new StringBuilder();
        int i0 = 0, i1;
        while ( (i1 = expr.indexOf( "${", i0 )) != -1 )
        {
            if ( (i1 == 0) || ((i1 > 0) && (expr.charAt( i1 - 1 ) != '\\')) )
            {
                int i2 = expr.indexOf( "}", i1 );
                if ( (i2 != -1) && (i2 > i1 + 2) )
                {
                    result.append( expr.substring( i0, i1 ) );
                    String k = expr.substring( i1 + 2, i2 );

                    if ( env.containsKey( k ) )
                    {
                        String resolved = env.getObject( k ).toString();
                        // The resolved string is again evaluated.
                        result.append( evaluateStringExpression(
                                resolved,
                                env ) );
                    }
                    else
                    {
                        String msg = String.format(
                                "no value for \"%s\" in \"%s\"", k, expr );
                        throw new LookupException( msg, k, String.class );
                    }

                    i0 = i2 + 1; // skip trailing "}"
                }
                else
                {
                    String msg =
                            String.format( "no closing brace in \"%s\"", expr );
                    throw new LookupException( msg, "<not found>", String.class );
                }
            }
            else
            { // we've found an escaped variable - "\${"
                result.append( expr.substring( i0, i1 - 1 ) );
                result.append( "${" );
                i0 = i1 + 2; // skip past "${"
            }
        }
        result.append( expr.substring( i0 ) );
        return result.toString();
    }

    private static boolean stringsValid( String ... strings )
    {
        for ( var c : strings )
        {
            if ( StringUtil.isEmpty( c ) )
                return false;
        }
        return true;
    }

    static URL findResourceBundle( String baseName, Module module )
    {
        if ( StringUtil.isEmpty( baseName ) )
            throw new IllegalArgumentException( "basename" );
        Objects.requireNonNull( module );

        baseName = baseName.replace( ".", "/" );

        Locale locale = Locale.getDefault();

        var language = locale.getLanguage();
        var script = locale.getScript();
        var country = locale.getCountry();
        var variant = locale.getVariant();

        ArrayList<String> toCheck = new ArrayList<String>();

//        baseName + "_" + language + "_" + script + "_" + country + "_" + variant
        if ( stringsValid( language, script, country, variant ) )
            toCheck.add( StringUtil.concatenate(
                    "_",
                    new String[] {baseName, language, script, country, variant } ) );

//        baseName + "_" + language + "_" + script + "_" + country
        if ( stringsValid( language, script, country ) )
            toCheck.add( StringUtil.concatenate(
                    "_",
                    new String[] {baseName, language, script, country } ) );
//        baseName + "_" + language + "_" + script
        if ( stringsValid( language, script ) )
            toCheck.add( StringUtil.concatenate(
                    "_",
                    new String[] {baseName, language, script } ) );

//        baseName + "_" + language + "_" + country + "_" + variant
        if ( stringsValid( language, country, variant ) )
            toCheck.add( StringUtil.concatenate(
                    "_",
                    new String[] {baseName, language, country, variant } ) );
//        baseName + "_" + language + "_" + country
        if ( stringsValid( language, country ) )
            toCheck.add( StringUtil.concatenate(
                    "_",
                    new String[] {baseName, language, country } ) );

//        baseName + "_" + language
        if ( stringsValid( language ) )
            toCheck.add( StringUtil.concatenate(
                    "_",
                    new String[] {baseName, language } ) );

        toCheck.add( baseName );

        for ( var c : toCheck )
        {
            var name = c + ".properties";

            var url = module.getClassLoader().getResource( name );
            if ( url != null )
                return url;
        }

        return null;
    }

    /**
     * Get class specific resources. If the passed classes full
     * name is "org.good.Class" then this operation loads
     * the resource bundle "org/good/resources/Class.properties".
     * Prefer {@link #getClassResourceMap(Class)}.
     *
     * @param c The class for which the resources should be loaded.
     * @return A ResourceBundle. If no resource bundle was found
     * for the passed class, then the result is {@code null}.
     */
    static NamedResourceBundle getClassResourcesImpl( Class<?> c )
    {
        String name = c.getName();

        try
        {
            return new NamedResourceBundle(
                    findResourceBundle( name, c.getModule() ),
                    name,
                    ResourceBundle.getBundle( name, c.getModule() ) );
        }
        catch ( MissingResourceException e )
        {
            return null;
        }
    }

    /**
     * Load a named resource from the resource package of the passed
     * class.
     *
     * @param c1ass The class used to locate the resource package.
     * @param name The name of the resource.
     * @return The resource InputStream.  Note that this must be
     * closed after use. Never null, throws a RuntimeException if
     * the resource was not found.
     */
    public static InputStream getResourceAsStream(
            Class<?> c1ass,
            String name )
    {
        InputStream result =
                c1ass.getResourceAsStream( name );

        if ( result == null )
            throw new RuntimeException(
                    "Resource not found: " + name );

        return result;
    }

    /**
     * Load a named resource from the resource package of the passed
     * class.
     *
     * @param c1ass The class used to locate the resource package.
     * @param name The name of the resource.
     * @return The resource content. Never null, throws a
     * RuntimeException if the resource was not found.
     */
    public static byte[] loadResource(
            Class<?> c1ass,
            String name )
    {
        try ( InputStream is = getResourceAsStream(
                c1ass,
                name ) )
        {
            return is.readAllBytes();
        }
        catch ( IOException e )
        {
            throw new IllegalArgumentException( name, e );
        }
    }

    /**
     * Forbid instantiation.
     */
    private ResourceUtil()
    {
        throw new AssertionError();
    }
}
