

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


package com.dawidweiss.carrot.tests.config;


import java.net.MalformedURLException;
import java.net.URL;


/**
 * The controller URL and properties.
 */
public class Controller
{
    URL url;

    public void setUrl(String url)
        throws MalformedURLException
    {
        this.url = new URL(url);
    }
}
