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
public class SimpleComponent
{
    /**
     * @label word
     */
    @Init
    @Input
    @Attribute
    private int singleWordLabel;

    /**
     * Only title
     * 
     * @label multi word label
     */
    @Init
    @Input
    @Attribute
    private int multipleWordLabel;

    /**
     * Attribute comment. Second sentence of attribute comment.
     * 
     * @label word2
     */
    @Init
    @Input
    @Attribute
    private int labelWithComment;
    
    @Init
    @Input
    @Attribute
    private int noJavadoc;
}
