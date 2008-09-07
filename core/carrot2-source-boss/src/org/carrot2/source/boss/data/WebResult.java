package org.carrot2.source.boss.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * A single Web search result.
 */
@Root(name = "result", strict = false)
public final class WebResult
{
    @Element(name = "abstract", required = false)
    public String summary;

    @Element(required = false)
    public String date;
    
    @Element(name = "dispurl", required = false)
    public String displayURL;
    
    @Element(name = "clickurl", required = false)
    public String clickURL;

    @Element(name = "size", required = false)
    public Long size;

    @Element(required = false)
    public String title;

    @Element(required = false)
    public String url;
}