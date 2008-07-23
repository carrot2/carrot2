package org.carrot2.workbench.editors.impl;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.carrot2.util.StringUtils;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.editors.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * An editor for any fields that have {@link ImplementingClasses} annotation. The field is
 * then initialized with an instance of one of the classes listed in
 * {@link ImplementingClasses#classes()}.
 */
public final class ImplementingClassesEditor extends AttributeEditorAdapter
{
    /**
     * The constraint.
     */
    private ImplementingClasses constraint;

    /**
     * Extracted from {@link #constraint}.
     */
    private List<Class<?>> classes;

    /*
     * 
     */
    private ComboViewer combo;

    /*
     * Event cycle avoidance.
     */
    private boolean updating;

    /*
     * 
     */
    public ImplementingClassesEditor()
    {
        super(new AttributeEditorInfo(1, false));
    }

    /*
     * 
     */
    @Override
    public AttributeEditorInfo init(AttributeDescriptor descriptor)
    {
        for (Annotation ann : descriptor.constraints)
        {
            if (ann instanceof ImplementingClasses)
            {
                constraint = (ImplementingClasses) ann;
            }
        }
        
        if (constraint == null)
        {
            throw new RuntimeException("Missing constraint.");
        }

        classes = Arrays.asList(constraint.classes());

        return super.init(descriptor);
    }

    /*
     * 
     */
    @Override
    public void createEditor(Composite parent, int gridColumns)
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
                propagateNewValue();
            }
        });

        combo.getCombo().setLayoutData(
            GUIFactory.editorGridData()
                .grab(true, false)
                .span(gridColumns, 1).create());
    }

    /*
     * 
     */
    private void propagateNewValue()
    {
        if (updating) 
        {
            return;
        }

        updating = true;
        fireAttributeChange(new AttributeChangedEvent(this));
        updating = false;
    }

    /*
     * 
     */
    @Override
    public void setValue(Object currentValue)
    {
        if (currentValue != null && currentValue != getValue())
        {
            int current = classes.indexOf(currentValue.getClass());
            combo.getCombo().select(current);

            propagateNewValue();
        }
    }

    /*
     * 
     */
    @Override
    public Object getValue()
    {
        final int current = combo.getCombo().getSelectionIndex();
        return (classes.get(current));
    }
}
