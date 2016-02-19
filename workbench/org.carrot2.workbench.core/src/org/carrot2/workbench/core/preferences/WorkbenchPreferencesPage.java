
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

package org.carrot2.workbench.core.preferences;

import java.util.*;

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.eclipse.jface.preference.*;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Global preferences for the workbench.
 */
public class WorkbenchPreferencesPage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage
{
    public static final String ID = "org.carrot2.workbench.core.preferences.WorkbenchPreferencesPage";

    private Collection<BooleanFieldEditor> editors = new ArrayList<BooleanFieldEditor>();

    public WorkbenchPreferencesPage()
    {
        super(GRID);

        setPreferenceStore(WorkbenchCorePlugin.getDefault().getPreferenceStore());
    }

    /**
     * We skip field editor creation and override {@link #createContents(Composite)}.
     */
    protected void createFieldEditors()
    {
        // Empty.
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI blocks
     * needed to manipulate various types of preferences. Each field editor knows how to
     * save and restore itself.
     */
    protected Control createContents(Composite p)
    {
        final Composite parent = createSpacerComposite(p);

        /*
         * Panels.
         */
        Group g = createOptionGroup(parent, "Visible editor panels");
        Composite spacer = createSpacerComposite(g);
        for (SearchEditor.PanelName s : EnumSet.allOf(SearchEditor.PanelName.class))
        {
            final BooleanFieldEditor editor = 
                new BooleanFieldEditor(s.prefKeyVisibility, s.name, spacer);

            editors.add(editor);
            addField(editor);
        }

        /*
         * Auto-update.
         */
        g = createOptionGroup(parent, "Editor auto-update");
        spacer = createSpacerComposite(g);

        final BooleanFieldEditor autoUpdateEditor = new BooleanFieldEditor(PreferenceConstants.AUTO_UPDATE, 
            "Automatically re-process after attributes change", spacer);
        autoUpdateEditor.fillIntoGrid(spacer, 2);
        addField(autoUpdateEditor);

        final IntegerFieldEditor delayEditor = new IntegerFieldEditor(PreferenceConstants.AUTO_UPDATE_DELAY, 
            "Auto-update after (milliseconds)", spacer);
        delayEditor.setEmptyStringAllowed(false);
        delayEditor.setValidRange(0, 5000);
        delayEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
        delayEditor.fillIntoGrid(spacer, 2);
        addField(delayEditor);

        /*
         * Search result view.
         */
        g = createOptionGroup(parent, "Search result display");
        spacer = createSpacerComposite(g);

        final IntegerFieldEditor maxFieldLength = 
            new IntegerFieldEditor(PreferenceConstants.MAX_FIELD_LENGTH, "Maximum snippet/title length", spacer);
        maxFieldLength.setEmptyStringAllowed(false);
        maxFieldLength.setValidRange(0, 100000);
        maxFieldLength.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
        maxFieldLength.fillIntoGrid(spacer, 2);
        addField(maxFieldLength);

        initialize();
        checkState();

        return parent;
    }

    /*
     * Create option group.
     */
    private Group createOptionGroup(Composite parent, String groupTitle)
    {
        final Group g = new Group(parent, SWT.LEFT);
        final GridLayout gl = new GridLayout(1, false);
        g.setLayout(gl);
        g.setText(groupTitle);
        g.setFont(parent.getFont());

        final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        g.setLayoutData(gd);

        return g;
    }

    /*
     * Create spacer composite.
     */
    private static Composite createSpacerComposite(Composite parent) {
        Composite g = new Composite(parent, SWT.NONE);
        g.setLayout(new GridLayout(1, false));
        g.setFont(parent.getFont());
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 1;
        gd.grabExcessHorizontalSpace = true;
        g.setLayoutData(gd);
        return g;
    }

    /**
     * Overriden to invoke {@link FieldEditorPreferencePage#checkState()} when value of
     * any editor is changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent event)
    {
        super.propertyChange(event);
        if (event.getProperty().equals(FieldEditor.VALUE))
        {
            checkState();
        }
    }

    public void init(IWorkbench workbench)
    {
        // empty.
    }

    /*
     * 
     */
    @Override
    protected void checkState()
    {
        super.checkState();

        if (isValid())
        {
            boolean oneSet = false;
            for (BooleanFieldEditor editor : editors)
            {
                oneSet |= editor.getBooleanValue();
            }

            if (!oneSet)
            {
                setErrorMessage("At least one panel must be visible");
                setValid(false);
            }
            else
            {
                setErrorMessage(null);
            }
        }
    }

}
