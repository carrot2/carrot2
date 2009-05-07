
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

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.DisposeBin;
import org.carrot2.workbench.core.helpers.DropDownMenuAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

/**
 * {@link SearchEditor}'s panels selection action.
 */
final class SearchEditorPanelsAction extends Action
{
    /**
     * The editor this action is attached to.
     */
    private final SearchEditor editor;

    /*
     * Drop-down menu associated with this action.
     */
    private IMenuCreator menuCreator = new IMenuCreator()
    {
        private DisposeBin bin = new DisposeBin();

        /*
         * 
         */
        public Menu getMenu(Control parent)
        {
            Menu menu = createItems(new Menu(parent));
            bin.add(menu);
            return menu;
        }

        /*
         * 
         */
        public Menu getMenu(Menu parent)
        {
            Menu menu = createItems(new Menu(parent));
            bin.add(menu);
            return menu;
        }

        /*
         * 
         */
        private Menu createItems(Menu menu)
        {
            for (final SearchEditorSections section : editor.getSections().keySet())
            {
                final MenuItem mi = new MenuItem(menu, SWT.CHECK);
                mi.setText(section.name);

                mi.setSelection(editor.getSections().get(section).visibility);
                mi.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e)
                    {
                        editor.setSectionVisibility(section, mi.getSelection());
                        e.doit = true;
                    }
                });
            }

            new MenuItem(menu, SWT.SEPARATOR);
            final MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Save as default layout");
            mi.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e)
                {
                    WorkbenchCorePlugin.getDefault().storeSectionsState(
                        editor.getSections());
                }
            });

            return menu;
        }

        public void dispose()
        {
            bin.dispose();
        }
    };

    /*
     * 
     */
    SearchEditorPanelsAction(String text, SearchEditor editor)
    {
        super(text, Action.AS_DROP_DOWN_MENU);
        this.editor = editor;

        setMenuCreator(menuCreator);
        setImageDescriptor(WorkbenchCorePlugin.getImageDescriptor("icons/panels.gif"));
    }

    /*
     * 
     */
    @Override
    public void runWithEvent(Event event)
    {
        DropDownMenuAction.showMenu(this, event);
    }
}
