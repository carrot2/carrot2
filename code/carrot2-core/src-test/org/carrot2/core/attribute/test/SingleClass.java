/**
 * 
 */
package org.carrot2.core.attribute.test;

import org.carrot2.core.attribute.*;

/**
 *
 */
@Bindable
@SuppressWarnings("unused")
public class SingleClass
{
    /**
     * Init input int attribute.
     * 
     * @label Init Input Int
     */
    @Init
    @Input
    @Attribute
    private int initInputInt = 10;
    
    /**
     * Processing input string attribute. Some description.
     * 
     * @label Processing Input String
     */
    @Processing
    @Input
    @Attribute
    private String processingInputString = "test";
    
    /**
     * This is not an attribute.
     */
    private String notAnAttribute;
}
