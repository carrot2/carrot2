/**
 * 
 */
package org.carrot2.core.attribute;

import java.io.File;
import java.util.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.*;

/**
 *
 */
public class AttributeMetadataBuilder
{
    public static final String ATTRIBUTE_KEY_PARAMETER = "key";

    private static final AttributeMetadataExtractor [] EXTRACTORS = new AttributeMetadataExtractor []
    {
        new AttributeLabelExtractor(), new AttributeTitleExtractor(),
        new AttributeDescriptionExtractor()
    };

    private static final Class<?> [] COMMON_METADATA_SOURCES = new Class<?> []
    {
        AttributeNames.class
    };

    private JavaDocBuilder javaDocBuilder = new JavaDocBuilder();
    private List<Class<?>> commonMetadataSources;

    private List<AttributeMetadataBuilderListener> listeners = Lists.newArrayList();

    /**
     *
     */
    public AttributeMetadataBuilder()
    {
        commonMetadataSources = Lists.newArrayList();
        commonMetadataSources.addAll(Arrays.asList(COMMON_METADATA_SOURCES));
    }

    public void addSourceTree(File directory)
    {
        javaDocBuilder.addSourceTree(directory);
    }

    public void addCommonMetadataSource(Class<?> clazz)
    {
        commonMetadataSources.add(clazz);
    }

    public void addListener(AttributeMetadataBuilderListener listener)
    {
        listeners.add(listener);
    }

    public void buildAttributeMetadata()
    {
        final JavaSource [] javaSources = javaDocBuilder.getSources();
        for (JavaSource javaSource : javaSources)
        {
            // Take first class in a file
            final JavaClass javaClass = javaSource.getClasses()[0];
            if (JavaDocBuilderUtils.hasAnnotation(javaClass, Bindable.class))
            {
                final Map<String, AttributeMetadata> attributeMetadata = buildAttributeMetadata(javaClass);
                for (AttributeMetadataBuilderListener listener : listeners)
                {
                    listener.attributeMetadataForBindableBuilt(javaClass,
                        attributeMetadata);
                }
            }
        }
    }

    private Map<String, AttributeMetadata> buildAttributeMetadata(JavaClass bindable)
    {
        final Map<String, AttributeMetadata> result = Maps.newHashMap();

        final JavaField [] fields = bindable.getFields();
        for (JavaField javaField : fields)
        {
            if (JavaDocBuilderUtils.hasAnnotation(javaField, Attribute.class))
            {
                AttributeMetadata metadata = new AttributeMetadata();
                for (AttributeMetadataExtractor extractor : EXTRACTORS)
                {
                    // First extract with the common metadata source
                    JavaField commonMetadataSource = resolveCommonMetadataSource(javaField);
                    if (commonMetadataSource != null)
                    {
                        extractor.extractMetadataItem(commonMetadataSource,
                            javaDocBuilder, metadata);
                    }

                    // Then override with the actual metadata source
                    extractor.extractMetadataItem(javaField, javaDocBuilder, metadata);
                }

                result.put(javaField.getName(), metadata);
            }
        }

        return result;
    }

    private JavaField resolveCommonMetadataSource(JavaField originalField)
    {
        final Annotation annotation = JavaDocBuilderUtils.getAnnotation(originalField,
            Attribute.class);

        // This bit is not really well documented in QDocs (well, nothing is really...),
        // so let's convert the value to a string and proceed
        final Object namedParameter = annotation
            .getNamedParameter(ATTRIBUTE_KEY_PARAMETER);
        if (namedParameter == null)
        {
            return null;
        }

        final String keyExpression = namedParameter.toString();
        int dotIndex = keyExpression.indexOf('.');
        if (dotIndex <= 0)
        {
            return null;
        }

        final String [] split = keyExpression.split("\\.");
        final String className = split[0];
        final String fieldName = split[1];

        for (Class<?> clazz : commonMetadataSources)
        {
            if (clazz.getName().indexOf(className) >= 0)
            {
                JavaClass commonClass = javaDocBuilder.getClassByName(clazz.getName());
                if (commonClass != null)
                {
                    JavaField commonField = commonClass.getFieldByName(fieldName);
                    if (commonField != null)
                    {
                        return commonField;
                    }
                }
            }
        }

        return null;
    }
}
