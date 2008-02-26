/**
 *
 */
package carrot2.util.attribute.metadata.test;

import carrot2.util.attribute.*;

/**
 *
 */
@Bindable
@SuppressWarnings("unused")
public class AttributeDescriptions
{
    /**
     * @label label
     */
    @Init
    @Input
    @Attribute
    private int noDescriptionNoTitle;

    /**
     * Title.
     *
     * @label label
     */
    @Init
    @Input
    @Attribute
    private int noDescription;

    /**
     * Title. Single sentence description.
     */
    @Init
    @Input
    @Attribute
    private int singleSentenceDescription;

    /**
     * Title. Description sentence 1. Description sentence 2.
     */
    @Init
    @Input
    @Attribute
    private int twoSentenceDescription;

    /**
     * Title. Description
     *
     * with     extra
     * space.
     */
    @Init
    @Input
    @Attribute
    private int descriptionWithExtraSpace;
}
