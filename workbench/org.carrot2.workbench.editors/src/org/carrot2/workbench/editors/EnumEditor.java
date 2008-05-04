package org.carrot2.workbench.editors;

import java.util.Arrays;

import org.carrot2.util.attribute.AttributeDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class EnumEditor extends AttributeEditorAdapter implements IAttributeEditor
{
    private Object [] constants;
    private ComboViewer viewer;

    @Override
    public void init(AttributeDescriptor descriptor)
    {
        super.init(descriptor);
        assert descriptor.type.isEnum();
        constants = descriptor.type.getEnumConstants();
    }

    @Override
    public void createEditor(Composite parent, Object layoutData)
    {
        viewer = new ComboViewer(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new LabelProvider()
        {
            @Override
            public String getText(Object element)
            {
                return ((Enum<?>) element).toString();
            }
        });
        viewer.setInput(constants);
        viewer.addSelectionChangedListener(new ISelectionChangedListener()
        {

            public void selectionChanged(SelectionChangedEvent event)
            {
                doEvent();
            }

        });
        viewer.getCombo().setLayoutData(layoutData);
    }

    private void doEvent()
    {
        AttributeChangeEvent event = new AttributeChangeEvent(this);
        fireAttributeChange(event);
    }

    @Override
    public void setValue(Object currentValue)
    {
        viewer.getCombo().select(Arrays.asList(constants).indexOf(currentValue));
    }

    @Override
    public Object getValue()
    {
        return constants[viewer.getCombo().getSelectionIndex()];
    }

}
