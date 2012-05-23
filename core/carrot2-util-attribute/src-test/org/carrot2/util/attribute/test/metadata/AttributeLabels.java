
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
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
    @TestInit
    @Input
    @Attribute
    @Label("word")
    private int singleWordLabel;

    @TestInit
    @Input
    @Attribute
    @Label("multi word label")
    private int multiWordLabel;

    @TestInit
    @Input
    @Attribute
    @Label("First label sentence. Second label sentence.")
    private int multiSentenceLabel;

    /**
     * Attribute comment. Second sentence of attribute comment.
     */
    @TestInit
    @Input
    @Attribute
    @Label("word")
    private int labelWithComment;
}
