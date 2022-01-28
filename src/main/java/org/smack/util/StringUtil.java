/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2008-2022 Michael G. Binz
 */
package org.smack.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Helper operations for strings.
 *
 * @author Michael Binz
 */
public class StringUtil
{
    /**
     * Instantiation forbidden.
     */
    private StringUtil()
    {
        throw new AssertionError();
    }

    /**
     * Checks whether a string has content.
     *
     * @param string The string to test.
     * @return {@code False} if the string is either {@code null} or has
     * a trimmed length of zero.  Otherwise {@code true}.
     * @see #hasContent(String, boolean)
     */
    public static boolean hasContent( String string )
    {
        return hasContent( string, true );
    }

    /**
     * Checks whether a string has content.
     *
     * @param string The string to test.
     * @param trim If {@code true}, then the string is trimmed before the test.
     * @return {@code False} if the string is either {@code null} or has
     * a length of zero.  Otherwise {@code true}.
     * @see #hasContent(String)
     * @see String#trim()
     */
    public static boolean hasContent( String string, boolean trim )
    {
        if ( string != null && trim )
            string = string.trim();

        return string != null && string.length() > 0;
    }

    /**
     * Checks whether a string has content.
     *
     * @param string The string to test.
     * @return {@code true} if the string is either {@code null} or has
     * a trimmed length of zero.  Otherwise {@code false}.
     * @see #isEmpty(String, boolean)
     */
    public static boolean isEmpty( String string )
    {
        return isEmpty( string, true );
    }

    /**
     * Checks whether a string has content.
     *
     * @param string The string to test.
     * @param trim If {@code true}, then the string is trimmed before the test.
     * @return {@code true} if the string is either {@code null} or has
     * a length of zero.  Otherwise {@code false}.
     * @see #isEmpty(String)
     * @see String#trim()
     */
    public static boolean isEmpty( String string, boolean trim )
    {
        if ( string != null && trim )
            string = string.trim();

        return string == null || string.isEmpty();
    }

    /**
     * Trims the characters passed in {@code charactersToTrim} from
     * the beginning and the end of {@code stringToTrim}.
     *
     * @param stringToTrim The string to be trimmed.
     * @param charactersToTrim The characters to be trimmed.
     * @return The trimmed string.
     */
    public static String trim( String stringToTrim, String charactersToTrim )
    {
    	Objects.requireNonNull( stringToTrim );
    	Objects.requireNonNull( charactersToTrim );

        int idx1;
        for ( idx1 = 0 ; idx1 < stringToTrim.length() ; idx1++ ) {
            if ( -1 == charactersToTrim.indexOf( stringToTrim.charAt( idx1 ) ) ) {
                break;
            }
        }

        int idx2;
        for ( idx2 = stringToTrim.length() -1 ; idx2 > idx1 ; idx2-- ) {
            if ( -1 == charactersToTrim.indexOf( stringToTrim.charAt( idx2 ) ) ) {
                idx2++;
                break;
            }
        }

        // No characters trimmed. Return identity.
        if ( idx1 == 0 && idx2 == stringToTrim.length() )
            return stringToTrim;

        if ( idx2 <= idx1 )
            return EMPTY_STRING;

        return stringToTrim.substring( idx1, idx2 );
    }

    /**
     * Create a string consisting of n occurrences of a single character.
     *
     * @param filler The character used to fill the string.
     * @param count The required length of the string.
     * @return A string containing count filler characters.
     * @deprecated Use {@link #createFilledString(String, int)}
     */
    @Deprecated
    public static String createFilledString( char filler, int count )
    {
        if ( count <= 0 )
            throw new IllegalArgumentException( "count <= 0" );

        StringBuilder result = new StringBuilder( count );

        while ( count-- > 0 )
            result.append( filler );

        return result.toString();
    }

    /**
     * Create a string consisting of n occurrences of a string.
     *
     * @param filler The character used to fill the string.
     * @param count The number of occurrences of filler.  May be 0.
     * @return A string containing count filler characters.
     */
    public static String createFilledString( String filler, int count )
    {
        Objects.nonNull( filler );

        if ( count < 0 )
            throw new IllegalArgumentException( "count < 0" );
        if ( count == 0 )
            return EMPTY_STRING;

        StringBuilder result = new StringBuilder( count );

        while ( count-- > 0 )
            result.append( filler );

        return result.toString();
    }

    /**
     * The canonical empty string.  Can be used to keep the number of
     * allocated empty string objects in an application small.
     */
    public static final String EMPTY_STRING = "";

    /**
     * The platform-dependent end of line.  Use instead of
     * "\n".
     */
    public static final String EOL = String.format( "%n" );

    /**
     * Creates a delimited string from the elements in the passed container.
     * Each element is converted to a string using the
     * {@link String#valueOf(Object)} operation and the resulting strings are
     * concatenated using the passed delimiter.  For a container holding the
     * elements 'Donald', 'Daisy' and 'Scrooge' and a delimiter of '+' this
     * will result in "Donald+Daisy+Scrooge".
     *
     * @param delimiter The delimiter to use.  {@code null} is allowed and
     * results in a concatenation without delimiters.
     * @param iterable The container used for building the delimited list.
     * {@code null} is allowed, resulting in an empty string.
     * @return The result string.
     */
    public static String concatenate( String delimiter, Iterable<?> iterable )
    {
        if ( delimiter == null )
            delimiter = EMPTY_STRING;

        if ( iterable == null )
            return EMPTY_STRING;

        StringBuilder result = new StringBuilder();

        for ( Object c : iterable )
        {
            if ( result.length() > 0 )
                result.append( delimiter );

            result.append( String.valueOf( c ) );
        }

        return result.toString();
    }

    /**
     * Creates a delimited string from the elements in the passed array.
     * Each element is converted to a string using the
     * {@link String#valueOf(Object)} operation and the resulting strings are
     * concatenated using the passed delimiter.  For an array holding the
     * elements 'Donald', 'Daisy' and 'Scrooge' and a delimiter of '+' this
     * will result in "Donald+Daisy+Scrooge".
     *
     * @param delimiter The delimiter to use.  {@code null} is allowed and
     * results in a concatenation without delimiters.
     * @param array The array used for building the delimited list.
     * {@code null} is allowed, resulting in an empty string.
     * @param <T> Input array type.
     * @return The result string.
     */
    public static <T> String concatenate( String delimiter, T[] array )
    {
        if ( array == null )
            return EMPTY_STRING;

        return concatenate( delimiter, Arrays.asList( array ) );
    }

    /**
     * Ensures that a string that is to be displayed to a human starts with
     * an upper case character.  If the first character has no upper case
     * equivalent, then the string is not modified.
     *
     * @param sentence The sentence to be converted.
     * @return The converted sentence.
     */
    public static String ensureFirstCharacterUppercase( String sentence )
    {
        if ( Character.isUpperCase( sentence.charAt( 0 ) ) )
            return sentence;

        String firstChar =
            sentence.substring( 0, 1 );
        String remaining =
            sentence.substring( 1 );

        return firstChar.toUpperCase() + remaining;
    }

    /**
     * Makes a string from an object.  If the object is {@code null}
     * returns the empty string.
     *
     * @param object The object.
     * @return The resulting string, never {@code null}.
     */
    public static String toString( Object object )
    {
        if ( object == null )
            return EMPTY_STRING;

        return object.toString();
    }

    /**
     * The default quote character for the quote related operations.
     */
    private static final char QUOTE_CHAR = '\"';
    private static final String ESCAPE_CHAR = "\\";

    /**
     * Quotes a string.
     *
     * @param quoteChar The quote character to use.
     * @param string The string to quote.  A {@code null} is allowed, resulting
     * in a quoted and empty string.
     * @return The resulting string.
     * @see #splitQuoted(String)
     */
    public static String quote( char quoteChar, String string )
    {
        if ( string == null || string.isEmpty() )
            return EMPTY_STRING + quoteChar + quoteChar;

        StringBuilder result = new StringBuilder();

        result.append( quoteChar );

        for ( int i = 0 ; i < string.length() ; i++ )
        {
            char c = string.charAt( i );

            if ( c == quoteChar )
                result.append( ESCAPE_CHAR );

            result.append( c );
        }

        result.append( quoteChar );

        return result.toString();
    }

    /**
     * Experimental.
     *
     * @param quoteChar The character used as the quote character.
     * @param toUnquote The string to unquote.
     * @return  The unquoted string.
     */
    public static String unquote( char quoteChar, String toUnquote )
    {
        if ( toUnquote.indexOf( quoteChar ) == -1 )
            return toUnquote;

        String intermediate =
                toUnquote.trim();

        String quoteString =
                EMPTY_STRING + quoteChar;

        if ( intermediate.startsWith( quoteString ) )
            intermediate = trim( intermediate, quoteString );

        if ( isEmpty( intermediate ) )
            return EMPTY_STRING;

        if ( intermediate.indexOf( quoteChar ) == -1 )
            return intermediate;

        StringBuilder result = new StringBuilder(
                intermediate.length() );

        for ( int i = 0 ; i < intermediate.length() ; i++ )
        {
            char c = intermediate.charAt( i );

            if ( c == quoteChar )
                throw new IllegalArgumentException( toUnquote );

            if ( c == ESCAPE_CHAR.charAt( 0 ) )
                // Skip an index when an escape char is found.
                // If the string ends with an escape char
                // we get a sioob exception. That's ok.
                c = intermediate.charAt( ++i );

            result.append( c );
        }

        return result.toString();
    }

    /**
     * Unquote the passed string using {@value #QUOTE_CHAR} as the quote
     * character.
     * @param toUnquote The string to unquote.
     * @return The unquoted string.
     */
    public static String unquote( String toUnquote )
    {
        return unquote( QUOTE_CHAR, toUnquote );
    }

    /**
     * Quotes the passed string.
     *
     * @param string The string to quote.  A {@code null} is allowed, resulting
     * in a quoted and empty string.
     * @return The quoted string.
     */
    public static String quote( String string )
    {
        return quote( QUOTE_CHAR, string );
    }

    /**
     * Splits a whitespace delimited and quoted string as used on command
     * lines into its elements.  For example 'Admiral "von Schneider"' is
     * split into 'Admiral' and 'von Schneider'.
     *
     * TODO(micbinz) does not support quoting of quote characters.
     * @param quoteChar The character used for quotes.
     * @param string The string to split.
     * @return The split strings.
     */
    public static String[] splitQuoted( char quoteChar, String string )
    {
        // See also http://stackoverflow.com/questions/10695143/split-a-quoted-string-with-a-delimiter
        // for a sketch of solving the same with regular expressions.  Can be made workable, but is even less
        // understandable.

        boolean inDoubleQuotes = false;

        ArrayList<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        for ( char c : string.toCharArray() )
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
     * @param toSplit The string to split
     * @return The split strings.
     */
    public static String[] splitQuoted( String toSplit )
    {
        return splitQuoted( QUOTE_CHAR, toSplit );
    }

    private static byte[] fromHexImpl( String string )
    {
        if ( MathUtil.isOdd( string.length() ))
            throw new IllegalArgumentException( "Length is odd: " + string );

        var result = new byte[ string.length() / 2 ];

        for ( int i = 0 ; i < result.length ; i++ )
        {
            int stringIndex =
                    i * 2;
            String pair =
                    string.substring( stringIndex, stringIndex+2 );
            result[i] = (byte)
                    Short.parseShort( pair, 16 );
        }

        return result;
    }

    /**
     * Convert an array to a hex string.  The resulting string consists of
     * an even number of hex figures, for each input byte two.  Example is
     * for input 1,2,10,15 the result is "01020a0f".
     *
     * @param array The array to convert.
     * @return The result string.
     */
    public static String toHex( byte[] array )
    {
        var result =
                new StringBuilder( array.length * 2 );

        for ( Byte c : array )
            result.append( String.format( "%02x", c ) );

        return result.toString();
    }

    /**
     * Convert the passed string into a byte array.  This is
     * symmetric to {@link #toHex(byte[])}.
     *
     * @param string The string to convert.
     * @return A byte array.  If the string cannot be converted null.
     */
    public static byte[] fromHex( String string )
    {
        try
        {
            return fromHexImpl( string );
        }
        catch( Exception e )
        {
            return null;
        }
    }
}
