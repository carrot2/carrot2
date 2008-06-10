package org.carrot2.workbench.core.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

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
        newShell.setText("Choose sections");
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
    }

}
