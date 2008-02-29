/**
 *
 */
package carrot2.util.attribute.metadata;

import java.io.File;

import org.simpleframework.xml.load.Persister;
import org.simpleframework.xml.stream.Format;


import com.thoughtworks.qdox.model.JavaClass;

/**
 * Saves the attribute metadata information next to the Java source file defining the
 * component.
 */
public class XmlSerializerBindableMetadataBuilderListener implements
    BindableMetadataBuilderListener
{
    public void bindableMetadataBuilt(JavaClass bindable,
        BindableMetadata bindableMetadata)
    {
        try
        {
            final File xmlFile = new File(new File(bindable.getSource().getURL().toURI())
                .getParentFile(), bindable.getName() + ".xml");

            final Persister persister = new Persister(new Format(2,
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
            persister.write(bindableMetadata, xmlFile);
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
