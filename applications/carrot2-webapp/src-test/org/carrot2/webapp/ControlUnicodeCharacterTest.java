
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

package org.carrot2.webapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.core.Persister;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import org.carrot2.shaded.guava.common.base.Charsets;

/** */
public class ControlUnicodeCharacterTest extends CarrotTestCase
{
    private byte [] utf8Xml = "<root> \u0096 </root>".getBytes(Charsets.UTF_8);

    @Test
    public void testParserHandling() throws Exception
    {
        final StringBuilder chars = new StringBuilder();
        XMLReader r = XMLReaderFactory.createXMLReader();
        r.setErrorHandler(new ErrorHandler() {
            public void warning(SAXParseException e) throws SAXException { throw e; }
            public void fatalError(SAXParseException e) throws SAXException { throw e; }
            public void error(SAXParseException e) throws SAXException { throw e; }
        });
        r.setContentHandler(new DefaultHandler() {
            @Override
            public void characters(char [] ch, int start, int length) throws SAXException {
                chars.append(ch, start, length);
            }
        });

        r.parse(new InputSource(new ByteArrayInputStream(utf8Xml)));
        assertThat(chars.toString()).isEqualTo(" \u0096 ");
    }
    
    @Root(name = "root")
    public static class Example
    {
        @Text()
        public String text;
    }
    
    @Test
    public void testSimpleXmlHandling() throws Exception
    {
        // From bytes.
        Persister p = new Persister();
        Example e = p.read(Example.class, new ByteArrayInputStream(utf8Xml));
        assertThat(e.text).isEqualTo(" \u0096 ");

        // round trip.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        p.write(e, baos);
        e = p.read(Example.class, new ByteArrayInputStream(utf8Xml));
        assertThat(e.text).isEqualTo(" \u0096 ");
    }
}
