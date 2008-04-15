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
        GridLayout gl = new GridLayout(2, false);
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        holder.setLayout(gl);

        createScale(holder);
        GridData gd1 = new GridData();
        gd1.horizontalAlignment = SWT.FILL;
        gd1.grabExcessHorizontalSpace = true;
        scale.setLayoutData(gd1);

        createSpinner(holder);
        GridData gd2 = new GridData();
        gd2.minimumWidth = 30;
        spinner.setLayoutData(gd2);
    }

    private void createSpinner(Composite holder)
    {
        spinner = new Spinner(holder, SWT.BORDER);
        spinner.setMinimum(constraint.min());
        spinner.setMaximum(constraint.max());
        spinner.setIncrement(scale.getIncrement());
        spinner.setPageIncrement(scale.getPageIncrement());
        spinner.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                scale.setSelection(spinner.getSelection());
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
        scale.setSelection((Integer) currentValue);
    }

    @Override
    public Object getValue()
    {
        return scale.getSelection();
    }

}
