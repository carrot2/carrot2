package org.carrot2.workbench.core.ui;

import org.carrot2.workbench.core.WorkbenchActionFactory;
import org.carrot2.workbench.editors.AttributeChangedEvent;
import org.carrot2.workbench.editors.IAttributeListener;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;

/**
 * A single page internally bound to a concrete editor.
 */
final class AttributeViewPage extends Page
{
    private final SearchEditor editor;
    private AttributeEditorGroups attributeEditors;
    
    /**
     * Synchronization of attribute values from {@link SearchEditor}'s
     * {@link SearchInput} back to this view.
     */
    private IAttributeListener editorToViewSync;

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
    }
    
    /*
     * 
     */
    @Override
    public void createControl(Composite parent)
    {
        attributeEditors = new AttributeEditorGroups(parent, editor.getAlgorithmDescriptor());
        registerListeners();
    }

    /*
     * 
     */
    @Override
    public Control getControl()
    {
        return this.attributeEditors;
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
         * Link attribute value changes:
         * attribute view -> search result
         */
        final IAttributeListener viewToEditorSync = new IAttributeListener() {
            public void attributeChange(AttributeChangedEvent event)
            {
                editor.getSearchResult().getInput().setAttribute(
                    event.key, event.value);
            }
        };
        this.attributeEditors.addAttributeChangeListener(viewToEditorSync);

        /*
         * Link attribute value changes:
         * search result -> attribute view
         */
        editorToViewSync = new IAttributeListener() {
            public void attributeChange(AttributeChangedEvent event)
            {
                /*
                 * temporarily unsubscribe from events from the attributes
                 * list to avoid event looping.
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
        editor.getSearchResult().getInput().removeAttributeChangeListener(editorToViewSync);
    }
}