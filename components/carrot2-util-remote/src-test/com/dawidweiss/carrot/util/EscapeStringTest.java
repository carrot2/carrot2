package com.dawidweiss.carrot.util;

import junit.framework.TestCase;

/**
 * Tests the Java-code "escape" string implementation.
 * 
 * @author Dawid Weiss
 */
public class EscapeStringTest extends TestCase {

	public EscapeStringTest(String s) {
		super(s);
	}

	public void testEscapeEmptyString() {
        assertEquals("", EscapeString.escapeString(""));
    }

    public void testEscapeQuoteString() {
        assertEquals("\\\"", EscapeString.escapeString("\""));
    }

    public void testEscapeCRString() {
        assertEquals("\\n", EscapeString.escapeString("\n"));
    }

    public void testEscapeLFString() {
        assertEquals("\\r", EscapeString.escapeString("\r"));
    }

    public void testEscapeTabString() {
        assertEquals("\\t", EscapeString.escapeString("\t"));
    }

    public void testEscapeBackslashString() {
        assertEquals("\\\\", EscapeString.escapeString("\\"));
    }
    
    public void testEscapeComplexString() {
        assertEquals("abc\\t\\t\\r\\n\\tdef", EscapeString.escapeString("abc\t\t\r\n\tdef"));
    }
    
    public void testBugzillaBug1179124() {
        System.out.println(EscapeString.escapeString("nt can be found <A HREF=\\\"%s\\\">here</A>.<P>"));
        assertEquals("nt can be found <A HREF=\\\\\\\"%s\\\\\\\">here</A>.<P>", EscapeString.escapeString("nt can be found <A HREF=\\\"%s\\\">here</A>.<P>"));
    }    
    
}
