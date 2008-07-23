package org.carrot2.workbench.editors.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.carrot2.util.resource.FileResource;
import org.carrot2.util.resource.Resource;
import org.carrot2.util.resource.URLResource;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.editors.AttributeChangedEvent;
import org.carrot2.workbench.editors.AttributeEditorAdapter;
import org.carrot2.workbench.editors.AttributeEditorInfo;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

/**
 * Editor for attributes that are of {@link Resource} type.
 */
public class ResourceEditor extends AttributeEditorAdapter
{
    private Image browseIcon;
    private Image openURLIcon;

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
                URI validURI = new URI(text);

                if (validURI.getScheme() == null)
                {
                    throw new URISyntaxException(text, "Empty scheme.");
                }
            } 
            catch (URISyntaxException e)
            {
                return "Not a valid URI";
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
        holder.setLayoutData(
            GUIFactory.editorGridData()
                .grab(true, false)
                .span(gridColumns, 1).create());

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
        this.resourceInfo = new Text(holder, SWT.READ_ONLY | SWT.BORDER | SWT.SINGLE);

        final GridData gd = GridDataFactory.fillDefaults().grab(true, false).create();
        resourceInfo.setLayoutData(gd);
    }

    /*
     * 
     */
    private void createFileButton(Composite holder)
    {
        browseIcon = EditorsPlugin.getImageDescriptor("icons/open_folder.gif").createImage();

        final Button button = new Button(holder, SWT.PUSH | SWT.CENTER);
        button.setImage(browseIcon);
        button.setLayoutData(GridDataFactory.fillDefaults().create());

        button.addSelectionListener(new SelectionAdapter() {
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
        // TODO: Store/restore previous directory.

        final FileDialog dialog = new FileDialog(this.resourceInfo.getShell());
        dialog.setFilterExtensions(new String []
        {
            "*.xml", "*.*"
        });
        dialog.setFilterNames(new String []
        {
            "XML files", "All"
        });

        final String path = dialog.open();
        if (path != null)
        {
            setValue(new FileResource(new File(path)));
        }
    }

    /*
     * 
     */
    private void createUrlButton(Composite holder)
    {
        openURLIcon = EditorsPlugin.getImageDescriptor("icons/open_url.gif").createImage();

        final Button button = new Button(holder, SWT.PUSH | SWT.CENTER);
        button.setImage(openURLIcon);
        button.setLayoutData(GridDataFactory.fillDefaults().create());

        button.addSelectionListener(new SelectionAdapter() {
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
        // TODO: Store/restore previous URL

        final String previous = "";
        final InputDialog dialog = new InputDialog(resourceInfo.getShell(), 
            "Open URL...", "Enter resource URL", previous, validatorURI);

        if (dialog.open() == IDialogConstants.OK_ID)
        {
            try
            {
                setValue(new URLResource(new URL(dialog.getValue())));
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

        this.resourceInfo.setText(
            resource == null ? "" : resource.toString());

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
        browseIcon.dispose();
        openURLIcon.dispose();
    }
}
