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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.smack.util.FormattedEx;
import org.smack.util.JavaUtil;
import org.smack.util.Pair;
import org.smack.util.ServiceManager;
import org.smack.util.converters.StringConverter;

/**
 * A map holding all resources defined in the resources for
 * the passed class.  Resources for a class {@code foo.bar.Elk} are
 * defined in the property file {@code Elk.property} in the same package.
 * <p>
 * A property named "color" in the above resource file is found
 * by the key 'color' and the key 'Elk.color' or simply 'color'.
 * </p>
 *
 * @author Michael Binz
 */
@SuppressWarnings("serial")
public class ResourceMap extends HashMap<String, String>
{
    /**
     * The class corresponding to this map.  Never {@code null}.
     */
    private final Class<?> _class;

    /**
     * The url of the resource package that contains {@link #_class}. In
     * an empty ResourceMap this may be {@code null}.
     */
    private final URL _url;

    /**
     * Populates the passed Map with the preprocessed values from the passed
     * resource bundle.
     *
     * @param bundle The resource bundle whose entries are processed.
     * @return The requested resource bundle or {@code null} if the bundle
     * did not exist.
     */
    private Map<String, String> preprocessResourceBundle(
            URL url, ResourceBundle bundle ) throws Exception
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
                value = evaluateStringExpression2(
                        key,
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
     * Matches '${' if not preceded by an '\' and not followed by any '}'.
     * For example "...${honkytonk".  This is an error match.
     */
    private final static Pattern UNCLOSED_MACRO = Pattern.compile("(?<!\\\\)\\$\\{(.*?)[^\\}]");

    /**
     * Matches '${group}' if not preceded by an '\'.
     * For example "...${honkytonk}".  This is a valid macro.
     */
    private final static Pattern VALID_MACRO = Pattern.compile("(?<!\\\\)\\$\\{(.*?)\\}");

    /**
     * Matches '\${'.  This matches escaped macros.
     */
    private final static Pattern ESCAPED_MACRO = Pattern.compile("\\\\\\$\\{");

    /**
     * Matches a string containing a whitespace.  To be used with
     * String.match().
     */
    private final static String CONTAINS_WHITESPACE = ".*\\s+.*";

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
    private String evaluateStringExpression2(
            String key,
            String expr,
            ResourceBundle env ) throws Exception
    {
        // Shortcut.  Anything to do?
        if ( !expr.contains( "${" ) )
            return expr;
        // Special case.
        if ( expr.trim().equals( "${null}" ) )
            return null;
        // Ensure that we have no unclosed macros.
        if ( UNCLOSED_MACRO.matcher( expr ).matches() )
        {
            throw new FormattedEx( "No closing brace '%s' @ %s @ %s",
                    key,
                    _class.getSimpleName(),
                    _url );
        }

        String result;

        {
            final StringBuilder b = new StringBuilder(expr.length());
            // Match ${word} only if it is not after a backslash.
            final Matcher m = VALID_MACRO.matcher(expr);

            while (m.find())
            {
                var matched =
                        m.group( 1 ).strip();
                if ( matched.matches( CONTAINS_WHITESPACE ) )
                    throw new FormattedEx( "Invalid name '%s:${%s}' @ %s @ %s",
                            key,
                            matched,
                            _class.getSimpleName(),
                            _url );

                if ( !env.containsKey( matched ) )
                {
                    throw new FormattedEx( "No value for '%s:${%s}' @ %s @ %s",
                            key,
                            matched,
                            _class.getSimpleName(),
                            _url );
                }

                var replacement =
                        evaluateStringExpression2(
                                key,
                                env.getString( matched ),
                                env );

                m.appendReplacement( b, replacement );
            }

            result = m.appendTail( b ).toString();
        }

        return ESCAPED_MACRO.matcher(result).replaceAll( "\\$\\{" );
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
    private static Pair<URL,ResourceBundle> getClassResourcesImpl( Class<?> cl )
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

    /**
     * Get a the resource map for a class.
     *
     * @param cl The class.
     * @return The corresponding resource map or {@code null} if no
     * resources were found.
     */
    public static ResourceMap getResourceMapExt( Class<?> cl )
    {
        try
        {
            String name = cl.getName();

            var resourceBundle =
                    ResourceBundle.getBundle( name, cl.getModule() );
            var url =
                    getResourceContainer( cl );

            return new ResourceMap( cl, url, resourceBundle );
        }
        catch ( MissingResourceException e )
        {
            return new ResourceMap( cl );
        }
    }

    /**
     * Create an instance.
     *
     * @param cl The class for which the resource map is created.
     * @param url The resource url of the class.
     * @param rb The resource bundle of the class.
     */
    private ResourceMap( Class<?> cl, URL url, ResourceBundle rb )
    {
        _class =
                Objects.requireNonNull( cl );
        _url =
                Objects.requireNonNull( url );
        try
        {
            Map<String, String> bundle =
                    preprocessResourceBundle(
                            url, rb );

            String classPrefix =
                    _class.getSimpleName() + ".";

            for ( String ckey : bundle.keySet() )
            {
                String value =
                        bundle.get( ckey );

                if ( ckey.equals( classPrefix ) )
                    throw new FormattedEx( "Invalid key '%s' @ %s @ %s",
                            classPrefix,
                            _class.getSimpleName(),
                            _url );

                // Register the qualified and the unqualified name.

                put( ckey, value );
                if ( ckey.startsWith( classPrefix ) )
                {
                    put(
                            ckey.substring( classPrefix.length() ),
                            value );
                }
                else
                {
                    put(
                            classPrefix + ckey,
                            value );
                }
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    /**
     * Create an empty instance.
     *
     * @param cl The class for which the resource map is created.
     */
    private ResourceMap( Class<?> cl )
    {
        _class =
                Objects.requireNonNull( cl );
        _url = null;
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
