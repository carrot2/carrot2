
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

package org.carrot2.workbench.editors;

import org.carrot2.core.IClusteringAlgorithm;
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
        IClusteringAlgorithm
    {

        @Attribute(key = "my.specificAttribute")
        @Input
        @Processing
        TestType specificAttribute;

        @Attribute
        @Input
        @Processing
        @IntRange(min = 0, max = 10)
        TestType oneConstraintAttribute;

        @Attribute
        @Input
        @Processing
        @IntRange(min = 0, max = 10)
        @IntModulo(modulo = 2, offset = 10)
        TestType twoConstraintsAttribute;

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

    public static class DedicatedEditor extends EmptyAttributeEditorAdapter
    {

    }

    public static class DedicatedEditor2 extends EmptyAttributeEditorAdapter
    {

    }

    public static class DedicatedEditor3 extends EmptyAttributeEditorAdapter
    {

    }

    public static class DedicatedEditor4 extends EmptyAttributeEditorAdapter
    {

    }

    public static class RangeEditor extends EmptyAttributeEditorAdapter
    {

    }

    public static class RangeModuloEditor extends EmptyAttributeEditorAdapter
    {

    }
    
    public static class IntEditor extends EmptyAttributeEditorAdapter
    {

    }

    public static class SubEditor extends EmptyAttributeEditorAdapter
    {

    }

    public static class InterfaceEditor extends EmptyAttributeEditorAdapter
    {

    }

    public static class RangeEditor2 extends EmptyAttributeEditorAdapter
    {

    }

    public static class ComboEditor extends EmptyAttributeEditorAdapter
    {

    }
}
