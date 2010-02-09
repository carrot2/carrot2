
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

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

import java.io.*;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.Project;
import org.carrot2.util.attribute.test.metadata.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

public class BindableMetadataBuilderTest
{
    private static final String SOURCE_PATH_PROPERTY = "source.paths";
    private static final String COMMON_ATTRIBUTE_NAMES_SOURCE_PATH_PROPERTY = "common.attribute.names.source.path";
    static Map<String, BindableMetadata> bindableMetadata;

    /**
     * @return Return <code>true</code> if source path property is available and tests can
     *         proceed.
     */
    private static boolean sourcePathAvailable()
    {
        return !StringUtils.isBlank(System.getProperty(SOURCE_PATH_PROPERTY))
            && !StringUtils.isBlank(COMMON_ATTRIBUTE_NAMES_SOURCE_PATH_PROPERTY);
    }

    /**
     * Generates metadata once for all the tests, which will significantly speed up
     * processing. The metadata is stored in a static field though, which might be an
     * issue if the test cases are executed in some parallel way.
     */
    @BeforeClass
    public static void generateMetadata() throws FileNotFoundException, IOException
    {
        if (!sourcePathAvailable())
        {
            LoggerFactory.getLogger(BindableMetadataBuilderTest.class)
                .warn(
                    "Some tests skipped: provide path to sources of test classes in the '"
                        + SOURCE_PATH_PROPERTY
                        + "' JVM property and a path to the common attribute names class source in the "
                        + COMMON_ATTRIBUTE_NAMES_SOURCE_PATH_PROPERTY + " property");

            // Return, the tests that require this property will be ignored.
            return;
        }

        final Project project = new Project();
        project.setName("Test");

        final BindableMetadataBuilder builder;
        final String sourcePaths = System.getProperty(SOURCE_PATH_PROPERTY);

        builder = new BindableMetadataBuilder(project);
        builder.addCommonMetadataSource(new File(System
            .getProperty(COMMON_ATTRIBUTE_NAMES_SOURCE_PATH_PROPERTY)));

        final String [] paths = sourcePaths.split(File.pathSeparator);
        for (final String path : paths)
        {
            builder.addSource(new File(path));
        }

        final BindableMetadataBuilderListener.MapStorageListener mapListener = new BindableMetadataBuilderListener.MapStorageListener();
        builder.addListener(mapListener);

        builder.buildAttributeMetadata();

        bindableMetadata = mapListener.getBindableMetadata();
    }

    @Test
    public void testEmptyJavadoc()
    {
        assumeTrue(sourcePathAvailable());

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
        assumeTrue(sourcePathAvailable());
        checkLabel(AttributeLabels.class, "singleWordLabel", "word");
    }

    @Test
    public void testMultiWordLabel()
    {
        assumeTrue(sourcePathAvailable());
        checkLabel(AttributeLabels.class, "multiWordLabel", "multi word label");
    }

    @Test
    public void testMultiSentenceLabel()
    {
        assumeTrue(sourcePathAvailable());
        checkLabel(AttributeLabels.class, "multiSentenceLabel",
            "First label sentence. Second label sentence.");
    }

    @Test
    public void testLabelWithComment()
    {
        assumeTrue(sourcePathAvailable());
        checkLabel(AttributeLabels.class, "labelWithComment", "word");
    }

    @Test
    public void testNoTitle()
    {
        assumeTrue(sourcePathAvailable());
        checkTitle(AttributeTitles.class, "noTitle", null);
    }

    @Test
    public void testEmptyTitle()
    {
        assumeTrue(sourcePathAvailable());
        checkTitle(AttributeTitles.class, "emptyTitle", "");
    }

    @Test
    public void testTitleWithPeriod()
    {
        assumeTrue(sourcePathAvailable());
        checkTitle(AttributeTitles.class, "titleWithPeriod", "Title with period");
    }

    @Test
    public void testLabelNotDefined()
    {
        assumeTrue(sourcePathAvailable());
        checkLabelOrTitle(AttributeTitles.class, "titleWithPeriod", "Title with period");
    }

    @Test
    public void testLabelDefined()
    {
        assumeTrue(sourcePathAvailable());
        checkLabelOrTitle(AttributeTitles.class, "titleWithLabel", "label");
    }

    @Test
    public void testTitleWithoutPeriod()
    {
        assumeTrue(sourcePathAvailable());
        checkTitle(AttributeTitles.class, "titleWithoutPeriod", "Title without period");
    }

    @Test
    public void testTitleWithDescription()
    {
        assumeTrue(sourcePathAvailable());
        checkTitle(AttributeTitles.class, "titleWithDescription",
            "Title with description");
    }

    @Test
    public void testTitleWithLabel()
    {
        assumeTrue(sourcePathAvailable());
        checkTitle(AttributeTitles.class, "titleWithLabel", "Title with label");
    }

    @Test
    public void testTitleWithExtraSpace()
    {
        assumeTrue(sourcePathAvailable());

        // Note that this scenario is not supported
        checkTitle(AttributeTitles.class, "titleWithExtraSpace", "Title with extra space");
    }

    @Test
    public void testTitleWithExclamationMark()
    {
        assumeTrue(sourcePathAvailable());

        // Note that this scenario is not supported
        checkTitle(AttributeTitles.class, "titleWithExclamationMark",
            "Title with exclamation mark! and something more");
        checkDescription(AttributeTitles.class, "titleWithExclamationMark",
            "Description.");
    }

    @Test
    public void testTitleWithExtraPeriods()
    {
        assumeTrue(sourcePathAvailable());

        // Note that this scenario is not supported
        checkTitle(AttributeTitles.class, "titleWithExtraPeriods",
            "Title with extra periods (e.g. www.carrot2.org)");
        checkDescription(AttributeTitles.class, "titleWithExtraPeriods", "Description.");
    }

    @Test
    public void testTitleWithLink()
    {
        assumeTrue(sourcePathAvailable());

        // Note that this scenario is not supported
        checkTitle(AttributeTitles.class, "titleWithLink",
            "Title with link to <code>AttributeTitles.titleWithLink</code>");
        checkDescription(AttributeTitles.class, "titleWithLink", "Description.");
    }

    @Test
    public void testDescriptionWithLinks()
    {
        assumeTrue(sourcePathAvailable());

        // Note that this scenario is not supported
        checkTitle(AttributeTitles.class, "descriptionWithLinks", "Title");
        checkDescription(AttributeTitles.class, "descriptionWithLinks",
            "Description with <code>titleAtTheBottom</code> and <code>String</code> links.");
    }
    
    @Test
    public void testTitleAtTheBottomNotSupported()
    {
        assumeTrue(sourcePathAvailable());

        // Note that this scenario is not supported
        checkTitle(AttributeTitles.class, "titleAtTheBottom", null);
    }

    @Test
    public void testNoDescriptionNoTitle()
    {
        assumeTrue(sourcePathAvailable());
        checkDescription(AttributeDescriptions.class, "noDescriptionNoTitle", null);
    }

    @Test
    public void testNoDescription()
    {
        assumeTrue(sourcePathAvailable());
        checkDescription(AttributeDescriptions.class, "noDescription", null);
    }

    @Test
    public void testSingleSentenceDescription()
    {
        assumeTrue(sourcePathAvailable());
        checkDescription(AttributeDescriptions.class, "singleSentenceDescription",
            "Single sentence description.");
    }

    @Test
    public void testTwoSentenceDescription()
    {
        assumeTrue(sourcePathAvailable());
        checkDescription(AttributeDescriptions.class, "twoSentenceDescription",
            "Description sentence 1. Description sentence 2.");
    }

    @Test
    public void testDescriptionWithExtraSpace()
    {
        assumeTrue(sourcePathAvailable());
        checkDescription(AttributeDescriptions.class, "descriptionWithExtraSpace",
            "Description with extra space.");
    }

    @Test
    public void testNamedAttributeNoJavadoc()
    {
        assumeTrue(sourcePathAvailable());

        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "noJavadoc";

        checkLabel(clazz, fieldName, "label");
        checkTitle(clazz, fieldName, "Title");
        checkDescription(clazz, fieldName, "Description.");
    }

    @Test
    public void testNamedAttributeLabelOverride()
    {
        assumeTrue(sourcePathAvailable());

        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "labelOverride";

        checkLabel(clazz, fieldName, "overridden");
        checkTitle(clazz, fieldName, "Title");
        checkDescription(clazz, fieldName, "Description.");
    }

    @Test
    public void testNamedAttributeTitleOverride()
    {
        assumeTrue(sourcePathAvailable());

        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "titleOverride";

        checkLabel(clazz, fieldName, "label");
        checkTitle(clazz, fieldName, "Title overridden");
        checkDescription(clazz, fieldName, "Description.");
    }

    @Test
    public void testNamedAttributeTitleDescriptionOverride()
    {
        assumeTrue(sourcePathAvailable());

        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "titleDescriptionOverride";

        checkLabel(clazz, fieldName, "label");
        checkTitle(clazz, fieldName, "Title overridden");
        checkDescription(clazz, fieldName, "Description overridden.");
    }

    @Test
    public void testNamedAttributeNoDotInKey()
    {
        assumeTrue(sourcePathAvailable());

        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "noDotInKey";

        checkLabel(clazz, fieldName, null);
        checkTitle(clazz, fieldName, null);
        checkDescription(clazz, fieldName, null);
    }

    @Test
    public void testClassNotInSourcePath()
    {
        assumeTrue(sourcePathAvailable());

        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "classNotInSourcePath";

        checkLabel(clazz, fieldName, null);
        checkTitle(clazz, fieldName, null);
        checkDescription(clazz, fieldName, null);
    }

    @Test
    public void testBindableMetadata()
    {
        assumeTrue(sourcePathAvailable());
        final BindableMetadata metadata = bindableMetadata.get(TestBindable.class
            .getName());
        assertNotNull(metadata);
        assertEquals("Some test bindable", metadata.title);
        assertEquals("Description.", metadata.description);
        assertEquals("Test Bindable", metadata.label);
    }

    @Test
    public void testBasicLevel()
    {
        assumeTrue(sourcePathAvailable());
        checkLevel(AttributeLevels.class, "basicLevel", AttributeLevel.BASIC);
    }

    @Test
    public void testMediumLevel()
    {
        assumeTrue(sourcePathAvailable());
        checkLevel(AttributeLevels.class, "mediumLevel", AttributeLevel.MEDIUM);
    }

    @Test
    public void testAdvancedLevel()
    {
        assumeTrue(sourcePathAvailable());
        checkLevel(AttributeLevels.class, "advancedLevel", AttributeLevel.ADVANCED);
    }

    @Test
    public void testUnknownLevel()
    {
        assumeTrue(sourcePathAvailable());
        assertNull(getLevel(AttributeLevels.class, "unknownLevel"));
    }

    @Test
    public void testNoLevel()
    {
        assumeTrue(sourcePathAvailable());
        assertNull(getLevel(AttributeLevels.class, "noLevel"));
    }

    @Test
    public void testOneWordGroup()
    {
        assumeTrue(sourcePathAvailable());
        checkGroup(AttributeGroups.class, "oneWordGroup", "Group");
    }

    @Test
    public void testMultiWordGroup()
    {
        assumeTrue(sourcePathAvailable());
        checkGroup(AttributeGroups.class, "multiWordGroup", "Multi word group");
    }

    @Test
    public void testNoGroup()
    {
        assumeTrue(sourcePathAvailable());
        assertNull(getGroup(AttributeGroups.class, "noGroup"));
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
    protected void checkGroup(final Class<?> clazz, final String fieldName,
        final String expectedGroup)
    {
        assertNotNull(getAttributeMetadata(clazz, fieldName));
        assertEquals(expectedGroup, getGroup(clazz, fieldName));
    }

    /**
     *
     */
    protected void checkLevel(final Class<?> clazz, final String fieldName,
        final AttributeLevel expectedLabel)
    {
        assertNotNull(getAttributeMetadata(clazz, fieldName));
        assertEquals(expectedLabel, getLevel(clazz, fieldName));
    }

    /**
     *
     */
    protected void checkLabelOrTitle(final Class<?> clazz, final String fieldName,
        final String expectedLabel)
    {
        assertNotNull(getAttributeMetadata(clazz, fieldName));
        assertEquals(expectedLabel, getLabelOrTitle(clazz, fieldName));
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
    protected String getGroup(Class<?> componentClass, String fieldName)
    {
        final CommonMetadata fieldAttributeMetadata = getAttributeMetadata(
            componentClass, fieldName);

        return ((AttributeMetadata) fieldAttributeMetadata).getGroup();
    }

    /**
     *
     */
    protected AttributeLevel getLevel(Class<?> componentClass, String fieldName)
    {
        final CommonMetadata fieldAttributeMetadata = getAttributeMetadata(
            componentClass, fieldName);

        return ((AttributeMetadata) fieldAttributeMetadata).getLevel();
    }

    /**
     *
     */
    protected String getLabelOrTitle(Class<?> componentClass, String fieldName)
    {
        final CommonMetadata fieldAttributeMetadata = getAttributeMetadata(
            componentClass, fieldName);

        return fieldAttributeMetadata.getLabelOrTitle();
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
