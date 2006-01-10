
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

package com.dawidweiss.carrot.input.snippetreader.service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dawidweiss.carrot.util.net.http.FormActionInfo;
import com.dawidweiss.carrot.util.net.http.FormParameters;
import com.dawidweiss.carrot.util.net.http.HTTPFormSubmitter;
import com.dawidweiss.carrot.util.net.http.Parameter;


/**
 * A class wrapping search service descriptor file
 * (an XML, see examples for details).
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class SearchService {

    private FormActionInfo formAction;
	private FormParameters formParameters;
	private String requestEncoding;

    /**
     * Creates an uninitialized instance. Initialize
     * using {@link #initialize(Document)}.
     */
    protected SearchService() {
    }

	/**
     * Creates a new instance out of a file
     * reference to the XML file with the service
     * descriptor.
     * 
     * @param descriptorFile XML file with service descriptor.
     * @throws IOException if XML cannot be found or parsed.
     */
	public SearchService(File descriptorFile) throws IOException {
        this(new FileInputStream(descriptorFile));
	}


    /**
     * Creates a new instance out of an input stream
     * with the XML file with the service descriptor.
     * 
     * @param descriptorStream A data stream with the
     * descriptor (XML).
     * @throws IOException if XML cannot be parsed.
     */
    public SearchService(InputStream descriptorStream) throws IOException {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(descriptorStream);
            initialize(document);
        } catch (DocumentException e) {
            throw new IOException("Error parsing descriptor file:" +
                    " " + e.toString());
        }
    }
    
    
	/**
     * Initializes the service with the data from
     * a descriptor XML document.
     * 
	 * @param document A DOM4J document with the service's
     * descriptor.
	 */
	protected void initialize(Document document) throws IllegalArgumentException {
        
        String requestEncoding =
            document.selectSingleNode("service/request/@encoding").getStringValue();
        String serviceUrl =
            document.selectSingleNode("service/request/service/@url").getStringValue();
        String method = 
            document.selectSingleNode("service/request/service/@method").getStringValue();

        FormActionInfo formAction;
        try {
            formAction = new FormActionInfo(new URL(serviceUrl), method);
            List httpHeaders = document.selectNodes("service/request/http-overrides/header");
            for (Iterator i=httpHeaders.iterator(); i.hasNext();) {
                Element node = (Element) i.next();
                String headerName = node.attributeValue("name");
                String headerValue = node.getTextTrim();
                formAction.setHttpHeader(headerName, headerValue);
            }
		} catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed service URL: " + serviceUrl);
		}
 
        // Collect parameters. 
        FormParameters formParameters = new FormParameters();
        List list = document.selectNodes("service/request/parameters/parameter");
        for (Iterator i = list.iterator(); i.hasNext() ; ) {
            Element paramElement = (Element) i.next();
            String parameterName = paramElement.attributeValue("name");
            // check whether constant-valued, or mapped
            if (paramElement.attributeValue("value") != null) {
                formParameters.addParameter(
                        new Parameter(parameterName, paramElement.attributeValue("value"), false));
            } else if (paramElement.attributeValue("mapto") != null) {
                formParameters.addParameter(
                        new Parameter(parameterName, paramElement.attributeValue("mapto"), true));
            } else {
                throw new IllegalArgumentException("Illegal XML structure: argument type unknown.");
            }
        }
        
        this.formAction = formAction;
        this.formParameters = formParameters;
        this.requestEncoding = requestEncoding;
	}

    /**
     * Submits a form using the descriptor file used to create an
     * object of this class and mapping mapped parameters to those
     * contained in the map <code>mappedParameters</code>.
     * 
     * <b>The calling thread must always close the input stream.</b> 
     * 
     * @return Returns an array of bytes returned from the server.
     * @throws IOException Thrown if an error occurred.
     */
    public InputStream getRawPage(Map mappedParameters) throws IOException {
        HTTPFormSubmitter submitter = new HTTPFormSubmitter(formAction);
        return submitter.submit(this.formParameters, mappedParameters, requestEncoding);
    }
}
