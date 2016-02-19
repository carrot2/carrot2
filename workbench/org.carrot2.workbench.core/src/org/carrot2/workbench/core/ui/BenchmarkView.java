
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui;

import java.io.IOException;
import java.util.HashMap;

import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.helpers.*;
import org.carrot2.workbench.core.ui.widgets.CScrolledComposite;
import org.carrot2.workbench.editors.AttributeEvent;
import org.carrot2.workbench.editors.AttributeListenerAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;

import org.carrot2.shaded.guava.common.collect.Maps;

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

    /**
     * Main GUI control of this view.
     */
    private CScrolledComposite mainControl;

    /**
     * Attribute groups panel.
     */
    private AttributeGroups attributeGroups;

    /**
     * State restoration.
     */
    private BenchmarkViewMemento restoreState;

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
        this.mainControl = scroller;

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
        
        // Restore GUI state.
        restoreState();
    }

    /*
     * 
     */
    @Override
    protected void showPageRec(PageRec pageRec)
    {
        super.showPageRec(pageRec);
        mainControl.reflow(true);
    }

    /*
     * 
     */
    @Override
    protected IPage createDefaultPage(PageBook book)
    {
        MessagePage defaultPage = new MessagePage();
        defaultPage.setMessage("No active search result.");
        initPage(defaultPage);
        defaultPage.createControl(book);
        return defaultPage;
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
     * Restore state between runs.
     */
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException
    {
        super.init(site, memento);

        try
        {
            if (memento != null)
            {
                restoreState = SimpleXmlMemento.getChild(BenchmarkViewMemento.class, memento);
            }
        }
        catch (IOException e)
        {
            Utils.logError(e, false);
        }
    }

    /**
     * Persist state between runs.
     */
    @Override
    public void saveState(IMemento memento)
    {
        super.saveState(memento);

        try
        {
            final BenchmarkViewMemento state = new BenchmarkViewMemento();
            state.settings = this.benchmarkSettings;
            state.sectionsExpansionState = this.attributeGroups.getExpansionStates();

            SimpleXmlMemento.addChild(memento, state);
        }
        catch (IOException e)
        {
            Utils.logError(e, false);
        }
    }
    
    /**
     * Restore GUI state. We can't do it in {@link #init(IViewSite, IMemento)}
     * because GUI elements are not available then.
     */
    private void restoreState()
    {
        if (this.restoreState == null) return;

        try
        {
            final HashMap<String, Object> attrs = Maps.newHashMap(); 
            AttributeBinder.get(restoreState.settings, attrs, Input.class);
            this.attributeGroups.setAttributes(attrs);
            this.attributeGroups.setExpanded(restoreState.sectionsExpansionState);
        }
        catch (Exception e)
        {
            Utils.logError(e, false);
        }
    }
    
    /**
     * Create settings panel.
     */
    private Control createSettingsPanel(Composite parent)
    {
        final BindableDescriptor descriptor = 
            BindableDescriptorBuilder.buildDescriptor(benchmarkSettings);

        attributeGroups = new AttributeGroups(
            parent, descriptor, GroupingMethod.GROUP, null, descriptor.getDefaultValues());
        attributeGroups.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        // Link changes in the editor to settings object.
        attributeGroups.addAttributeListener(new AttributeListenerAdapter()
        {
            public void valueChanged(AttributeEvent event)
            {
                try
                {
                    final HashMap<String, Object> attrs = Maps.newHashMap();
                    attrs.put(event.key, event.value);
                    AttributeBinder.set(benchmarkSettings, attrs, Input.class);
                }
                catch (InstantiationException e)
                {
                    Utils.logError(e, true);
                }
            }
        });
        attributeGroups.setExpanded(false);

        return attributeGroups;
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
    BenchmarkSettings getCurrentSettings()
    {
        final BenchmarkSettings cloned = new BenchmarkSettings();
        try
        {
            HashMap<String, Object> attrs = Maps.newHashMap();
            AttributeBinder.get(benchmarkSettings, attrs, Input.class);
            AttributeBinder.set(cloned, attrs, Input.class);
        }
        catch (Exception e)
        {
            Utils.logError(e, false);
        }
        return cloned;
    }
}
