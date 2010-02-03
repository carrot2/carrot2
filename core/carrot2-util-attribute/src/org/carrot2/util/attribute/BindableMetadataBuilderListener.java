
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute;

import java.io.File;
import java.util.Map;

import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import com.google.common.collect.Maps;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * Allows to get notifications about {@link BindableMetadata} being built by
 * {@link BindableMetadataBuilder}.
 */
abstract class BindableMetadataBuilderListener
{
    /**
     * Invoked after {@link BindableMetadata} has been built.
     * 
     * @param bindable the Java class for which the metadata has been built.
     * @param bindableMetadata metadata that has been built.
     */
    abstract void bindableMetadataBuilt(JavaClass bindable,
        BindableMetadata bindableMetadata);

    /**
     * Stores attribute metadata in a map using fully qualified class names as keys.
     * Useful for unit testing.
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
        private final File outputDir;

        public XmlSerializerListener(File outputDir)
        {
            this.outputDir = outputDir;
        }

        public void bindableMetadataBuilt(JavaClass bindable,
            BindableMetadata bindableMetadata)
        {
            try
            {
                final File xmlFile = new File(outputDir, bindable.getFullyQualifiedName()
                    + ".xml");

                System.out.println("Writing: " + xmlFile);

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
