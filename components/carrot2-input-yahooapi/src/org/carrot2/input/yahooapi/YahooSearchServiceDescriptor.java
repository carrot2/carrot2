
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

package org.carrot2.input.yahooapi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.carrot2.util.httpform.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * A Yahoo search service descriptor.
 * 
 * @author Dawid Weiss
 */
public class YahooSearchServiceDescriptor {
    private final static int DEFAULT_MAXPERQUERY = 100;

    private FormActionInfo formActionInfo;
    private FormParameters formParameters;

    private int maxResultsPerQuery;

    /**
     * Creates an empty descriptor. Initialize it using
     * {@link #initializeFromXML(InputStream)}.
     */
    public YahooSearchServiceDescriptor() {
        maxResultsPerQuery = DEFAULT_MAXPERQUERY;
    }

    /**
     * Creates a descriptor based on the provided specification.
     */
    public YahooSearchServiceDescriptor(FormActionInfo formActionInfo, 
            FormParameters formParameters) {
        this.formActionInfo = formActionInfo;
        this.formParameters = formParameters;
    }

    /**
     * Initializes the descriptor from an XML file. See the unit tests
     * for examples.
     * 
     * Any previous content is erased.
     *  
     * @throws IOException  
     */
    public void initializeFromXML(InputStream xmlStream) throws IOException {
        Document doc;
        final SAXReader reader = new SAXReader(false);
        try {
            doc = reader.read(xmlStream);
        } catch (DocumentException e) {
            throw new IOException("Description parsing error: " + e.toString());
        }

        final Element configuration = doc.getRootElement();
        
        final Element service = configuration.element("service");
        final String SERVICE_ATTR = "max-results-per-query";
        if (service.attribute(SERVICE_ATTR) != null) {
            this.maxResultsPerQuery = Integer.parseInt(
                service.attributeValue(SERVICE_ATTR));
        }

        FormActionInfo formActionInfo = new FormActionInfo(configuration);
        FormParameters formParameters = new FormParameters(configuration.element("parameters"));

        this.formActionInfo = formActionInfo;
        this.formParameters = formParameters;
    }

    /**
     * Returns form action info associated with this service.
     */
    public FormActionInfo getFormActionInfo() {
        return formActionInfo;
    }
    
    /**
     * Returns form parameters used in this service. Certain form
     * parameters may require mappings to real values 
     * (see {@link HTTPFormSubmitter#submit(FormParameters, Map, String)}).
     */
    public FormParameters getFormParameters() {
        return formParameters;
    }

    /**
     * Returns maximum results per query. Currently hardcoded at 50.
     */
    public int getMaxResultsPerQuery() {
        return maxResultsPerQuery;
    }
    
    /**
     * For tests only.
     */
    final void setMaxResultsPerQuery(int value) {
        this.maxResultsPerQuery = value;
    }
}
