

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


import java.net.URL;


/**
 * Stores configuration options for performing external tests.
 */
public class TestsConfiguration
{
    private Controller controller;

    public Controller createController()
    {
        return new Controller();
    }


    public URL getControllerURL()
    {
        return controller.url;
    }


    public void setController(Controller controller)
    {
        this.controller = controller;
    }
}
