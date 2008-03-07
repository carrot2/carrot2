package org.carrot2.source.xml;

import java.io.*;
import java.util.Collection;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.attribute.*;

/**
 * Fetches documents from XML files and stream.
 */
@Bindable
public class XmlDocumentSource extends ProcessingComponentBase implements DocumentSource
{
    /**
     * Path to load the XML data from. The path can be either absolute or relative to
     * the current working directory.
     * 
     * @label Path
     */
    @Input
    @Processing
    @Attribute
    @Required
    private String path;

    @SuppressWarnings("unused")
    @Processing
    @Output
    @Attribute(key = AttributeNames.DOCUMENTS)
    private Collection<Document> documents;

    @Override
    public void process() throws ProcessingException
    {
        final File xmlFile = new File(path);

        FileInputStream fileInputStream = null;
        try
        {
            fileInputStream = new FileInputStream(xmlFile);
            documents = ProcessingResult.deserialize(fileInputStream).getDocuments();
        }
        catch (FileNotFoundException e)
        {
            throw new ProcessingException("File does not exist: "
                + xmlFile.getAbsolutePath());
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
