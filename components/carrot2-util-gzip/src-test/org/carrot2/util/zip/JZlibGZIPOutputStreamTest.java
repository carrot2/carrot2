
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.zip;

import java.io.*;

import junit.framework.*;
import junitx.framework.ArrayAssert;

import org.carrot2.util.StreamUtils;

/**
 * Test GZIP output stream.
 * 
 * @author Dawid Weiss
 */
public class JZlibGZIPOutputStreamTest extends TestCase {
    private final int bufferSize;
    private final int encodingStrategy;
    private final byte [] data;

    private final String name;

    public JZlibGZIPOutputStreamTest(String test, int bufferSize, int encodingStrategy, byte [] data, String dataName) {
        super(test);
        this.bufferSize = bufferSize;
        this.encodingStrategy = encodingStrategy;
        this.data = data;

        this.name = 
            test + " in: " + CompressionTestsUtils.humanSize(data.length) + ", buf: " + CompressionTestsUtils.humanSize(bufferSize)
            + ", data: " + dataName + ", enc: " + encodingStrategy;
    }

    public void testEncoding() throws IOException {
        ByteArrayOutputStream baos;
        OutputStream output;

        baos = new ByteArrayOutputStream();
        output = new JZlibGZIPOutputStream(baos, encodingStrategy, bufferSize);
        deflateStreamDecode(data, baos, output);
    }

    public void testEncodingWithFlush() throws IOException {
        ByteArrayOutputStream baos;
        OutputStream output;

        baos = new ByteArrayOutputStream();
        output = new JZlibGZIPOutputStream(baos, 
                encodingStrategy, bufferSize);

        if (data.length > 2) {
            output.flush();
            output.flush();
            output.write(data[0]);
            output.flush();
            output.flush();
            output.write(data[1]);
            output.write(data[2]);
            output.flush();
            output.write(data, 3, data.length - 3);
            output.flush();
        } else {
            output.flush();
            output.write(data);
            output.flush();
        }
        output.close();

        final byte[] decoded = StreamUtils.readFullyAndCloseInput(
                new java.util.zip.GZIPInputStream(new ByteArrayInputStream(baos.toByteArray())));

        ArrayAssert.assertEquals(data, decoded);
    }

    public String getName() {
        return name;
    }

    protected final static void deflateStreamDecode(byte[] data, ByteArrayOutputStream baos, 
            OutputStream output) 
        throws IOException
    {
        StreamUtils.copy(new ByteArrayInputStream(data), output, 8 * 1024);
        output.close();

        final byte[] decoded = StreamUtils.readFullyAndCloseInput(
                new java.util.zip.GZIPInputStream(new ByteArrayInputStream(baos.toByteArray())));

        ArrayAssert.assertEquals(data, decoded);
    }
    
    /**
     * Create a set of tests with different parameters.
     */
    public static Test suite() {
        final TestSuite suite = new TestSuite();
        suite.setName(JZlibDeflaterOutputStream.class.getName());

        final String [] methods = new String [] {
                "testEncoding", "testEncodingWithFlush"
        };

        final int [] inputSizes = new int [] {
                0, 1, 2, 
                11 * CompressionTestsUtils.KB,
                101 * CompressionTestsUtils.KB,
                201 * CompressionTestsUtils.KB,
        };

        final int [] inputBuffers = new int [] {
                1, 2, 
                1 * CompressionTestsUtils.KB,
                4 * CompressionTestsUtils.KB,
                100 * CompressionTestsUtils.KB,
        };

        final int [] encodingStrategy = new int [] {
                JZlibDeflaterOutputStream.Z_DEFAULT_COMPRESSION,
                JZlibDeflaterOutputStream.Z_BEST_COMPRESSION,
                JZlibDeflaterOutputStream.Z_BEST_SPEED,
                JZlibDeflaterOutputStream.Z_NO_COMPRESSION,
        };

        for (int i = 0; i < inputSizes.length; i++) {
            byte [] randomData = CompressionTestsUtils.generateRandom(inputSizes[i]);
            byte [] zeroData = CompressionTestsUtils.generateZeros(inputSizes[i]);
            byte [] seqData = CompressionTestsUtils.generateSequence(inputSizes[i]);

            for (int j = 0; j < inputBuffers.length; j++) {
                for (int m = 0; m < methods.length; m++) {
                    for (int k = 0; k < encodingStrategy.length; k++) {
                        suite.addTest(
                                new JZlibGZIPOutputStreamTest(methods[m], inputBuffers[j], encodingStrategy[k], randomData, "random"));
                        suite.addTest(
                                new JZlibGZIPOutputStreamTest(methods[m], inputBuffers[j], encodingStrategy[k], zeroData, "zero"));
                        suite.addTest(
                                new JZlibGZIPOutputStreamTest(methods[m], inputBuffers[j], encodingStrategy[k], seqData, "sequence"));
                    }
                }
            }
        }
        
        return suite;
    }
}
