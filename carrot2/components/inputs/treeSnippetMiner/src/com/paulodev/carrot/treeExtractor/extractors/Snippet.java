package com.paulodev.carrot.treeExtractor.extractors;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Poznań University of Technology</p>
 * @author Paweł Kowalik
 * @version 1.0
 */

import org.put.util.text.*;

public class Snippet
{
    protected int OrdNum;
    protected String title = new String();
    protected String description = new String();
    protected String url = new String();

    public Snippet(TreeExtractor.SnippetBuilder builder)
    {
        this.OrdNum = builder.OrdNum;
        this.title = builder.getValueForItem("title");
        this.description = builder.getValueForItem("description");
        this.url = builder.getValueForItem("url");
    }

    public int getOrderNumber()
    {
        return OrdNum;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public String getUrl()
    {
        return url;
    }

    private String xmlencode(String x)
    {
        return "<![CDATA[" + x + "]]>";
    }

    public String toString()
    {
        String res = new String();
        res += "<document id=\"" + OrdNum + "\">\n\t<title>";
        res += xmlencode(HtmlHelper.removeHtmlTags(title));
        res += "</title>\n";

        res += "\t<url>";
        res += xmlencode(url);
        res += "</url>\n";

        res += "\t<snippet>";
        res += xmlencode(HtmlHelper.removeHtmlTags(description));
        res += "</snippet>\n";
        res += "</document>\n";
        return res;
    }
}