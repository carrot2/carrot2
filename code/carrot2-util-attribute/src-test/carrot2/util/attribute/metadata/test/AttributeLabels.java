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
public class AttributeLabels
{
    /**
     * @label word
     */
    @Init
    @Input
    @Attribute
    private int singleWordLabel;

    /**
     * @label multi word label
     */
    @Init
    @Input
    @Attribute
    private int multiWordLabel;

    /**
     * @label First label sentence. Second label sentence.
     */
    @Init
    @Input
    @Attribute
    private int multiSentenceLabel;

    /**
     * Attribute comment. Second sentence of attribute comment.
     *
     * @label word
     */
    @Init
    @Input
    @Attribute
    private int labelWithComment;
}
