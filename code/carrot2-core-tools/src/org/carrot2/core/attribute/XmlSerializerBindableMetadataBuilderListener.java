/**
 * 
 */
package org.carrot2.core.attribute;

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
    @Override
    public void bindableMetadataBuilt(JavaClass bindable,
        BindableMetadata bindableMetadata)
    {
        try
        {
            final File xmlFile = new File(new File(bindable.getSource().getURL().toURI())
                .getParentFile(), bindable.getName() + ".xml");

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
