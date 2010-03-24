
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

package org.carrot2.core;

import java.util.*;

import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;


/**
 * This class measures the overhead resulting from reflection stuff performed by the
 * controller for a "typical" setup. Here, we're measuring the worst case -- for each
 * requests components get created and initialized.
 */
public class SimpleControllerBenchmark
{
    @Bindable
    @SuppressWarnings("unused")
    public static class ProcessingComponent1 extends ProcessingComponentBase
    {
        @Processing
        @Input
        @Attribute
        private final int int1 = 0;

        @Processing
        @Input
        @Attribute
        private final double double1 = 1.0;

        @Processing
        @Input
        @Attribute
        private final String string1 = "test";

        @Processing
        @Input
        @Attribute
        private final boolean boolean1 = false;

        @Processing
        @Input
        @Attribute(key = "in1")
        private final List<String> inAttribute = new ArrayList<String>();

        @Processing
        @Output
        @Attribute(key = "out2")
        private final List<String> outAttribute = new ArrayList<String>();

        @Processing
        @Output
        @Attribute(key = "debug2")
        private final List<String> debugAttribute = new ArrayList<String>();
    }

    @Bindable
    @SuppressWarnings("unused")
    public static class ProcessingComponent2 extends ProcessingComponentBase
    {
        @Processing
        @Input
        @Attribute
        private final int int2 = 0;

        @Processing
        @Input
        @Attribute
        private final double double2 = 1.0;

        @Processing
        @Input
        @Attribute
        private final String string2 = "test";

        @Processing
        @Input
        @Attribute
        private final boolean boolean2 = false;

        @Processing
        @Input
        @Attribute(key = "in2")
        private final List<String> inAttribute = new ArrayList<String>();

        @Processing
        @Output
        @Attribute(key = "out2")
        private final List<String> outAttribute = new ArrayList<String>();

        @Processing
        @Output
        @Attribute(key = "debug2")
        private final List<String> debugAttribute = new ArrayList<String>();
    }

    public static void main(String [] args) throws InstantiationException
    {
        final Controller controller = ControllerFactory.createSimple();

        final int requests = 100000;

        final long start = System.currentTimeMillis();

        for (int i = 0; i < requests; i++)
        {
            final Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("int1", 1);
            attributes.put("int2", 1);
            attributes.put("double1", 1.0);
            attributes.put("double2", 2.0);
            attributes.put("string1", "t");
            attributes.put("string2", "z");
            attributes.put("boolean1", false);
            attributes.put("boolean2", true);
            attributes.put("in1", null);

            controller.process(attributes, ProcessingComponent1.class,
                ProcessingComponent2.class);
        }

        final long stop = System.currentTimeMillis();

        System.out.printf("Request time    : %.2f ms\n", (stop - start)
            / (double) requests);
        System.out.printf("Requests per sec: %.2f", requests / ((stop - start) / 1000.0));
    }
}
