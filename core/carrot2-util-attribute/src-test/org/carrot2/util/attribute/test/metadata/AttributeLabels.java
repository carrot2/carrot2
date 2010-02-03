
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
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
