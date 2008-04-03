package org.carrot2.workbench.editors;

import junit.framework.TestCase;

import org.carrot2.core.ClusteringAlgorithm;
import org.carrot2.core.ProcessingComponentBase;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.constraint.DoubleRange;
import org.carrot2.util.attribute.constraint.IntRange;

public class EditorsTest extends TestCase
{

    public class TestComponent extends ProcessingComponentBase implements
        ClusteringAlgorithm
    {

        @Attribute
        @Input
        @Processing
        int specificAttribute;

        @Attribute
        @Input
        @Processing
        @IntRange(min = 0, max = 10)
        int rangeAttribute;

        @Attribute
        @Input
        @Processing
        int simpleAttribute;

        @Attribute
        @Input
        @Processing
        @DoubleRange(min = 0, max = 10)
        double rangeDoubleAttribute;
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
        // specificAttribute -> DedicatedEditor
        // rangeAttribute -> RangeEditor
        // simpleAttribute -> IntEditor
        // rangeDoubleAttribute -> NotFound!!
    }

}
