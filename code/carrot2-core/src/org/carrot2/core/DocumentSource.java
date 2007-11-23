package org.carrot2.core;

import java.util.Iterator;

public interface DocumentSource
{
    public Iterator<Document> getDocuments();
}