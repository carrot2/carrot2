package com.paulodev.carrot.treeExtractor.extractors;

import com.dawidweiss.carrot.util.common.StringUtils;
import com.dawidweiss.carrot.util.common.XMLSerializerHelper;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Poznań University of Technology</p>
 * @author Paweł Kowalik
 * @version 1.0
 */

public class Snippet
{
    protected int OrdNum;
    protected String title = new String();
    protected String description = new String();
    protected String url = new String();
    private   XMLSerializerHelper serializer = XMLSerializerHelper.getInstance();

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

    public String toString()
    {
        String res = new String();
        res += "<document id=\"" + OrdNum + "\">\n\t<title>";
        res += serializer.toValidXmlText(StringUtils.removeMarkup(title), false);
        res += "</title>\n";

        res += "\t<url>";
        res += serializer.toValidXmlText(url, false);
        res += "</url>\n";

        res += "\t<snippet>";
        res += serializer.toValidXmlText(StringUtils.removeMarkup(description), false);
        res += "</snippet>\n";
        res += "</document>\n";
        return res;
    }
}