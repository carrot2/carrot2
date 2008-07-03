package org.carrot2.workbench.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public abstract class RangeEditorBase extends AttributeEditorAdapter implements
    IAttributeEditor
{
    private boolean duringSelection;
    private Scale scale;
    private Spinner spinner;

    @Override
    public void createEditor(Composite parent, Object layoutData)
    {
        Composite holder = new Composite(parent, SWT.NULL);
        holder.setLayoutData(layoutData);
        GridLayout gl = new GridLayout();
        gl.marginWidth = 0;
        gl.marginHeight = 0;

        if (isBounded())
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

    protected abstract int getDigits();

    protected abstract int getIncrement();

    protected int getIntValue()
    {
        return spinner.getSelection();
    }

    protected abstract int getMaximum();

    protected abstract int getMinimum();

    protected abstract int getPageIncrement();

    protected abstract boolean isBounded();

    protected void setIntValue(int currentValue)
    {
        spinner.setSelection(currentValue);
        if (scale != null)
        {
            scale.setSelection(currentValue);
        }
    }

    private void attachEvents(final Control control)
    {
        Listener eventDoer = new Listener()
        {
            public void handleEvent(Event event)
            {
                doEvent();
            }
        };
        control.addListener(SWT.KeyUp, eventDoer);
        control.addListener(SWT.MouseUp, eventDoer);
    }

    private void createScale(Composite holder)
    {
        scale = new Scale(holder, SWT.HORIZONTAL);
        scale.setMinimum(getMinimum());
        scale.setMaximum(getMaximum());
        scale.setIncrement(getIncrement());
        scale.setPageIncrement(getPageIncrement());
        scale.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event event)
            {
                duringSelection = true;
                spinner.setSelection(scale.getSelection());
            }
        });
        attachEvents(scale);
    }

    private void createSpinner(Composite holder)
    {
        spinner = new Spinner(holder, SWT.BORDER);
        spinner.setMinimum(getMinimum());
        spinner.setMaximum(getMaximum());
        spinner.setDigits(getDigits());
        if (isBounded())
        {
            spinner.setIncrement(getIncrement());
            spinner.setPageIncrement(getPageIncrement());
        }
        else
        {
            spinner.setIncrement(getIncrement());
            spinner.setPageIncrement(getPageIncrement());
        }
        spinner.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                duringSelection = true;
                if (scale != null)
                {
                    scale.setSelection(spinner.getSelection());
                }
            }
        });
        attachEvents(spinner);
    }

    private void doEvent()
    {
        if (duringSelection)
        {
            duringSelection = false;
            AttributeChangedEvent event = new AttributeChangedEvent(this);
            fireAttributeChange(event);
        }
    }

}
