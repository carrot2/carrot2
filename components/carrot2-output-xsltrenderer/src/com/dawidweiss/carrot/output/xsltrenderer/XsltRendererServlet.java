
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.output.xsltrenderer;


import com.dawidweiss.carrot.util.CommonComponentInitializationServlet;
import com.dawidweiss.carrot.util.common.ElapsedTimeTimer;
import com.dawidweiss.carrot.util.http.PostRequestElement;
import com.dawidweiss.carrot.util.http.PostRequestParametersIterator;
import org.apache.log4j.Logger;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 * A servlet with the simplest possible implementation of Carrot2 output type component. It accepts
 * Carrot2 format data at a given URL, and processes them using XSLT.
 */
public class XsltRendererServlet
    extends CommonComponentInitializationServlet
{
    private static final Logger log = Logger.getLogger(XsltRendererServlet.class);

    /** A key to the application context's attributes - reference to the available stylesheets. */
    public static final String XSLT_RENDERER_STYLESHEETS = "@@XSLT_RENDERER_STYLESHEETS@@";

    /** The default stylesheet name */
    private String defaultStylesheet;

    /** A hashmap of all available stylesheets */
    private HashMap stylesheets = new HashMap();

    /** Transformer factory used to compile stylesheets */
    private TransformerFactory tFactory;

    /**
     * If true, the servlet will recheck if the compiled stylesheet is up-to-date and reload it if
     * necessary
     */
    private boolean checkUpToDate = false;

    /**
     * Initializes the servlet - checks whether XSLT processor is available.
     *
     * @param servletConfig Servlet configuration passed from servlet container.
     */
    public void init(ServletConfig servletConfig)
        throws ServletException
    {
        super.init(servletConfig);

        // Get an instance of XSLT transformer factory.
        if (servletConfig.getInitParameter("xslt.transformer.factory") != null)
        {
            try
            {
                tFactory = (TransformerFactory) this.getClass().getClassLoader()
                                                    .loadClass(
                        servletConfig.getInitParameter("xslt.transformer.factory")
                    ).newInstance();
            }
            catch (Exception e)
            {
                throw new ServletException(
                    "XSLT transformer factory could not be instantiated: "
                    + servletConfig.getInitParameter("xslt.transformer.factory"), e
                );
            }
        }
        else
        {
            tFactory = TransformerFactory.newInstance();
        }

        log.debug("Using XSLT Transformer class: " + tFactory.getClass().getName());

        this.checkUpToDate = Boolean.valueOf(servletConfig.getInitParameter("reload")).booleanValue();

        this.reloadStylesheets();
    }


    /**
     * Reloads stylesheets (if needed).
     */
    private final void reloadStylesheets()
        throws ServletException
    {
        HashMap newStylesheets = new HashMap();

        ServletConfig servletConfig = this.getServletConfig();

        // load available stylesheets.
        String stylesheetsDir = servletConfig.getInitParameter("stylesheetsDir");

        if (stylesheetsDir == null)
        {
            log.fatal("Initialization parameter 'stylesheetsDir' not present.");
            throw new ServletException("Initialization parameter 'stylesheetsDir' not present.");
        }

        File directory = new File(servletConfig.getServletContext().getRealPath(stylesheetsDir));

        if (!directory.isDirectory())
        {
            log.fatal(directory.getAbsolutePath() + " not a folder or inaccessible.");
            throw new ServletException(
                directory.getAbsolutePath() + " not a folder or inaccessible."
            );
        }

        File [] files = directory.listFiles(
                new FilenameFilter()
                {
                    public boolean accept(File dir, String name)
                    {
                        return name.endsWith(".xsl");
                    }
                }
            );

        for (int i = 0; i < files.length; i++)
        {
            String name = files[i].getName();

            if (files[i].canRead())
            {
                log.debug("Adding stylesheet: " + name);

                try
                {
                    CompiledStylesheet stylesheet = new CompiledStylesheet(files[i]);
                    newStylesheets.put(stylesheet.getName(), stylesheet);
                }
                catch (TransformerConfigurationException e)
                {
                    log.error("Cannot create translet for stylesheet " + files[i], e);
                }
            }
        }

        defaultStylesheet = servletConfig.getInitParameter("default");

        if (defaultStylesheet == null)
        {
            log.fatal("Initialization parameter 'default' not present.");
            throw new ServletException("Initialization parameter 'default' not present.");
        }

        if (newStylesheets.get(defaultStylesheet) == null)
        {
            log.fatal("Stylesheet " + defaultStylesheet + " does not exist.");
            throw new ServletException("Stylesheet " + defaultStylesheet + " does not exist.");
        }

        stylesheets = newStylesheets;
        servletConfig.getServletContext().setAttribute(XSLT_RENDERER_STYLESHEETS, stylesheets);
    }


    /**
     * Serves an HTTP POST request with XML results in Carrot data exchange format, returning an
     * XML processed with a stylesheet specified in servlet initialization parameters, or runtime
     * configuration. POST in Carrot2 specification of an output component is reserved for
     * processing XML results of a query/ filtering/ clustering. The request <b>must</b> contain
     * one parameter - carrot-xml-stream, which contents is a valid XML stream conforming to the
     * Carrot data exchange specification.
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException
    {
        ElapsedTimeTimer timer = new ElapsedTimeTimer();
        ServletOutputStream outputStream = null;

        try
        {
            ServletInputStream postedData = request.getInputStream();
            Locale responseLocale = request.getLocale();
            outputStream = response.getOutputStream();

            response.setLocale(responseLocale);

            // detect POST message type? We currently don't support multipart POSTs. Only
            // application/x-www-form-urlencoded forms are allowed.
            // Iterate over the parameters passed in POST request, look for carrot XML data.
            //
            // ENCODING OF THE CARROT XML DATA MUST BE PROVIDED IN UTF-8
            Iterator i = new PostRequestParametersIterator(
                    new BufferedInputStream(postedData, /* read buffer */
                        8000
                    ), "iso8859-1"
                );

            String stylesheet;
            Map stylesheetParams = null;

            if (request.getQueryString() != null)
            {
                Map params = parseQueryString(request.getQueryString(), "UTF-8");
                Object x = params.get("stylesheet");

                if (x == null)
                {
                    stylesheet = defaultStylesheet;
                }
                else if (x instanceof String)
                {
                    stylesheet = (String) x;
                }
                else
                {
                    log.warn("More than one stylesheet has been specified: " + x);
                    outputStream.write("Only one stylesheet name can be specified.".getBytes());

                    return;
                }
            }
            else
            {
                stylesheet = defaultStylesheet;
            }

            while (i.hasNext())
            {
                PostRequestElement p = (PostRequestElement) i.next();

                if ("stylesheet".equals(p.getParameterName()))
                {
                    stylesheet = p.getParameterValueAsString();
                }
                else if ("carrot-xchange-data".equals(p.getParameterName()))
                {
                    InputStream carrotData = p.getParameterValueAsInputStream();

                    // run XSLT transformation on the carrot stream.
                    Writer output = new OutputStreamWriter(outputStream, "UTF-8");
                    process(
                        request, new InputStreamReader(carrotData, "UTF-8"), output, stylesheet,
                        stylesheetParams
                    );
                    output.flush();

                    break;
                }
                else
                {
                    if (stylesheetParams == null)
                    {
                        stylesheetParams = new HashMap();
                    }

                    String paramName = p.getParameterName();
                    String paramValue = p.getParameterValueAsString();
                    log.debug("Parameter passed to stylesheet: " + paramName + "=" + paramValue);

                    if (stylesheetParams.put(paramName, paramValue) != null)
                    {
                        throw new IOException(
                            "Cannot pass more than one value of parameter: " + paramName
                            + " to stylesheet."
                        );
                    }
                }
            }
        }
        catch (Exception e)
        {
            if (e instanceof org.xml.sax.SAXParseException)
            {
                processingException(
                    "Cannot parse input (not an XML?): " + e.toString(),
                    HttpServletResponse.SC_BAD_REQUEST, e, response
                );
            }
            else if (e instanceof TransformerException)
            {
                processingException(
                    "XSLT Transformer exception when processing request: " + e.toString(),
                    HttpServletResponse.SC_BAD_REQUEST, e, response
                );
            }
            else if (e instanceof IOException)
            {
                processingException(
                    "IO exception when processing request: " + e.toString(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, response
                );
            }
            else
            {
                processingException(
                    "Internal error: " + e.toString(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e, response
                );
            }
        }
        finally
        {
            try
            {
                if (outputStream != null)
                {
                    outputStream.close();
                }
            }
            catch (Exception e)
            {
            }

            log.debug("Request processed in: " + timer.elapsedString() + " sec.");
        }
    }


    /* -------------------------------------------------------------------- protected methods */

    /**
     * Handling of processing exception. Logs the message to the app logger, then takes care of the
     * response.
     */
    protected void processingException(
        String message, int httpErrorCode, Exception exception, HttpServletResponse response
    )
        throws ServletException
    {
        if (exception != null)
        {
            log.error(message, exception);
        }
        else
        {
            log.error(message);
        }

        // Check if we can rollback, if not, just end the output stream.
        if (!response.isCommitted())
        {
            response.reset();

            try
            {
                response.sendError(httpErrorCode, message);
            }
            catch (IOException ex)
            {
                log.error("Cannot send error header.", ex);
            }
        }
    }


    /**
     * Processes the carrot input stream data with a given stylesheet and dumps the result to some
     * OutputStream.
     */
    protected void process(
        HttpServletRequest request, Reader xmlData, Writer output, String stylesheet, Map params
    )
        throws TransformerException
    {
        ElapsedTimeTimer timer = new ElapsedTimeTimer();

        if ("default".equalsIgnoreCase(stylesheet))
        {
            stylesheet = defaultStylesheet;
        }

        Object compiledStylesheet;

        if ((compiledStylesheet = stylesheets.get(stylesheet)) == null)
        {
            throw new IllegalArgumentException("Stylesheet " + stylesheet + " not found.");
        }

        Transformer transformer = ((CompiledStylesheet) compiledStylesheet).getTranslet()
                                   .newTransformer();

        String sname = request.getServerName();

        if ("localhost".equalsIgnoreCase(sname))
        {
            try
            {
                sname = java.net.InetAddress.getLocalHost().getHostAddress();
            }
            catch (java.net.UnknownHostException e)
            {
                sname = "127.0.0.1";
            }
        }

        transformer.setParameter(
            "base.url",
        	// BUGFIX: We temporarily remove full URL as it is troublesome when
        	// you have a proxy -- the URLs returned are pointing to the actual
        	// server, not the proxy.
        	// The workaround is to return full path (from the root of the server),
        	// the browser should rewrite the URL using the proxy's address. One
        	// problem that remains is that the proxy and the server must have identically
        	// aligned paths to the component.
            // request.getScheme() + "://" + sname + ":" + request.getServerPort() +
            request.getContextPath()
        );
        
        final String ga = System.getProperty("google.analytics");
        if (ga != null) {
        	transformer.setParameter("googleanalytics", ga);
        }

        if (params != null)
        {
            for (Iterator i = params.keySet().iterator(); i.hasNext();)
            {
                String key = (String) i.next();
                transformer.setParameter(key, params.get(key));
            }
        }

        transformer.transform(new StreamSource(xmlData), new StreamResult(output));

        log.debug(
            "Tranforming using stylesheet [" + stylesheet + "] took: " + timer.elapsedString()
        );
    }

    /**
     * This class holds a precompiled stylesheet and stores a link to the original file.
     */
    private class CompiledStylesheet
    {
        private long loadTime;
        private File stylesheetFile;
        private String name;
        private Templates compiled;

        public CompiledStylesheet(File stylesheet)
            throws TransformerConfigurationException
        {
            this.stylesheetFile = stylesheet;
            this.loadTime = stylesheet.lastModified();

            Templates translet = tFactory.newTemplates(new StreamSource(stylesheet));
            name = stylesheetFile.getName();
            name = name.substring(0, name.lastIndexOf(".xsl"));

            this.compiled = translet;
        }

        public Templates getTranslet()
        {
            if (checkUpToDate)
            {
                if (this.loadTime < this.stylesheetFile.lastModified())
                {
                    synchronized (XsltRendererServlet.this.getClass())
                    {
                        log.debug("Reloading stylesheet: " + getName());

                        HashMap stylesheetsCopy = new HashMap();
                        stylesheetsCopy.putAll(stylesheets);

                        try
                        {
                            CompiledStylesheet newcs = new CompiledStylesheet(this.stylesheetFile);
                            stylesheetsCopy.put(this.getName(), newcs);
                            stylesheets = stylesheetsCopy;

                            return newcs.getTranslet();
                        }
                        catch (Exception e)
                        {
                            log.error("Cannot reload stylesheet " + getName(), e);
                        }
                    }
                }
            }

            return compiled;
        }


        public String getName()
        {
            return name;
        }


        public final boolean equals(Object obj)
        {
            if (obj instanceof CompiledStylesheet && ((CompiledStylesheet) obj).name.equals(name))
            {
                return true;
            }
            else
            {
                return false;
            }
        }


        public final int hashCode()
        {
            return name.hashCode();
        }
    }
}
