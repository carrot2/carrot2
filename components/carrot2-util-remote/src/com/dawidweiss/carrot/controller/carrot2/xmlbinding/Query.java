
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

package com.dawidweiss.carrot.controller.carrot2.xmlbinding;

import java.io.IOException;
import java.io.Writer;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class Query {
    private final String content;

    private final int requestedResults;
    private boolean hasRequestedResults;

    /**
     * Creates a new query object.
     * 
     * @param content The content of the query (a string).
     * @param requestedResults Number of requested results if <code>hasRequestedResults</code> is true.
     * @param hasRequestedResults If <code>false</code>, the query has no requested results.
     */
    public Query(String content, int requestedResults, boolean hasRequestedResults) {
        this.content = content;
        this.requestedResults = requestedResults;
        this.hasRequestedResults = hasRequestedResults;
    }

    /**
     * Unmarshall query from XML format.
     */
    public static Query unmarshal(Element root) throws IllegalArgumentException {
        if (!"query".equals(root.getName())) {
            throw new IllegalArgumentException("XML root is not 'query'");
        }

        final String content = root.getText();
        
        final String requestedResults;
        final Attribute attr = root.attribute("requested-results");
        if (attr != null) {
            requestedResults = attr.getValue();            
        } else {
            requestedResults = null;
        }
        
        boolean hasResults = true;
        int results;
        try {
            results = Integer.parseInt(requestedResults);
        } catch (NumberFormatException e) {
            hasResults = false;
            results = -1;
        }

        return new Query(content, results, hasResults);
    }

    /**
     * Marshall query to XML format.
     */
    public void marshal(Writer sw) throws IOException {
        final OutputFormat format = OutputFormat.createCompactFormat();
        format.setEncoding("UTF-8");
        final XMLWriter writer = new XMLWriter(sw, format);
        final DocumentFactory factory = new DocumentFactory();

        final Element query = factory.createElement("query");
        query.setText(this.content);
        if (this.hasRequestedResults) {
            query.addAttribute("requested-results", Integer.toString(requestedResults));
        }

        writer.write(query);
    }

    public String getContent() {
        return this.content;
    }

    public int getRequestedResults() {
        if (this.hasRequestedResults == false) 
            throw new IllegalStateException("This query has no requested results.");
        return this.requestedResults;
    }

    public boolean hasRequestedResults() {
        return this.hasRequestedResults;
    }
}