
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

import java.util.Collections;

import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.WorkbenchActionFactory;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.core.preferences.PreferenceConstants;
import org.carrot2.workbench.core.ui.actions.GroupingMethodAction;
import org.carrot2.workbench.core.ui.widgets.CScrolledComposite;
import org.carrot2.workbench.editors.*;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;

/**
 * A single page internally bound to a concrete editor.
 */
final class AttributeViewPage extends Page
{
    /**
     * The search editor this page is attached to.
     */
    private final SearchEditor editor;

    /**
     * Attribute editors.
     */
    private AttributeGroups attributeEditors;

    /**
     * Synchronization of attribute values from {@link SearchEditor}'s
     * {@link SearchInput} back to this view.
     */
    private IAttributeListener editorToViewSync;

    /**
     * Synchronization of attribute values from this page to the {@link SearchEditor}.
     */
    private AttributeListenerAdapter viewToEditorSync;

    /**
     * Main control in this page.
     */
    private Composite mainControl;

    /*
     * 
     */
    public AttributeViewPage(SearchEditor editor)
    {
        this.editor = editor;
    }

    /*
     * 
     */
    @Override
    public void init(IPageSite pageSite)
    {
        super.init(pageSite);

        final IActionBars bars = pageSite.getActionBars();
        createToolbarActions(bars.getToolBarManager());

        bars.updateActionBars();
    }

    /*
     * 
     */
    private void createToolbarActions(IToolBarManager toolBarManager)
    {
        /*
         * Add a local auto-update action button.
         */
        final IWorkbenchWindow window = getSite().getWorkbenchWindow();
        toolBarManager.add(WorkbenchActionFactory.AUTO_UPDATE_ACTION.create(window));

        /*
         * Add attribute grouping action.
         */
        toolBarManager.add(new GroupingMethodAction(
            PreferenceConstants.GROUPING_ATTRIBUTE_VIEW));
    }

    /**
     * Update grouping state in toolbars/ menus, push initial values to editors.
     */
    private void updateGroupingState(GroupingMethod grouping)
    {
        attributeEditors.setGrouping(grouping);
        attributeEditors.setAttributes(
            editor.getSearchResult().getInput()
            .getAttributeValueSet().getAttributeValues());
    }

    /*
     * 
     */
    @Override
    public void createControl(Composite parent)
    {
        final IPreferenceStore prefStore = WorkbenchCorePlugin.getDefault()
            .getPreferenceStore();

        final String key = PreferenceConstants.GROUPING_ATTRIBUTE_VIEW;
        prefStore.addPropertyChangeListener(new PropertyChangeListenerAdapter(key)
        {
            protected void propertyChangeFiltered(PropertyChangeEvent event)
            {
                updateGroupingState(GroupingMethod.valueOf(prefStore.getString(key)));
            }
        });

        final SharedScrolledComposite scroller = new CScrolledComposite(parent,
            SWT.H_SCROLL | SWT.V_SCROLL);

        final Composite spacer = GUIFactory.createSpacer(scroller);

        scroller.setContent(spacer);
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(false);

        final GroupingMethod defaultGrouping = GroupingMethod.valueOf(prefStore
            .getString(key));

        attributeEditors = new AttributeGroups(spacer, editor.getAlgorithmDescriptor(),
            defaultGrouping, null, Collections.<String, Object> emptyMap());
        attributeEditors.setLayoutData(GridDataFactory.fillDefaults().grab(true, true)
            .create());

        this.mainControl = scroller;
        scroller.reflow(true);

        updateGroupingState(defaultGrouping);
        registerListeners();
    }

    /*
     * 
     */
    @Override
    public Control getControl()
    {
        return this.mainControl;
    }

    /*
     * 
     */
    @Override
    public void setFocus()
    {
        attributeEditors.setFocus();
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        unregisterListeners();
        attributeEditors.dispose();

        super.dispose();
    }

    /*
     * 
     */
    private void registerListeners()
    {
        /*
         * Link attribute value changes: attribute view -> search result
         */
        viewToEditorSync = new AttributeListenerAdapter()
        {
            public void valueChanged(AttributeEvent event)
            {
                editor.getSearchResult().getInput().setAttribute(event.key, event.value);
            }
        };
        this.attributeEditors.addAttributeListener(viewToEditorSync);

        /*
         * Link attribute value changes: search result -> attribute view
         */
        editorToViewSync = new AttributeListenerAdapter()
        {
            public void valueChanged(AttributeEvent event)
            {
                /*
                 * temporarily unsubscribe from events from the attributes list to avoid
                 * event looping.
                 */
                attributeEditors.removeAttributeListener(viewToEditorSync);
                attributeEditors.setAttribute(event.key, event.value);
                attributeEditors.addAttributeListener(viewToEditorSync);
            }
        };
        editor.getSearchResult().getInput().addAttributeListener(editorToViewSync);
    }

    /*
     * 
     */
    private void unregisterListeners()
    {
        editor.getSearchResult().getInput().removeAttributeListener(editorToViewSync);
        attributeEditors.removeAttributeListener(viewToEditorSync);
    }
}
