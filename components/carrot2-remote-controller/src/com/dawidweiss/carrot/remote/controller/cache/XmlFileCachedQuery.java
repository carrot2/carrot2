
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


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query;
import com.dawidweiss.carrot.util.common.StreamUtils;
import com.dawidweiss.carrot.util.net.URLEncoding;


/**
 * A query cached in a filesystem location somewhere
 */
class XmlFileCachedQuery
    extends CachedQuery
{
    private final File file;
    private final Query query;
    private final String componentId;
    private final Map optionalParams;
    private byte [] data;

    public static void createEntry(File cachedFileName, CachedQuery cachedQuery)
        throws IOException
    {
        final DocumentFactory factory = new DocumentFactory();
        
        Element root = factory.createElement("cached-query");
        Element query = factory.createElement("query");
        root.add(query);

        try
        {
            StringWriter sw = new StringWriter();
            cachedQuery.getQuery().marshal(sw);

            Element qroot = new SAXReader(false).read(new StringReader(sw.toString())).getRootElement();
            qroot.detach();
            query.add(qroot);
        }
        catch (ValidationException e)
        {
            throw new IOException("Cannot validate Query object: " + e.toString());
        }
        catch (MarshalException e)
        {
            throw new IOException("Cannot marshal Query object: " + e.toString());
        }
        catch (DocumentException e)
        {
            throw new IOException("Cannot convert Query object to DOM4j: " + e.toString());
        }

        Element componentId = factory.createElement("componentId");
        root.add(componentId);
        componentId.setText(cachedQuery.getComponentId());

        if (cachedQuery.getOptionalParams() != null)
        {
            Element params = factory.createElement("params");
            root.add(params);

            Map paramsMap = cachedQuery.getOptionalParams();

            for (Iterator i = paramsMap.keySet().iterator(); i.hasNext();)
            {
                Object key = i.next();
                Object value = paramsMap.get(key);
                Element param = factory.createElement("param");
                params.add(param);
                param.addAttribute((String) key, (String) value);
            }
        }

        Element data = factory.createElement("stream");
        root.add(data);

        byte [] bytes = StreamUtils.readFullyAndCloseInput(cachedQuery.getData());
        data.setText(new String(URLEncoding.encode(bytes), "iso8859-1"));

        OutputStream os = new FileOutputStream(cachedFileName);
        try
        {
            OutputFormat fmt = OutputFormat.createCompactFormat();
            fmt.setEncoding("UTF-8");
            final XMLWriter outputter = new XMLWriter(os, fmt);

            outputter.write(root);
        }
        finally
        {
            if (os != null)
            {
                os.close();
            }
        }
    }

    public XmlFileCachedQuery(File cacheFileName)
        throws IOException
    {
        this.file = cacheFileName;

        SAXReader reader = new SAXReader(false);
        try
        {
            Element root = reader.read(cacheFileName).getRootElement();

            if (root.getName().equals("cached-query"))
            {
                Element query = root.element("query");
                if (query == null)
                {
                    throw new IOException("Query subelement required.");
                }

                // deserialize query.
                StringWriter sw = new StringWriter();
                XMLWriter outputter = new XMLWriter(sw);
                outputter.write((Element) query.elements().get(0));

                try
                {
                    this.query = Query.unmarshal(new StringReader(sw.toString()));
                }
                catch (MarshalException e)
                {
                    throw new IOException("Cannot unmarshall Query object: " + e.toString());
                }
                catch (ValidationException e)
                {
                    throw new IOException(
                        "Cannot validate unmarshalled Query object: " + e.toString()
                    );
                }

                Element componentId = root.element("componentId");

                if (componentId == null)
                {
                    throw new IOException("Component Id subelement required.");
                }

                this.componentId = componentId.getText();

                Element optionalParams = root.element("params");

                if (optionalParams == null)
                {
                    this.optionalParams = null;
                }
                else
                {
                    List params = optionalParams.elements("param");
                    this.optionalParams = new HashMap(params.size());

                    for (Iterator i = params.iterator(); i.hasNext();)
                    {
                        Element p = (Element) i.next();
                        this.optionalParams.put(
                            p.attribute("key").getValue(), p.attribute("value").getValue()
                        );
                    }
                }

                Element data = root.element("stream");
                if (data == null)
                {
                    throw new IOException("No cached data stream.");
                }

                byte [] content = data.getText().getBytes("iso8859-1");
                this.data = URLEncoding.decode(content);
            }
            else
            {
                throw new IOException("File is not a cached query.");
            }
        }
        catch (DocumentException e)
        {
            throw new IOException(
                "Cannot deserialize cached query: " + cacheFileName.getAbsolutePath()
                + ", reason: " + e.toString()
            );
        }
        catch (IOException e)
        {
            throw new IOException(
                "Cannot deserialize cached query: " + cacheFileName.getAbsolutePath()
                + ", reason: " + e.getMessage()
            );
        }
    }

    public Query getQuery()
    {
        return query;
    }


    public String getComponentId()
    {
        return componentId;
    }


    public Map getOptionalParams()
    {
        return optionalParams;
    }


    public InputStream getData()
    {
        return new ByteArrayInputStream(data);
    }


    public File getFile()
    {
        return file;
    }
}
