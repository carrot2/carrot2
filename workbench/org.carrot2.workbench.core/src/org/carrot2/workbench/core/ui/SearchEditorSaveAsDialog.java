
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
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

import org.apache.commons.lang.StringUtils;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.ui.SearchEditor.SaveOptions;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;

/**
 * Displays a dialog prompting for the location of the output XML file and options
 * indicating what to save (clusters, documents or both).
 */
final class SearchEditorSaveAsDialog extends TrayDialog
{
    /**
     * Global most recent path in case the editor did not have a previous one.
     */
    private static final String GLOBAL_PATH_PREF = SearchEditorSaveAsDialog.class
        .getName()
        + ".savePath";

    private Text fileNameText;
    private Button browseButton;

    private Button clusterOption;
    private Button docOption;

    /**
     * Save options.
     */
    public SaveOptions options;

    /*
     * 
     */
    public SearchEditorSaveAsDialog(Shell parentShell, SearchEditor.SaveOptions options)
    {
        super(parentShell);
        this.options = options;
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
        options.directory = f.getParent();
        options.fileName = f.getName();
        options.includeClusters = clusterOption.getSelection();
        options.includeDocuments = docOption.getSelection();

        WorkbenchCorePlugin.getDefault().getPluginPreferences().setValue(
            GLOBAL_PATH_PREF, options.directory);

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

        if (options.directory == null)
        {
            options.directory = WorkbenchCorePlugin.getDefault().getPluginPreferences()
                .getString(GLOBAL_PATH_PREF);

            if (StringUtils.isEmpty(options.directory))
            {
                final File home = new File(System.getProperty("user.home", "."));
                options.directory = home.getAbsolutePath();
            }
        }

        fileNameText.setText(options.getFullPath());

        browseButton.addListener(Selection, new Listener()
        {
            public void handleEvent(Event event)
            {
                final Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                final FileDialog dialog = new FileDialog(shell, SWT.SAVE);

                final IPath file = new Path(fileNameText.getText());
                if (file.isValidPath(fileNameText.getText()))
                {
                    dialog.setFileName(file.lastSegment());
                    dialog.setFilterPath(file.removeLastSegments(1).toOSString());
                }

                dialog.setFilterExtensions(new String []
                {
                    "*.xml", "*.*"
                });
                dialog.setFilterNames(new String []
                {
                    "XML Files", "All Files"
                });

                String newPath = dialog.open();
                if (newPath != null)
                {
                    fileNameText.setText(newPath);
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
        {
            docOption = new Button(root, SWT.CHECK | SWT.LEFT);
            GridData docOptionLData = new GridData();
            docOptionLData.horizontalSpan = 3;
            docOption.setLayoutData(docOptionLData);
            docOption.setText("Include documents");
            docOption.setSelection(true);
        }
        {
            clusterOption = new Button(root, SWT.CHECK | SWT.LEFT);
            GridData clusterOptionLData = new GridData();
            clusterOptionLData.horizontalSpan = 3;
            clusterOption.setLayoutData(clusterOptionLData);
            clusterOption.setText("Include clusters");
            clusterOption.setSelection(true);
        }
    }

}
