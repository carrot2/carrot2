
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

package org.carrot2.workbench.editors.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.carrot2.util.attribute.constraint.ResourceNameFilter;
import org.carrot2.util.attribute.constraint.ResourceNameFilters;
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

import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * Editor for attributes that are of {@link IResource} type.
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
    private IResource resource = null;

    /**
     * Resource name filters.
     */
    private ResourceNameFilter [] filters;

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
    @Override
    protected AttributeEditorInfo init(Map<String,Object> defaultValues)
    {
        if (descriptor.getAnnotation(ResourceNameFilters.class) != null)
        {
            filters = descriptor.getAnnotation(ResourceNameFilters.class).filters();
        }
        return new AttributeEditorInfo(1, false);
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
        gl.numColumns = 4;
        gl.horizontalSpacing = 3;

        holder.setLayout(gl);

        createTextBox(holder);
        createFileButton(holder);
        createUrlButton(holder);
        createClearButton(holder);
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
    private void createClearButton(Composite holder)
    {
        final Image image = EditorsPlugin.getImageDescriptor("icons/clear.png").createImage();
        disposeBin.add(image);

        final Button button = new Button(holder, SWT.PUSH | SWT.CENTER);
        button.setImage(image);
        button.setLayoutData(GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER)
            .create());

        button.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                setValue(null);
            }
        });
    }    

    /*
     * 
     */
    private void createFileButton(Composite holder)
    {
        final Image image = EditorsPlugin.getImageDescriptor("icons/open_folder.png")
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
        
        java.util.List<String> patterns = Lists.newArrayList();
        java.util.List<String> names = Lists.newArrayList();
        if (filters != null)
        {
            for (ResourceNameFilter f : filters)
            {
                patterns.add(f.pattern());
                names.add(f.description());
            }
        }
        else
        {
            // Backwards compatibility.
            patterns.addAll(Arrays.asList("*.xml;*.XML", "*.*"));
            names.addAll(Arrays.asList("XML files", "All"));
        }

        dialog.setFilterExtensions(patterns.toArray(new String [patterns.size()]));
        dialog.setFilterNames(names.toArray(new String [names.size()]));

        if (this.resource != null && resource instanceof FileResource)
        {
            dialog.setFileName(((FileResource) resource).getFile().getAbsolutePath());
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
        final Image image = EditorsPlugin.getImageDescriptor("icons/open_url.png")
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
            previous = ((URLResource) resource).getUrl().toExternalForm();
        }

        if (resource != null && resource instanceof URLResourceWithParams)
        {
            previous = ((URLResourceWithParams) resource).getUrl().toExternalForm();
        }

        final InputDialog dialog = new InputDialog(resourceInfo.getShell(),
            "Enter resource URL", "Enter resource URL", previous, validatorURI);

        if (dialog.open() == IDialogConstants.OK_ID)
        {
            try
            {
                setValue(new URLResourceWithParams(new URL(dialog.getValue())));
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
        if (ObjectUtils.equals(newValue, resource))
        {
            return;
        }

        if (newValue != null && !(newValue instanceof IResource))
        {
            return;
        }

        this.resource = (IResource) newValue;
        this.resourceInfo.setText(resource == null ? "" : resource.toString());

        fireAttributeChanged(new AttributeEvent(this));
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
