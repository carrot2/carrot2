

/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the license "carrot2.LICENSE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.dawidweiss.carrot.util.common;

/**
 * Various helper methods for plain text processing.
 */
public class PlainTextHelper
{

    /**
     * 
     */
    public PlainTextHelper()
    {
        super();
    }


    /**
     * Returns a string with hexadecimal representation of the byte array.
     * @param bytes    The array to dump.
     * @param offset   Offset to start with (if negative, count from the end of the array).
     * @param length   How many bytes to dump.
     * @return A string with hexadecimal dump of the array.
     */
    public static String hexDump( byte [] bytes, int offset, int length)
    {
        final int BYTES_PER_LINE = 16;
        /* xxxxxxxx:  bb bb bb bb bb bb bb bb bb ... bb  ;ccccc...cc */
        final int CHARS_PER_LINE = 8 + 1 + 2 + (BYTES_PER_LINE * 3) + 3 + BYTES_PER_LINE + 1;

        if (offset < 0)
        {
            offset = bytes.length + offset;
            if (offset < 0) 
                offset = 0;
        }

        if (offset > bytes.length)
            throw new IllegalArgumentException("Offset beyond the length of the array: "
                + offset + " > " + bytes.length);
        if (length < 0)
            throw new IllegalArgumentException("Length must be greater than zero.");
        
        if (offset + length > bytes.length)
        {
            length = bytes.length - offset;
        }

        StringBuffer buffer = new StringBuffer( ((length / BYTES_PER_LINE) + 1) * CHARS_PER_LINE );
        final StringBuffer lineBuf = new StringBuffer();
        final char [] hexChars = "0123456789abcdef".toCharArray();
        final int end = offset + length;
        while (offset < end)
        {
            lineBuf.setLength(0);
            
            // emit offset.
            int tmp = offset;
            for (int i=0;i<8;i++)
            {
                tmp = ((tmp << 4) | (tmp >>> 28));
                lineBuf.append(hexChars[tmp & 0xf]);
            }
            lineBuf.append(":  ");
            
            for (int i=0; i<BYTES_PER_LINE; i++)
            {
                if (offset + i < end) {
                    int b = bytes[ offset + i]; 
                    lineBuf.append( hexChars[(b >>> 4) & 0xf] );
                    lineBuf.append( hexChars[b & 0xf] );
                    lineBuf.append(' ');
                }
                else {
                    while (i<BYTES_PER_LINE) {
                        lineBuf.append("   ");
                        i++;
                    }
                    break;
                }
            }
            
            lineBuf.append(" ; ");

            for (int i=0; i<BYTES_PER_LINE; i++)
            {
                if (offset + i < end) {
                    int b = (bytes[ offset + i]) & 0xff; 
                    if (b >= 32)
                        lineBuf.append((char) b);
                    else
                        lineBuf.append('.');
                }
                else {
                    while (i<BYTES_PER_LINE) {
                        lineBuf.append(' ');
                        i++;
                    }
                    break;
                }
            }
                        
            buffer.append(lineBuf);
            buffer.append('\n');
            offset += BYTES_PER_LINE;
        }

        return buffer.toString();
    }

}
