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
