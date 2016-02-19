
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

package org.carrot2.core;

import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;

@Bindable
public abstract class DelegatingProcessingComponent implements IProcessingComponent
{
    @Init
    @Input
    @Attribute(key = "instanceAttribute")
    public String instanceAttribute = "";

    @Processing
    @Input
    @Attribute(key = "runtimeAttribute")
    public String runtimeAttribute = "";

    @Processing
    @Input
    @Output
    @Required
    @Attribute(key = "data")
    public String data = null;

    public void init(IControllerContext context)
    {
        getDelegate().init(context);
    }

    public void beforeProcessing() throws ProcessingException
    {
        getDelegate().beforeProcessing();
    }

    public void process() throws ProcessingException
    {
        getDelegate().process();

        // Do some simple processing
        data = data + instanceAttribute;
        data = data + runtimeAttribute;
    }

    public void afterProcessing()
    {
        getDelegate().afterProcessing();
    }

    public void dispose()
    {
        getDelegate().dispose();
    }

    abstract IProcessingComponent getDelegate();
}
