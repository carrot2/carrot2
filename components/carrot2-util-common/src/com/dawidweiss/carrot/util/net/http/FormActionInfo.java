
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
package com.dawidweiss.carrot.util.net.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.dom4j.Element;


/**
 * Objects of this class describe where and how form parameters are submitted. Service's URL
 * and HTTP method (POST/ GET) must be specified. Additional HTTP headers may also be
 * specified.
 *
 * This object can be initialized using DOM4J's XML Element.
 *
 * @author Dawid Weiss
 */
public class FormActionInfo
{
    protected URL     serviceURL;
    protected String  serviceMethod;
    protected HashMap httpHeaders = new HashMap();


    /**
     * Initialize with constant values. Service method should be a valid HTTP method (POST or GET).
     */
    public FormActionInfo(URL serviceURL, String method)
    {
        this.serviceURL    = serviceURL;
        this.serviceMethod = method;
    }


    /**
     * Initialize using DOM4j XML fragment. The XML must follow this structure:
     *
     * <PRE>
     * &lt;request&gt;
     *     &lt;!-- Query processing service.
     *
     *         Specify the base url of the script/ host, which processes queries
     *         (don't include any parameters - they will be defined later).
     *
     *         Also, specify the method of posting parameters to the service,
     *         either "post" or "get" are available.
     *    --&gt;
     *    &lt;service url="http://www.google.com/search" method="get" /&gt;
     *
     *    &lt;!-- override the http-headers you need here --&gt;
     *    &lt;http-overrides&gt;
     *        &lt;header name="User-Agent"&gt;Mozilla/5.0 (X11; U; Linux i686; en-US; rv:0.9.7) Gecko/20011221&lt;/header&gt;
     *        &lt;header name="Accept-Charset"&gt;ISO-8859-1&lt;/header&gt;
     *    &lt;/http-overrides&gt;
     *  &lt;/request&gt;
     * </PRE>
     *
     * @param initFromXML DOM4j Element with initialization parameters.
     *        See the example XML configuration file.
     */
    public FormActionInfo(Element initFromXML)
        throws MalformedURLException
    {
        initInstanceFromXML(initFromXML);
    }


    /**
     * Returns the url of the service to which the request should be made.
     * @return Returns the url of the service to which the request should be made.
     */
    public URL getServiceURL()
    {
        return serviceURL;
    }


    /**
     * Returns the HTTP method of the service: POST or GET.
     *
     * @return Returns the HTTP method of the service: POST or GET.
     */
    public String getMethod()
    {
        return serviceMethod;
    }


    /**
     * Returns a hashmap with http headers to be overriden in the request
     *
     * @return Returns a hashmap with http headers to be overriden in the request.
     */
    public HashMap getHttpHeaders()
    {
        return httpHeaders;
    }


    /**
     * Adds a custom HTTP header to be submitted with requests made to the service described
     * by this object.
     */
    public void setHttpHeader(String headerName, String value)
    {
        httpHeaders.put(headerName, value);
    }


    /**
     * Initializes this object using an XML Element.
     * @param qr DOM4j's Element with initialization parameters.
     */
    protected void initInstanceFromXML(Element qr)
            throws MalformedURLException
    {
        if (!"request".equals(qr.getName())) throw new RuntimeException("Expected 'request' element.");

        serviceURL    = new URL(qr.selectSingleNode("service/@url").getText());
        serviceMethod = qr.selectSingleNode("service/@method").getText();

        // retrieve http header overrides
        final List httpHeaders = qr.selectNodes("http-overrides/header");
        if (httpHeaders != null)
        {
            for (ListIterator param = httpHeaders.listIterator(); param.hasNext(); )
            {
                Element paramElement = (Element) param.next();

                // get the name of the parameter
                String header = paramElement.attributeValue("name");
                String value  = paramElement.getText();

                this.httpHeaders.put(header, value);
            }
        }
    }
}