package org.carrot2.workbench.core.preferences;

import java.util.ArrayList;
import java.util.Collection;

import org.carrot2.workbench.core.CorePlugin;
import org.eclipse.jface.preference.*;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the Preferences dialog.
 * By subclassing <samp>FieldEditorPreferencePage</samp>, we can use the field support
 * built into JFace that allows us to create a page that is small and knows how to save,
 * restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store
 * that belongs to the main plug-in class. That way, preferences can be accessed directly
 * via the preference store.
 */

public class CarrotPreferencePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage
{
    public static final String ID =
        "org.carrot2.workbench.core.preferences.CarrotPreferencePage";

    private Collection<BooleanFieldEditor> editors = new ArrayList<BooleanFieldEditor>();

    public CarrotPreferencePage()
    {
        super(GRID);
        setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
        setDescription("Choose sections to show in results editor by default:");
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI blocks
     * needed to manipulate various types of preferences. Each field editor knows how to
     * save and restore itself.
     */
    public void createFieldEditors()
    {
        editors.add(new BooleanFieldEditor(PreferenceConstants.P_SHOW_CLUSTERS,
            "&Clusters", getFieldEditorParent()));
        editors.add(new BooleanFieldEditor(PreferenceConstants.P_SHOW_DOCUMENTS,
            "&Documents", getFieldEditorParent()));
        editors.add(new BooleanFieldEditor(PreferenceConstants.P_SHOW_ATTRIBUTES,
            "&Attributes", getFieldEditorParent()));
        for (FieldEditor editor : editors)
        {
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