
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import java.io.UnsupportedEncodingException;


/**
 * A replacement for pre-JDK1.4 URLEncoder/ URLDecoder classes, which did not
 * take encodings into account when converting to URL-encoded scheme.
 *
 * Highly optimized version. If you can think of any other performace gains, let me know:
 * dawid.weiss@cs.put.poznan.pl
 */
public final class URLEncoding {
    /** Character-to-value conversion tables */
    private final static short hexConversion[];

    /* Initialization of conversion tables */
    static {
        hexConversion = new short[256];

        for (int i=0;i<256;i++) {
            if (i>='0' && i<='9') {
                hexConversion[i] = (short) (i-'0');
            } else if (i>='a' && i<='f') {
                hexConversion[i] = (short) (i - 'a' + 10);
            } else if (i>='A' && i<='F') {
                hexConversion[i] = (short) (i - 'A' + 10);
            } else hexConversion[i] = -1;      // Error detection.
        };
    }

    /**
     *  Bytes URL-encoded into shorts. If they don't need encoding, only the lower byte
     *  is set to the value of that byte
     */
    private final static short [] encodableBytes;
    static {
        byte [] byteToHex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        encodableBytes = new short [256];
        for (int i=0;i<encodableBytes.length;i++) {
            if ( i >= '0' && i <= '9' )
                encodableBytes[i] = (short) i;
            else
            if ( i >= 'a' && i <= 'z' )
                encodableBytes[i] = (short) i;
            else
            if ( i >= 'A' && i <= 'Z' )
                encodableBytes[i] = (short) i;
            else
            switch (i) {
                case ' ':
                    encodableBytes[i] = '+';
                    break;
                case '-':
                case '_':
                case '.':
                case '*':
                    encodableBytes[i] = (short) i;
                    break;
                default:
                    // byte must be encoded.
                    encodableBytes[i] = (short)(
                        (byteToHex[i>>>4] << 8)
                        |
                        (byteToHex[i & 0x0f])
                    );
            }
        }
    }


    /**
     * Prevents class instantiation.
     */
    private URLEncoding() {
    }


    /**
     * URL-decodes a sequence of bytes without altering the source array
     * (the result array must be allocated off heap's space). Double the
     * size of the input array is also allocated during processing.
     *
     * @param bytes The URL-encoded array of bytes.
     * @return A decoded array of bytes.
     */
    public final static byte [] decode(byte [] bytes) {
        byte result [] = (byte []) bytes.clone();
        byte [] trimmedResult = new byte[ decodeInPlace( result ) ];
        System.arraycopy(result, 0, trimmedResult, 0, trimmedResult.length);
        return trimmedResult;
    }


    /**
     * URL-decodes a sequence of bytes. The decoded output is
     * placed in <strong>the same array</strong> as the input.
     *
     * @param bytes The URL-encoded array of bytes.
     * @return The length of the output decoded subarray.
     */
    public final static int decodeInPlace(byte [] bytes) {
        int i=0;
        int j=0;
        final int length = bytes.length;
        int decoded;
        try {
            while (i<length) {
                byte c = bytes[i];
                switch (c) {
                    case '+': bytes[j] = ' ';
                              break;
                    case '%': decoded = (hexConversion[bytes[++i]] << 4) | hexConversion[bytes[++i]];
                              if ((decoded & 0xffffff00) != 0)
                                  throw new IllegalArgumentException("Malformed characters in encoded stream.");
                              bytes[j] = (byte) decoded;
                              break;
                    default:  bytes[j] = c;
                }
                i++;
                j++;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Incomplete trailing '%' sequence.");
        }
        return j;
    }


    /**
     * Encodes bytes in the <code>source</code> array, starting at index
     * <code>from</code> and ending at <code>to-1</code>. The URL-encoded
     * bytes are placed in <code>output</code> array, the returned integer
     * is the length of the encoded output.
     *
     * This method may throw <code>ArrayIndexOutOfBoundsException</code> if
     * <code>output</code> array is not big enough to hold the result.
     *
     * @param source The array of bytes to be encoded
     * @param from  An index to the first byte to be encoded in the source.
     * @param to    An index to the last byte to be encoded in the source minus 1.
     * @param output The array where the encoded result is stored.
     * @return The length of the encoded array.
     * @throws ArrayIndexOutOfBoundsException If <code>output</code> array is not large enough
     *                  to store the result.
     */
    public final static int encodeToArray( byte [] source, int from, int to, byte [] output)
            throws ArrayIndexOutOfBoundsException
    {
        int length = 0;

        int i = from;
        short encoded;
        while (i<to) {
            encoded = encodableBytes[ source[i++] & 0xff ];
            if (encoded <= 0xff) {
                output[length++] = (byte) encoded;
            } else {
                // output encoded byte
                output[length++] = '%';
                output[length++] = (byte) ( encoded >>> 8 );
                output[length++] = (byte) ( encoded );
            }
        }

        return length;
    }


    /**
     * URL-encodes a sequence of bytes.
     */
    public static byte [] encode(byte [] bytes) {
        // The encoded table may consume at most 3 times the input
        byte [] tmp = new byte[ bytes.length * 3 ];
        int length = encodeToArray( bytes, 0, bytes.length, tmp );
        byte [] result = new byte[ length ];
        System.arraycopy(tmp, 0, result, 0, result.length);
        return result;
    }


    /**
     * URL-encodes a string, converting its characters to bytes according
     * to the specified encoding prior to applying the URL-encoding routine.
     */
    public static String encode(String s, String enc)
		throws UnsupportedEncodingException {
        return new String(encode(s.getBytes(enc)), "iso8859-1");
    }


    /**
     * Decodes an URL-encoded input, transforming the decoded array of bytes
     * into a String according to the specified character encoding.
     */
    public static String decode(String s, String enc)
		throws UnsupportedEncodingException {
        return new String(decode( s.getBytes("iso8859-1")), enc);
    }
}

