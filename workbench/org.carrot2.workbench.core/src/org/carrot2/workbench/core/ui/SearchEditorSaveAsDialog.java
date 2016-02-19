
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.eclipse.swt.SWT.Modify;
import static org.eclipse.swt.SWT.Selection;

import java.io.File;

import org.carrot2.workbench.core.ui.SearchEditor.SaveOptions;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * Displays a dialog prompting for the location of the output XML file and options
 * indicating what to save (clusters, documents or both).
 */
final class SearchEditorSaveAsDialog extends TrayDialog
{
    private Text fileNameText;
    private Button browseButton;

    private Button clusterOption;
    private Button docOption;
    private Button attributesOption;

    /**
     * Save options (from the editor).
     */
    public SaveOptions editorOptions;

    /*
     * 
     */
    public SearchEditorSaveAsDialog(Shell parentShell, SearchEditor.SaveOptions options)
    {
        super(parentShell);
        this.editorOptions = options;
    }

    @Override
    protected void configureShell(Shell newShell)
    {
        super.configureShell(newShell);
        newShell.setText("Save as XML");
        newShell.addShellListener(new ShellAdapter()
        {
            @Override
            public void shellActivated(ShellEvent e)
            {
                validateInput();
            }
        });
    }

    @Override
    protected void okPressed()
    {
        final File f = new File(this.fileNameText.getText());
        editorOptions.directory = f.getParent();
        editorOptions.fileName = f.getName();
        editorOptions.includeClusters = clusterOption.getSelection();
        editorOptions.includeDocuments = docOption.getSelection();
        editorOptions.includeAttributes = attributesOption.getSelection();

        editorOptions.saveGlobal();
        super.okPressed();
    }

    /*
     * 
     */
    @Override
    protected Control createDialogArea(Composite parent)
    {
        final Composite root = (Composite) super.createDialogArea(parent);
        createControls(root);

        final Path fullPath = new Path(editorOptions.getFullPath());
        fileNameText.setText(fullPath.toOSString());
        browseButton.addListener(Selection, new Listener()
        {
            public void handleEvent(Event event)
            {
                Path newPath = FileDialogs.openSaveXML(fullPath);
                if (newPath != null)
                {
                    fileNameText.setText(newPath.toOSString());
                }
            }
        });

        final Listener correctnessChecker = new Listener()
        {
            public void handleEvent(Event event)
            {
                validateInput();
            }
        };
        
        docOption.addListener(Selection, correctnessChecker);
        clusterOption.addListener(Selection, correctnessChecker);
        fileNameText.addListener(Modify, correctnessChecker);
        return root;
    }

    /*
     * 
     */
    private void validateInput()
    {
        boolean invalid = false;

        invalid |= (docOption.getSelection() == false && clusterOption.getSelection() == false);

        if (isBlank(fileNameText.getText()))
        {
            invalid = true;
        }
        else
        {
            invalid |= (!new File(fileNameText.getText()).getAbsoluteFile()
                .getParentFile().isDirectory());
        }

        getButton(IDialogConstants.OK_ID).setEnabled(!invalid);
    }

    /*
     * 
     */
    private void createControls(Composite root)
    {
        final GridLayout parentLayout = (GridLayout) root.getLayout();

        parentLayout.numColumns = 3;

        root.setLayout(parentLayout);
        {
            Label fileNameLabel = new Label(root, SWT.NONE);
            fileNameLabel.setText("Location:");
        }
        {
            GridData fileNameTextLData = new GridData();
            fileNameTextLData.horizontalAlignment = GridData.FILL;
            fileNameTextLData.grabExcessHorizontalSpace = true;
            fileNameTextLData.verticalAlignment = GridData.FILL;
            fileNameTextLData.horizontalIndent = 5;
            fileNameTextLData.minimumWidth = 280;
            fileNameTextLData.widthHint = 280;
            fileNameText = new Text(root, SWT.BORDER);
            fileNameText.setLayoutData(fileNameTextLData);
        }
        {
            browseButton = new Button(root, SWT.NONE);
            GridData dialogButtonLData = new GridData();
            dialogButtonLData.horizontalAlignment = GridData.FILL;
            dialogButtonLData.verticalAlignment = GridData.FILL;
            browseButton.setText("Browse...");
            dialogButtonLData.widthHint = browseButton.computeSize(SWT.DEFAULT,
                SWT.DEFAULT).x
                + 2 * IDialogConstants.BUTTON_MARGIN;
            browseButton.setLayoutData(dialogButtonLData);
        }
        
        docOption = createCheckbox(root, "Include documents", editorOptions.includeDocuments);
        clusterOption = createCheckbox(root, "Include clusters", editorOptions.includeClusters);
        attributesOption = createCheckbox(root, "Include other attributes", editorOptions.includeAttributes);
    }

    private Button createCheckbox(Composite root, final String label, final Boolean checked)
    {
        new Label(root, SWT.NONE).setVisible(false);
        
        final Button checkbox = new Button(root, SWT.CHECK | SWT.LEFT);
        final GridData checkboxLData = new GridData();
        checkboxLData.horizontalIndent = 5;
        checkboxLData.horizontalSpan = 2;
        checkbox.setLayoutData(checkboxLData);
        checkbox.setText(label);
        checkbox.setSelection(checked);
        
        return checkbox;
    }
}
