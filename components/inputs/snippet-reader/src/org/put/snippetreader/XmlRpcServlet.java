

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


package org.put.snippetreader;


import com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query;
import com.dawidweiss.carrot.util.Log4jStarter;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcServer;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.put.snippetreader.readers.WebSnippetReader;
import org.put.util.exception.ExceptionHelper;
import org.put.util.io.FileHelper;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Main SnippetReader servlet. Serves POST requests (XMLRPC and Carrot2, depending on the URL).
 */
public class XmlRpcServlet
    extends javax.servlet.http.HttpServlet
{
    private static final Logger log = Logger.getLogger(XmlRpcServlet.class);
    private XmlRpcServer xmlrpc;
    private String errorMessage;
    private HashMap registeredServices = new HashMap();
    private HashMap serviceFiles = new HashMap();

    /**
     * This class is exposed as a 'meta' information provider. It, among others, is capable of
     * telling what other services have been registered and are available.
     */
    protected class ServiceMetaInfo
    {
        public Vector getServicesList()
        {
            Set services = registeredServices.keySet();

            return new Vector(services);
        }


        public Vector getVerboseServicesList()
        {
            Set services = registeredServices.keySet();
            Vector all = new Vector(services.size());

            for (Iterator i = services.iterator(); i.hasNext();)
            {
                String serviceName = (String) i.next();
                Hashtable serviceAttrs = new Hashtable();

                serviceAttrs.put("name", serviceName);
                serviceAttrs.put("class", registeredServices.get(serviceName).getClass().getName());

                if (registeredServices.get(serviceName) instanceof WebSnippetReader)
                {
                    serviceAttrs.put("Carrot2", "yes");
                }

                // find out about object's methods.
                Object o = registeredServices.get(serviceName);
                Method [] methods = o.getClass().getDeclaredMethods();

                for (int j = 0; j < methods.length; j++)
                {
                    if (Modifier.isPublic(methods[j].getModifiers()))
                    {
                        serviceAttrs.put(
                            "public method: " + methods[j].getName(), methods[j].toString()
                        );
                    }
                }

                all.add(serviceAttrs);
            }

            return all;
        }


        public String getServiceConfig(String service)
            throws Exception
        {
            Object f = serviceFiles.get(service);

            if (f != null)
            {
                FileInputStream is = new FileInputStream((File) f);
                byte [] b = FileHelper.readFullyAndCloseInput(is);

                return new String(b);
            }
            else
            {
                throw new Exception("No such service.");
            }
        }


        /**
         * Updates service's configuration.
         */
        public boolean updateServiceConfig(String service, String newConfigXml)
            throws Exception
        {
            Object f = serviceFiles.get(service);

            if (f != null)
            {
                // attempt to parse the XML. As for now there's no validation, but maybe in the future..
                SAXBuilder builder = new SAXBuilder();
                Document config;
                builder.setValidation(false);

                try
                {
                    config = builder.build(new StringReader(newConfigXml));
                }
                catch (JDOMException e)
                {
                    StringBuffer msg = new StringBuffer();
                    msg.append("Service XML does not parse correctly.\n");
                    msg.append(e.getMessage());
                    msg.append(ExceptionHelper.getStackTrace(e));
                    throw new Exception(msg.toString());
                }

                // update the service engine.
                WebSnippetReader snippetReaderService = new WebSnippetReader(
                        config.getRootElement()
                    );

                FileOutputStream os = new FileOutputStream((File) f);

                try
                {
                    os.write(newConfigXml.getBytes());
                }
                finally
                {
                    os.close();
                }

                xmlrpc.removeHandler(service);
                xmlrpc.addHandler(service, snippetReaderService);
                registeredServices.put(service, snippetReaderService);
            }
            else
            {
                throw new Exception("No such service.");
            }

            return true;
        }
    }

    /**
     * Initialization of service provider.
     */
    public void init(ServletConfig servletConfig)
        throws ServletException
    {
        Log4jStarter.getLog4jStarter().initializeLog4j(servletConfig);

        XmlRpc.setDriver(com.icl.saxon.aelfred.SAXDriver.class);
        XmlRpc.setEncoding("UTF-8");

        xmlrpc = new XmlRpcServer();

        try
        {
            String servicesPath = servletConfig.getInitParameter("services");

            if (servicesPath == null)
            {
                errorMessage = "Required parameter 'services' not found.";
                log(errorMessage);

                return;
            }

            File servicesDir = new File(
                    servletConfig.getServletContext().getRealPath(servicesPath)
                );

            if (servicesDir.isDirectory() == false)
            {
                errorMessage = "Services parameter does not point to a directory.";
                log(errorMessage);

                return;
            }

            File [] services = servicesDir.listFiles(
                    new java.io.FilenameFilter()
                    {
                        public boolean accept(File dir, String name)
                        {
                            if (name.endsWith(".xml"))
                            {
                                return true;
                            }
                            else
                            {
                                return false;
                            }
                        }
                    }
                );

            for (int i = 0; i < services.length; i++)
            {
                try
                {
                    // add an instance of this class as default handler
                    SAXBuilder builder = new SAXBuilder();
                    Document config;
                    builder.setValidation(false);
                    config = builder.build(services[i]);

                    WebSnippetReader snippetReaderService = new WebSnippetReader(
                            config.getRootElement()
                        );

                    String serviceName = services[i].getName();
                    serviceName = serviceName.substring(0, serviceName.lastIndexOf(".xml"));

                    xmlrpc.addHandler(serviceName, snippetReaderService);
                    registeredServices.put(serviceName, snippetReaderService);
                    serviceFiles.put(serviceName, services[i]);

                    log.info("Added service " + serviceName);
                }
                catch (Exception e)
                {
                    log.error("Exception when adding service: " + services[i].getName(), e);
                }
            }

            // Add the 'meta' information object to the list of services.
            ServiceMetaInfo si = new ServiceMetaInfo();
            xmlrpc.addHandler("_meta", si);
            registeredServices.put("_meta", si);
        }
        catch (Exception x)
        {
            log.fatal("Exception initializing XmlRpcServlet.", x);
        }
    }


    /**
     * Map the POST request to an XmlRpc or Carrot2 call.
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        String method = req.getPathInfo();

        req.setCharacterEncoding("UTF-8");

        if ((method != null) && method.startsWith("/carrot2"))
        {
            String serviceName = method.substring(method.indexOf("/", 1) + 1);

            log.debug("Processing request method: " + method + ", service name: " + serviceName);

            // process Carrot2 request.
            Object service = registeredServices.get(serviceName);

            if ((service == null) || !(service instanceof WebSnippetReader))
            {
                // this will throw a parser exception on client side, but
                // at least indicate the cause of the error.
                res.getOutputStream().write(
                    ("Service " + service + " not available or does not support Carrot2.").getBytes()
                );
            }
            else
            {
                try
                {
                    // process request. Let container parse POST parameters (they're not streams anyway).
                    String reqxml = req.getParameter("carrot-request");

                    Query query = null; /* JBuilder complains about uninitialized variable if we do a direct assignment here. */
                    query = Query.unmarshal(new StringReader(reqxml));

                    OutputStream output = res.getOutputStream();
                    Writer w = new OutputStreamWriter(output, "UTF-8");

                    ((WebSnippetReader) service).getSnippetsAsCarrot2XML(
                        w, query.getContent(),
                        query.hasRequestedResults() ? query.getRequestedResults()
                                                    : 100
                    );

                    w.flush();
                    output.close();
                }
                catch (Exception e)
                {
                    log.error("Exception when processing request.", e);
                }
            }
        }
        else
        {
            // apply XMLRPC call
            byte [] result = xmlrpc.execute(req.getInputStream());

            res.setContentType("text/xml");
            res.setContentLength(result.length);

            OutputStream output = res.getOutputStream();

            output.write(result);
            output.flush();
        }
    }


    /**
     * Return an information page in return for a GET request (a regular browser may issue such
     * request).
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        res.sendRedirect(req.getContextPath() + "/listServices.jsp");
    }


    /**
     * Returns the base SnippetReader Service URL.
     */
    public static String getSnippetReaderServiceURL(HttpServletRequest request)
    {
        return request.getScheme() + "://" + request.getServerName() + ":"
        + request.getServerPort() + request.getContextPath() + "/extractor";
    }
}
