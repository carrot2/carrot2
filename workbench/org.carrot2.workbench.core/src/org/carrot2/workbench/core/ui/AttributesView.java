package org.carrot2.workbench.core.ui;

import org.carrot2.workbench.editors.AttributeChangedEvent;
import org.carrot2.workbench.editors.IAttributeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.Page;

/**
 * A view showing attribute values associated with the active editor's
 * {@link SearchResult}.
 */
public final class AttributesView extends PageBookViewBase
{
    public static final String ID = "org.carrot2.workbench.core.views.attributes";

    /**
     * A single page internally bound to a concrete editor.
     */
    private static class AttributeEditorPage extends Page
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
        public AttributeEditorPage(SearchEditor editor)
        {
            this.editor = editor;
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
    
    /**
     * Create a tree view for the given part.
     */
    @Override
    protected PageRec doCreatePage(IWorkbenchPart part)
    {
        final SearchEditor editor = (SearchEditor) part;

        final AttributeEditorPage page = new AttributeEditorPage(editor);
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
}
