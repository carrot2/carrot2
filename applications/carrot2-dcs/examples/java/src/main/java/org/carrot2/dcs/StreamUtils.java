
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.dcs;

import java.io.*;

/**
 * Stream utilities.
 */
final class StreamUtils
{
    /**
     * Reads all the input stream, closes the stream and returns the content.
     */
    public static byte [] readFullyAndClose(final InputStream input) throws IOException
    {
        try
        {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(8 * 1024);
            final byte [] buffer = new byte [8 * 1024];

            int z;
            while ((z = input.read(buffer)) > 0)
            {
                baos.write(buffer, 0, z);
            }

            return baos.toByteArray();
        }
        finally
        {
            if (input != null)
            {
                input.close();
            }
        }
    }
}
