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
        @BeforeProcessing
        @Input
        @Parameter
        private int intParameter1 = 0;

        @BeforeProcessing
        @Input
        @Parameter
        private double doubleParameter1 = 1.0;

        @BeforeProcessing
        @Input
        @Parameter
        private String stringParameter1 = "test";

        @BeforeProcessing
        @Input
        @Parameter
        private boolean booleanParameter1 = false;

        @Attribute(key = "in1", bindingDirection = BindingDirection.IN)
        private String inAttribute;

        @Attribute(key = "out1", bindingDirection = BindingDirection.OUT)
        private List<String> outAttribute = new ArrayList<String>();

        @Attribute(key = "debug1", bindingDirection = BindingDirection.OUT)
        private List<String> debugAttribute = new ArrayList<String>();
    }

    @Bindable
    public static class ProcessingComponent2 extends ProcessingComponentBase
    {
        @BeforeProcessing
        @Input
        @Parameter
        private int intParameter2 = 0;

        @BeforeProcessing
        @Input
        @Parameter
        private double doubleParameter2 = 1.0;

        @BeforeProcessing
        @Input
        @Parameter
        private String stringParameter2 = "test";

        @BeforeProcessing
        @Input
        @Parameter
        private boolean booleanParameter2 = false;

        @Attribute(key = "in2", bindingDirection = BindingDirection.IN)
        private List<String> inAttribute = new ArrayList<String>();

        @Attribute(key = "out2", bindingDirection = BindingDirection.OUT)
        private List<String> outAttribute = new ArrayList<String>();

        @Attribute(key = "debug2", bindingDirection = BindingDirection.OUT)
        private List<String> debugAttribute = new ArrayList<String>();
    }

    public static void main(String [] args) throws InstantiationException
    {
        SimpleController controller = new SimpleController();

        final int requests = 10000;

        long start = System.currentTimeMillis();

        for (int i = 0; i < requests; i++)
        {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("intParameter1", 1);
            parameters.put("intParameter2", 1);
            parameters.put("doubleParameter1", 1.0);
            parameters.put("doubleParameter2", 2.0);
            parameters.put("stringParameter1", "t");
            parameters.put("stringParameter2", "z");
            parameters.put("booleanParameter1", false);
            parameters.put("booleanParameter2", true);

            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("in1", "t");

            controller.process(parameters, attributes, ProcessingComponent1.class,
                ProcessingComponent2.class);
        }

        long stop = System.currentTimeMillis();

        System.out.printf("Request time    : %.2f ms\n", (stop - start) / (double) requests);
        System.out.printf("Requests per sec: %.2f", requests / ((stop - start) / 1000.0));
    }
}
