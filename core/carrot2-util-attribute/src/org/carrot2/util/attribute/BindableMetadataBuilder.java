package org.carrot2.util.attribute;

import java.io.*;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.*;

/**
 * Builds metadata for {@link Bindable} types.
 */
class BindableMetadataBuilder
{
    /** A field used in tests */
    static final String ATTRIBUTE_KEY_PARAMETER = "key";

    /**
     * Metadata extractors for attributes.
     */
    private static final MetadataExtractor [] ATTRIBUTE_METADATA_EXTRACTORS = new MetadataExtractor []
    {
        MetadataExtractor.LABEL_EXTRACTOR, MetadataExtractor.GROUP_EXTRACTOR,
        MetadataExtractor.LEVEL_EXTRACTOR, MetadataExtractor.TITLE_EXTRACTOR,
        MetadataExtractor.DESCRIPTION_EXTRACTOR
    };

    /**
     * Metadata extractors for bindable types.
     */
    private static final MetadataExtractor [] BINDABLE_METADATA_EXTRACTORS = new MetadataExtractor []
    {
        MetadataExtractor.LABEL_EXTRACTOR, MetadataExtractor.TITLE_EXTRACTOR,
        MetadataExtractor.DESCRIPTION_EXTRACTOR
    };

    /** JavaDoc parser */
    private final JavaDocBuilder javaDocBuilder = new JavaDocBuilder();

    /**
     * List of fully qualified names of classes containing common attribute keys. An
     * example of such a class can be org.carrot2.core.attribute.AttributeNames. If the
     * key of an attribute is taken from any class on this list, the metadata from the
     * actual field will be complemented by the metadata provided for the constant
     * defining the key.
     */
    private final List<String> commonMetadataSources;

    /**
     * Metadata listeners, useful for tests and serialization application.
     */
    private final List<BindableMetadataBuilderListener> listeners = Lists.newArrayList();

    /**
     * Creates a {@link BindableMetadataBuilder} with empty commonMetadataSources.
     */
    BindableMetadataBuilder()
    {
        commonMetadataSources = Lists.newArrayList();
    }

    /**
     * Adds Java sources to be parsed.
     */
    void addSource(File file) throws FileNotFoundException, IOException
    {
        if (file.isDirectory())
        {
            javaDocBuilder.addSourceTree(file);
        }
        else
        {
            javaDocBuilder.addSource(file);
        }
    }

    /**
     * Adds a class to commonMetadataSources.
     */
    void addCommonMetadataSource(File file) throws FileNotFoundException, IOException
    {
        JavaSource addSource = javaDocBuilder.addSource(file);
        commonMetadataSources.add(addSource.getClasses()[0].getFullyQualifiedName());
    }

    /**
     * Adds a metadata listener.
     */
    void addListener(BindableMetadataBuilderListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Builds attribute metadata, notifying all the listeners when
     * {@link BindableMetadata} gets built.
     */
    void buildAttributeMetadata()
    {
        final JavaSource [] javaSources = javaDocBuilder.getSources();
        for (final JavaSource javaSource : javaSources)
        {
            // Take first class in a file
            final JavaClass javaClass = javaSource.getClasses()[0];
            if (MetadataExtractorUtils.hasAnnotation(javaClass, Bindable.class))
            {
                final BindableMetadata bindableMetadata = new BindableMetadata();
                buildBindableMetadata(javaClass, bindableMetadata);
                buildAttributeMetadata(javaClass, bindableMetadata);

                for (final BindableMetadataBuilderListener listener : listeners)
                {
                    listener.bindableMetadataBuilt(javaClass, bindableMetadata);
                }
            }
        }
    }

    /**
     * Fills {@link BindableMetadata} with data collected from the Java class.
     */
    private void buildBindableMetadata(JavaClass javaClass,
        BindableMetadata bindableMetadata)
    {
        for (final MetadataExtractor extractor : BINDABLE_METADATA_EXTRACTORS)
        {
            extractor.extractMetadataItem(javaClass, javaDocBuilder, bindableMetadata);
        }
    }

    /**
     * Fills {@link BindableMetadata} with {@link AttributeMetadata} data collected from
     * the Java class.
     */
    private void buildAttributeMetadata(JavaClass bindable,
        BindableMetadata bindableMetadata)
    {
        final Map<String, AttributeMetadata> result = Maps.newLinkedHashMap();

        final JavaField [] fields = bindable.getFields();
        for (final JavaField javaField : fields)
        {
            if (MetadataExtractorUtils.hasAnnotation(javaField, Attribute.class))
            {
                final AttributeMetadata metadata = new AttributeMetadata();
                for (final MetadataExtractor extractor : ATTRIBUTE_METADATA_EXTRACTORS)
                {
                    // First extract with the common metadata source
                    final JavaField commonMetadataSource = resolveCommonMetadataSource(javaField);
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

        bindableMetadata.setAttributeMetadata(result);
    }

    /**
     * Resolves additional metadata from commonMetadataSources.
     */
    private JavaField resolveCommonMetadataSource(JavaField originalField)
    {
        final Annotation annotation = MetadataExtractorUtils.getAnnotation(originalField,
            Attribute.class);

        // This bit is not really well documented in QDocs (well, nothing is, really...),
        // so let's convert the value to a string and proceed
        final Object namedParameter = annotation
            .getNamedParameter(ATTRIBUTE_KEY_PARAMETER);
        if (namedParameter == null)
        {
            return null;
        }

        final String keyExpression = namedParameter.toString();
        final int dotIndex = keyExpression.indexOf('.');
        if (dotIndex <= 0)
        {
            return null;
        }

        final String [] split = keyExpression.split("\\.");
        final String className = split[0];
        final String fieldName = split[1];

        for (final String metadataSourceClassName : commonMetadataSources)
        {
            if (metadataSourceClassName.indexOf(className) >= 0)
            {
                final JavaClass commonClass = javaDocBuilder
                    .getClassByName(metadataSourceClassName);
                if (commonClass != null)
                {
                    final JavaField commonField = commonClass.getFieldByName(fieldName);
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
