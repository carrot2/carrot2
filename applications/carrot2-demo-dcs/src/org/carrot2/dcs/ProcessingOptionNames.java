
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.dcs;

/**
 * Named keys of processing options.
 * 
 * @see AppConfig#getProcessingDefaults()
 */
public class ProcessingOptionNames
{
    /**
     * An instance of {@link String}.
     */
    public final static String ATTR_PROCESSID = "dcs.default.algorithm";

    /**
     * If set to {@link Boolean#TRUE}, the input documents will not be mirrored in the
     * result. Saves network bandwidth.
     */
    public final static String ATTR_CLUSTERS_ONLY = "dcs.clusters.only";

    /**
     * Name of the process used to serialize the output.
     */
    public static final String ATTR_OUTPUT_FORMAT = "dcs.default.output";

    private ProcessingOptionNames()
    {
        // no instances.
    }
}
