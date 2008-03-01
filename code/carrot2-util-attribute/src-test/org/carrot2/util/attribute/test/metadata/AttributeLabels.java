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
public class AttributeLabels
{
    /**
     * @label word
     */
    @TestInit
    @Input
    @Attribute
    private int singleWordLabel;

    /**
     * @label multi word label
     */
    @TestInit
    @Input
    @Attribute
    private int multiWordLabel;

    /**
     * @label First label sentence. Second label sentence.
     */
    @TestInit
    @Input
    @Attribute
    private int multiSentenceLabel;

    /**
     * Attribute comment. Second sentence of attribute comment.
     *
     * @label word
     */
    @TestInit
    @Input
    @Attribute
    private int labelWithComment;
}
