package org.carrot2.workbench.core.ui;

import java.util.EnumMap;
import java.util.Map;

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.ui.SearchEditor.SectionReference;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * Selection of visible panels.
 */
final class SearchEditorPanelSelectorDialog extends TrayDialog
{
    private static final int SAVE_DEFAULTS_ID = IDialogConstants.CLIENT_ID + 1;

    private final SearchEditor editor;
    private EnumMap<SearchEditorSections, SectionReference> localSections;

    /*
     * 
     */
    public SearchEditorPanelSelectorDialog(SearchEditor editor)
    {
        super(editor.getSite().getShell());

        this.editor = editor;

        localSections = new EnumMap<SearchEditorSections, SectionReference>(SearchEditorSections.class);
        for (Map.Entry<SearchEditorSections, SectionReference> e 
            : editor.getSections().entrySet())
        {
            localSections.put(e.getKey(), new SectionReference(e.getValue()));
        }
    }

    /*
     * 
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
        createButton(parent, SAVE_DEFAULTS_ID, "Save Default", false);
        createButton(parent, IDialogConstants.OK_ID, "Apply", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
    }

    /*
     * 
     */
    @Override
    protected void configureShell(Shell s)
    {
        super.configureShell(s);
        s.setText("Configure editor panels");
    }

    /*
     * 
     */
    @Override
    protected void buttonPressed(int buttonId)
    {
        if (buttonId == SAVE_DEFAULTS_ID)
        {
            WorkbenchCorePlugin.getDefault().storeSectionsState(
                localSections);
        }

        if (buttonId == IDialogConstants.OK_ID || buttonId == SAVE_DEFAULTS_ID)
        {
            for (SearchEditorSections section : localSections.keySet())
            {
                editor.setSectionVisibility(section, 
                    localSections.get(section).visibility);
            }
            okPressed();
        }
        else
        {
            cancelPressed();
        }
    }

    /*
     * 
     */
    @Override
    protected Control createDialogArea(Composite parent)
    {
        final Composite root = (Composite) super.createDialogArea(parent);
        createControls(root);
        root.layout();

        return root;
    }

    /*
     * 
     */
    private void createControls(Composite root)
    {
        final Composite content = new Composite(root, SWT.LEAD);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        content.setLayout(layout);

        GridData gd;
        final Label label = new Label(content, SWT.LEAD);
        label.setText("Select active panels:");
        gd = new GridData();
        gd.horizontalSpan = 2;
        label.setLayoutData(gd);

        int totalWeight = 0;
        for (SearchEditorSections section : localSections.keySet())
        {
            final SectionReference target = localSections.get(section);
            totalWeight += target.weight;
        }
        if (totalWeight == 0) totalWeight = 1;

        for (SearchEditorSections section : localSections.keySet())
        {
            final SectionReference target = localSections.get(section);

            final Button checkbox = new Button(content, SWT.CHECK | SWT.LEFT);
            checkbox.setText(section.name);
            checkbox.setSelection(target.visibility);
            checkbox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    target.visibility = checkbox.getSelection();
                }
            });

            final Label weightLabel = new Label(content, SWT.LEAD);
            weightLabel.setText((target.weight * 100) / totalWeight + "%");
        }
    }
}
