package org.carrot2.workbench.core.ui;

import java.util.EnumMap;

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.ui.SearchEditor.SectionReference;
import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

/**
 * {@link SearchEditor}'s panels selection action.
 */
final class SearchEditorPanelSelectorAction extends Action
{
    /*
     * Drop-down menu associated with this action.
     */
    private IMenuCreator menuCreator = new IMenuCreator()
    {
        private Menu menu;

        public Menu getMenu(Control parent)
        {
            menu = new Menu(parent);
            createItems();
            return menu;
        }

        /*
         * 
         */
        public Menu getMenu(Menu parent)
        {
            if (menu == null)
            {
                menu = new Menu(parent);
                createItems();
            }
            return menu;
        }

        /*
         * 
         */
        private void createItems()
        {
            for (final SearchEditorSections section : editor.getSections().keySet())
            {
                final MenuItem mi = new MenuItem(menu, SWT.CHECK);
                mi.setText(section.name);

                mi.setSelection(editor.getSections().get(section).visibility);
                mi.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        editor.setSectionVisibility(section, mi.getSelection());
                        e.doit = true;
                    }
                });
            }
        }

        public void dispose()
        {
            if (menu != null)
            {
                menu.dispose();
            }
        }
    };

    /**
     * The editor this action is attached to.
     */
    private final SearchEditor editor;

    /*
     * 
     */
    SearchEditorPanelSelectorAction(String text, SearchEditor editor)
    {
        super(text, IAction.AS_DROP_DOWN_MENU);
        this.editor = editor;
    }

    /*
     * 
     */
    @Override
    public void run()
    {
        final EnumMap<SearchEditorSections, Boolean> visibility = 
            new EnumMap<SearchEditorSections, Boolean>(SearchEditorSections.class);

        final EnumMap<SearchEditorSections, SectionReference> sections = editor.getSections();

        for (SearchEditorSections section : sections.keySet())
        {
            visibility.put(section, sections.get(section).visibility);
        }

        final SearchEditorPanelSelectorDialog dialog = new SearchEditorPanelSelectorDialog(
            Display.getDefault().getActiveShell(), visibility);

        if (dialog.open() != Window.CANCEL)
        {
            for (SearchEditorSections section : sections.keySet())
            {
                editor.setSectionVisibility(section, visibility.get(section));
            }
        }
    }

    /*
     * 
     */
    @Override
    public IMenuCreator getMenuCreator()
    {
        return menuCreator;
    }

    /*
     * 
     */
    @Override
    public ImageDescriptor getImageDescriptor()
    {
        return WorkbenchCorePlugin.getImageDescriptor("icons/panels.gif");
    }
}