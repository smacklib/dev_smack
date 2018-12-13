/* $Id$
 *
 * Copyright © 2014 Michael G. Binz
 */
package org.jdesktop.smack.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Resource Bundle helpers.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class ResourceUtils
{
    private static final String RESOURCE_PACKAGE_NAME = "resources";

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
     * resources. The class is used in {@link ResourceUtils#getClassResources(Class)}
     * as the result.
     */
    private static class NamedResourceBundle extends ResourceBundle
    {
        private final String _name;

        public NamedResourceBundle( String name, ResourceBundle parent )
        {
            this.parent = parent;
            this._name = name;
        }

        @Override
        public String getBaseBundleName()
        {
            return _name;
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
     * Populates the passed Map with the preprocessed values from the named
     * resource bundle.
     *
     * @param bundleName The resource bundle whose entries are processed.
     * @param loc The locale to use.
     * @param cl The classloader used to resolve the resource bundle.
     * @return The requested resource bundle or {@code null} if the bundle
     * did not exist.
     */
    public static Map<String, String> getPreprocessedResourceBundle(
            String bundleName, Locale loc, ClassLoader cl )
    {
        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( bundleName, loc,
                    cl ); /*,
                    // We only want property resource bundles.
                    Control.getControl( Control.FORMAT_PROPERTIES ) ); */

            return preprocessResourceBundle( bundle );
        }
        catch ( MissingResourceException ignore )
        {
            return null;
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
    public static Map<String, String> preprocessResourceBundle(
            ResourceBundle bundle )
    {
        try
        {
            Map<String, String> result = new HashMap<>();

            // Preprocessing is currently limited to a single
            // resource bundle. A broader context may be
            // pretty confusing. michab.

            for ( String key : bundle.keySet() )
            {
                result.put( key,
                        // Note that we perform all preprocessing for the
                        // string values in the resource bundle here.
                        // Later stages of processing see only the evaluated
                        // values.
                        evaluateStringExpression(
                                bundle.getString( key ),
                                bundle ) );
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
    public static ResourceBundle getClassResources( Class<?> c )
    {
        String name = c.getName();

        int lastDotIdx = name.lastIndexOf( '.' );

        if ( lastDotIdx > 0 )
        {
            StringBuilder sb = new StringBuilder( name ) ;
            sb.insert( lastDotIdx, "." + RESOURCE_PACKAGE_NAME );
            name = sb.toString();
        }

        try
        {
            return new NamedResourceBundle(
                    name,
                    ResourceBundle.getBundle( name ) );
        }
        catch ( MissingResourceException e )
        {
            return null;
        }
    }

    /**
     * Get class specific resources. If the passed classes full
     * name is "org.good.Class" then this operation loads
     * the resource bundle "org/good/resources/Class.properties".
     *
     * @param c The class for which the resources should be loaded.
     * @return A map holding the resource strings. If no resources were
     * found, then the result is an empty map.
     */
    public static Map<String,String> getClassResourceMap( Class<?> c )
    {
        ResourceBundle bundle = getClassResources( c );

        if ( bundle == null )
            return Collections.emptyMap();

        return bundle.keySet().stream().collect( Collectors.toMap(
                s -> s,
                s -> bundle.getString( s ) ) );
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
        String packageName =
                RESOURCE_PACKAGE_NAME + "/" + name;
        InputStream result =
                c1ass.getResourceAsStream( packageName );

        if ( result == null )
            throw new RuntimeException(
                    "Resource not found: " + packageName );

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
        InputStream is = getResourceAsStream(
                c1ass,
                name );

        // Note Java 9 offers
        // byte[] array = InputStream.readAllBytes();
        ByteArrayOutputStream result =
                new ByteArrayOutputStream();

        try
        {
            while ( true )
            {
                int c = is.read();

                if ( c == -1 )
                    return result.toByteArray();

                result.write( c );
            }
        }
        catch ( Exception e )
        {
            FileUtils.forceClose( is );
            throw new RuntimeException(
                    "Failed reading resource: " + name, e );
        }
    }

    /**
     * Forbid instantiation.
     */
    private ResourceUtils()
    {
        throw new AssertionError();
    }
}
