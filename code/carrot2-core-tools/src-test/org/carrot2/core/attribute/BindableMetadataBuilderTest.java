/**
 * 
 */
package org.carrot2.core.attribute;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;

import org.carrot2.core.attribute.metadata.tests.*;
import org.junit.Test;

/**
 *
 */
public class BindableMetadataBuilderTest
{
    private static final String SOURCE_PATH_PROPERTY = "source.paths";
    protected BindableMetadataBuilder builder;
    protected Map<String, BindableMetadata> bindableMetadata;

    public BindableMetadataBuilderTest()
    {
        final String sourcePaths = System.getProperty(SOURCE_PATH_PROPERTY);
        if (sourcePaths == null)
        {
            fail("Please provide path to sources of test classes in the '"
                + SOURCE_PATH_PROPERTY + "' JVM property");

        }

        builder = new BindableMetadataBuilder();
        builder.addCommonMetadataSource(TestAttributeNames.class);

        String [] paths = sourcePaths.split(File.pathSeparator);
        for (String path : paths)
        {
            builder.addSourceTree(new File(path));
        }

        MemoryStorageBindableMetadataBuilderListener mapListener = new MemoryStorageBindableMetadataBuilderListener();
        builder.addListener(mapListener);

        builder.buildAttributeMetadata();

        bindableMetadata = mapListener.getBindableMetadata();
    }

    @Test
    public void testEmptyJavadoc()
    {
        final Class<?> clazz = NoJavadoc.class;
        final String fieldName = "noJavadoc";

        assertNotNull(getAttributeMetadata(clazz, fieldName));
        assertNull(getLabel(clazz, fieldName));
        assertNull(getTitle(clazz, fieldName));
        assertNull(getDescription(clazz, fieldName));
    }

    @Test
    public void testSingleWordLabel()
    {
        checkLabel(AttributeLabels.class, "singleWordLabel", "word");
    }

    @Test
    public void testMultiWordLabel()
    {
        checkLabel(AttributeLabels.class, "multiWordLabel", "multi word label");
    }

    @Test
    public void testMultiSentenceLabel()
    {
        checkLabel(AttributeLabels.class, "multiSentenceLabel",
            "First label sentence. Second label sentence.");
    }

    @Test
    public void testLabelWithComment()
    {
        checkLabel(AttributeLabels.class, "labelWithComment", "word");
    }

    @Test
    public void testNoTitle()
    {
        checkTitle(AttributeTitles.class, "noTitle", null);
    }

    @Test
    public void testEmptyTitle()
    {
        checkTitle(AttributeTitles.class, "emptyTitle", "");
    }

    @Test
    public void testTitleWithPeriod()
    {
        checkTitle(AttributeTitles.class, "titleWithPeriod", "Title with period");
    }

    @Test
    public void testTitleWithoutPeriod()
    {
        checkTitle(AttributeTitles.class, "titleWithoutPeriod", "Title without period");
    }

    @Test
    public void testTitleWithDescription()
    {
        checkTitle(AttributeTitles.class, "titleWithDescription",
            "Title with description");
    }

    @Test
    public void testTitleWithLabel()
    {
        checkTitle(AttributeTitles.class, "titleWithLabel", "Title with label");
    }

    @Test
    public void testTitleWithExtraSpace()
    {
        // Note that this scenario is not supported
        checkTitle(AttributeTitles.class, "titleWithExtraSpace", "Title with extra space");
    }

    @Test
    public void testTitleWithExclamationMark()
    {
        // Note that this scenario is not supported
        checkTitle(AttributeTitles.class, "titleWithExclamationMark",
            "Title with exclamation mark! and something more");
        checkDescription(AttributeTitles.class, "titleWithExclamationMark",
            "Description.");
    }

    @Test
    public void testTitleWithExtraPeriods()
    {
        // Note that this scenario is not supported
        checkTitle(AttributeTitles.class, "titleWithExtraPeriods",
            "Title with extra periods (e.g. www.carrot2.org)");
        checkDescription(AttributeTitles.class, "titleWithExtraPeriods", "Description.");
    }

    @Test
    public void testTitleWithLink()
    {
        // Note that this scenario is not supported
        checkTitle(AttributeTitles.class, "titleWithLink",
            "Title with link to ProcessingComponent#init()");
        checkDescription(AttributeTitles.class, "titleWithLink", "Description.");
    }

    @Test
    public void testTitleAtTheBottomNotSupported()
    {
        // Note that this scenario is not supported
        checkTitle(AttributeTitles.class, "titleAtTheBottom", null);
    }

    @Test
    public void testNoDescriptionNoTitle()
    {
        checkDescription(AttributeDescriptions.class, "noDescriptionNoTitle", null);
    }

    @Test
    public void testNoDescription()
    {
        checkDescription(AttributeDescriptions.class, "noDescription", null);
    }

    @Test
    public void testSingleSentenceDescription()
    {
        checkDescription(AttributeDescriptions.class, "singleSentenceDescription",
            "Single sentence description.");
    }

    @Test
    public void testTwoSentenceDescription()
    {
        checkDescription(AttributeDescriptions.class, "twoSentenceDescription",
            "Description sentence 1. Description sentence 2.");
    }

    @Test
    public void testDescriptionWithExtraSpace()
    {
        checkDescription(AttributeDescriptions.class, "descriptionWithExtraSpace",
            "Description with extra space.");
    }

    @Test
    public void testNamedAttributeNoJavadoc()
    {
        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "noJavadoc";

        checkLabel(clazz, fieldName, "label");
        checkTitle(clazz, fieldName, "Title");
        checkDescription(clazz, fieldName, "Description.");
    }

    @Test
    public void testNamedAttributeLabelOverride()
    {
        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "labelOverride";

        checkLabel(clazz, fieldName, "overriden");
        checkTitle(clazz, fieldName, "Title");
        checkDescription(clazz, fieldName, "Description.");
    }

    @Test
    public void testNamedAttributeTitleOverride()
    {
        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "titleOverride";

        checkLabel(clazz, fieldName, "label");
        checkTitle(clazz, fieldName, "Title overriden");
        checkDescription(clazz, fieldName, "Description.");
    }

    @Test
    public void testNamedAttributeTitleDescriptionOverride()
    {
        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "titleDescriptionOverride";

        checkLabel(clazz, fieldName, "label");
        checkTitle(clazz, fieldName, "Title overriden");
        checkDescription(clazz, fieldName, "Description overriden.");
    }

    @Test
    public void testNamedAttributeNoDotInKey()
    {
        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "noDotInKey";

        checkLabel(clazz, fieldName, null);
        checkTitle(clazz, fieldName, null);
        checkDescription(clazz, fieldName, null);
    }

    @Test
    public void testClassNotInSourcePath()
    {
        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "classNotInSourcePath";

        checkLabel(clazz, fieldName, null);
        checkTitle(clazz, fieldName, null);
        checkDescription(clazz, fieldName, null);
    }

    @Test
    public void testBindableMetadata()
    {
        BindableMetadata metadata = bindableMetadata.get(TestBindable.class.getName());
        assertNotNull(metadata);
        assertEquals("Some test bindable", metadata.title);
        assertEquals("Description.", metadata.description);
        assertEquals("Test Bindable", metadata.label);
    }

    /**
     *
     */
    protected void checkLabel(final Class<?> clazz, final String fieldName,
        final String expectedLabel)
    {
        assertNotNull(getAttributeMetadata(clazz, fieldName));
        assertEquals(expectedLabel, getLabel(clazz, fieldName));
    }

    /**
     *
     */
    protected void checkTitle(final Class<?> clazz, final String fieldName,
        final String expectedTitle)
    {
        assertNotNull(getAttributeMetadata(clazz, fieldName));
        assertEquals(expectedTitle, getTitle(clazz, fieldName));
    }

    /**
     *
     */
    protected void checkDescription(final Class<?> clazz, final String fieldName,
        final String expectedDescription)
    {
        assertNotNull(getAttributeMetadata(clazz, fieldName));
        assertEquals(expectedDescription, getDescription(clazz, fieldName));
    }

    /**
     *
     */
    protected String getLabel(Class<?> componentClass, String fieldName)
    {
        final CommonMetadata fieldAttributeMetadata = getAttributeMetadata(
            componentClass, fieldName);

        return fieldAttributeMetadata.getLabel();
    }

    /**
     *
     */
    protected String getTitle(Class<?> componentClass, String fieldName)
    {
        final CommonMetadata fieldAttributeMetadata = getAttributeMetadata(
            componentClass, fieldName);

        return fieldAttributeMetadata.getTitle();
    }

    /**
     *
     */
    protected String getDescription(Class<?> componentClass, String fieldName)
    {
        final CommonMetadata fieldAttributeMetadata = getAttributeMetadata(
            componentClass, fieldName);

        return fieldAttributeMetadata.getDescription();
    }

    /**
     *
     */
    private CommonMetadata getAttributeMetadata(Class<?> componentClass, String fieldName)
    {
        final Map<String, AttributeMetadata> componentAttributeMetadata = bindableMetadata
            .get(componentClass.getName()).getAttributeMetadata();
        if (componentAttributeMetadata == null)
        {
            return null;
        }

        final CommonMetadata fieldAttributeMetadata = componentAttributeMetadata
            .get(fieldName);
        if (fieldAttributeMetadata == null)
        {
            return null;
        }

        return fieldAttributeMetadata;
    }
}
