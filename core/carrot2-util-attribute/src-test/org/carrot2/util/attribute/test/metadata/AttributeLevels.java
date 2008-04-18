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
public class AttributeLevels
{
    /**
     * @level Basic
     */
    @TestInit
    @Input
    @Attribute
    private int basicLevel;

    /**
     * @level Medium
     */
    @TestInit
    @Input
    @Attribute
    private int mediumLevel;

    /**
     * @level Advanced
     */
    @TestInit
    @Input
    @Attribute
    private int advancedLevel;

    /**
     * 
     */
    @TestInit
    @Input
    @Attribute
    private int noLevel;
    
    /**
     * @level Unknown
     */
    @TestInit
    @Input
    @Attribute
    private int unknownLevel;

}
