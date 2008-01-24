/**
 * 
 */
package org.carrot2.core.controller;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.parameter.*;

/**
 * This class measures the overhead resulting from reflection stuff performed by the
 * controller for a "typical" setup. Here, we're measuring the worst case -- for each
 * requests components get created and initialized.
 */
public class SimpleControllerBenchmark
{
    @Bindable
    public static class ProcessingComponent1 extends ProcessingComponentBase
    {
        @Processing
        @Input
        @Parameter
        private int intParameter1 = 0;

        @Processing
        @Input
        @Parameter
        private double doubleParameter1 = 1.0;

        @Processing
        @Input
        @Parameter
        private String stringParameter1 = "test";

        @Processing
        @Input
        @Parameter
        private boolean booleanParameter1 = false;

        @Processing
        @Input
        @Parameter(key = "in1")
        private List<String> inAttribute = new ArrayList<String>();

        @Processing
        @Output
        @Parameter(key = "out2")
        private List<String> outAttribute = new ArrayList<String>();

        @Processing
        @Output
        @Parameter(key = "debug2")
        private List<String> debugAttribute = new ArrayList<String>();
    }

    @Bindable
    public static class ProcessingComponent2 extends ProcessingComponentBase
    {
        @Processing
        @Input
        @Parameter
        private int intParameter2 = 0;

        @Processing
        @Input
        @Parameter
        private double doubleParameter2 = 1.0;

        @Processing
        @Input
        @Parameter
        private String stringParameter2 = "test";

        @Processing
        @Input
        @Parameter
        private boolean booleanParameter2 = false;

        @Processing
        @Input
        @Parameter(key = "in2")
        private List<String> inAttribute = new ArrayList<String>();

        @Processing
        @Output
        @Parameter(key = "out2")
        private List<String> outAttribute = new ArrayList<String>();

        @Processing
        @Output
        @Parameter(key = "debug2")
        private List<String> debugAttribute = new ArrayList<String>();
    }

    public static void main(String [] args) throws InstantiationException
    {
        SimpleController controller = new SimpleController();

        final int requests = 10000;

        long start = System.currentTimeMillis();

        for (int i = 0; i < requests; i++)
        {
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("intParameter1", 1);
            attributes.put("intParameter2", 1);
            attributes.put("doubleParameter1", 1.0);
            attributes.put("doubleParameter2", 2.0);
            attributes.put("stringParameter1", "t");
            attributes.put("stringParameter2", "z");
            attributes.put("booleanParameter1", false);
            attributes.put("booleanParameter2", true);
            attributes.put("in1", "t");

            controller.process(attributes, ProcessingComponent1.class,
                ProcessingComponent2.class);
        }

        long stop = System.currentTimeMillis();

        System.out.printf("Request time    : %.2f ms\n", (stop - start)
            / (double) requests);
        System.out.printf("Requests per sec: %.2f", requests / ((stop - start) / 1000.0));
    }
}
