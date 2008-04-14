package org.carrot2.workbench.editors;

import java.lang.annotation.Annotation;

import org.carrot2.util.RangeUtils;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.constraint.IntRange;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;

public class IntegerRangeEditor extends AttributeEditorAdapter implements
    IAttributeEditor
{
    private IntRange constraint;
    private Scale scale;

    @Override
    public void init(AttributeDescriptor descriptor)
    {
        super.init(descriptor);
        for (Annotation ann : descriptor.constraints)
        {
            if (ann instanceof IntRange)
            {
                constraint = (IntRange) ann;
            }
        }
    }

    @Override
    public void createEditor(Composite parent, Object layoutData)
    {
        scale = new Scale(parent, SWT.HORIZONTAL);
        scale.setMinimum(constraint.min());
        scale.setMaximum(constraint.max());
        scale.setIncrement(RangeUtils
            .getIntMinorTicks(constraint.min(), constraint.max()));
        scale.setPageIncrement(RangeUtils.getIntMajorTicks(constraint.min(), constraint
            .max()));
        scale.setLayoutData(layoutData);
        scale.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doEvent();
            }
        });
    }

    private void doEvent()
    {
        AttributeChangeEvent event = new AttributeChangeEvent(this);
        fireAttributeChange(event);
    }

    @Override
    public void setValue(Object currentValue)
    {
        scale.setSelection((Integer) currentValue);
    }

    @Override
    public Object getValue()
    {
        return scale.getSelection();
    }

}
