/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.xml;

import org.carrot2.core.ProcessingException;
import org.carrot2.core.RequestContext;

public class ParameterizableUrlXmlLocalInputComponent extends XmlLocalInputComponent
{
    private final String sourceUrl;

    public ParameterizableUrlXmlLocalInputComponent(String url, Object xslt,
        String queryEncoding)
    {
        super(null, xslt, queryEncoding);
        this.sourceUrl = url;
    }

    public void startProcessing(RequestContext requestContext) throws ProcessingException
    {
        requestContext.getRequestParameters().put(
            XmlLocalInputComponent.PARAM_SOURCE_XML, sourceUrl);
        super.startProcessing(requestContext);
    }
}
