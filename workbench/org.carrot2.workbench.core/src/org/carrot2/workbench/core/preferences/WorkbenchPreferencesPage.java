package org.carrot2.workbench.core.preferences;

import java.util.*;

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.ui.SearchEditorSections;
import org.eclipse.jface.preference.*;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * 
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
        setDescription("Choose panels to show in results editors:");
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI blocks
     * needed to manipulate various types of preferences. Each field editor knows how to
     * save and restore itself.
     */
    public void createFieldEditors()
    {
        final Composite parent = getFieldEditorParent();

        for (SearchEditorSections s : EnumSet.allOf(SearchEditorSections.class))
        {
            final String key = PreferenceConstants.getSectionVisibilityKey(s);
            final BooleanFieldEditor editor = new BooleanFieldEditor(key, s.name, parent);

            editors.add(editor);
            addField(editor);
        }
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

    @Override
    public boolean isValid()
    {
        boolean valid = false;
        for (BooleanFieldEditor editor : editors)
        {
            valid |= editor.getBooleanValue();
        }
        return valid;
    }

    @Override
    protected void checkState()
    {
        boolean valid = false;
        for (BooleanFieldEditor editor : editors)
        {
            valid |= editor.getBooleanValue();
        }
        if (valid)
        {
            setErrorMessage(null);
            super.checkState();
        }
        else
        {
            setValid(false);
            setErrorMessage("At least one section has to be chosen");
        }
    }

}