/**
 * 
 */
package org.carrot2.workbench.core.ui;

import java.util.*;

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

/**
 * {@link SearchEditor}'s panels selection action.
 */
final class SearchEditorPanelSelectorAction extends Action
{
    /*
     * Drop-down menu from action.
     */
    private IMenuCreator creator = new IMenuCreator()
    {
        private Menu menu;
        private Collection<Image> images = new ArrayList<Image>();

        public Menu getMenu(Control parent)
        {
            menu = new Menu(parent);
            createItems();
            return menu;
        }

        public Menu getMenu(Menu parent)
        {
            menu = new Menu(parent);
            createItems();
            return menu;
        }

        private void createItems()
        {
            /*
             * TODO: This scans through all the extensions and retrieves an icon for
             * the panel. Replace with something less awkward?
             */
            IExtension ext = Platform.getExtensionRegistry().getExtension(
                "org.eclipse.ui.views", "org.carrot2.workbench.core.views");
            final Map<String, ImageDescriptor> icons = new HashMap<String, ImageDescriptor>();
            for (int i = 0; i < ext.getConfigurationElements().length; i++)
            {
                IConfigurationElement view = ext.getConfigurationElements()[i];
                if (view.getName().equals("view")
                    && view.getAttribute("icon") != null)
                {
                    icons.put(view.getAttribute("id"), WorkbenchCorePlugin
                        .getImageDescriptor(view.getAttribute("icon")));
                }
            }

            for (SearchEditorSections section : EnumSet
                .allOf(SearchEditorSections.class))
            {
                createItem(section, icons.get(section.iconID));
            }
        }

        private void createItem(final SearchEditorSections section,
            ImageDescriptor image)
        {
            final MenuItem mi = new MenuItem(menu, SWT.CHECK);
            mi.setText(section.name);

            final Image icon = image.createImage();
            images.add(icon);
            mi.setImage(icon);
            mi.setSelection((Boolean) editor.getSections().get(section).getData("visible"));
            mi.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    editor.toogleSectionVisibility(section, mi.getSelection());
                    e.doit = true;
                }
            });
        }

        public void dispose()
        {
            for (Image icon : images)
            {
                icon.dispose();
            }

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
        final EnumMap<SearchEditorSections, Boolean> visibility = new EnumMap<SearchEditorSections, Boolean>(
            SearchEditorSections.class);

        for (SearchEditorSections section : EnumSet.allOf(SearchEditorSections.class))
        {
            visibility.put(section, (Boolean) editor
                .getSections().get(section).getData("visible"));
        }

        final SearchEditorPanelSelectorDialog dialog = new SearchEditorPanelSelectorDialog(Display
            .getDefault().getActiveShell(), visibility);

        if (dialog.open() != Window.CANCEL)
        {
            for (SearchEditorSections section : EnumSet
                .allOf(SearchEditorSections.class))
            {
                editor.toogleSectionVisibility(section, visibility.get(section));
            }
        }
    }

    @Override
    public IMenuCreator getMenuCreator()
    {
        return creator;
    }

    @Override
    public ImageDescriptor getImageDescriptor()
    {
        return WorkbenchCorePlugin.getImageDescriptor("icons/panels.gif");
    }
}