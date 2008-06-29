package org.carrot2.workbench.core.ui;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.eclipse.swt.SWT.Modify;
import static org.eclipse.swt.SWT.Selection;

import java.io.File;

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder,
 * which is free for non-commercial use. If Jigloo is being used commercially (ie, by a
 * corporation, company or business for any purpose whatever) then you should purchase a
 * license for each developer using Jigloo. Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT
 * BEEN PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY
 * CORPORATE OR COMMERCIAL PURPOSE.
 */
public class SaveToXmlDialog extends TrayDialog
{
    private static final String LAST_PATH_PREF = "action.saveToXml.LastPathChosen";

    private Button clusterOption;
    private Button docOption;
    private Button dialogButton;
    private Text fileNameText;
    private boolean docSelected;
    private boolean clustersSelected;
    private String filePath;
    private String initialFileName;

    public SaveToXmlDialog(Shell parentShell, String query)
    {
        super(parentShell);
        initialFileName = convertToFileName(query) + ".xml";
    }

    public boolean saveDocuments()
    {
        return docSelected;
    }

    public boolean saveClusters()
    {
        return clustersSelected;
    }

    public String getFilePath()
    {
        return filePath;
    }
    
    private static String convertToFileName(String baseString)
    {
        String result = baseString.replaceAll("[^a-zA-Z0-9\\s]", "");
        result = result.replaceAll("[\\s]+", "-");
        result = result.toLowerCase();
        return result;
    }

    @Override
    protected void configureShell(Shell newShell)
    {
        super.configureShell(newShell);
        newShell.setText("Save to XML");
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
        docSelected = docOption.getSelection();
        clustersSelected = clusterOption.getSelection();
        filePath = fileNameText.getText();
        WorkbenchCorePlugin.getDefault().getPluginPreferences().setValue(LAST_PATH_PREF,
            new File(filePath).getParent());
        super.okPressed();
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite root = (Composite) super.createDialogArea(parent);

        createControls(root);
        String lastChosenPath =
            WorkbenchCorePlugin.getDefault().getPluginPreferences().getString(LAST_PATH_PREF);
        if (lastChosenPath.length() != 0)
        {
            fileNameText.setText(new File(lastChosenPath, initialFileName)
                .getAbsolutePath());
        }
        dialogButton.addListener(Selection, new Listener()
        {
            public void handleEvent(Event event)
            {
                FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell());
                dialog.setFileName(fileNameText.getText());
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
        Listener correctnessChecker = new Listener()
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

    private void validateInput()
    {
        if ((!docOption.getSelection() && !clusterOption.getSelection())
            || isBlank(fileNameText.getText()))
        {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
        else if (!getButton(IDialogConstants.OK_ID).isEnabled())
        {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        }
    }

    private void createControls(Composite root)
    {
        GridLayout parentLayout = new GridLayout();
        parentLayout.numColumns = 3;
        parentLayout.horizontalSpacing = 0;
        root.setLayout(parentLayout);
        {
            Label fileNameLabel = new Label(root, SWT.NONE);
            fileNameLabel.setText("File Name:");
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
            dialogButton = new Button(root, SWT.PUSH | SWT.CENTER | SWT.FLAT);
            GridData dialogButtonLData = new GridData();
            dialogButtonLData.horizontalAlignment = GridData.FILL;
            dialogButtonLData.verticalAlignment = GridData.FILL;
            dialogButton.setLayoutData(dialogButtonLData);
            dialogButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(
                ISharedImages.IMG_OBJ_FOLDER));
        }
        {
            docOption = new Button(root, SWT.CHECK | SWT.LEFT);
            GridData docOptionLData = new GridData();
            docOptionLData.horizontalSpan = 3;
            docOption.setLayoutData(docOptionLData);
            docOption.setText("Save documents");
            docOption.setSelection(true);
        }
        {
            clusterOption = new Button(root, SWT.CHECK | SWT.LEFT);
            GridData clusterOptionLData = new GridData();
            clusterOptionLData.horizontalSpan = 3;
            clusterOption.setLayoutData(clusterOptionLData);
            clusterOption.setText("Save clusters");
            clusterOption.setSelection(true);
        }
    }

}
