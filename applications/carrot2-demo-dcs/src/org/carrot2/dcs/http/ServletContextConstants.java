package org.carrot2.dcs.http;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.carrot2.dcs.ControllerContext;

/**
 * Named {@link ServletContext} attributes. 
 */
public class ServletContextConstants
{
    /** An instance of {@link ControllerContext}. */
    public final static String ATTR_CONTROLLER_CONTEXT = "controller.context";

    /** An instance of {@link String}. */
    public final static String ATTR_DEFAULT_PROCESSID = "default.processid";
    
    /** An instance of {@link Logger}. */
    public final static String ATTR_DCS_LOGGER = "dcs.logger";

    private ServletContextConstants()
    {
        // no instances.
    }
}
