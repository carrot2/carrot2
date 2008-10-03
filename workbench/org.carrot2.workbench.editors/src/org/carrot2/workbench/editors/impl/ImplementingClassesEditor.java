package org.carrot2.workbench.editors.impl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.carrot2.util.StringUtils;
import org.carrot2.util.attribute.Required;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.editors.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.common.collect.Lists;

/**
 * An editor for any fields that have {@link ImplementingClasses} annotation. The field is
 * initialized with an instance of one of the classes listed in
 * {@link ImplementingClasses#classes()}.
 */
public final class ImplementingClassesEditor extends AttributeEditorAdapter
{
    /**
     * Special value for no-selection.
     */
    private final static String NULL_VALUE = "";

    /**
     * The constraint.
     */
    private ImplementingClasses constraint;

    /**
     * Labels from {@link #constraint}.
     */
    private List<String> hints;

    /**
     * Classes from {@link #constraint}.
     */
    private List<Class<?>> classes;

    /*
     * 
     */
    private ComboViewer combo;

    /**
     * If <code>true</code> valid value selection is required (the attribute cannot be
     * <code>null</code>).
     */
    private boolean valueRequired;

    /*
     * 
     */
    @Override
    protected AttributeEditorInfo init()
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

        valueRequired = (descriptor.getAnnotation(Required.class) != null);

        hints = new ArrayList<String>();
        classes = Lists.newArrayList();

        if (!valueRequired)
        {
            hints.add(NULL_VALUE);
            classes.add(null);
        }

        for (Class<?> clazz : constraint.classes())
        {
            hints.add(StringUtils.splitCamelCase(ClassUtils.getShortClassName(clazz)));
            classes.add(clazz);
        }
        
        return new AttributeEditorInfo(1, false);
    }

    /*
     * 
     */
    @Override
    public void createEditor(Composite parent, int gridColumns)
    {
        combo = new ComboViewer(parent, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.SINGLE);
        
        combo.setContentProvider(new ArrayContentProvider());
        combo.setInput(hints);

        combo.addSelectionChangedListener(new ISelectionChangedListener()
        {
            public void selectionChanged(SelectionChangedEvent event)
            {
                fireAttributeChanged(new AttributeEvent(ImplementingClassesEditor.this));
            }
        });

        combo.getCombo().setLayoutData(
            GUIFactory.editorGridData()
                .grab(true, false)
                .hint(100, SWT.DEFAULT)
                .span(gridColumns, 1).create());

        combo.setSelection(StructuredSelection.EMPTY);
    }

    /*
     * 
     */
    @Override
    public void setValue(Object newValue)
    {
        if (newValue != null && !(newValue instanceof Class<?>))
        {
            newValue = newValue.getClass();
        }

        if (newValue != getValue())
        {
            int index;

            if (newValue == null)
            {
                index = -1;
            }
            else
            {
                index = classes.indexOf(newValue);
            }

            if (index == -1)
            {
                if (valueRequired) 
                {
                    combo.setSelection(StructuredSelection.EMPTY);
                    return;
                }
                index = 0;
            }

            combo.getCombo().select(index);
            fireAttributeChanged(new AttributeEvent(ImplementingClassesEditor.this));
        }
    }

    /*
     * 
     */
    @Override
    public Object getValue()
    {
        final int current = combo.getCombo().getSelectionIndex();

        if (current == -1)
        {
            return null;
        }
        else
        {
            return classes.get(current);
        }
    }
}
