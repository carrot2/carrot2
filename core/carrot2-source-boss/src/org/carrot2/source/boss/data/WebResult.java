package org.carrot2.source.boss.data;

import org.apache.commons.lang.StringEscapeUtils;
import org.carrot2.util.StringUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.load.Commit;

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

    /**
     * On commit, unescape HTML fields.
     */
    @Commit
    @SuppressWarnings("unused")
    private void unescape()   
    {
        summary = clean(summary);
        title = clean(title);
    }

    /*
     * 
     */
    private static String clean(String value)
    {
        if (value == null || org.apache.commons.lang.StringUtils.isEmpty(value))
            return value;

        return StringUtils.removeHtmlTags(StringEscapeUtils.unescapeHtml(value));
    }
}