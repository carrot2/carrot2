/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui;

import java.util.HashMap;

import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.ui.widgets.CScrolledComposite;
import org.carrot2.workbench.editors.AttributeEvent;
import org.carrot2.workbench.editors.AttributeListenerAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchPart;

/**
 * {@link SearchEditor}-tied view for running benchmarks.
 */
public final class BenchmarkView extends PageBookViewBase
{
    /**
     * Public identifier of this view.
     */
    public static final String ID = "org.carrot2.workbench.core.views.benchmark";

    /** Current global benchmark settings. */
    private final BenchmarkSettings benchmarkSettings = new BenchmarkSettings();

    /** Current global benchmark settings (attribute values). */
    private HashMap<String, Object> attrs;

    /**
     * Benchmark view is a composite of global attribute editors and editor-specific
     * part that shows the most recent benchmarking result.
     */
    @Override
    public void createPartControl(Composite parent)
    {
        final CScrolledComposite scroller = 
            new CScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);

        final Composite innerComposite = GUIFactory.createSpacer(scroller);
        final GridLayout gridLayout = (GridLayout) innerComposite.getLayout();
        gridLayout.numColumns = 1;
        gridLayout.verticalSpacing = LayoutConstants.getSpacing().y;
        scroller.setContent(innerComposite);

        // Create editor-specific benchmark page.
        super.createPartControl(innerComposite);
        getPageBook().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        // Create global editors.
        createSeparator(innerComposite);
        createSettingsPanel(innerComposite);
    }

    /**
     * Create the benchmarking view for a given part.
     */
    @Override
    protected PageRec doCreatePage(IWorkbenchPart part)
    {
        final SearchEditor editor = (SearchEditor) part;

        final BenchmarkViewPage page = new BenchmarkViewPage(editor, this);
        initPage(page);
        page.createControl(getPageBook());

        return new PageRec(part, page);
    }

    /**
     * Only react to {@link SearchEditor} instances.
     */
    @Override
    protected boolean isImportant(IWorkbenchPart part)
    {
        return part instanceof SearchEditor;
    }

    /**
     * Create settings panel.
     */
    private Control createSettingsPanel(Composite parent)
    {
        final BindableDescriptor descriptor = 
            BindableDescriptorBuilder.buildDescriptor(benchmarkSettings, true);

        attrs = descriptor.getDefaultValues();
        final AttributeGroups panel = new AttributeGroups(
            parent, descriptor, GroupingMethod.GROUP, null, attrs);
        panel.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        // Link changes in the editor to settings object.
        panel.addAttributeListener(new AttributeListenerAdapter()
        {
            public void valueChanged(AttributeEvent event)
            {
                attrs.put(event.key, event.value);
                try
                {
                    AttributeBinder.bind(benchmarkSettings, attrs, Input.class);
                }
                catch (InstantiationException e)
                {
                    Utils.logError(e, true);
                }
            }
        });
        panel.collapseAll();

        return panel;
    }

    /**
     * Create separator between settings and the benchmark panel.
     */
    private void createSeparator(Composite parent)
    {
        final Label label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(
            GridDataFactory.fillDefaults().grab(true, false).create());
    }

    /**
     * @return Return a clone of the current settings.
     */
    public BenchmarkSettings getCurrentSettings()
    {
        final BenchmarkSettings cloned = new BenchmarkSettings();
        try
        {
            AttributeBinder.bind(cloned, attrs, Input.class);
        }
        catch (Exception e)
        {
            Utils.logError(e, false);
        }
        return cloned;
    }
}
