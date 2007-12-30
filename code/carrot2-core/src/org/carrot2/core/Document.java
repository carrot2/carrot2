package org.carrot2.core;

import java.util.*;

/**
 * TODO: how do we handle document ids? Expose a public method for setting/getting id?
 * This is very much internal, so ideally setting ids would not be available for everyone.
 */
public class Document
{
    public static final String TITLE = "title";
    public static final String SUMMARY = "summary";
    public static final String CONTENT_URL = "url";

    private Map<String, Object> fields = new HashMap<String, Object>();
    private Map<String, Object> fieldsView = Collections.unmodifiableMap(fields);

    private int id;

    public Document()
    {
    }

    Document(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    void setId(int id)
    {
        this.id = id;
    }

    public Map<String, Object> getFields()
    {
        return fieldsView;
    }

    @SuppressWarnings("unchecked")
    public <T> T getField(String name)
    {
        return (T) fields.get(name);
    }

    public void addField(String name, Object value)
    {
        fields.put(name, value);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj == null || !(obj instanceof Document))
        {
            return false;
        }

        // TODO: shall we just compare ids (fast!) or all fields
        return this.id == ((Document) obj).id;
    }

    @Override
    public int hashCode()
    {
        return id;
    }

    public static Document create(int id, String title, String summary, String contentUrl)
    {
        Document document = new Document(id);

        document.addField(TITLE, title);
        document.addField(SUMMARY, summary);
        document.addField(CONTENT_URL, contentUrl);

        return document;
    }
}
