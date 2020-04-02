/* $Id$
 *
 * Common.
 *
 * Released under Gnu Public License
 * Copyright © 2018 Michael G. Binz
 */
package org.smack.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Support classes for working with integer values.  Note that in the
 * documentation below integer means the concept integer, not Java
 * 32 bit integer.
 *
 * @author micbinz
 */
public class IntegerUtil
{
    /**
     * Convert the passed bytes to a primitive value.
     *
     * @param endianness One of the constants {@link #Big} or {@link #Little}.
     * @param bytes The byte buffer to use. If the buffer contains less bytes
     * than the target type can hold, the bytes are filled with zero.  If the
     * buffer contains more bytes then only as many bytes are used as fit
     * into the target type.
     * @return The resulting integer primitive.
     */
    public static long toLong( Endianness endianness, byte[] bytes )
    {
        return toLong( endianness, bytes, 0 );
    }

    /**
     * Convert the passed bytes to a primitive value. Allows to specify the
     * start position in the input buffer for in-place reading of the input.
     *
     * @param endianness One of the constants {@link #Big} or {@link #Little}.
     * @param bytes The byte buffer to use. If the buffer contains less bytes
     * than the target type can hold, the bytes are filled with zero.  If the
     * buffer contains more bytes then only as many bytes are used as fit
     * into the target type.
     * @param startIdx A start index into the input buffer.
     * @return The resulting integer primitive.
     */
    public static long toLong( Endianness endianness, byte[] bytes, int startIdx )
    {
        return endianness.toLong( bytes, Long.SIZE, startIdx );
    }

    /**
     * Convert the passed bytes to a primitive value.
     *
     * @param endianness One of the constants {@link #Big} or {@link #Little}.
     * @param bytes The byte buffer to use. If the buffer contains less bytes
     * than the target type can hold, the bytes are filled with zero.  If the
     * buffer contains more bytes then only as many bytes are used as fit
     * into the target type.
     * @return The resulting integer primitive.
     */
    public static int toInt( Endianness endianness, byte[] bytes )
    {
        return toInt( endianness, bytes, 0  );
    }

    /**
     * Convert the passed bytes to a primitive value. Allows to specify the
     * start position in the input buffer for in-place reading of the input.
     *
     * @param endianness One of the constants {@link #Big} or {@link #Little}.
     * @param bytes The byte buffer to use. If the buffer contains less bytes
     * than the target type can hold, the bytes are filled with zero.  If the
     * buffer contains more bytes then only as many bytes are used as fit
     * into the target type.
     * @param startIdx A start index into the input buffer.
     * @return The resulting integer primitive.
     */
    public static int toInt( Endianness endianness, byte[] bytes, int startIdx )
    {
        return (int)endianness.toLong( bytes, Integer.SIZE, startIdx );
    }

    /**
     * Convert the passed bytes to a primitive value.
     *
     * @param endianness One of the constants {@link #Big} or {@link #Little}.
     * @param bytes The byte buffer to use. If the buffer contains less bytes
     * than the target type can hold, the bytes are filled with zero.  If the
     * buffer contains more bytes then only as many bytes are used as fit
     * into the target type.
     * @return The resulting integer primitive.
     */
    public static short toShort( Endianness endianness, byte[] bytes )
    {
        return toShort( endianness, bytes, 0 );
    }

    /**
     * Convert the passed bytes to a primitive value. Allows to specify the
     * start position in the input buffer for in-place reading of the input.
     *
     * @param endianness One of the constants {@link #Big} or {@link #Little}.
     * @param bytes The byte buffer to use. If the buffer contains less bytes
     * than the target type can hold, the bytes are filled with zero.  If the
     * buffer contains more bytes then only as many bytes are used as fit
     * into the target type.
     * @param startIdx A start index into the input buffer.
     * @return The resulting integer primitive.
     */
    public static short toShort( Endianness endianness, byte[] bytes, int startIdx )
    {
        return (short)endianness.toLong( bytes, Short.SIZE, startIdx );
    }

    /**
     * Creates an integer from bytes in a buffer.
     *
     * @param endianness One of the constants {@link #Big} or {@link #Little}.
     * @param bitCount The number of bits to fetch from the passed buffer.
     * Note that it is allowed to fetch uncommon bit widths as long they are
     * multiples of eight and less than 64.
     * @param bytes The byte buffer.
     * @param startIdx The start index into the byte buffer.  It must be
     * possible to access bitCount/8 bytes starting from this index.
     * @return A 64 bit integer value that has bitCount valid bits and is
     * extended using zero bits.  The result is only negative if the maximum
     * bit width of 64 was requested and the number in the buffer has the
     * leading bit set.  For all smaller bit widths the result is always
     * positive.
     *
     * @throws IllegalArgumentException If bitCount is not a multiple of eight,
     * if bitCount is larger than 64 or if the buffer does not contain
     * bitCount/8 bytes beginning with the startIdx.
     */
    public static long toInteger(
            Endianness endianness,
            int bitCount,
            byte[] bytes,
            int startIdx )
    {
        if ( bitCount > Long.SIZE )
            throw new IllegalArgumentException(
                    "bitCount exceeds " + Long.SIZE + "." );
        if ( (bitCount & 0b111) != 0 )
            throw new IllegalArgumentException(
                    "bitCount not multiple of 8: " + bitCount + "." );

        int remainingLengthFromStartIdx =
                bytes.length - startIdx;
        int requiredBytes =
                bitCount / 8;

        if ( requiredBytes > remainingLengthFromStartIdx )
            throw new IllegalArgumentException( String.format(
                    "bufferTooShort: Required %d, available %d.",
                    requiredBytes,
                    remainingLengthFromStartIdx ) );

        return endianness.toLong( bytes, bitCount, startIdx );
    }

    /**
     * Create a byte array from an integer primitive.
     *
     * @param endianness One of the constants {@link #Big} or {@link #Little}.
     * @param elLongo The primitive value to convert.
     * @return The result of the conversion in a newly allocated array. The length
     * of the array corresponds to the bit width of the value parameter.
     */
    public static byte[] fromLong( Endianness endianness, long elLongo )
    {
        byte[] result = new byte[ Long.SIZE / Byte.SIZE ];

        fromLong( endianness, elLongo, result, 0 );

        return result;
    }

    /**
     * Create a byte array from an integer primitive. Uses an existing array.
     *
     * @param endianness One of the constants {@link #Big} or {@link #Little}.
     * @param value The primitive value to convert.
     * @param target The target buffer receiving the result.
     * @param targetIdx The first index used when writing into the target buffer.
     */
    public static void fromLong( Endianness endianness, long value, byte[] target, int targetIdx )
    {
        endianness.fromLong( value, Long.SIZE, target, targetIdx );
    }

    /**
     * Create a byte array from an integer primitive. Uses an existing array and allows to
     * specify the result width.  If the target bit width is less than the bit width of the
     * value then the most significant bit of the target vale are dropped.
     *
     * @param endianness One of the constants {@link #Big} or {@link #Little}.
     * @param value The primitive value to convert.
     * @param bitCount The number of bits to be used in the target buffer.  Must be a
     * multiple of eight.
     * @param target The target buffer receiving the result.
     * @param targetIdx The first index used when writing into the target buffer.
     * @throws IllegalArgumentException If the passed bit count is not a multiple of
     * eight.
     */
    public static void fromLong(
            Endianness endianness,
            long value,
            int bitCount,
            byte[] target,
            int targetIdx )
    {
        if ( (bitCount % 8) != 0  )
            throw new IllegalArgumentException( "bitCount not multiple of eight: " + bitCount );

        endianness.fromLong( value, bitCount, target, targetIdx );
    }

    /**
     * Create a byte array from an integer primitive.
     *
     * @param endianness One of the constants {@link #Big} or {@link #Little}.
     * @param value The primitive value to convert.
     * @return The result of the conversion in a newly allocated array. The length
     * of the array corresponds to the bit width of the value parameter.
     */
    public static byte[] fromInt( Endianness endianness, int value )
    {
        byte[] result = new byte[ Integer.SIZE / Byte.SIZE ];

        fromInt( endianness, value, result, 0 );

        return result;
    }

    /**
     * Create a byte array from an integer primitive. Uses an existing array.
     *
     * @param endianness One of the constants {@link #Big} or {@link #Little}.
     * @param value The primitive value to convert.
     * @param target The target buffer receiving the result.
     * @param targetIdx The first index used when writing into the target buffer.
     */
    public static void fromInt( Endianness endianness, int value, byte[] target, int targetIdx )
    {
        endianness.fromLong( value, Integer.SIZE, target, targetIdx );
    }

    /**
     * Create a byte array from an integer primitive.
     *
     * @param endianness One of the constants {@link #Big} or {@link #Little}.
     * @param value The primitive value to convert.
     * @return The result of the conversion in a newly allocated array. The length
     * of the array corresponds to the bit width of the value parameter.
     */
    public static byte[] fromShort( Endianness endianness, short value )
    {
        byte[] result = new byte[ Short.SIZE / Byte.SIZE ];

        fromShort( endianness, value, result, 0 );

        return result;
    }

    /**
     * Create a byte array from an integer primitive. Uses an existing array.
     *
     * @param endianness One of the constants {@link #Big} or {@link #Little}.
     * @param value The primitive value to convert.
     * @param target The target buffer receiving the result.
     * @param targetIdx The first index used when writing into the target buffer.
     */
    public static void fromShort( Endianness endianness, short value, byte[] target, int targetIdx )
    {
        endianness.fromLong( value, Short.SIZE, target, targetIdx );
    }

    /**
     * Compute the bitmask for the passed bit length and perform a range check
     * for the value.
     *
     * @param length Expected mask bit length.
     * @param value The value to check.
     * @return The passed value.
     * @throws IllegalArgumentException if the passed value does not fit in the
     * bit length.
     */
    static public long rangeCheck( int length, long value )
    {
        long mask = ~ getMask( length );

        // Perform a range check.
        if ( (value & mask) != 0 )
            throw new IllegalArgumentException(
                    String.format(
                            "%d does not fit into %d bits.",
                            value,
                            length ) );

        return value;
    }

    /**
     * Compute a bit mask for the passed bit length.
     *
     * @param length The bit length.
     * @return The resulting bit mask.  For an example of length=4
     * the result is 1111 binary, i.e. a long with the lowest four
     * bits set.
     */
    static public long getMask( int length )
    {
        if (length >= Long.SIZE)
            return ~ 0;

        return (0x1L << length)-1;
    }

    /**
     * Parses a string to a byte array.  The string may optionally
     * start with an '0x' prefix and must consist of an even number
     * of hex digits in upper or lower case.
     *
     * @param spayload The string to parse.
     * @return The resulting byte array.
     * @throws Exception In case of a syntax error.
     */
    static public byte[] parse( String spayload )
            throws NumberFormatException
    {
        spayload = spayload.toLowerCase();

        // Remove hex prefix.
        if ( spayload.startsWith( "0x" ) )
            spayload = spayload.substring( 2 );

        if ( (spayload.length() & 1) != 0 )
            throw new NumberFormatException( String.format(
                    "Even number of digits required in '%s'. Count is %d.",
                    spayload,
                    spayload.length() ) );

        int numberOfBytes = spayload.length() / 2;

        byte[] result = new byte[ numberOfBytes ];

        for ( int i = 0 ; i < numberOfBytes ; i++ )
        {
            // Two hex digits must fit into a byte.  Note that
            // Byte.parseByte( x, 16 ) cannot parse numbers
            // above 128. Fails with 88, AA, etc.
            result[i] = (byte)Integer.parseInt(
                    spayload.substring( i*2, (i*2) + 2 ),
                    16 );
        }

        return result;
    }

    /**
     * Parses a string to a byte array.  The string may optionally
     * start with an '0x' prefix and must consist of an even number
     * of hex digits in upper or lower case.
     *
     * @param spayload The string to parse.
     * @param length The expected result length.  If the parsed string is
     * shorter than this expected length, it is extended with zero bytes.
     * In case the parsed byte array is longer than the requested length,
     * this results in an exception.
     * @return The resulting byte array.
     * @throws Exception In case of a syntax error.
     */
    static public byte[] parse( int length, String spayload )
            throws Exception
    {
        try
        {
            byte[] parsed = parse( spayload );

            if ( parsed.length > length )
                throw new SmackException( "Payload '%s' does not fit in the passed length %d.",
                        spayload,
                        length );

            if ( parsed.length == length )
                return parsed;

            return Arrays.copyOf( parsed, length );
        }
        catch ( NumberFormatException e )
        {
            throw new Exception( e );
        }
    }

    /**
     * Parses an byte array to an hex string.
     *
     * @param array The byte array to parse.
     * @return The resulting hex string.
     */
    public static String toHexString ( byte[] array )
    {
        List<String> strings = new ArrayList<>( array.length );

        for ( byte c : array )
        {
            strings.add( String.format( "0x%02x", c ));
        }

        return StringUtil.concatenate( " ", strings );
    }

    /**
     * Hide constructor.
     */
    private IntegerUtil()
    {
        throw new AssertionError();
    }

    /**
     * A class used to implement the endianness dependencies. Use one
     * of the constant instances offered by IntegerUtil.
     */
    abstract static public class Endianness
    {
        /**
         * Catch constructor.
         */
        Endianness()
        {
        }

        /**
         * Make a primitive long based on the passed parameters.
         * @param bytes The buffer holding the bytes to use.
         * @param maxbits The number of valid bits of the result.
         * @param startIdx The start index used if accessing the byte array.
         * @return The resulting long.
         */
        abstract long toLong( byte[] bytes, int maxbits, int startIdx );

        /**
         * Create a byte array from the passed integer value.
         * @param value The integer value to place in the byte array.
         * @param validBits The number of valid bits in the integer value.
         * @param target The buffer that will receive the result.
         * @param startIdx The start index used when writing the result into
         * the buffer.
         */
        abstract void fromLong( long value, int validBits, byte[] target, int startIdx );
    }

    /**
     * Constant used with the byte array integer conversions for Little endian.
     */
    public static final Endianness Little = new Endianness()
    {
        /**
         * Converts the passed bytes to a long assuming little endian byte order.
         * Bytes[ 0x4d 0x3c 0x2b 0x1a 0x00 0x00 0x00 0x00 ]
         *
         * Long64: 000000001A2B3C4Dh
         *
         * long( b8 b7 b6 b5 b4 b3 b2 b1 b0 )
         */
        @Override
        public long toLong( byte[] bytes, int maxbits, int startIdx )
        {
            long result = 0;

            int validBytes = maxbits / Byte.SIZE;

            // Ensure that the index stays inside the array bounds.
            int from = Math.min(
                    startIdx + validBytes,
                    bytes.length ) -1;
            int to = startIdx;

            for ( int i = from ; i >= to ; i-- )
                result = (result << 8) | (bytes[i] & 0xff);

            return result;
        }

        /**
         * Converts the passed long to a byte array assuming little endian byte
         * order.
         *
         * Long64: 000000001A2B3C4Dh =>
         * Bytes[ 0x4d 0x3c 0x2b 0x1a 0x00 0x00 0x00 0x00 ]
         */
        @Override
        public void fromLong( long longo, int bits, byte[] target, int startIdx )
        {
            int validBytes = bits / Byte.SIZE;

            for ( int i = 0 ; i < validBytes ; i++ )
            {
                // No need for bit masking. Just chop off the
                // unneeded bits.
                target[startIdx + i] = (byte)longo;
                longo >>>= Byte.SIZE;
            }
        }

        @Override
        public String toString()
        {
            return "Little";
        }
    };

    /**
     * Constant used with the byte array integer conversions for Big endian.
     */
    public static final Endianness Big = new Endianness()
    {
        // Implementation note: Implement this in terms
        // of the little endian stuff.  Results in this being slightly
        // less efficient than the Little endian routines.

        @Override
        public long toLong( byte[] bytes, int maxbits, int startIdx )
        {
            int validBits = (bytes.length - startIdx) * Byte.SIZE;
            if ( maxbits > validBits )
                maxbits = validBits;

            long result = Long.reverseBytes( Little.toLong( bytes, maxbits, startIdx ) );
            result >>>= Long.SIZE - maxbits;

            return result;
        }

        @Override
        public void fromLong( long longo, int bits, byte[] target, int startIdx )
        {
            long intermediate = Long.reverseBytes( longo );

            intermediate >>= Long.SIZE - bits;

            Little.fromLong( intermediate, bits, target, startIdx );
        }

        @Override
        public String toString()
        {
            return "Big";
        }
    };
}
