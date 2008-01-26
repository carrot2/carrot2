/**
 *
 */
package org.carrot2.core.attribute.javadoc;

import java.io.*;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.sun.javadoc.*;

/**
 * TODO: specify the supported tags
 * <p>
 * TODO: add HTML support to certain tags
 * <p>
 * TODO: add support for inline tags
 */
public class AttributeJavadocToXmlDoclet extends Doclet
{
    private static final AttributeMetadataExtractor [] EXTRACTORS = new AttributeMetadataExtractor []
    {
        new AttributeTitleExtractor(), new AttributeLabelExtractor(),
        new AttributeDescriptionExtractor()
    };

    /**
     *
     */
    public static boolean start(RootDoc rootDoc)
    {
        final ClassDoc [] classes = rootDoc.classes();
        for (final ClassDoc classDoc : classes)
        {
            if (AttributeJavadocUtils.isBindable(classDoc, rootDoc))
            {
                // Create document and root node
                final Document document = DocumentHelper.createDocument();
                final Element componentMetadata = document
                    .addElement("component-metadata");

                // Output attributes
                final Element attributes = componentMetadata.addElement("attributes");
                outputAttributeMetadata(rootDoc, classDoc, attributes);

                // Write everything to the file
                writeXml(classDoc, document);
            }
        }

        return true;
    }

    /**
     * Adds attribute metadata to the provided element.
     */
    private static void outputAttributeMetadata(RootDoc rootDoc, ClassDoc bindable,
        Element attributes)
    {
        // Iterate over fields and invoke metadata extractors for them
        final FieldDoc [] fields = bindable.fields(false);
        for (final FieldDoc fieldDoc : fields)
        {
            final AnnotationDesc attributeAnnotation = AttributeJavadocUtils
                .getAnnotation(fieldDoc.annotations(),
                    AttributeJavadocUtils.ATTRIBUTE_ANNOTATION_NAME);
            if (attributeAnnotation != null)
            {
                final Element attributeMetadata = attributes
                    .addElement("attribute-metadata");

                final Doc resolvedFieldDoc = AttributeJavadocUtils
                    .getAttributeNameConstantFieldDoc(rootDoc, attributeAnnotation);

                for (AttributeMetadataExtractor extractor : EXTRACTORS)
                {
                    extractor.extractMetadata(rootDoc, fieldDoc, resolvedFieldDoc,
                        attributeMetadata);
                }
            }
        }
    }

    /**
     * Writes the XML document created for a single component class.
     */
    private static void writeXml(ClassDoc bindable, final Document document)
    {
        // Write the file next to the source file
        final File outputFile = new File(bindable.position().file().getParentFile(),
            bindable.name() + ".xml");

        FileWriter fileWriter = null;
        try
        {
            System.out.println("Writing " + bindable.name() + ".xml...");

            fileWriter = new FileWriter(outputFile);
            XMLWriter writer = new XMLWriter(fileWriter, OutputFormat.createPrettyPrint());
            writer.write(document);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not write to file: "
                + outputFile.getAbsolutePath(), e);
        }
        finally
        {
            if (fileWriter != null)
            {
                try
                {
                    fileWriter.close();
                }
                catch (IOException e)
                {
                    // Ignore.
                }
            }
        }
    }
}
