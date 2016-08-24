
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

package org.carrot2.workbench.editors.lucene;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.carrot2.workbench.core.helpers.*;
import org.carrot2.workbench.editors.*;
import org.carrot2.workbench.editors.impl.EditorsPlugin;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * An {@link IAttributeEditor} for editing Apache Lucene's {@link Directory} attributes.
 * The only currently valid selection is to point to a local Lucene index ({@link FSDirectory}
 * is created).
 */
public class IndexDirectoryEditor extends AttributeEditorAdapter
{
    /** Preference store key for keeping last selected path. */
    public static final String PREF_LAST_SELECTED_LUCENE_DIR = "resource-editor.last-selected-lucene-dir";

    /** Disposal of resources. */
    private DisposeBin disposeBin = new DisposeBin();

    /**
     * Directory location info string.
     */
    private Text resourceInfo;

    /**
     * The current value.
     */
    private Directory current = null;

    /*
     * 
     */
    @Override
    protected AttributeEditorInfo init(Map<String,Object> defaultValues)
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

        GridLayout gl = GUIFactory.zeroMarginGridLayout();
        gl.numColumns = 2;
        gl.horizontalSpacing = 3;
        holder.setLayout(gl);

        createTextBox(holder);
        createFileButton(holder);
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
    private void openIndexDialog()
    {
        final DirectoryDialog dialog = new DirectoryDialog(this.resourceInfo.getShell());

        if (this.current != null && current instanceof FSDirectory)
        {
            dialog.setFilterPath(((FSDirectory) current).getDirectory().toAbsolutePath().toString());
        }
        else
        {
            // In case we can't restore last file, refer to global last key.
            dialog.setFilterPath(EditorsPlugin.getDefault().getPreferenceStore()
                .getString(PREF_LAST_SELECTED_LUCENE_DIR));
        }

        final String path = dialog.open();
        if (path != null)
        {
            try
            {
                final Path p = Paths.get(path);

                EditorsPlugin.getDefault().getPreferenceStore().setValue(
                    PREF_LAST_SELECTED_LUCENE_DIR, p.toAbsolutePath().toString());

                setValue(FSDirectory.open(p));
            }
            catch (Exception e)
            {
                Utils.logError("Could not open index in directory: " + path, e, true);
            }
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

        if (!(newValue instanceof Directory))
        {
            return;
        }

        this.current = (Directory) newValue;

        final String representation;
        if (current == null)
        {
            representation = "";
        }
        else if (current instanceof FSDirectory)
        {
            representation = ((FSDirectory) current).getDirectory().toAbsolutePath().toString();
        }
        else representation = current.getClass().getSimpleName();

        this.resourceInfo.setText(representation);

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
