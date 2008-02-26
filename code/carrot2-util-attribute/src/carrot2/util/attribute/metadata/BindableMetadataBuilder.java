/**
 *
 */
package carrot2.util.attribute.metadata;

import java.io.File;
import java.util.List;
import java.util.Map;

import carrot2.util.attribute.Attribute;
import carrot2.util.attribute.Bindable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.*;

/**
 *
 */
public class BindableMetadataBuilder
{
    public static final String ATTRIBUTE_KEY_PARAMETER = "key";

    private static final MetadataExtractor [] ATTRIBUTE_METADATA_EXTRACTORS = new MetadataExtractor []
    {
        new LabelExtractor(), new TitleExtractor(), new DescriptionExtractor()
    };

    private static final MetadataExtractor [] BINDABLE_METADATA_EXTRACTORS = new MetadataExtractor []
    {
        new LabelExtractor(), new TitleExtractor(), new DescriptionExtractor()
    };

    private final JavaDocBuilder javaDocBuilder = new JavaDocBuilder();
    private final List<String> commonMetadataSources;

    private final List<BindableMetadataBuilderListener> listeners = Lists.newArrayList();

    /**
     *
     */
    public BindableMetadataBuilder()
    {
        commonMetadataSources = Lists.newArrayList();
    }

    public void addSourceTree(File directory)
    {
        javaDocBuilder.addSourceTree(directory);
    }

    public void addCommonMetadataSource(String className)
    {
        commonMetadataSources.add(className);
    }

    public void addCommonMetadataSource(Class<?> clazz)
    {
        commonMetadataSources.add(clazz.getName());
    }

    public void addListener(BindableMetadataBuilderListener listener)
    {
        listeners.add(listener);
    }

    public void buildAttributeMetadata()
    {
        final JavaSource [] javaSources = javaDocBuilder.getSources();
        for (final JavaSource javaSource : javaSources)
        {
            // Take first class in a file
            final JavaClass javaClass = javaSource.getClasses()[0];
            if (JavaDocBuilderUtils.hasAnnotation(javaClass, Bindable.class))
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

    private void buildBindableMetadata(JavaClass javaClass,
        BindableMetadata bindableMetadata)
    {
        for (final MetadataExtractor extractor : BINDABLE_METADATA_EXTRACTORS)
        {
            extractor.extractMetadataItem(javaClass, javaDocBuilder, bindableMetadata);
        }
    }

    private void buildAttributeMetadata(JavaClass bindable,
        BindableMetadata bindableMetadata)
    {
        final Map<String, AttributeMetadata> result = Maps.newLinkedHashMap();

        final JavaField [] fields = bindable.getFields();
        for (final JavaField javaField : fields)
        {
            if (JavaDocBuilderUtils.hasAnnotation(javaField, Attribute.class))
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

    private JavaField resolveCommonMetadataSource(JavaField originalField)
    {
        final Annotation annotation = JavaDocBuilderUtils.getAnnotation(originalField,
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
