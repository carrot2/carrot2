package org.carrot2.source.boss.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * A single News search result.
 */
@Root(name = "result", strict = false)
public final class NewsResult
{
    @Element(name = "abstract", required = false)
    public String summary;

    @Element(name = "clickurl", required = false)
    public String clickURL;

    @Element(required = false)
    public String title;

    @Element(required = false)
    public String language;

    @Element(required = false)
    public String date;

    @Element(required = false)
    public String time;

    @Element(required = false)
    public String source;

    @Element(name = "sourceurl", required = false)
    public String sourceURL;

    @Element(required = false)
    public String url;
}