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
public class AttributeTitles
{
    /**
     * @label label
     */
    @Init
    @Input
    @Attribute
    private int noTitle;

    /**
     * . Description follows.
     * 
     * @label label
     */
    @Init
    @Input
    @Attribute
    private int emptyTitle;
    
    /**
     * Title with period.
     */
    @Init
    @Input
    @Attribute
    private int titleWithPeriod;
    
    /**
     * Title    with
     * 
     * 
     * extra    space.   
     */
    @Init
    @Input
    @Attribute
    private int titleWithExtraSpace;

    /**
     * Title without period
     */
    @Init
    @Input
    @Attribute
    private int titleWithoutPeriod;

    /**
     * Title with description. Description follows.
     */
    @Init
    @Input
    @Attribute
    private int titleWithDescription;

    /**
     * Title with label.
     * 
     * @label label
     */
    @Init
    @Input
    @Attribute
    private int titleWithLabel;

    /**
     * @label label Title at the bottom. This arrangement is not supported.
     */
    @Init
    @Input
    @Attribute
    private int titleAtTheBottom;
}
