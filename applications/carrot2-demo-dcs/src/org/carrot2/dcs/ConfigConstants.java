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

package org.carrot2.dcs;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

/**
 * Named {@link ServletContext} attributes.
 */
public class ConfigConstants
{
    /** An instance of {@link ControllerContext}. */
    public final static String ATTR_CONTROLLER_CONTEXT = "dcs.controller.context";

    /** An instance of {@link String}. */
    public final static String ATTR_DEFAULT_PROCESSID = "dcs.default.algorithm";

    /** An instance of {@link Logger}. */
    public final static String ATTR_DCS_LOGGER = "dcs.logger";

    /**
     * If set to {@link Boolean#TRUE}, the input documents will not be mirrored in the result. 
     * Saves network bandwidth.
     */
    public final static String ATTR_CLUSTERS_ONLY = "dcs.clusters.only";

    /**
     * Name of the process used to serialize the output.
     * 
     * @see ControllerContext#RESULTS_TO_JSON
     * @see ControllerContext#RESULTS_TO_XML
     */
    public static final String ATTR_OUTPUT_FORMAT = "dcs.output";

    private ConfigConstants()
    {
        // no instances.
    }
}
