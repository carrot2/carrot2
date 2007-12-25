/**
 * 
 */
package org.carrot2.core;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class SimpleControllerTest
{
    private IMocksControl mocksControl;

    private ProcessingComponent processingComponent1Mock;
    private ProcessingComponent processingComponent2Mock;
    private ProcessingComponent processingComponent3Mock;

    private SimpleController controller;

    @Before
    public void init()
    {
        mocksControl = createStrictControl();

        processingComponent1Mock = mocksControl.createMock(DocumentSource.class);
        processingComponent2Mock = mocksControl.createMock(DocumentSource.class);
        processingComponent3Mock = mocksControl.createMock(DocumentSource.class);

        controller = new SimpleController();
    }

    @Test
    public void testNormalExecution1Component() throws InstantiationException
    {
        processingComponent1Mock.init();
        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.performProcessing();
        processingComponent1Mock.afterProcessing();
        processingComponent1Mock.dispose();

        mocksControl.replay();

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("delegate", processingComponent1Mock);
        parameters.put("instanceParameter", "i");
        parameters.put("runtimeParameter", "r");

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("data", "d");

        controller.process(parameters, attributes, DelegatingProcessingComponent.class);

        mocksControl.verify();

        assertEquals("dir", attributes.get("data"));
    }
}
