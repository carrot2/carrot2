
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
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
import java.util.Arrays;

import org.carrot2.workbench.core.ui.SearchEditor.SaveOptions;
import org.carrot2.workbench.core.ui.SearchEditor.SaveOptions.SaveFormat;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.*;
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
    /**
     * Global most recent path in case the editor did not have a previous one.
     */
    private static final String GLOBAL_PATH_PREF = 
        SearchEditorSaveAsDialog.class.getName() + ".savePath";

    private Text fileNameText;
    private Button browseButton;

    private Button clusterOption;
    private Button docOption;
    private ComboViewer format;

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
    public boolean close()
    {
        final File f = new File(this.fileNameText.getText());
        options.directory = f.getParent();
        options.fileName = f.getName();
        options.includeClusters = clusterOption.getSelection();
        options.includeDocuments = docOption.getSelection();
        options.format = (SaveFormat) (((StructuredSelection) format.getSelection())
            .getFirstElement());

        FileDialogs.rememberPath(GLOBAL_PATH_PREF, new Path(options.directory));

        return super.close();
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
            options.directory = 
                FileDialogs.recallPath(GLOBAL_PATH_PREF).toOSString();
        }

        final Path fullPath = new Path(options.getFullPath());
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
        
        final ISelectionChangedListener alwaysSaveDocumentsForRss = new ISelectionChangedListener()
        {
            public void selectionChanged(SelectionChangedEvent event)
            {
                final SaveFormat formatChoice = (SaveFormat) (((StructuredSelection) format
                    .getSelection()).getFirstElement());
                
                docOption.setEnabled(formatChoice != SaveFormat.RSS20);
                if (formatChoice == SaveFormat.RSS20)
                {
                    docOption.setSelection(true);
                }
            }
        };

        format.addSelectionChangedListener(alwaysSaveDocumentsForRss);
        
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
        {
            Label formatLabel = new Label(root, SWT.NONE);
            formatLabel.setText("Format:");

            final Combo combo = new Combo(root, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
            GridData formatLData = new GridData();
            formatLData.horizontalIndent = 5;
            formatLData.horizontalSpan = 2;
            combo.setLayoutData(formatLData);
            combo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

            format = new ComboViewer(combo);
            format.setContentProvider(new ArrayContentProvider());
            format.setInput(Arrays.asList(SaveFormat.values()));
            format.setSelection(new StructuredSelection(options.format));
        }
        {
            new Label(root, SWT.NONE).setVisible(false);

            docOption = new Button(root, SWT.CHECK | SWT.LEFT);
            GridData docOptionLData = new GridData();
            docOptionLData.horizontalSpan = 2;
            docOptionLData.horizontalIndent = 5;
            docOption.setLayoutData(docOptionLData);
            docOption.setText("Include documents");
            docOption.setSelection(options.includeDocuments);
        }
        {
            new Label(root, SWT.NONE).setVisible(false);
            
            clusterOption = new Button(root, SWT.CHECK | SWT.LEFT);
            GridData clusterOptionLData = new GridData();
            clusterOptionLData.horizontalIndent = 5;
            clusterOptionLData.horizontalSpan = 2;
            clusterOption.setLayoutData(clusterOptionLData);
            clusterOption.setText("Include clusters");
            clusterOption.setSelection(options.includeClusters);
        }
        
        docOption.setEnabled(options.format != SaveFormat.RSS20);
        if (options.format == SaveFormat.RSS20)
        {
            docOption.setSelection(true);
        }
    }
}
