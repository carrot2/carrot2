
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.zip;

import java.text.MessageFormat;
import java.util.Random;

/**
 * Compression test utilities. 
 *  
 * @author Dawid Weiss
 */
final class CompressionTestsUtils {
    public static final int KB = 1024;
    public static final int MB = KB * KB;

    private CompressionTestsUtils() {
        // no instances
    }

    public static byte[] generateRandom(final int length) {
        final Random rnd = new Random(0x10203040);
        final byte [] chunk = new byte[length];
        rnd.nextBytes(chunk);
        return chunk;
    }

    public static byte[] generateSequence(final int length) {
        final byte [] sequence = "sequence!".getBytes();
        final byte [] chunk = new byte[length];
        int pos = 0;
        for (int i = 0; i < chunk.length; i++) {
            chunk[i] = sequence[pos];
            pos = (pos + 1) % sequence.length;
        }
        return chunk;
    }

    public static byte[] generateZeros(final int length) {
        final byte [] chunk = new byte[length];
        return chunk;
    }

    public static String humanSize(int size) {
        if (size < KB) {
            return size + " byte" + (size > 1 ? "s" : "");
        } 

        final String unit;
        final double divider;
        
        if (size < MB) {
            divider = KB;
            unit = "kB";
        } else {
            divider = MB;
            unit = "MB";
        }
        return MessageFormat.format("{0,number,#.#} {1}",
                new Object [] { new Double(size / divider), unit});
    }
}