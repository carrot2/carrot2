
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.dcs;

import org.carrot2.util.attribute.*;

/**
 * Parameters for DCS request.
 */
@Bindable
public class DcsRequestModel
{
    @Input
    @Attribute(key = "dcs.source")
    public String source;

    @Input
    @Attribute(key = "dcs.algorithm")
    public String algorithm;

    @Input
    @Attribute(key = "dcs.clusters.only")
    public boolean clustersOnly;

    @Input
    @Attribute(key = "dcs.output.format")
    public OutputFormat outputFormat = OutputFormat.XML;
    
    @Input
    @Attribute(key = "dcs.json.callback")
    public String jsonCallback;

    /**
     * Output format requested from DCS,
     */
    public enum OutputFormat
    {
        XML("text/xml"), JSON("text/json");

        /** Content type for this output format */
        public final String contentType;

        private OutputFormat(String contentType)
        {
            this.contentType = contentType;
        }
    }
}
