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
