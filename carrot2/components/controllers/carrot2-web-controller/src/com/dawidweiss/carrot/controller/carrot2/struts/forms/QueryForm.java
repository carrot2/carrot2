

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


package com.dawidweiss.carrot.controller.carrot2.struts.forms;


import org.apache.struts.action.*;
import javax.servlet.ServletContext;
import javax.servlet.http.*;


/**
 * A query form with user query and some additional
 */
public class QueryForm
    extends ActionForm
{
    /** User query */
    protected String query;

    /** Number of requested results. */
    protected int resultsRequested;

    /** The key of a processing chain to use */
    protected String processingChain;

    // --------------------------------------------------------------- accessors
    public void setQuery(String query)
    {
        this.query = query;
    }


    public String getQuery()
    {
        return query;
    }


    public void setResultsRequested(int resultsRequested)
    {
        this.resultsRequested = resultsRequested;
    }


    public int getResultsRequested()
    {
        return resultsRequested;
    }


    public String getProcessingChain()
    {
        return processingChain;
    }


    public void setProcessingChain(String chainNameKey)
    {
        this.processingChain = chainNameKey;
    }

    /**
     * Initialize.
     */
    public QueryForm()
    {
    }

    /**
     * Returns true if form has been initialized.
     */
    public boolean isInitialized()
    {
        return (query != null) && (resultsRequested != 0) && (processingChain != null);
    }


    /**
     * Initializes the form.
     */
    public void initialize(ServletContext application)
    {
        if (query == null)
        {
            query = "";
        }

        if (resultsRequested == 0)
        {
            resultsRequested = 100;
        }
    }


    /**
     * Validates the form.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
    {
        return null;
    }
}
