
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
                        .getResource("solr-to-c2.xsl"));
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
