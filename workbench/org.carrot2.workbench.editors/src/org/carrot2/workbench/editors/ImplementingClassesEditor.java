package org.carrot2.workbench.editors;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.carrot2.util.StringUtils;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ImplementingClassesEditor extends AttributeEditorAdapter
{
    private ImplementingClasses constraint;
    private ComboViewer combo;
    private List<Class<?>> classes;

    @Override
    public void init(AttributeDescriptor descriptor)
    {
        super.init(descriptor);
        for (Annotation ann : descriptor.constraints)
        {
            if (ann instanceof ImplementingClasses)
            {
                constraint = (ImplementingClasses) ann;
            }
        }
        classes = Arrays.asList(constraint.classes());
    }

    @Override
    public void createEditor(Composite parent, Object layoutData)
    {
        combo = new ComboViewer(parent, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.SINGLE);
        combo.setContentProvider(new ArrayContentProvider());
        combo.setLabelProvider(new LabelProvider()
        {

            public String getText(Object element)
            {
                return StringUtils.splitCamelCase(ClassUtils
                    .getShortClassName((Class<?>) element));
            }
        });
        combo.setInput(constraint.classes());
        combo.addSelectionChangedListener(new ISelectionChangedListener()
        {

            public void selectionChanged(SelectionChangedEvent event)
            {
                doEvent();
            }

        });
        combo.getCombo().setLayoutData(layoutData);
    }

    private void doEvent()
    {
        AttributeChangedEvent event = new AttributeChangedEvent(this);
        fireAttributeChange(event);
    }

    @Override
    public void setValue(Object currentValue)
    {
        if (currentValue != null)
        {
            int current = classes.indexOf(currentValue.getClass());
            combo.getCombo().select(current);
        }
    }

    @Override
    public Object getValue()
    {
        int current = combo.getCombo().getSelectionIndex();
        return (classes.get(current));
    }

}
