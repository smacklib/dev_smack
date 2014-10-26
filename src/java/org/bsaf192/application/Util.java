/*
 * Copyright � 2012 Daimler TSS. All Rights Reserved.
 *
 * Reproduction or transmission in whole or in part, in any form or by any
 * means, is prohibited without the prior written consent of the copyright
 * owner.
 */
package org.bsaf192.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.zip.CRC32;

import de.michab.util.StringUtils;

/**
 * Helper operations.
 *
 * @version $Rev: 735 $
 * @author micbinz
 */
public class Util {

    private static final char QUOTE_CHAR = '\"';

    /**
     * Quotes a string.
     * TODO(micbinz) does not support quoting of quote characters.
     * @param parts The string to quote.
     * @return The resulting string.
     * @see #splitQuoted(String)
     */
    public static String quote( char quoteChar, String part )
    {
        if ( ! StringUtils.hasContent( part ) )
            return "";

        StringBuilder result = new StringBuilder();

        result.append( quoteChar );
        // TODO(micbinz) do also internal quoting.
        result.append( part );
        result.append( quoteChar );

        return result.toString();
    }
    public static String quote( String part )
    {
        return quote( QUOTE_CHAR, part );
    }

    /**
     * Splits a whitespace delimited and quoted string as used on command
     * lines into its elements.  For example 'Admiral "von Schneider"' is
     * split into 'Admiral' and 'von Schneider'.
     *
     * TODO(micbinz) does not support quoting of quote characters.
     * @param quoteChar The character used for quotes.
     * @param someString The string to split
     * @return The split strings.
     */
    public static String[] splitQuoted( char quoteChar, String someString )
    {
        // See also http://stackoverflow.com/questions/10695143/split-a-quoted-string-with-a-delimiter
        // for a sketch of solving the same with regular expressions.  Can be made workable, but is even less
        // understandable.

        boolean inDoubleQuotes = false;

        ArrayList<String> result = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();

        for ( char c : someString.toCharArray() )
        {
            boolean isDoubleQuote = c == quoteChar;

            if ( isDoubleQuote )
            {
                inDoubleQuotes = !inDoubleQuotes;

                if ( ! inDoubleQuotes )
                {
                    // End of the quoted sequence.
                    result.add( sb.toString() );
                    sb.setLength( 0 );
                }
                continue;
            }

            if ( inDoubleQuotes )
            {
                sb.append( c );
                continue;
            }
            else if ( Character.isWhitespace( c ) )
            {
                if ( sb.length() > 0 )
                {
                    result.add( sb.toString() );
                    sb.setLength( 0 );
                }
                continue;
            }

            sb.append( c );
        }

        if ( sb.length() > 0 || inDoubleQuotes )
            result.add( sb.toString() );

        return result.toArray( new String[ result.size()] );
    }
    /**
     * Splits a whitespace delimited and quoted string as used on command
     * lines into its elements.  For example 'Admiral "von Schneider"' is
     * split into 'Admiral' and 'von Schneider'.
     *
     * TODO(micbinz) does not support quoting of quote characters.
     * @param toParse The string to split
     * @return The split strings.
     */
    public static String[] splitQuoted( String someString )
    {
        return splitQuoted( QUOTE_CHAR, someString );
    }


    static void testSplitQuote()
    {
        // Test split quote.
        String[] testCases = {
                // Plain
                "ab cd ef",
                // Whitespace is tab.
                "ab\tcd\tef",
                // Whitespace is mixed and at the end.
                "ab\tcd ef\t \t \t \t \tgh \t",
                // Quoted simple.
                "ab \"cd ef\" gh",
                // Quoted leading and trailing spaces.
                "ab \" cd ef \" gh",
                // Last quote not terminated, trailing space.
                "ab \" cd ef ",
                // Empty string.
                "ab \"\" cd",
                // Pathological: ab" cd ef" -> "ab cd ef"
                "ab\" cd ef ",
                // Empty string at eol.
                "michael \""
        };

        for ( String c : testCases )
        {
            System.err.println( "parseQuoted( '" + c + "' )" );
            for ( String c1 : splitQuoted( c ) )
                System.err.println( "'" + c1 + "'" );
        }
    }

    /**
     * Concatenates the strings in the passed list using the passed delimiter. For example:
     * '#', [Tick Trick Track] -> "Tick#Trick#Track".
     *
     * @param delimiter The delimiter to use.
     * @param list The list of strings.
     * @return A concatenated string.
     */
    public static String concat( char delimiter, String[] list )
    {
        if ( list.length == 0 )
            return "";

        StringBuilder result = null;

        for ( String c : list )
        {
            if ( result == null )
                result = new StringBuilder();
            else
                result.append( delimiter );

            result.append( c );
        }

        return result.toString();
    }
    public static String concat( char delimiter, Collection<String> list )
    {
        return concat( delimiter, list.toArray( new String[list.size()] ) );
    }

    public static void testConcat()
    {
        String[] nephewsArray = new String[]{ "Huey", "Dewey", "Louie" };
        List<String> nephewsList = Arrays.asList( nephewsArray );
        String[] uncaArray = new String[]{ "Donald" };

        String result;

        result = concat( ';', nephewsArray );
        System.err.println( result);
        result = concat( '_', nephewsList );
        System.err.println( result);
        result = concat( '_', uncaArray );
        System.err.println( result);
    }

    /**
     * Compute the crc32 checksum for the passed buffer.
     *
     * @param buffer The buffer to compute the  checksum for.
     * @return The checksum.
     */
    public long crc32( byte[] buffer )
    {
        CRC32 checksum = new CRC32();

        checksum.update( buffer );

        return checksum.getValue();
    }

    private Util() {
        throw new AssertionError();
    }
}
