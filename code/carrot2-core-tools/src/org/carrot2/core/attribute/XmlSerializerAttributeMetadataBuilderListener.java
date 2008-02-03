/**
 * 
 */
package org.carrot2.core.attribute;

import java.io.File;
import java.util.Map;

import org.simpleframework.xml.load.Persister;
import org.simpleframework.xml.stream.Format;

import com.thoughtworks.qdox.model.JavaClass;

/**
 * Saves the attribute metadata information next to the Java source file defining the
 * component.
 */
public class XmlSerializerAttributeMetadataBuilderListener implements
    AttributeMetadataBuilderListener
{
    @Override
    public void attributeMetadataForBindableBuilt(JavaClass bindable,
        Map<String, AttributeMetadata> metadata)
    {
        try
        {
            final File xmlFile = new File(new File(bindable.getSource().getURL().toURI())
                .getParentFile(), bindable.getName() + ".xml");

            BindableMetadata bindableMetadata = new BindableMetadata();
            bindableMetadata.setAttributeMetadata(metadata);

            Persister persister = new Persister(new Format(2,
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
            persister.write(bindableMetadata, xmlFile);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
