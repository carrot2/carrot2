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
}
