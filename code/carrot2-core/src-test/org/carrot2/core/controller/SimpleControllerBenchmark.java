/**
 * 
 */
package org.carrot2.core.controller;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.*;

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
        private int int1 = 0;

        @Processing
        @Input
        @Attribute
        private double double1 = 1.0;

        @Processing
        @Input
        @Attribute
        private String string1 = "test";

        @Processing
        @Input
        @Attribute
        private boolean boolean1 = false;

        @Processing
        @Input
        @Attribute(key = "in1")
        private List<String> inAttribute = new ArrayList<String>();

        @Processing
        @Output
        @Attribute(key = "out2")
        private List<String> outAttribute = new ArrayList<String>();

        @Processing
        @Output
        @Attribute(key = "debug2")
        private List<String> debugAttribute = new ArrayList<String>();
    }

    @Bindable
    @SuppressWarnings("unused")
    public static class ProcessingComponent2 extends ProcessingComponentBase
    {
        @Processing
        @Input
        @Attribute
        private int int2 = 0;

        @Processing
        @Input
        @Attribute
        private double double2 = 1.0;

        @Processing
        @Input
        @Attribute
        private String string2 = "test";

        @Processing
        @Input
        @Attribute
        private boolean boolean2 = false;

        @Processing
        @Input
        @Attribute(key = "in2")
        private List<String> inAttribute = new ArrayList<String>();

        @Processing
        @Output
        @Attribute(key = "out2")
        private List<String> outAttribute = new ArrayList<String>();

        @Processing
        @Output
        @Attribute(key = "debug2")
        private List<String> debugAttribute = new ArrayList<String>();
    }

    public static void main(String [] args) throws InstantiationException
    {
        SimpleController controller = new SimpleController();

        final int requests = 100000;

        long start = System.currentTimeMillis();

        for (int i = 0; i < requests; i++)
        {
            Map<String, Object> attributes = new HashMap<String, Object>();
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

        long stop = System.currentTimeMillis();

        System.out.printf("Request time    : %.2f ms\n", (stop - start)
            / (double) requests);
        System.out.printf("Requests per sec: %.2f", requests / ((stop - start) / 1000.0));
    }
}
