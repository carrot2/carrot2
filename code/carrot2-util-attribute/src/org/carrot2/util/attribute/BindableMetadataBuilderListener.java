package org.carrot2.util.attribute;

import java.io.File;
import java.util.Map;

import org.simpleframework.xml.load.Persister;
import org.simpleframework.xml.stream.Format;

import com.google.common.collect.Maps;
import com.thoughtworks.qdox.model.JavaClass;

abstract class BindableMetadataBuilderListener
{
    abstract void bindableMetadataBuilt(JavaClass bindable,
        BindableMetadata bindableMetadata);

    /**
     * Stores attribute metadata in a map using fully qualified class names as keys.
     */
    static class MapStorageListener extends BindableMetadataBuilderListener
    {
        private final Map<String, BindableMetadata> bindableMetadata = Maps
            .newLinkedHashMap();

        public void bindableMetadataBuilt(JavaClass bindable, BindableMetadata metadata)
        {
            bindableMetadata.put(bindable.getFullyQualifiedName(), metadata);
        }

        public Map<String, BindableMetadata> getBindableMetadata()
        {
            return bindableMetadata;
        }
    }

    /**
     * Saves the attribute metadata information next to the Java source file defining the
     * component.
     */
    static class XmlSerializerListener extends BindableMetadataBuilderListener
    {
        public void bindableMetadataBuilt(JavaClass bindable,
            BindableMetadata bindableMetadata)
        {
            try
            {
                final File xmlFile = new File(new File(bindable.getSource().getURL()
                    .toURI()).getParentFile(), bindable.getName() + ".xml");

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
}
