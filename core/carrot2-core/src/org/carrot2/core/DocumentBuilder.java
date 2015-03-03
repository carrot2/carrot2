package org.carrot2.core;

import java.util.Arrays;

/**
 * Assists in building up a {@link Document}. 
 */
public final class DocumentBuilder extends AttributeSetBuilder<Document>
{
    public DocumentBuilder id(String id) { return attr(Document.ID, id); }
    public DocumentBuilder title(String title) { return attr(Document.TITLE, title); }
    public DocumentBuilder summary(String summary) { return attr(Document.SUMMARY, summary); }
    public DocumentBuilder contentURL(String contentURL) { return attr(Document.CONTENT_URL, contentURL); }
    public DocumentBuilder clickURL(String clickURL) { return attr(Document.CLICK_URL, clickURL); }
    public DocumentBuilder thumbURL(String thumbURL) { return attr(Document.THUMBNAIL_URL, thumbURL); }
    public DocumentBuilder language(LanguageCode language) { return attr(Document.LANGUAGE, language); }
    public DocumentBuilder sources(String... sources) { return attr(Document.SOURCES, Arrays.asList(sources)); }

    public DocumentBuilder attr(String key, Object value)
    {
        return (DocumentBuilder) attr(key, value);
    }

    public Document build()
    {
        return new Document(cloneAndClearAttributes());
    }
}
