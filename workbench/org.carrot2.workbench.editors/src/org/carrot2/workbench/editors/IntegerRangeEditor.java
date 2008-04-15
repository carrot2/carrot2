package org.carrot2.workbench.editors;

import java.lang.annotation.Annotation;

import org.carrot2.util.RangeUtils;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.constraint.IntRange;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class IntegerRangeEditor extends AttributeEditorAdapter implements
    IAttributeEditor
{
    private IntRange constraint;
    private Scale scale;
    private Spinner spinner;

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
        Composite holder = new Composite(parent, SWT.NULL);
        holder.setLayoutData(layoutData);
        GridLayout gl = new GridLayout();
        gl.marginWidth = 0;
        gl.marginHeight = 0;

        if (constraint.max() < Integer.MAX_VALUE)
        {
            createScale(holder);
            GridData gd1 = new GridData();
            gd1.horizontalAlignment = SWT.FILL;
            gd1.grabExcessHorizontalSpace = true;
            scale.setLayoutData(gd1);

            gl.numColumns = 2;
        }

        createSpinner(holder);
        GridData gd2 = new GridData();
        gd2.minimumWidth = 30;
        gd2.horizontalAlignment = SWT.FILL;
        gd2.grabExcessHorizontalSpace = true;
        spinner.setLayoutData(gd2);

        holder.setLayout(gl);
    }

    private void createSpinner(Composite holder)
    {
        spinner = new Spinner(holder, SWT.BORDER);
        spinner.setMinimum(constraint.min());
        spinner.setMaximum(constraint.max());
        spinner.setIncrement(RangeUtils.getIntMinorTicks(constraint.min(), constraint
            .max()));
        spinner.setPageIncrement(RangeUtils.getIntMajorTicks(constraint.min(), constraint
            .max()));
        spinner.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (scale != null)
                {
                    scale.setSelection(spinner.getSelection());
                }
                doEvent();
            }
        });
    }

    private void createScale(Composite holder)
    {
        scale = new Scale(holder, SWT.HORIZONTAL);
        scale.setMinimum(constraint.min());
        scale.setMaximum(constraint.max());
        scale.setIncrement(RangeUtils
            .getIntMinorTicks(constraint.min(), constraint.max()));
        scale.setPageIncrement(RangeUtils.getIntMajorTicks(constraint.min(), constraint
            .max()));
        scale.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                spinner.setSelection(scale.getSelection());
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
        spinner.setSelection((Integer) currentValue);
        if (scale != null)
        {
            scale.setSelection((Integer) currentValue);
        }
    }

    @Override
    public Object getValue()
    {
        return spinner.getSelection();
    }

}
