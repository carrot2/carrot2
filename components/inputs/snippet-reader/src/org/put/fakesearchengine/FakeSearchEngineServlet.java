

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


package org.put.fakesearchengine;


import java.io.*;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.put.util.io.FileHelper;


/**
 * Mimics the behavior of a search engine. Accepts POST and GET queries, returns some static data.
 */
public class FakeSearchEngineServlet
    extends javax.servlet.http.HttpServlet
{
    private final String FAKE_VIRT_PATH = "/fakes";

    public void serve(HttpServletRequest req, HttpServletResponse res)
        throws javax.servlet.ServletException, java.io.IOException
    {
        OutputStream os = res.getOutputStream();
        ServletConfig servletConfig = getServletConfig();

        if (req.getParameter("response") == null)
        {
            StringBuffer info = new StringBuffer();
            info.append(
                "<HTML><BODY><H1>FakeService</H1><P>Please pass HTTP parameter <code>response</code> with one of the following values:</P>"
            );

            File fakesDir = new File(servletConfig.getServletContext().getRealPath(FAKE_VIRT_PATH));

            if (fakesDir.isDirectory() == true)
            {
                File [] files = fakesDir.listFiles();
                info.append("<ul>");

                for (int i = 0; i < files.length; i++)
                {
                    info.append("<li><b>" + files[i].getName() + "</b></li>");
                }

                info.append("</ul>");
            }
            else
            {
                log("Fake responses parameter does not point to a directory.");
            }

            info.append("</body></html>");
            os.write(info.toString().getBytes());

            return;
        }
        else
        {
            File fake = new File(
                    servletConfig.getServletContext().getRealPath(
                        FAKE_VIRT_PATH + '/' + req.getParameter("response")
                    )
                );

            if ((fake.exists() == false) || (fake.isFile() == false))
            {
                log("Requested response file " + req.getParameter("response") + " doesn't exist.");

                return;
            }

            InputStream fis = new FileInputStream(fake);
            byte [] all = FileHelper.readFullyAndCloseInput(fis);

            if (
                (req.getHeader("Accept-Encoding") != null)
                    && (req.getHeader("Accept-Encoding").indexOf("gzip") != -1)
            )
            {
                res.addHeader("Content-Encoding", "gzip");
                os = new GZIPOutputStream(os);
            }

            os.write(all);
            os.flush();
            os.close();
        }
    }


    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws javax.servlet.ServletException, java.io.IOException
    {
        serve(req, resp);
    }


    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws javax.servlet.ServletException, java.io.IOException
    {
        serve(req, resp);
    }
}
