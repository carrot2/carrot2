package org.carrot2.workbench.editors.impl;

import java.io.File;
import java.net.*;

import org.carrot2.util.resource.*;
import org.carrot2.workbench.core.helpers.DisposeBin;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.editors.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * Editor for attributes that are of {@link Resource} type.
 */
public class ResourceEditor extends AttributeEditorAdapter
{
    /*
     * Disposal of resources.
     */
    private DisposeBin disposeBin = new DisposeBin();

    /**
     * Resource information string.
     */
    private Text resourceInfo;

    /**
     * The actual resource (most recent valid value or <code>null</code>).
     */
    private Resource resource = null;

    /*
     * Event cycle avoidance.
     */
    private boolean updating;

    /*
     * Validator for URIs.
     */
    private final static IInputValidator validatorURI = new IInputValidator()
    {
        public String isValid(String text)
        {
            try
            {
                URL validURI = new URL(text);

                if (validURI.getProtocol() == null)
                {
                    throw new MalformedURLException("Empty scheme.");
                }
            }
            catch (MalformedURLException e)
            {
                return "Not a valid URL";
            }

            return null;
        }
    };

    /*
     * 
     */
    public ResourceEditor()
    {
        super(new AttributeEditorInfo(1, false));
    }

    /*
     * 
     */
    @Override
    public void createEditor(Composite parent, int gridColumns)
    {
        final Composite holder = new Composite(parent, SWT.NONE);
        holder.setLayoutData(GUIFactory.editorGridData().grab(true, false).span(
            gridColumns, 1).create());

        GridLayout gl = GUIFactory.zeroMarginGridLayout();
        gl.numColumns = 3;
        gl.horizontalSpacing = 3;

        holder.setLayout(gl);

        createTextBox(holder);

        createFileButton(holder);
        createUrlButton(holder);
    }

    /*
     * 
     */
    private void createTextBox(Composite holder)
    {
        this.resourceInfo = new Text(holder, SWT.READ_ONLY | SWT.NO_FOCUS | SWT.BORDER
            | SWT.SINGLE);

        final GridData gd = GridDataFactory.fillDefaults().grab(true, false).hint(100,
            SWT.DEFAULT).align(SWT.FILL, SWT.CENTER).create();
        resourceInfo.setLayoutData(gd);
    }

    /*
     * 
     */
    private void createFileButton(Composite holder)
    {
        final Image image = EditorsPlugin.getImageDescriptor("icons/open_folder.gif")
            .createImage();
        disposeBin.add(image);

        final Button button = new Button(holder, SWT.PUSH | SWT.CENTER);
        button.setImage(image);
        button.setLayoutData(GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER)
            .create());

        button.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                openFileResourceDialog();
            }
        });
    }

    /*
     * 
     */
    private void openFileResourceDialog()
    {
        final FileDialog dialog = new FileDialog(this.resourceInfo.getShell());
        dialog.setFilterExtensions(new String []
        {
            "*.xml;*.XML", "*.*"
        });
        dialog.setFilterNames(new String []
        {
            "XML files", "All"
        });

        if (this.resource != null && resource instanceof FileResource)
        {
            dialog.setFileName(((FileResource) resource).file.getAbsolutePath());
        }
        else
        {
            // In case we can't restore last file, refer to global last key.
            dialog.setFileName(EditorsPlugin.getDefault().getPreferenceStore().getString(
                EditorsPluginConstants.PREF_LAST_SELECTED_FILE));
        }

        final String path = dialog.open();
        if (path != null)
        {
            final File file = new File(path);

            EditorsPlugin.getDefault().getPreferenceStore().setValue(
                EditorsPluginConstants.PREF_LAST_SELECTED_FILE, file.getAbsolutePath());

            setValue(new FileResource(file));
        }
    }

    /*
     * 
     */
    private void createUrlButton(Composite holder)
    {
        final Image image = EditorsPlugin.getImageDescriptor("icons/open_url.gif")
            .createImage();
        disposeBin.add(image);

        final Button button = new Button(holder, SWT.PUSH | SWT.CENTER);
        button.setImage(image);
        button.setLayoutData(GridDataFactory.fillDefaults().create());

        button.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                openURLResourceDialog();
            }
        });
    }

    /*
     * 
     */
    private void openURLResourceDialog()
    {
        String previous = "";
        if (resource != null && resource instanceof URLResource)
        {
            previous = ((URLResource) resource).url.toExternalForm();
        }

        if (resource != null && resource instanceof ParameterizedUrlResource)
        {
            previous = ((ParameterizedUrlResource) resource).url.toExternalForm();
        }

        final InputDialog dialog = new InputDialog(resourceInfo.getShell(),
            "Enter resource URL", "Enter resource URL", previous, validatorURI);

        if (dialog.open() == IDialogConstants.OK_ID)
        {
            try
            {
                setValue(new ParameterizedUrlResource(new URL(dialog.getValue())));
            }
            catch (MalformedURLException e)
            {
                // Simply skip, shouldn't happen.
            }
        }
    }

    /*
     * 
     */
    @Override
    public void setValue(Object newValue)
    {
        if (updating || newValue == resource)
        {
            return;
        }

        if (resource != null && resource.equals(newValue))
        {
            return;
        }

        if (!(newValue instanceof Resource))
        {
            return;
        }

        updating = true;

        this.resource = (Resource) newValue;

        this.resourceInfo.setText(resource == null ? "" : resource.toString());

        fireAttributeChange(new AttributeChangedEvent(this));

        updating = false;
    }

    /*
     * 
     */
    @Override
    public Object getValue()
    {
        return resource;
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        disposeBin.dispose();
    }
}
