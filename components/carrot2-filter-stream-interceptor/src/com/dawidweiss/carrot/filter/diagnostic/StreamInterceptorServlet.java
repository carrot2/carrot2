

/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.filter.diagnostic;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dawidweiss.carrot.util.common.StreamUtils;


/**
 * This filter read all of POSTed data and save it to a log file. This filter will return the input
 * stream as it was (and thus will probably cause an exception in the subsequent filter, because
 * it also returns POST headers.
 */
public class StreamInterceptorServlet
    extends com.dawidweiss.carrot.filter.FilterRequestProcessorServlet
{
    protected File outputDirectory;
    private static volatile int counter;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");

    public void init(ServletConfig servletConfig)
        throws ServletException
    {
        initialize(servletConfig);
        log.info("Stream interceptor will dump data to: " + outputDirectory.getAbsolutePath());
    }


    protected void initialize(ServletConfig servletConfig)
        throws ServletException
    {
        String outputDir;

        if ((outputDir = servletConfig.getInitParameter("intercepted.streams.folder")) == null)
        {
	        outputDir = System.getProperty("resin.home");
	        if (outputDir == null)
	        {
		        outputDir = System.getProperty("catalina.home");
		        if (outputDir == null)
		        {
			        outputDir = System.getProperty("catalina.base");
		        }
	        }
	        
            if (outputDir == null)
            {
                throw new ServletException(
                    "Could not locate output directory for intercepted streams."
                    + " catalina.base system property not available, intercepted.streams.folder servlet"
                    + " parameter not available."
                );
            }

            File logDir = new File(outputDir, "logs");

            if (logDir.exists() && logDir.isDirectory())
            {
                outputDir = logDir.getAbsolutePath();
            }
        }

        outputDirectory = new File(outputDir);

        if (!outputDirectory.exists() || !outputDirectory.isDirectory())
        {
            throw new ServletException(
                "Output directory does not exist: " + outputDirectory.getAbsolutePath()
            );
        }
    }


    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        InputStream is = req.getInputStream();

        try
        {
            byte [] bytes = StreamUtils.readFully(is);

            OutputStream os = res.getOutputStream();
            os.write(bytes);
            os.close();

            FileOutputStream fos = new FileOutputStream(getNextStreamFile());
            fos.write(bytes);
            fos.close();
        }
        catch (IOException e)
        {
            log.error("Cannot intercept the stream.", e);
        }
    }


    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        OutputStream output = res.getOutputStream();
        Writer w = new OutputStreamWriter(output, "UTF-8");
        w.write("<html><body>Stream Interceptor works here.</body></html>");
        w.close();
    }


    protected File getNextStreamFile()
    {
        synchronized (this.getClass())
        {
            File f = new File(
                    outputDirectory.getAbsolutePath() + File.separatorChar + "full-stream-"
                    + this.dateFormat.format(new Date()) + "-stream." + (counter++)
                );
            log.debug("Intercepting stream to: " + f.getAbsolutePath());

            return f;
        }
    }
}
