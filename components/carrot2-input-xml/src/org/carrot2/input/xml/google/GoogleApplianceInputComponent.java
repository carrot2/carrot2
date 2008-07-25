/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.xml.google;

import java.util.Map;

import org.carrot2.core.ProcessingException;
import org.carrot2.core.RequestContext;
import org.carrot2.input.xml.XmlLocalInputComponent;
import org.carrot2.util.StringUtils;
import org.carrot2.util.resources.ClassResource;

/**
 * Carrot2 input component for getting search results from a Google Appliance XML feed.
 * <p>
 * Note that XSLT transformation from Google search results to C2 format takes place
 * on the Carrot2 side. It would be more efficient to configure the Appliance to return
 * search results in XML suitable for Carrot2 and use identity transformation together with
 * {@link XmlLocalInputComponent}, bypassing XSLT on the Java side. Refer to Google Appliance
 * documentation for details. 
 * 
 * @see http://code.google.com/apis/searchappliance/documentation/50/xml_reference.html#results_xml
 */
public class GoogleApplianceInputComponent extends XmlLocalInputComponent
{
    /**
     * A request-time parameter that specifies the Appliance's XML feed URL, e.g.
     * Value of this parameter must be of type {@link String}.
     */
    public static final String PARAM_GOOGLE_APPLIANCE_SERVICE_URL_BASE = "org.carrot2.input.google.appliance.url.base";

    /**
     * A request-time parameter that specifies the URL to the custom XSLT style sheet to be
     * used to convert Google Appliance's output format to Carrot2 format. Value must be of type
     * {@link java.lang.String} and must be a vaild URL.
     */
    public static final String PARAM_GOOGLE_APPLIANCE_XSLT = "google.appliance.xslt";

    /** Full URL to the service with substitutable parameters. */
    private final String defaultServiceUrl;

    /**
     * Creates an empty instance of the Google appliance input component, where
     * the service URI must be passed at runtime using {@link #PA}.
     */
    public GoogleApplianceInputComponent()
    {
        super(null, new ClassResource(GoogleApplianceInputComponent.class, "google-appliance-to-c2.xsl"));
        defaultServiceUrl = null;
    }
    
    /**
     * Creates a new instance of the Google Appliance input component, given
     * the URL to the search XML feed. The URL should contains substitutable parameters
     * for the query and number of results. Example:
     * <pre>
     * http://appliance.mycompany.com/search?q=${query}&num=${requested-results}&entqr=0&output=xml_no_dtd&client=my_collection&ud=1&oe=UTF-8&ie=UTF-8&site=my_collection&access=p
     * </pre>
     * There should be documentation about parameters and their meaning delivered
     * together with Google Appliance. Note the <code>output=xml_no_dtd</code>
     * parameter - it is crucial to include it to keep high 
     * performance of the XSLT transformation that follows.
     */
    public GoogleApplianceInputComponent(String serviceURL)
    {
        super(null, new ClassResource(GoogleApplianceInputComponent.class, 
            "google-appliance-to-c2.xsl"));

        defaultServiceUrl = serviceURL;
    }

    /**
     * 
     */
    public void startProcessing(RequestContext requestContext) throws ProcessingException
    {
        final Map params = requestContext.getRequestParameters();

        final String serviceUrlBase = (String) params.get(PARAM_GOOGLE_APPLIANCE_SERVICE_URL_BASE);

        if (serviceUrlBase != null)
        {
            params.put(XmlLocalInputComponent.PARAM_SOURCE_XML, serviceUrlBase);
        }
        else
        {
            params.put(XmlLocalInputComponent.PARAM_SOURCE_XML, defaultServiceUrl);
        }

        if (defaultXSLT == null)
        {
            final String customXsltUrl = (String) params.get(PARAM_GOOGLE_APPLIANCE_XSLT);
            if (StringUtils.isBlank(customXsltUrl))
            {
                // This means the caller did not comply with the class contract: used
                // the empty constructor, but did not provide valid parameters.
                throw new ProcessingException("Required parameter missing: "
                    + PARAM_GOOGLE_APPLIANCE_XSLT);
            }

            params.put(XmlLocalInputComponent.PARAM_XSLT, customXsltUrl);
        }

        super.startProcessing(requestContext);
    }
}
