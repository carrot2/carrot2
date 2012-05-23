
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute;

import java.util.Map;

import org.carrot2.util.attribute.metadata.AttributeMetadata;
import org.carrot2.util.attribute.metadata.BindableMetadata;
import org.carrot2.util.attribute.metadata.CommonMetadata;
import org.carrot2.util.attribute.test.metadata.AttributeDescriptions;
import org.carrot2.util.attribute.test.metadata.AttributeGroups;
import org.carrot2.util.attribute.test.metadata.AttributeLabels;
import org.carrot2.util.attribute.test.metadata.AttributeLevels;
import org.carrot2.util.attribute.test.metadata.AttributeTitles;
import org.carrot2.util.attribute.test.metadata.NoJavadoc;
import org.carrot2.util.attribute.test.metadata.TestBindable;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;

public class BindableMetadataBuilderTest extends CarrotTestCase
{
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
        checkTitle(AttributeTitles.class, "emptyTitle", null);
    }

    @Test
    public void testTitleWithPeriod()
    {

        checkTitle(AttributeTitles.class, "titleWithPeriod", "Title with period");
    }

    @Test
    public void testLabelNotDefined()
    {

        checkLabelOrTitle(AttributeTitles.class, "titleWithPeriod", "Title with period");
    }

    @Test
    public void testLabelDefined()
    {

        checkLabelOrTitle(AttributeTitles.class, "titleWithLabel", "label");
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
            "Title with link to <code>AttributeTitles.titleWithLink</code>");
        checkDescription(AttributeTitles.class, "titleWithLink", "Description.");
    }

    @Test
    public void testDescriptionWithLinks()
    {

        // Note that this scenario is not supported
        checkTitle(AttributeTitles.class, "descriptionWithLinks", "Title");
        checkDescription(AttributeTitles.class, "descriptionWithLinks",
            "Description with <code>" +
            AttributeTitles.class.getName() + 
            ".descriptionWithLinks</code> and <code>String</code> links.");
    }
    
    @Test
    public void testDescriptionWithNumericEntities()
    {
        
        // Note that this scenario is not supported
        checkTitle(AttributeTitles.class, "descriptionWithNumericEntities", "Title");
        checkDescription(AttributeTitles.class, "descriptionWithNumericEntities",
            "Description with &#160;.");
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

        checkLabel(clazz, fieldName, "overridden");
        checkTitle(clazz, fieldName, "Title");
        checkDescription(clazz, fieldName, "Description.");
    }

    @Test
    public void testNamedAttributeTitleOverride()
    {

        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "titleOverride";

        checkLabel(clazz, fieldName, "label");
        checkTitle(clazz, fieldName, "Title overridden");
        checkDescription(clazz, fieldName, "Description.");
    }

    @Test
    public void testNamedAttributeTitleDescriptionOverride()
    {

        final Class<NamedAttributes> clazz = NamedAttributes.class;
        final String fieldName = "titleDescriptionOverride";

        checkLabel(clazz, fieldName, "label");
        checkTitle(clazz, fieldName, "Title overridden");
        checkDescription(clazz, fieldName, "Description overridden.");
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
    public void testBindableMetadata()
    {
        final BindableMetadata metadata = BindableDescriptorBuilder.buildDescriptor(
            new TestBindable()).metadata;

        assertNotNull(metadata);
        assertEquals("Some test bindable", metadata.getTitle());
        assertEquals("Description.", metadata.getDescription());
        assertEquals("Test Bindable", metadata.getLabel());
    }

    @Test
    public void testBasicLevel()
    {

        checkLevel(AttributeLevels.class, "basicLevel", AttributeLevel.BASIC);
    }

    @Test
    public void testMediumLevel()
    {

        checkLevel(AttributeLevels.class, "mediumLevel", AttributeLevel.MEDIUM);
    }

    @Test
    public void testAdvancedLevel()
    {

        checkLevel(AttributeLevels.class, "advancedLevel", AttributeLevel.ADVANCED);
    }

    @Test
    public void testNoLevel()
    {

        assertNull(getLevel(AttributeLevels.class, "noLevel"));
    }

    @Test
    public void testOneWordGroup()
    {

        checkGroup(AttributeGroups.class, "oneWordGroup", "Group");
    }

    @Test
    public void testMultiWordGroup()
    {

        checkGroup(AttributeGroups.class, "multiWordGroup", "Multi word group");
    }

    @Test
    public void testNoGroup()
    {
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
        try
        {
            final Map<String, AttributeMetadata> componentAttributeMetadata =
                BindableDescriptorBuilder.buildDescriptor(
                    componentClass.newInstance()).metadata.getAttributeMetadata();
    
            if (componentAttributeMetadata == null)
            {
                return null;
            }

            final CommonMetadata fieldAttributeMetadata = 
                componentAttributeMetadata.get(fieldName);

            return fieldAttributeMetadata;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
