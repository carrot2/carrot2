

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.mwroblewski.carrot;


import com.dawidweiss.carrot.filter.FilterRequestProcessor;
import com.mwroblewski.carrot.filter.ahcfilter.AHCFilter;
import com.mwroblewski.carrot.filter.termsfilter.TermsFilter;
import org.apache.log4j.Logger;
import java.io.*;
import java.util.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;


/**
 * Fake controller class
 *
 * @author Micha� Wr�blewski
 */
public class Main
    extends HttpServlet
{
    private final Logger log = Logger.getLogger(this.getClass());

    public void init(ServletConfig config)
        throws javax.servlet.ServletException
    {
        super.init(config);
    }


    protected void setParam(Map params, String name, String value)
    {
        LinkedList paramsList = new LinkedList();
        paramsList.add(value);
        params.put(name, paramsList);
    }


    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        response.setContentType("text/xml");

        InputStream data = new FileInputStream(
                new File(this.getServletContext().getRealPath("/test/clinton.xml"))
            );

        // invoking filters
        File tmpFile = new File(this.getServletContext().getRealPath("/test/tempfile.xml"));

        FilterRequestProcessor termsFilter = new TermsFilter(tmpFile);
        FilterRequestProcessor ahcFilter = new AHCFilter();

        try
        {
            // invoking TermsFilter
            HashMap params = new HashMap();
            setParam(
                params, "termsWeighing",
                "com.mwroblewski.carrot.filter.termsfilter.weighing.TfWeighing"
            );
            setParam(params, "maxPhrasesLength", "5");
            setParam(params, "minPhrasesStrength", "1.0");
            setParam(params, "strongTermsWeight", "1.0");
            setParam(params, "removeQuery", "true");
            setParam(params, "removeSingleTerms", "true");

            termsFilter.processFilterRequest(data, request, response, params);

            FileInputStream ahcData = new FileInputStream(tmpFile);

            // invoking AHCFilter
            params = new HashMap();
            setParam(
                params, "similarityMeasure",
                "com.mwroblewski.carrot.filter.ahcfilter.ahc.similarity.CosineSimilarity"
            );
            setParam(
                params, "linkageMethod",
                "com.mwroblewski.carrot.filter.ahcfilter.ahc.linkage.CompleteLinkage"
            );
            setParam(params, "stopCondition", "");
            setParam(params, "groupsCreatingThreshold", "0.0");
            setParam(params, "removeGroupsSimilarWithParents", "false");
            setParam(params, "maxDescriptionLength", "3");
            setParam(params, "minDescriptionOccurrence", "0.6");
            setParam(params, "showDebugGroupDescription", "false");
            setParam(params, "groupOverlapThreshold", "0.5");
            setParam(params, "groupCoverageThreshold", "0.2");
            setParam(params, "removeGroupsWithoutDescription", "true");
            setParam(params, "mergeGroupsWithSimilarDescriptions", "true");
            setParam(params, "removeTopGroup", "true");

            ahcFilter.processFilterRequest(ahcData, request, response, params);

            //tmpFile.delete();
        }
        catch (Exception e)
        {
            log.error("error: ", e);
        }

        log.info("finished !!!");
    }
}
