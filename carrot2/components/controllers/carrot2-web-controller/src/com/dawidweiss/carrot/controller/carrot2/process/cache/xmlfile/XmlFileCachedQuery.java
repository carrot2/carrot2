

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.controller.carrot2.process.cache.xmlfile;


import com.dawidweiss.carrot.controller.carrot2.process.cache.*;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query;
import org.exolab.castor.xml.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import java.io.*;
import java.util.*;


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
        XMLOutputter outputter = new XMLOutputter();
        outputter.setEncoding("UTF-8");
        outputter.setIndent("  ");

        Element root = new Element("cached-query");

        Element query = new Element("query");
        root.addContent(query);

        try
        {
            StringWriter sw = new StringWriter();
            cachedQuery.getQuery().marshal(sw);

            Element qroot = new SAXBuilder(false).build(new StringReader(sw.toString()))
                                                 .getRootElement();
            query.addContent(qroot);
        }
        catch (ValidationException e)
        {
            throw new IOException("Cannot validate Query object: " + e.toString());
        }
        catch (MarshalException e)
        {
            throw new IOException("Cannot marshal Query object: " + e.toString());
        }
        catch (JDOMException e)
        {
            throw new IOException("Cannot convert Query object to JDOM: " + e.toString());
        }

        Element componentId = new Element("componentId");
        root.addContent(componentId);
        componentId.setText(cachedQuery.getComponentId());

        if (cachedQuery.getOptionalParams() != null)
        {
            Element params = new Element("params");
            root.addContent(params);

            Map paramsMap = cachedQuery.getOptionalParams();

            for (Iterator i = paramsMap.keySet().iterator(); i.hasNext();)
            {
                Object key = i.next();
                Object value = paramsMap.get(key);
                Element param = new Element("param");
                params.addContent(param);
                param.setAttribute((String) key, (String) value);
            }
        }

        Element data = new Element("stream");
        root.addContent(data);

        byte [] bytes = org.put.util.io.FileHelper.readFullyAndCloseInput(cachedQuery.getData());
        data.setText(new String(org.put.util.net.URLEncoding.encode(bytes), "iso8859-1"));

        OutputStream os = new FileOutputStream(cachedFileName);

        try
        {
            outputter.output(root, os);
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

        SAXBuilder reader = new SAXBuilder(false);

        try
        {
            Element root = reader.build(cacheFileName).getRootElement();

            if (root.getName().equals("cached-query"))
            {
                Element query = root.getChild("query");

                if (query == null)
                {
                    throw new IOException("Query subelement required.");
                }

                // deserialize query.
                XMLOutputter outputter = new XMLOutputter();
                StringWriter sw = new StringWriter();
                outputter.output((Element) query.getChildren().get(0), sw);

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

                Element componentId = root.getChild("componentId");

                if (componentId == null)
                {
                    throw new IOException("Component Id subelement required.");
                }

                this.componentId = componentId.getText();

                Element optionalParams = root.getChild("params");

                if (optionalParams == null)
                {
                    this.optionalParams = null;
                }
                else
                {
                    List params = optionalParams.getChildren("param");
                    this.optionalParams = new HashMap(params.size());

                    for (Iterator i = params.iterator(); i.hasNext();)
                    {
                        Element p = (Element) i.next();
                        this.optionalParams.put(
                            p.getAttribute("key").getValue(), p.getAttribute("value").getValue()
                        );
                    }
                }

                Element data = root.getChild("stream");

                if (data == null)
                {
                    throw new IOException("No cached data stream.");
                }

                byte [] content = data.getText().getBytes("iso8859-1");
                this.data = org.put.util.net.URLEncoding.decode(content);
            }
            else
            {
                throw new IOException("File is not a cached query.");
            }
        }
        catch (JDOMException e)
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
