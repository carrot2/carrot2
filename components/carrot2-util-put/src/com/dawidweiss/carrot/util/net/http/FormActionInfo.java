package com.dawidweiss.carrot.util.net.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.jdom.Element;
import com.dawidweiss.carrot.util.jdom.JDOMHelper;


/**
 * Objects of this class describe where and how form parameters are submitted. Service's URL
 * and HTTP method (POST/ GET) must be specified. Additional HTTP headers may also be
 * specified.
 *
 * This object can be initialized using JDOM's XML Element. The XML structure
 * is explained in the constructor below.
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
     * Initialize using JDOM's XML fragment. The XML must follow this structure:
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
     * @param initFromXML JDOM's Element with initialization parameters.
     *        See the example XML configuration file.
     * @throws WrappedException
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


    // ------------------------------------------------------- protected section


    /**
     * Initializes this object using an XML Element.
     * @param qr JDOM's Element with initialization parameters.
     */
    protected void initInstanceFromXML(Element qr)
            throws MalformedURLException
    {
        final String SERVICE_URL_ATT    = "/request/service#url";
        final String SERVICE_METHOD_ATT = "/request/service#method";
        final String HTTP_HEADER_NODE   = "/request/http-overrides/header";
        final String HTTP_HEADER_NAME   = "name";

        serviceURL    = new URL(JDOMHelper.getStringFromJDOM(SERVICE_URL_ATT, qr, true));
        serviceMethod = JDOMHelper.getStringFromJDOM(SERVICE_METHOD_ATT, qr, true);

        // retrieve http header overrides
        List httpHeaders = JDOMHelper.getElements(HTTP_HEADER_NODE, qr);

        if (httpHeaders != null)
        {
            for (ListIterator param = httpHeaders.listIterator(); param.hasNext(); )
            {
                Element paramElement = (Element) param.next();

                // get the name of the parameter
                String header = paramElement.getAttribute(HTTP_HEADER_NAME).getValue();
                String value  = paramElement.getText();

                this.httpHeaders.put(header, value);
            }
        }
    }
}



