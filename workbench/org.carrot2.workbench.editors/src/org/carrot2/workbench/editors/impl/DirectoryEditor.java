
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
import java.util.Map;

import org.carrot2.util.attribute.constraint.IsDirectory;
import org.carrot2.workbench.core.helpers.DisposeBin;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.editors.*;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * Editor for attributes that are of {@link File} type, with {@link IsDirectory}
 * constraint (directory selection).
 */
public class DirectoryEditor extends AttributeEditorAdapter
{
    /** Preference store key for keeping last selected path. */
    public static final String PREF_LAST_SELECTED_DIR = "directory-editor.last-selected-dir";

    /** Disposal of resources. */
    private DisposeBin disposeBin = new DisposeBin();

    /**
     * Directory location.
     */
    private Text dirLocation;

    /**
     * The last valid selected value.
     */
    private File current = null;

    /*
     * 
     */
    @Override
    protected AttributeEditorInfo init(Map<String, Object> defaultValues)
    {
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

        final GridLayout gl = GUIFactory.zeroMarginGridLayout();
        gl.numColumns = 3;
        gl.horizontalSpacing = 3;
        holder.setLayout(gl);

        createTextBox(holder);
        createFileButton(holder);
        createClearButton(holder);
    }

    /*
     * 
     */
    private void createTextBox(Composite holder)
    {
        this.dirLocation = new Text(holder, SWT.READ_ONLY | SWT.NO_FOCUS | SWT.BORDER
            | SWT.SINGLE);

        final GridData gd = GridDataFactory.fillDefaults().grab(true, false).hint(100,
            SWT.DEFAULT).align(SWT.FILL, SWT.CENTER).create();
        dirLocation.setLayoutData(gd);
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
                openIndexDialog();
            }
        });
    }

    /*
     * 
     */
    private void createClearButton(Composite holder)
    {
        final Image image = EditorsPlugin.getImageDescriptor("icons/clear.png")
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
                setValue(null);
            }
        });
    }

    /*
     * 
     */
    private void openIndexDialog()
    {
        final DirectoryDialog dialog = new DirectoryDialog(this.dirLocation.getShell());

        if (this.current != null)
        {
            dialog.setFilterPath(current.getAbsolutePath());
        }
        else
        {
            // In case we can't restore last file, refer to global last key.
            dialog.setFilterPath(EditorsPlugin.getDefault().getPreferenceStore()
                .getString(PREF_LAST_SELECTED_DIR));
        }

        final String path = dialog.open();
        if (path != null)
        {
            final File file = new File(path);
            EditorsPlugin.getDefault().getPreferenceStore().setValue(
                PREF_LAST_SELECTED_DIR, file.getAbsolutePath());
            setValue(file);
        }
    }

    /*
     * 
     */
    @Override
    public void setValue(Object newValue)
    {
        if (newValue == current)
        {
            return;
        }

        if (newValue != null && !(newValue instanceof File))
        {
            return;
        }

        if (newValue == null)
        {
            this.current = null;
            this.dirLocation.setText("");
        }
        else
        {
            this.current = (File) newValue;
            this.dirLocation.setText(current.getAbsolutePath());
        }

        fireAttributeChanged(new AttributeEvent(this));
    }

    /*
     * 
     */
    @Override
    public Object getValue()
    {
        return current;
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
