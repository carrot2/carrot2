package org.carrot2.source.boss.data;

import java.util.List;

import org.simpleframework.xml.*;

@Root(name = "resultset_news", strict = false)
public class NewsResultSet
{
    @Attribute(required = false)
    public Long count;

    @Attribute(required = false)
    public Long start;

    @Attribute(required = false)
    public Long totalhits;

    @Attribute(required = false)
    public Long deephits;

    @ElementList(inline = true, entry = "result", required = false)
    public List<NewsResult> results;
}
