
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.remote.controller.cache;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.BasicConfigurator;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXWriter;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 * Rotates files from one cache to a chain of other caches.
 */
public class CacheRotator
{
    public CacheRotator()
    {
    }

    public void addQueryForAll(CachedQueriesContainer from, CachedQueriesContainer to)
        throws ParserConfigurationException, IOException, SAXException
    {
        for (Iterator i = from.getCachedElementSignatures(); i.hasNext();)
        {
            Object signature = i.next();
            CachedQuery q = from.getCachedElement(signature);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.parse(q.getData());

            if (document.getElementsByTagName("searchresult").getLength() != 1)
            {
                throw new RuntimeException("This query cannot be converted: " + q.getSignature());
            }
            else
            {
                Node node = document.getElementsByTagName("searchresult").item(0);
                Node child = document.createElement("query");
                child.appendChild(document.createTextNode(q.getQuery().getContent()));

                if (q.getQuery().hasRequestedResults())
                {
                    Node attr = document.createAttribute("requested-results");
                    attr.setNodeValue(Integer.toString(q.getQuery().getRequestedResults()));
                    child.getAttributes().setNamedItem(attr);
                }

                node.insertBefore(child, node.getFirstChild());

                StringWriter sw = new StringWriter();
                OutputFormat fmt = OutputFormat.createCompactFormat();
                fmt.setEncoding("UTF-8");
                final XMLWriter writer = new XMLWriter(sw, fmt);
                writer.write(document.getDocumentElement());

                MemoryCachedQuery mq = new MemoryCachedQuery(
                        q.getQuery(), q.getComponentId(), q.getOptionalParams(),
                        sw.getBuffer().toString().getBytes("UTF-8")
                    );
                to.addToCache(mq);
            }
        }
    }


    public void copyAll(CachedQueriesContainer from, CachedQueriesContainer to)
    {
        for (Iterator i = from.getCachedElementSignatures(); i.hasNext();)
        {
            Object signature = i.next();
            CachedQuery q = from.getCachedElement(signature);
            System.out.println("Copying..." + q.getQuery());

            if (q != null)
            {
                to.addToCache(q);
            }
        }
    }


    public void containsAll(CachedQueriesContainer source, CachedQueriesContainer dest)
        throws IOException
    {
        for (Iterator i = source.getCachedElementSignatures(); i.hasNext();)
        {
            Object signature = i.next();
            CachedQuery q = source.getCachedElement(signature);
            CachedQuery q2 = dest.getCachedElement(signature);

            InputStream is1 = q.getData();
            InputStream is2 = q2.getData();

            int pos = 0;

            while (true)
            {
                pos++;

                int a = is1.read();
                int b = is2.read();

                if (a != b)
                {
                    System.out.println(pos);
                    System.out.println((char) a);
                    System.out.println((char) b);
                    throw new RuntimeException(
                        "The files differ for query: " + q.getSignature() + " " + a + " != " + b
                    );
                }

                if (a == -1)
                {
                    is1.close();
                    is2.close();

                    break;
                }
            }
        }
    }


    public static void main(String [] args)
        throws Exception
    {
        BasicConfigurator.configure();

        CacheRotator rotator = new CacheRotator();

        AbstractFilesystemCachedQueriesContainer from = new ZIPCachedQueriesContainer();
        from.setReadOnly(true);
        from.setAbsoluteDir("f:\\from");
        from.configure();

        AbstractFilesystemCachedQueriesContainer to = new ZIPCachedQueriesContainer();
        to.setAbsoluteDir("f:\\to");
        to.configure();

        rotator.addQueryForAll(from, to);
    }
}
