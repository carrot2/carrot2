package org.carrot2.workbench.core.ui;

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.preferences.CarrotPreferencePage;
import org.carrot2.workbench.core.preferences.PreferenceConstants;
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
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder,
 * which is free for non-commercial use. If Jigloo is being used commercially (ie, by a
 * corporation, company or business for any purpose whatever) then you should purchase a
 * license for each developer using Jigloo. Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT
 * BEEN PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY
 * CORPORATE OR COMMERCIAL PURPOSE.
 */
public class ChooseSectionsDialog extends org.eclipse.jface.dialogs.TrayDialog
{

    private Button clustersCheckbox;
    private Button documentsCheckbox;
    private Button attributesCheckbox;
    private boolean [] flags;

    public ChooseSectionsDialog(Shell parent, boolean [] visibilityFlags)
    {
        super(parent);
        assert (visibilityFlags.length == 3);
        flags = visibilityFlags;
    }

    @Override
    protected void configureShell(Shell newShell)
    {
        super.configureShell(newShell);
        newShell.setText("Choose visible panels");
    }

    @Override
    protected void okPressed()
    {
        flags[0] = clustersCheckbox.getSelection();
        flags[1] = documentsCheckbox.getSelection();
        flags[2] = attributesCheckbox.getSelection();
        super.okPressed();
    }

    public boolean [] getVisibilityFlags()
    {
        return flags;
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite root = (Composite) super.createDialogArea(parent);
        root.setSize(400, root.getSize().y);
        createControls(root);
        clustersCheckbox.setSelection(flags[0]);
        documentsCheckbox.setSelection(flags[1]);
        attributesCheckbox.setSelection(flags[2]);
        return root;
    }

    private void createControls(Composite root)
    {
        GridLayout parentLayout = new GridLayout();
        root.setLayout(parentLayout);
        {
            Label label = new Label(root, SWT.LEFT);
            label.setText("Choose panels in this editor:");
        }
        {
            clustersCheckbox = new Button(root, SWT.CHECK | SWT.LEFT);
            clustersCheckbox.setText("Clusters");
        }
        {
            documentsCheckbox = new Button(root, SWT.CHECK | SWT.LEFT);
            documentsCheckbox.setText("Documents");
        }
        {
            attributesCheckbox = new Button(root, SWT.CHECK | SWT.LEFT);
            attributesCheckbox.setText("Attributes");
        }
        {
            Link preferencesLink = new Link(root, SWT.NONE);
            GridData gridData = new GridData();
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
                            CarrotPreferencePage.ID, new String []
                            {
                                CarrotPreferencePage.ID
                            }, null);
                    int result = dialog.open();
                    if (result == Window.OK)
                    {
                        IPreferenceStore store =
                            WorkbenchCorePlugin.getDefault().getPreferenceStore();
                        clustersCheckbox.setSelection(store
                            .getBoolean(PreferenceConstants.P_SHOW_CLUSTERS));
                        documentsCheckbox.setSelection(store
                            .getBoolean(PreferenceConstants.P_SHOW_DOCUMENTS));
                        attributesCheckbox.setSelection(store
                            .getBoolean(PreferenceConstants.P_SHOW_ATTRIBUTES));
                    }
                }
            });
        }
    }

}
