
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

package com.dawidweiss.carrot.util;


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;


/**
 * This is a base abstract servlet class, which performs initialization tasks common for all
 * components. These include:
 * 
 * <ul>
 * <li>
 * Initialization of log4j, if servlet initialization parameter <code>log4j.properties</code> has
 * been defined.
 * </li>
 * </ul>
 */
public class CommonComponentInitializationServlet
    extends HttpServlet
{
    /**
     * Initialize servlet context. See class description for initialization tasks performed.
     *
     * @param servletConfig Servlet configuration passed from servlet container.
     */
    public void init(ServletConfig servletConfig)
        throws ServletException
    {
        super.init(servletConfig);

        String log4jConfig = servletConfig.getInitParameter("log4j.properties");

        if (log4jConfig != null)
        {
            // Initialize log4j according to the specified configuration file.
            Log4jStarter.getLog4jStarter().initializeLog4j(this.getServletConfig());
        }
    }


    /**
     * Parses a query string of the form key=value&key=value... Both keys and values are
     * URL-decoded assuming <code>encoding</code> character set was used during encoding.
     *
     * @return A Map of keys to values. If a parameter has single value, it is returned as a
     *         string, if it has more than one value, the returned mapping will be a List
     *         instance.
     */
    public static Map parseQueryString(String queryString, String encoding)
    {
        if (queryString == null)
        {
            throw new IllegalArgumentException();
        }

        HashMap parameters = new HashMap();
        StringTokenizer st = new StringTokenizer(queryString, "&");

        while (st.hasMoreTokens())
        {
            String token = st.nextToken();
            String key;
            String value;

            int divider = token.indexOf('=');

            try
            {
                if (divider == -1)
                {
                    key = com.dawidweiss.carrot.util.net.URLEncoding.decode(token, encoding);
                    value = "";
                }
                else
                {
                    key = com.dawidweiss.carrot.util.net.URLEncoding.decode(
                            token.substring(0, divider), encoding
                        );
                    value = com.dawidweiss.carrot.util.net.URLEncoding.decode(
                            token.substring(divider + 1), encoding
                        );
                }
            }
            catch (UnsupportedEncodingException e)
            {
                throw new RuntimeException(
                    "Encoding: " + encoding + " unsupported on your Java platform."
                );
            }

            Object prevValue = parameters.get(key);

            if (prevValue != null)
            {
                List t = new LinkedList();
                t.add(prevValue);
                t.add(value);
                parameters.put(key, t);
            }
            else
            {
                parameters.put(key, value);
            }
        }

        return parameters;
    }
}
