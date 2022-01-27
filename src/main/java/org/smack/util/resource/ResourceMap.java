/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2017-2022 Michael G. Binz
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
import java.util.logging.Logger;
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
 * by the key 'color' and the key 'Elk.color'.
 * </p>
 *
 * @author Michael Binz
 */
@SuppressWarnings("serial")
public class ResourceMap extends HashMap<String, String>
{
    private static final Logger LOG =
            Logger.getLogger( ResourceMap.class.getName() );

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
     * Creates a map containing preprocessed values from the passed
     * ResourceBundle.
     *
     * @param url The resource file URL of the ResourceBundle.
     * @param bundle The resource bundle whose entries are processed.
     * @return A resource map with preprocessed values.
     */
    private Map<String, String> preprocess(
            URL url,
            ResourceBundle bundle ) throws Exception
    {
        Objects.requireNonNull(
                url );
        Objects.requireNonNull(
                bundle );

        final var urlPrefix =
                url.toExternalForm();
        JavaUtil.Assert( urlPrefix.endsWith( "/" ) );

        Map<String, String> result = new HashMap<>();

        for ( String key : bundle.keySet() )
        {
            var value = evalExpression(
                    key,
                    bundle.getString( key ),
                    bundle );

            // Value may be null because of ${null}.
            if ( value != null && value.startsWith( "@" ) )
            {
                value =
                        urlPrefix +
                        // Remove the '@'.
                        value.substring( 1 );
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
     * @param key The key to evaluate. This is used for syntax checks.
     * @param expr The expression to evaluate.
     * @param env The resource bundle to use for token look-up.
     * @return The evaluated expression.
     */
    private String evalExpression(
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

                if ( matched.contentEquals( key ) )
                    throw new FormattedEx( "Recursion detected '%s:${%s}' @ %s @ %s",
                            key,
                            matched,
                            _class.getSimpleName(),
                            _url );

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
                        evalExpression(
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
            LOG.info( e::getMessage );
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
     * @return The corresponding resource map.  If no resources
     * were found, the map is empty.
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
                    preprocess(
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
                else if ( ! ckey.contains( "." ) )
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
     * @param key The property key to convert.
     * @param targetType The expected result type.
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

    /**
     * Convert the passed key to a target type.
     *
     * @param <T> The expected target type.
     * @param key The property key to convert.
     * @param targetType The expected result type.
     * @param orDefault The value to return if the key is not found.
     * @return The conversion result.
     */
    public <T> T getAs( String key, Class<T> targetType, T orDefault )
    {
        if ( ! containsKey( key ) )
            return orDefault;

        String resolved =
                get( key );

        try
        {
            var converter = ServiceManager.getApplicationService(
                    StringConverter.class );

            return converter.convert(
                    targetType,
                    resolved );
        }
        catch ( Exception e )
        {
            return orDefault;
        }
    }

    /**
     * If no arguments are specified, return the String value
     * of the resource named <tt>key</tt>.  This is
     * equivalent to calling <tt>getObject(key, String.class)</tt>
     * If arguments are provided, then the type of the resource
     * named <tt>key</tt> is assumed to be
     * {@link String#format(String, Object...) format} string,
     * which is applied to the arguments if it's non null.
     * For example, given the following resources
     * <pre>
     * hello = Hello %s
     * </pre>
     * then the value of <tt>getString("hello", "World")</tt> would
     * be <tt>"Hello World"</tt>.
     *
     * @param key The resource key. null is not allowed.
     * @param args
     * @return the formatted String value of the resource named <tt>key</tt>.
     * @see String#format(String, Object...)
     */
    public String getFormatted(String key, Object... args)
    {
        Objects.requireNonNull( key );

        if ( ! containsKey( key ) )
            return null;

        var value = get( key );

        if ( JavaUtil.isEmptyArray( args ) )
            return value;

        return String.format( value, args );
    }

    /**
     * Get the value to which the key is mapped.  The operation propagates
     * along the superclass chain.
     *
     * @return The value associated with key, {@code null} if no mapping was
     * found.
     */
    @Override
    public String get( Object key )
    {
        if ( containsKey( key ) )
            return super.get( key );

        if ( _class.getSuperclass() == null )
            return null;

        return getResourceMapExt( _class.getSuperclass() ).get( key );
    }
}
