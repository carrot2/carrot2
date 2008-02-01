/**
 * 
 */
package org.carrot2.core.attribute.metadata;

import java.io.File;
import java.io.Reader;
import java.util.*;

import org.carrot2.core.attribute.Attribute;
import org.carrot2.core.attribute.Bindable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.*;

/**
 *
 */
public class AttributeMetadataBuilder
{
    private static final AttributeMetadataExtractor [] EXTRACTORS = new AttributeMetadataExtractor []
    {
        new AttributeLabelExtractor(), new AttributeTitleExtractor()
    };

    private JavaDocBuilder javaDocBuilder = new JavaDocBuilder();

    public void addSource(Reader reader)
    {
        javaDocBuilder.addSource(reader);
    }

    public void addSourceTree(File directory)
    {
        javaDocBuilder.addSourceTree(directory);
    }

    public Map<String, Collection<AttributeMetadata>> buildAttributeMetadata()
    {
        final Map<String, Collection<AttributeMetadata>> result = Maps.newHashMap();

        final JavaSource [] javaSources = javaDocBuilder.getSources();
        for (JavaSource javaSource : javaSources)
        {
            // Take first class in a file
            final JavaClass javaClass = javaSource.getClasses()[0];
            if (JavaDocBuilderUtils.hasAnnotation(javaClass, Bindable.class))
            {
                result.put(javaClass.getFullyQualifiedName(),
                    buildAttributeMetadata(javaClass));
            }
        }

        return result;
    }

    private Collection<AttributeMetadata> buildAttributeMetadata(JavaClass bindable)
    {
        final List<AttributeMetadata> result = Lists.newArrayList();
        
        final JavaField [] fields = bindable.getFields();
        for (JavaField javaField : fields)
        {
            if (JavaDocBuilderUtils.hasAnnotation(javaField, Attribute.class))
            {
                AttributeMetadata metadata = new AttributeMetadata();
                for (AttributeMetadataExtractor extractor : EXTRACTORS)
                {
                    extractor.extractMetadataItem(javaField, javaDocBuilder, metadata);
                }
                
                result.add(metadata);
            }
        }

        return result;
    }
}
