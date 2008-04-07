package org.carrot2.workbench.editors;

import junit.framework.TestCase;

import org.carrot2.core.ClusteringAlgorithm;
import org.carrot2.core.ProcessingComponentBase;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.DoubleRange;
import org.carrot2.util.attribute.constraint.IntRange;
import org.carrot2.workbench.editors.factory.EditorFactory;

public class EditorsTest extends TestCase
{
    public class TestType
    {
    }

    public class TestType2
    {
    }

    @Bindable
    public class TestComponent extends ProcessingComponentBase implements
        ClusteringAlgorithm
    {

        @Attribute
        @Input
        @Processing
        TestType specificAttribute;

        @Attribute
        @Input
        @Processing
        @IntRange(min = 0, max = 10)
        TestType rangeAttribute;

        @Attribute
        @Input
        @Processing
        TestType simpleAttribute;

        @Attribute
        @Input
        @Processing
        @DoubleRange(min = 0, max = 10)
        TestType2 rangeDoubleAttribute;
    }

    public class DedicatedEditor extends AttributeEditorAdapter
    {

    }

    public class RangeEditor extends AttributeEditorAdapter
    {

    }

    public class IntEditor extends AttributeEditorAdapter
    {

    }

    public class RangeEditor2 extends AttributeEditorAdapter
    {

    }

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testEditorFactory()
    {
        TestComponent c = new TestComponent();
        BindableDescriptor desc = BindableDescriptorBuilder.buildDescriptor(c, false);
        // specificAttribute -> DedicatedEditor
        assertEquals(DedicatedEditor.class, EditorFactory.getEditorFor(
            desc.attributeDescriptors.get(AttributeUtils.getKey(TestComponent.class,
                "specificAttribute"))).getClass());
        // rangeAttribute -> RangeEditor
        assertEquals(RangeEditor.class, EditorFactory.getEditorFor(
            desc.attributeDescriptors.get(AttributeUtils.getKey(TestComponent.class,
                "rangeAttribute"))).getClass());
        // simpleAttribute -> IntEditor
        assertEquals(IntEditor.class, EditorFactory.getEditorFor(
            desc.attributeDescriptors.get(AttributeUtils.getKey(TestComponent.class,
                "simpleAttribute"))).getClass());
        // rangeDoubleAttribute -> NotFound!!
        try
        {
            EditorFactory.getEditorFor(desc.attributeDescriptors.get(AttributeUtils
                .getKey(TestComponent.class, "rangeDoubleAttribute")));
            fail("No editor for field 'rangeDoubleAttribute' should be found");
        }
        catch (EditorNotFoundException e)
        {
            // TODO: handle exception
        }
    }

}
