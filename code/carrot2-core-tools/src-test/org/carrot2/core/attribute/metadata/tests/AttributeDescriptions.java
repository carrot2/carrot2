/**
 * 
 */
package org.carrot2.core.attribute.metadata.tests;

import org.carrot2.core.attribute.*;

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
