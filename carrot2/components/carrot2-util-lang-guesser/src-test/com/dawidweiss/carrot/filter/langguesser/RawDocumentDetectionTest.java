package com.dawidweiss.carrot.filter.langguesser;

import java.lang.reflect.Field;

import junit.framework.TestCase;

/**
 * Tests some internal methods of the {@link RawDocumentLanguageDetection}.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class RawDocumentDetectionTest extends TestCase {
    
    public RawDocumentDetectionTest(String s) {
        super(s);
    }
    
    
    public void testBufferAppend() throws Exception {
        RawDocumentLanguageDetection obj = new RawDocumentLanguageDetection(null);
        
        obj.resetBuffer();
        obj.append("abcdef");
        
        assertBufferEquals(obj, "abcdef");
    }

    public void testBufferAppendCrossingLimit() throws Exception {
        RawDocumentLanguageDetection obj = new RawDocumentLanguageDetection(null);

        int maxSize = RawDocumentLanguageDetection.MAX_DETECTION_BUFFER_SIZE;

        char [] fitExactly = genArray( maxSize );
        obj.resetBuffer();
        obj.append(new String(fitExactly));
        assertBufferEquals(obj, new String(fitExactly));
        
        obj.resetBuffer();
        obj.append(new String(fitExactly));
        obj.append("exceeding");
        assertBufferEquals(obj, new String(fitExactly));
        
        obj.resetBuffer();
        StringBuffer buf = new StringBuffer();
        buf.append( genArray( maxSize - 1 ));
        obj.append( buf.toString());
        buf.append( "partiallyexceeding" );
        obj.append( "partiallyexceeding" );
        assertBufferEquals(obj, buf.toString().substring(0, maxSize));
    }
    
    private char [] genArray(int size) {
        char [] pattern = "ABCDEF1234567!@#".toCharArray();
        char [] ar = new char [size];
        for (int i=0;i<ar.length;i++) {
            ar[i] = pattern[ i % pattern.length ];
        }
        return ar;
    }
    
    /**
     * Accesses <code>buffer</code> field via reflection, because
     * it is in private scope, and compares it to another string. 
     */
    private final void assertBufferEquals( RawDocumentLanguageDetection det, String s) 
        throws Exception {
        Field f = det.getClass().getDeclaredField("buffer");
        f.setAccessible(true);
        char [] bufferValue = (char []) f.get(det);
        f = det.getClass().getDeclaredField("bufferLength");
        f.setAccessible(true);
        int bufferLength = f.getInt(det);
        assertEquals( s, new String( bufferValue, 0, bufferLength));
    }
}
