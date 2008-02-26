/**
 *
 */
package carrot2.util.attribute.test;

import carrot2.util.attribute.*;

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
    private final int initInputInt = 10;

    /**
     * Processing input string attribute. Some description.
     *
     * @label Processing Input String
     */
    @Processing
    @Input
    @Attribute
    private final String processingInputString = "test";

    /**
     * This is not an attribute.
     */
    private String notAnAttribute;
}
