
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
public class AttributeTitles
{
    @TestInit
    @Input
    @Attribute
    @Label("label")
    private int noTitle;

    /**
     * . Description follows.
     */
    @TestInit
    @Input
    @Attribute
    @Label("label")
    private int emptyTitle;

    /**
     * Title with period.
     */
    @TestInit
    @Input
    @Attribute
    private int titleWithPeriod;

    /**
     * Title    with
     *
     *
     * extra    space.
     */
    @TestInit
    @Input
    @Attribute
    private int titleWithExtraSpace;

    /**
     * Title without period
     */
    @TestInit
    @Input
    @Attribute
    private int titleWithoutPeriod;

    /**
     * Title with exclamation mark! and something more. Description.
     */
    @TestInit
    @Input
    @Attribute
    private int titleWithExclamationMark;

    /**
     * Title with extra periods (e.g. www.carrot2.org). Description.
     */
    @TestInit
    @Input
    @Attribute
    private int titleWithExtraPeriods;

    /**
     * Title with link to {@link AttributeTitles#titleWithLink}. Description.
     */
    @TestInit
    @Input
    @Attribute
    private int titleWithLink;

    /**
     * Title. Description with {@link #descriptionWithLinks} and {@link String} links.
     */
    @TestInit
    @Input
    @Attribute
    private int descriptionWithLinks;
    
    /**
     * Title. Description with &#160;.
     */
    @TestInit
    @Input
    @Attribute
    private int descriptionWithNumericEntities;
    
    /**
     * Title with description. Description follows.
     */
    @TestInit
    @Input
    @Attribute
    private int titleWithDescription;

    /**
     * Title with label.
     */
    @TestInit
    @Input
    @Attribute
    @Label("label")
    private int titleWithLabel;
}
