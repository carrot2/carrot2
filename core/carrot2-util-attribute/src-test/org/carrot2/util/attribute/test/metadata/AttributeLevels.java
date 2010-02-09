
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
