package org.carrot2.workbench.core.ui;

import java.util.EnumMap;

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.preferences.PreferenceConstants;
import org.carrot2.workbench.core.preferences.WorkbenchPreferencesPage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * Visible panels selection dialog.
 */
final class SearchEditorPanelSelectorDialog extends org.eclipse.jface.dialogs.TrayDialog
{
    private final EnumMap<SearchEditorSections, Boolean> visibility;
    private final EnumMap<SearchEditorSections, Button> buttons;

    public SearchEditorPanelSelectorDialog(Shell parent, EnumMap<SearchEditorSections, Boolean> visibility)
    {
        super(parent);

        this.visibility = visibility;
        this.buttons = new EnumMap<SearchEditorSections, Button>(SearchEditorSections.class);
    }

    @Override
    protected void configureShell(Shell newShell)
    {
        super.configureShell(newShell);
        newShell.setText("Editor panels");
    }

    @Override
    protected void okPressed()
    {
        for (SearchEditorSections section : buttons.keySet())
        {
            visibility.put(section, buttons.get(section).getSelection());
        }

        super.okPressed();
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite root = (Composite) super.createDialogArea(parent);
        createControls(root);
        root.layout();

        return root;
    }

    private void createControls(Composite root)
    {
        GridLayout parentLayout = new GridLayout();
        root.setLayout(parentLayout);

        Label label = new Label(root, SWT.LEFT);
        label.setText("Visible panels:");

        for (SearchEditorSections section : visibility.keySet())
        {
            final Button checkbox = new Button(root, SWT.CHECK | SWT.LEFT);
            checkbox.setText(section.name);
            checkbox.setSelection(visibility.get(section));

            buttons.put(section, checkbox);
        }

        final Link preferencesLink = new Link(root, SWT.NONE);
        final GridData gridData = new GridData();
        gridData.verticalIndent = 10;
        preferencesLink.setLayoutData(gridData);
        preferencesLink.setText("<a>Edit defaults...</a>");
        preferencesLink.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                PreferenceDialog dialog =
                    PreferencesUtil.createPreferenceDialogOn(getShell(),
                        WorkbenchPreferencesPage.ID, new String []
                        {
                            WorkbenchPreferencesPage.ID
                        }, null);

                if (dialog.open() == Window.OK)
                {
                    IPreferenceStore store =
                        WorkbenchCorePlugin.getDefault().getPreferenceStore();

                    // Set to new defaults.
                    for (SearchEditorSections section : buttons.keySet())
                    {
                        final String key = PreferenceConstants.getSectionVisibilityKey(section);
                        buttons.get(section).setSelection(store.getBoolean(key));
                    }
                }
            }
        });
    }
}
