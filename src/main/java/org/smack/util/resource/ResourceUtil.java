/* $Id$
 *
 * Copyright © 2014 Michael G. Binz
 */
package org.smack.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.smack.util.JavaUtil;
import org.smack.util.Pair;

/**
 * Resource Bundle helpers.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public final class ResourceUtil
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
     * Populates the passed Map with the preprocessed values from the passed
     * resource bundle.
     *
     * @param bundle The resource bundle whose entries are processed.
     * @return The requested resource bundle or {@code null} if the bundle
     * did not exist.
     */
    static Map<String, String> preprocessResourceBundle(
            URL url, ResourceBundle bundle )
    {
        Map<String, String> result = new HashMap<>();

        for ( String key : bundle.keySet() )
        {
            var value = bundle.getString( key );

            if ( value.startsWith( "@" ) )
            {
                var urlPrefix = url.toExternalForm();

                JavaUtil.Assert( urlPrefix.endsWith( "/" ) );

                value =
                        urlPrefix +
                        // Remove the '@'.
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
     * Get the URL of the resource position of the passed class.  The URL
     * always ends in a '/'.
     *
     * @param cl The class whose resource position is sought.
     * @return The resource position or {@code null} if this could not be
     * detected.
     */
    private static URL getResourceContainer( Class<?> cl )
    {
        CodeSource codeSource =
                cl
                .getProtectionDomain()
                .getCodeSource();

        if ( codeSource == null )
            return null;

        URL classContainerUrl =
                codeSource.getLocation();

        if ( classContainerUrl == null )
            return null;

        String classContainer =
                classContainerUrl.toExternalForm();

        if ( classContainer.endsWith( ".jar" ) )
            classContainer = String.format( "jar:%s!", classContainer );
        if ( ! classContainer.endsWith( "/" ) )
            classContainer += "/";

        classContainer =
                classContainer +
                cl.getPackageName().replace( ".", "/" ) +
                "/";

        try
        {
            return new URL( classContainer );
        }
        catch ( MalformedURLException e )
        {
            System.out.println( e.getMessage() );
            return null;
        }
    }

    /**
     * Get class specific resources. If the full name of the passed
     * class is "org.good.Class" then this operation loads the resource
     * bundle "org/good/Class.properties".
     * Prefer {@link #getClassResourceMap(Class)}.
     *
     * @param cl The class for which the resources should be loaded.
     * @return A ResourceBundle and its corresponding URL.  If no resource bundle was found
     * for the passed class, then the result is {@code null}.
     */
    static Pair<URL,ResourceBundle> getClassResourcesImpl( Class<?> cl )
    {
        try
        {
            String name = cl.getName();

            var resourceBundle =
                    ResourceBundle.getBundle( name, cl.getModule() );
            var url =
                    getResourceContainer( cl );

            if ( url == null )
                return null;

            return new Pair<>(
                    url,
                    resourceBundle );
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
     * @param cl The class used to locate the resource package.
     * @param name The name of the resource.
     * @return The resource content. Never null, throws a
     * RuntimeException if the resource was not found.
     */
    public static byte[] loadResource(
            Class<?> cl,
            String name )
    {
        try ( InputStream is = cl.getResourceAsStream( name ) )
        {
            if ( is == null )
                throw new RuntimeException(
                        "Resource not found: " + name );

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
