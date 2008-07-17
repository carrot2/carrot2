package org.carrot2.workbench.core.ui;

import java.util.Map;

import org.carrot2.core.ProcessingResult;
import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.WorkbenchActionFactory;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.core.preferences.PreferenceConstants;
import org.carrot2.workbench.core.ui.widgets.CScrolledComposite;
import org.carrot2.workbench.editors.AttributeChangedEvent;
import org.carrot2.workbench.editors.AttributeListenerAdapter;
import org.carrot2.workbench.editors.IAttributeListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
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
     * Layout actions associated with this page's menu.
     */
    private LayoutAction [] layoutActions = new LayoutAction []
    {
        new LayoutAction(GroupingMethod.GROUP, "Attribute semantics"),
        new LayoutAction(GroupingMethod.LEVEL, "Attribute level"),
        new LayoutAction(GroupingMethod.STRUCTURE, "Declaring class"), null, /* Separator */
        new LayoutAction(GroupingMethod.NONE, "None"),
    };

    /**
     * Main control in this page.
     */
    private Composite mainControl;

    /**
     * Sets the given grouping method and updates state of all controls associated with
     * grouping.
     */
    private final class LayoutAction extends Action
    {
        public final GroupingMethod grouping;

        public LayoutAction(GroupingMethod grouping, String label)
        {
            super(label, Action.AS_RADIO_BUTTON);
            this.grouping = grouping;
        }

        @Override
        public void run()
        {
            /*
             * Save the default.
             */
            WorkbenchCorePlugin.getDefault().getPreferenceStore().setValue(
                PreferenceConstants.ATTRIBUTE_GROUPING_LAYOUT, grouping.name());

            updateGroupingState(grouping);
        }
    }

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

        createMenu(bars.getMenuManager());

        bars.updateActionBars();
    }

    /*
     * 
     */
    private void createMenu(IMenuManager viewMenu)
    {
        /*
         * Layout menu.
         */
        final IMenuManager layoutSubMenu = new MenuManager("&Grouping");
        viewMenu.add(layoutSubMenu);
        for (IAction action : layoutActions)
        {
            if (action == null)
            {
                layoutSubMenu.add(new Separator());
                continue;
            }
            layoutSubMenu.add(action);
        }
        viewMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
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
    }

    /**
     * Update grouping state in toolbars/ menus, push initial values 
     * to editors.
     */
    private void updateGroupingState(GroupingMethod grouping)
    {
        attributeEditors.setGrouping(grouping);
        for (LayoutAction action : layoutActions)
        {
            if (action != null)
            {
                action.setChecked(grouping.equals(action.grouping));
            }
        }
        
        /*
         * Refresh current attribute values. 
         */

        final ProcessingResult pr = this.editor.getSearchResult().getProcessingResult();
        if (pr != null)
        {
            for (Map.Entry<String, Object> e : pr.getAttributes().entrySet())
            {
                attributeEditors.setAttribute(e.getKey(), e.getValue());
            }
        }
    }

    /*
     * 
     */
    @Override
    public void createControl(Composite parent)
    {
        final GroupingMethod defaultGrouping = GroupingMethod.valueOf(WorkbenchCorePlugin
            .getDefault().getPreferenceStore().getString(
                PreferenceConstants.ATTRIBUTE_GROUPING_LAYOUT));

        final SharedScrolledComposite scroller = new CScrolledComposite(parent,
            SWT.H_SCROLL | SWT.V_SCROLL);

        final Composite spacer = GUIFactory.createSpacer(scroller);

        scroller.setContent(spacer);
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(false);

        attributeEditors = new AttributeGroups(spacer, editor.getAlgorithmDescriptor(),
            defaultGrouping);
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
        final IAttributeListener viewToEditorSync = new AttributeListenerAdapter()
        {
            public void attributeChange(AttributeChangedEvent event)
            {
                editor.getSearchResult().getInput().setAttribute(event.key, event.value);
            }
        };
        this.attributeEditors.addAttributeChangeListener(viewToEditorSync);

        /*
         * Link attribute value changes: search result -> attribute view
         */
        editorToViewSync = new AttributeListenerAdapter()
        {
            public void attributeChange(AttributeChangedEvent event)
            {
                /*
                 * temporarily unsubscribe from events from the attributes list to avoid
                 * event looping.
                 */
                attributeEditors.removeAttributeChangeListener(viewToEditorSync);
                attributeEditors.setAttribute(event.key, event.value);
                attributeEditors.addAttributeChangeListener(viewToEditorSync);
            }
        };
        editor.getSearchResult().getInput().addAttributeChangeListener(editorToViewSync);
    }

    /*
     * 
     */
    private void unregisterListeners()
    {
        editor.getSearchResult().getInput().removeAttributeChangeListener(
            editorToViewSync);
    }
}