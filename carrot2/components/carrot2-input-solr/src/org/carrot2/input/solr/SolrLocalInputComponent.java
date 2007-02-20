/*
 * Copyright (c) 2004 Poznan Supercomputing and Networking Center
 * 10 Noskowskiego Street, Poznan, Wielkopolska 61-704, Poland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Poznan Supercomputing and Networking Center ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into
 * with PSNC.
 */

package org.carrot2.input.solr;

import org.carrot2.input.xml.*;

/**
 * Carrot2 input component for getting search results from a Solr service.
 * 
 * @author stachoo
 */
public class SolrLocalInputComponent
    extends XmlLocalInputComponent
{
    /**
     * Creates a new instance of the Solr input component.
     * 
     * @param solrServiceUrlBase the base url (including protocol, host name,
     * port and path) at which the Solr service is ruinning, e.g.
     * <code>http://localhost:8983/solr/select</code>.
     */
    public SolrLocalInputComponent(String solrServiceUrlBase)
    {
        super(preprocessUrlBase(solrServiceUrlBase)
                        + "/?q=${query}&start=0&rows=${requested-results}&indent=off",
               SolrLocalInputComponent.class
                        .getResourceAsStream("solr-to-c2.xsl"));
    }


    private static String preprocessUrlBase(String solrServiceUrlBase)
    {
        if (solrServiceUrlBase.endsWith("/")) {
            solrServiceUrlBase = solrServiceUrlBase.substring(0,
                    solrServiceUrlBase.length() - 1);
        }
        return solrServiceUrlBase;
    }
}
