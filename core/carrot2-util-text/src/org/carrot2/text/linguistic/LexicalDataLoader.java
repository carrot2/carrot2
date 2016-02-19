
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;

import static org.carrot2.util.resource.ResourceLookup.Location.CONTEXT_CLASS_LOADER;

import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.annotations.AspectModified;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.DefaultGroups;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Level;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.resource.ResourceLookup;

/**
 * Common attributes related to loading and caching of lexical resources.
 */
@Bindable
public class LexicalDataLoader
{
    /**
     * Reloads cached stop words and stop labels on every processing request. For best
     * performance, lexical resource reloading should be disabled in production.
     * 
     * <p>This flag is reset to <code>false</code> after successful resource reload to prevent
     * multiple resource reloads during the same processing cycle.</p> 
     */
    @Processing
    @Input
    @Attribute(key = "reload-resources")
    @Label("Reload lexical resources")
    @Level(AttributeLevel.MEDIUM)
    @Group(DefaultGroups.PREPROCESSING)
    public boolean reloadResources = false;

    /**
     * Lexical resource lookup facade. By default, resources are sought in the current
     * thread's context class loader. An override of this attribute is possible both at
     * the initialization time and at processing time.
     */
    @Init
    @Processing
    @Input 
    @Internal
    @Attribute(key = "resource-lookup")
    @ImplementingClasses(classes = {}, strict = false)
    @Label("Resource lookup facade")
    @Level(AttributeLevel.ADVANCED)
    @Group(DefaultGroups.PREPROCESSING)
    @AspectModified("Substituted with an assembly lookup in .NET release")
    public ResourceLookup resourceLookup = new ResourceLookup(CONTEXT_CLASS_LOADER);
}
