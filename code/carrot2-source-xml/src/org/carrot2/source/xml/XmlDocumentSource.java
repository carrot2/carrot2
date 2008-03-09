package org.carrot2.source.xml;

import java.io.*;
import java.util.Collection;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.resource.Resource;
import org.carrot2.util.resource.ResourceUtils;

/**
 * Fetches documents from XML files and stream.
 */
@Bindable
public class XmlDocumentSource extends ProcessingComponentBase implements DocumentSource
{
    /**
     * The resource to load XML data from. Use {@link ResourceUtils} to easily obtain
     * {@link Resource} instances for many different types of resources.
     * 
     * @label XML Resource
     */
    @Input
    @Processing
    @Attribute
    @Required
    private Resource resource;

    @SuppressWarnings("unused")
    @Processing
    @Output
    @Attribute(key = AttributeNames.DOCUMENTS)
    private Collection<Document> documents;

    @Override
    public void process() throws ProcessingException
    {
        InputStream fileInputStream = null;
        try
        {
            fileInputStream = resource.open();
            documents = ProcessingResult.deserialize(fileInputStream).getDocuments();
        }
        catch (Exception e)
        {
            throw new ProcessingException("Could not deserialize XML data", e);
        }
        finally
        {
            CloseableUtils.close(fileInputStream);
        }
    }
}
