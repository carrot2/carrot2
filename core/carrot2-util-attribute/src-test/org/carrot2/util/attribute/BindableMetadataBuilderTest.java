package org.carrot2.util.attribute;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.carrot2.util.attribute.test.metadata.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junitext.Prerequisite;
import org.junitext.runners.AnnotationRunner;

@RunWith(AnnotationRunner.class)
public class BindableMetadataBuilderTest
{
    private static final String SOURCE_PATH_PROPERTY = "source.paths";
    protected static Map<String, BindableMetadata> bindableMetadata;

    /**
     * @return Return <code>true</code> if source path property is available and tests
     *         can proceed.
     */
    public static boolean isSourcePathAvailable()
    {
        final String sourcePaths = System.getProperty(SOURCE_PATH_PROPERTY);
        return !StringUtils.isEmpty(sourcePaths);
    }

    /**
     * Generates metadata once for all the tests, which will significantly speed up
     * processing. The metadata is stored in a static field though, which might be an
     * issue if the test cases are executed in some parallel way.
     */
    @BeforeClass
    public static void generateMetadata()
    {
        if (!isSourcePathAvailable())
        {
            org.apache.log4j.Logger.getLogger(BindableMetadataBuilderTest.class)
                .warn("Some tests skipped: provide path to sources of test classes in the '" 
                    + SOURCE_PATH_PROPERTY + "' JVM property");

            // Return, the tests that require this property will be ignored. 
            return;
        }
        
        final BindableMetadataBuilder builder;
        final String sourcePaths = System.getProperty(SOURCE_PATH_PROPERTY);

        builder = new BindableMetadataBuilder();
        builder.addCommonMetadataSource(TestAttributeNames.class);

        final String [] paths = sourcePaths.split(File.pathSeparator);
        for (final String path : paths)
        {
            builder.addSourceTree(new File(path));
        }

        final BindableMetadataBuilderListener.MapStorageListener mapListener = new BindableMetadataBuilderListener.MapStorageListener();
        builder.addListener(mapListener);

        builder.buildAttributeMetadata();

        bindableMetadata = mapListener.getBindableMetadata();
    }

    @Prerequisite(requires = "isSourcePathAvailable")
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

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testSingleWordLabel()
    {
        checkLabel(AttributeLabels.class, "singleWordLabel", "word");
    }
    
    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testMultiWordLabel()
    {
        checkLabel(AttributeLabels.class, "multiWordLabel", "multi word label");
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testMultiSentenceLabel()
    {
        checkLabel(AttributeLabels.class, "multiSentenceLabel",
            "First label sentence. Second label sentence.");
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testLabelWithComment()
    {
        checkLabel(AttributeLabels.class, "labelWithComment", "word");
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testNoTitle()
    {
        checkTitle(AttributeTitles.class, "noTitle", null);
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testEmptyTitle()
    {
        checkTitle(AttributeTitles.class, "emptyTitle", "");
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testTitleWithPeriod()
    {
        checkTitle(AttributeTitles.class, "titleWithPeriod", "Title with period");
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testTitleWithoutPeriod()
    {
        checkTitle(AttributeTitles.class, "titleWithoutPeriod", "Title without period");
    }
    
    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testTitleWithDescription()
    {
        checkTitle(AttributeTitles.class, "titleWithDescription",
            "Title with description");
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testTitleWithLabel()
    {
        checkTitle(AttributeTitles.class, "titleWithLabel", "Title with label");
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testTitleWithExtraSpace()
    {
        // Note that this scenario is not supported
        checkTitle(AttributeTitles.class, "titleWithExtraSpace", "Title with extra space");
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testTitleWithExclamationMark()
    {
        // Note that this scenario is not supported
        checkTitle(AttributeTitles.class, "titleWithExclamationMark",
            "Title with exclamation mark! and something more");
        checkDescription(AttributeTitles.class, "titleWithExclamationMark",
            "Description.");
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testTitleWithExtraPeriods()
    {
        // Note that this scenario is not supported
        checkTitle(AttributeTitles.class, "titleWithExtraPeriods",
            "Title with extra periods (e.g. www.carrot2.org)");
        checkDescription(AttributeTitles.class, "titleWithExtraPeriods", "Description.");
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testTitleWithLink()
    {
        // Note that this scenario is not supported
        checkTitle(AttributeTitles.class, "titleWithLink",
            "Title with link to ProcessingComponent#init()");
        checkDescription(AttributeTitles.class, "titleWithLink", "Description.");
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testTitleAtTheBottomNotSupported()
    {
        // Note that this scenario is not supported
        checkTitle(AttributeTitles.class, "titleAtTheBottom", null);
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testNoDescriptionNoTitle()
    {
        checkDescription(AttributeDescriptions.class, "noDescriptionNoTitle", null);
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testNoDescription()
    {
        checkDescription(AttributeDescriptions.class, "noDescription", null);
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testSingleSentenceDescription()
    {
        checkDescription(AttributeDescriptions.class, "singleSentenceDescription",
            "Single sentence description.");
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testTwoSentenceDescription()
    {
        checkDescription(AttributeDescriptions.class, "twoSentenceDescription",
            "Description sentence 1. Description sentence 2.");
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testDescriptionWithExtraSpace()
    {
        checkDescription(AttributeDescriptions.class, "descriptionWithExtraSpace",
            "Description with extra space.");
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testNamedAttributeNoJavadoc()
    {
        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "noJavadoc";

        checkLabel(clazz, fieldName, "label");
        checkTitle(clazz, fieldName, "Title");
        checkDescription(clazz, fieldName, "Description.");
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testNamedAttributeLabelOverride()
    {
        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "labelOverride";

        checkLabel(clazz, fieldName, "overridden");
        checkTitle(clazz, fieldName, "Title");
        checkDescription(clazz, fieldName, "Description.");
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testNamedAttributeTitleOverride()
    {
        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "titleOverride";

        checkLabel(clazz, fieldName, "label");
        checkTitle(clazz, fieldName, "Title overridden");
        checkDescription(clazz, fieldName, "Description.");
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testNamedAttributeTitleDescriptionOverride()
    {
        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "titleDescriptionOverride";

        checkLabel(clazz, fieldName, "label");
        checkTitle(clazz, fieldName, "Title overridden");
        checkDescription(clazz, fieldName, "Description overridden.");
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testNamedAttributeNoDotInKey()
    {
        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "noDotInKey";

        checkLabel(clazz, fieldName, null);
        checkTitle(clazz, fieldName, null);
        checkDescription(clazz, fieldName, null);
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testClassNotInSourcePath()
    {
        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "classNotInSourcePath";

        checkLabel(clazz, fieldName, null);
        checkTitle(clazz, fieldName, null);
        checkDescription(clazz, fieldName, null);
    }

    @Prerequisite(requires = "isSourcePathAvailable")
    @Test
    public void testBindableMetadata()
    {
        final BindableMetadata metadata = bindableMetadata.get(TestBindable.class
            .getName());
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
