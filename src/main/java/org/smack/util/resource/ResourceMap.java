/* $Id$
 *
 * Smack Utilities.
 *
 * Copyright © 2017-21 Michael G. Binz
 */
package org.smack.util.resource;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

import org.smack.util.JavaUtil;
import org.smack.util.Pair;
import org.smack.util.ServiceManager;
import org.smack.util.converters.StringConverter;


/**
 * A map holding all resources defined in the resources for
 * the passed class.  Resources for a class foo.bar.Elk are
 * defined in the property file foo.bar.resources.Elk.
 * <p>
 * A property named "color" in the above resource file is found
 * by the key 'color' and the key 'Elk.color'.
 * </p>
 *
 * @author Michael Binz
 */
@SuppressWarnings("serial")
public class ResourceMap extends HashMap<String, String>
{
    private final Class<?> _class;

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
     * Get a the resource map for a class.
     *
     * @param cl The class.
     * @return The corresponding resource map or {@code null} if no
     * resources were found.
     */
    public static ResourceMap getResourceMap( Class<?> cl )
    {
        Pair<URL, ResourceBundle> crb =
                getClassResourcesImpl(
                        Objects.requireNonNull( cl ) );
        if ( crb == null )
            return null;

        return new ResourceMap( cl, crb.left, crb.right );
    }

    private ResourceMap( Class<?> cl, URL url, ResourceBundle rb )
    {
        _class =
                Objects.requireNonNull( cl );
        Map<String, String> bundle =
                preprocessResourceBundle(
                        url, rb );

        String classPrefix =
                _class.getSimpleName() + ".";

        for ( String ck : bundle.keySet() )
        {
            String value =
                    bundle.get( ck );

            if ( ck.equals( classPrefix ) )
                throw new AssertionError( "Invalid property name: " + classPrefix );

            put( ck, value );
            if ( ck.startsWith( classPrefix ) )
            {
                put(
                        ck.substring( classPrefix.length() ),
                        value );
            }
            else
            {
                put(
                        classPrefix + ck,
                        value );
            }
        }
    }

    /**
     * @return The class that this resource map holds resources for.
     */
    public Class<?> getResourceClass()
    {
        return _class;
    }

    /**
     * Convert the passed key to a target type.
     *
     * @param <T> The expected target type.
     * @param targetType The expected result type.
     * @param key The property key to convert.
     * @return The conversion result.
     */
    public <T> T getAs( String key, Class<T> targetType )
        throws Exception
    {
        String resolved =
                get( key );
        if ( resolved == null )
            throw new IllegalArgumentException( "Key not found: " + key );

        var converter = ServiceManager.getApplicationService(
                StringConverter.class );

        return converter.convert(
                targetType,
                resolved );
    }
}
