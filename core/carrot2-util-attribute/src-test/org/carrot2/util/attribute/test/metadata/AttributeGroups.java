/**
 *
 */
package org.carrot2.util.attribute.test.metadata;

import org.carrot2.util.attribute.*;

/**
 *
 */
@Bindable
@SuppressWarnings("unused")
public class AttributeGroups
{
    /**
     * @group Group
     */
    @TestInit
    @Input
    @Attribute
    private int oneWordGroup;

    /**
     * @group Multi word group
     */
    @TestInit
    @Input
    @Attribute
    private int multiWordGroup;

    /**
     * 
     */
    @TestInit
    @Input
    @Attribute
    private int noGroup;
}
