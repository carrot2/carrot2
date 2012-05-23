
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
public class AttributeGroups
{
    @TestInit
    @Input
    @Attribute
    @Group("Group")
    private int oneWordGroup;

    @TestInit
    @Input
    @Attribute
    @Group("Multi word group")
    private int multiWordGroup;

    /**
     * 
     */
    @TestInit
    @Input
    @Attribute
    private int noGroup;
}
