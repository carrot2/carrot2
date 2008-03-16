package org.carrot2.core;


/**
 * Test cases for {@link SimpleController}.
 */
public class SimpleControllerTest extends ControllerTestBase
{
    @Override
    protected Controller createController()
    {
        return new SimpleController();
    }
}
