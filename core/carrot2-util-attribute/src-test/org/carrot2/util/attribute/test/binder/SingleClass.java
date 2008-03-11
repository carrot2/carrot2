/**
 *
 */
package org.carrot2.util.attribute.test.binder;

import org.carrot2.util.attribute.*;

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
    @TestInit
    @Input
    @Attribute
    private int initInputInt = 10;

    /**
     * Processing input string attribute. Some description.
     *
     * @label Processing Input String
     */
    @TestProcessing
    @Input
    @Attribute
    private String processingInputString = "test";

    /**
     * This is not an attribute.
     */
    private String notAnAttribute;
}
