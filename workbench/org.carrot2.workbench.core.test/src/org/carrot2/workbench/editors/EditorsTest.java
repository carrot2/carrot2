package org.carrot2.workbench.editors;

import org.carrot2.core.ClusteringAlgorithm;
import org.carrot2.core.ProcessingComponentBase;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.*;

public class EditorsTest
{
    public static class TestType
    {
    }

    public static class TestType2
    {
    }

    public static class SubTestType extends TestType implements TestInterface
    {
    }

    public static interface TestInterface
    {
    }

    @Bindable
    public static class TestComponent extends ProcessingComponentBase implements
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
        SubTestType subAttribute;

        @Attribute
        @Input
        @Processing
        @IntRange(min = 0, max = 10)
        SubTestType subRangeAttribute;

        @Attribute
        @Input
        @Processing
        @IntModulo(modulo = 2, offset = 1)
        TestInterface interfaceRangeAttribute;

        @Attribute
        @Input
        @Processing
        TestInterface interfaceAttribute;

        @Attribute
        @Input
        @Processing
        @DoubleRange(min = 0, max = 10)
        TestType2 rangeDoubleAttribute;

        @Attribute
        @Input
        @Processing
        @IntModulo(modulo = 2, offset = 3)
        TestType2 comboAttribute;

        @Attribute
        @Input
        @Processing
        @IntModulo(modulo = 2, offset = 1)
        IAttributeEditor editor = new DedicatedEditor();

        @Attribute
        @Input
        @Processing
        IAttributeEditor editor2 = new DedicatedEditor();

    }

    public static class DedicatedEditor extends AttributeEditorAdapter
    {

    }

    public static class DedicatedEditor2 extends AttributeEditorAdapter
    {

    }

    public static class DedicatedEditor3 extends AttributeEditorAdapter
    {

    }

    public static class DedicatedEditor4 extends AttributeEditorAdapter
    {

    }

    public static class RangeEditor extends AttributeEditorAdapter
    {

    }

    public static class IntEditor extends AttributeEditorAdapter
    {

    }

    public static class SubEditor extends AttributeEditorAdapter
    {

    }

    public static class InterfaceEditor extends AttributeEditorAdapter
    {

    }

    public static class RangeEditor2 extends AttributeEditorAdapter
    {

    }

    public static class ComboEditor extends AttributeEditorAdapter
    {

    }

}
