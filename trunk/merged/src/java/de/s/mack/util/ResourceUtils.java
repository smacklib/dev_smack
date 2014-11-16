/* $Id$
 *
 * Common.
 *
 * Released under Gnu Public License
 * Copyright © 2014 Michael G. Binz
 */

package de.s.mack.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

/**
 * Resource Bundle helpers.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class ResourceUtils
{
    /**
     * Unchecked exception thrown by {@link #getObject} when resource lookup
     * fails, for example because string conversion fails.  This is
     * not a missing resource exception.  If a resource isn't defined
     * for a particular key, getObject does not throw an exception.
     *
     * @see #getObject
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
     * Populates the passed Map with the preprocessed values from the named
     * resource bundle.
     *
     * @param bundleName
     *            The resource bundle whose entries are processed.
     * @param result
     *            The map to populate.
     */
    public static Map<String, String> getPreprocessedResourceBundle(
            String bundleName, Locale loc, ClassLoader cl )
    {
        try
        {
            Map<String, String> result = new HashMap<String, String>();

            // Preprocessing is currently limited to a single
            // resource bundle. A broader context may be
            // pretty confusing. michab.

            ResourceBundle bundle = ResourceBundle.getBundle( bundleName, loc,
                    cl,
                    // We only want property resource bundles.
                    Control.getControl( Control.FORMAT_PROPERTIES ) );

            for ( String key : bundle.keySet() )
            {
                result.put( key,
                // Note that we perform all preprocessing for the
                        // string values in the resource bundle here.
                        // Later stages of processing see only the evaluated
                        // values.
                        evaluateStringExpression( bundle.getString( key ),
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
     * hello = Hello world = World place = ${world}
     *
     * The value of evaluateStringExpression("${hello} ${place}") would be
     * "Hello World". The value of ${null} is null.
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
                        result
                                .append( evaluateStringExpression( resolved,
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
     * Forbid instantiation.
     */
    private ResourceUtils()
    {
        throw new AssertionError();
    }
}
