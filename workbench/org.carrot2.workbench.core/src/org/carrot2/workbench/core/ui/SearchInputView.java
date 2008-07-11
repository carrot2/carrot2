package org.carrot2.workbench.core.ui;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.*;

import org.carrot2.core.ProcessingComponent;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ConstraintValidator;
import org.carrot2.workbench.core.*;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.editors.AttributeChangedEvent;
import org.carrot2.workbench.editors.IAttributeListener;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;

/**
 * The search view defines a combination of source, algorithm and required input
 * parameters required to open a new editor.
 */
public class SearchInputView extends ViewPart
{
    /**
     * Public identifier of this view.
     */
    public static final String ID = "org.carrot2.workbench.core.views.search";

    /**
     * A bit modified {@link StackLayout}. The size of the composite equals the size of
     * the visible control, not the biggest control available on the attributesPagesStack.
     */
    private static class VisibleComponentSizeStackLayout extends StackLayout
    {
        @Override
        protected Point computeSize(Composite composite, int wHint, int hHint,
            boolean flushCache)
        {
            if (topControl == null) return new Point(0, 0);

            return topControl.computeSize(wHint, hHint);
        }
    }

    /**
     * Memento branch for persisting algorithm component ID;
     */
    private static final String MEMENTO_ALGORITHM_ID = "algorithmId";

    /**
     * Memento branch for persisting source component ID;
     */
    private static final String MEMENTO_SOURCE_ID = "sourceId";

    /**
     * Memento branch for persisting {@link #attributes}.
     */
    private static final String MEMENTO_ATTRIBUTES = "attribute-set";
    
    /**
     * A serialized {@link AttributeValueSet} attribute inside memento.
     */
    private static final String MEMENTO_ATTRIBUTE_SET_SERIALIZED = "serialized";

    /**
     * State persistence.
     */
    private IMemento state;

    private ComboViewer sourceViewer;
    private ComboViewer algorithmViewer;
    private Composite innerComposite;

    /**
     * A composite with a {@link StackLayout}, holding {@link AttributeEditorList}s for
     * all document sources. 
     */
    private Composite editorStack;
    private VisibleComponentSizeStackLayout attributesPagesStack;

    /*
     * 
     */
    private Button processButton;

    /**
     * A map of {@link AttributeEditorList} associated with all document source IDs from
     * {@link WorkbenchCorePlugin#getSources()}.
     */
    private Map<String, AttributeEditorList> editors = new HashMap<String, AttributeEditorList>();

    /**
     * A map of {@link AttributeValueSet} associated with all document source IDs from
     * {@link WorkbenchCorePlugin#getSources()}.
     */
    private Map<String, AttributeValueSet> attributes = new HashMap<String, AttributeValueSet>();

    /**
     * Create user interface for the view.
     */
    @Override
    public void createPartControl(Composite parent)
    {
        createComponents(parent);

        /*
         * Hook processing startup to the processing button and on focus-list (when all
         * attributes are given).
         */
        processButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                fireProcessing();
            }
        });

        parent.addTraverseListener(new TraverseListener()
        {
            public void keyTraversed(TraverseEvent e)
            {
                if (e.detail == SWT.TRAVERSE_RETURN)
                {
                    /*
                     * Consume the traversal event, spawn a new editor.
                     */
                    e.doit = false;
                    e.detail = SWT.TRAVERSE_NONE;

                    fireProcessing();
                }
            }
        });
    }

    /**
     * Creates permanent GUI elements (source, algorithm combos).
     */
    private void createComponents(Composite parent)
    {
        final WorkbenchCorePlugin core = WorkbenchCorePlugin.getDefault();
        parent.setLayout(new FillLayout());

        innerComposite = new Composite(parent, SWT.NONE);
        innerComposite.setLayout(new GridLayout(2, false));

        sourceViewer = createComboViewer("Source", core.getSources());
        sourceViewer.addSelectionChangedListener(new ISelectionChangedListener()
        {
            public void selectionChanged(SelectionChangedEvent event)
            {
                final IStructuredSelection selection = (IStructuredSelection) event
                    .getSelection();
                final ExtensionImpl impl = (ExtensionImpl) selection.getFirstElement();
                if (impl != null)
                {
                    attributesPagesStack.topControl = editors.get(impl.id);
                    editorStack.layout();
                    innerComposite.layout();
                }
            }
        });

        algorithmViewer = createComboViewer("Algorithm", core.getAlgorithms());

        createRequiredAttributesLayout();

        final GridData processButtonGridData = new GridData();
        processButtonGridData.horizontalAlignment = GridData.END;
        processButtonGridData.verticalAlignment = GridData.END;
        processButtonGridData.horizontalSpan = 2;

        processButton = new Button(innerComposite, SWT.PUSH);
        processButton.setText("Process");
        processButton.setLayoutData(processButtonGridData);

        /*
         * Restore state and push initial values to editors.
         */
        restoreState();

        for (String sourceID : editors.keySet())
        {
            final AttributeValueSet sourceAttrs = attributes.get(sourceID);
            final AttributeEditorList editor = editors.get(sourceID);

            for (Map.Entry<String, Object> entry : sourceAttrs.getAttributeValues().entrySet())
            {
                editor.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        /*
         * Hook up listeners updating attributes on changes in editors. 
         */
        for (String sourceID : editors.keySet())
        {
            final AttributeValueSet sourceAttrs = attributes.get(sourceID);
            final AttributeEditorList editor = editors.get(sourceID);

            editor.addAttributeChangeListener(new IAttributeListener() {
                public void attributeChange(AttributeChangedEvent event)
                {
                    sourceAttrs.setAttributeValue(event.key, event.value);
                    processButton.setEnabled(hasAllRequiredAttributes(getSourceId()));
                }
            });
        }
    }

    /**
     * creates a JFace ComboViewer around a collection of extension point implementations.
     * Restores saved selection state if possible.
     */
    private ComboViewer createComboViewer(String comboLabel, ExtensionLoader loader)
    {
        final Label label = new Label(innerComposite, SWT.CENTER);
        label.setLayoutData(new GridData());
        label.setText(comboLabel);

        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final Combo combo = new Combo(innerComposite, SWT.DROP_DOWN | SWT.READ_ONLY
            | SWT.BORDER);
        combo.setLayoutData(gridData);

        combo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

        final ComboViewer viewer = new ComboViewer(combo);
        viewer.setLabelProvider(new LabelProvider()
        {
            public String getText(Object element)
            {
                return ((ExtensionImpl) element).label;
            }
        });
        viewer.setComparator(new ViewerComparator(new Comparator<String>()
        {
            public int compare(String s1, String s2)
            {
                return s1.toLowerCase().compareTo(s2.toLowerCase());
            }
        }));

        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setInput(loader.getImplementations());

        return viewer;
    }

    /**
     * Initiate query processing. Open an editor with the current parameter values.
     */
    private void fireProcessing()
    {
        if (!hasAllRequiredAttributes(getSourceId()))
        {
            return;
        }

        final IWorkbenchPage page = getViewSite().getWorkbenchWindow().getActivePage();

        /*
         * Clone current attribute values so that they can be freely 
         * modified in the editor and in the input view.
         */
        final AttributeValueSet current = attributes.get(getSourceId());
        final AttributeValueSet cloned = new AttributeValueSet("request");
        cloned.setAttributeValues(current.getAttributeValues());

        final SearchInput input = new SearchInput(getSourceId(), getAlgorithmId(), cloned);
        try
        {
            page.openEditor(input, SearchEditor.ID);
        }
        catch (Exception x)
        {
            final IStatus status = new OperationStatus(IStatus.ERROR,
                WorkbenchCorePlugin.PLUGIN_ID, -2, "Editor could not be opened.", x);
            Utils.showError(status);
        }
    }

    /**
     * Check if all required {@link Input} attributes of a given source are properly initialized.
     */
    private boolean hasAllRequiredAttributes(String sourceId)
    {
        final AttributeEditorList attributeEditorList = editors.get(sourceId);
        final AttributeValueSet values = attributes.get(sourceId);
        for (AttributeDescriptor d : attributeEditorList.getAttributeDescriptors())
        {
            final Object value = values.getAttributeValue(d.key);

            if (!isValid(d, value))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns <code>true</code> if the value described by a given attribute descriptor
     * is valid.
     */
    private boolean isValid(AttributeDescriptor d, Object value)
    {
        Annotation [] constraints = d.constraints.toArray(new Annotation [d.constraints
            .size()]);
        return ConstraintValidator.isMet(value, constraints).length == 0;
    }

    /*
     * 
     */
    @SuppressWarnings("unchecked")
    private void createRequiredAttributesLayout()
    {
        final WorkbenchCorePlugin core = WorkbenchCorePlugin.getDefault();

        attributesPagesStack = new VisibleComponentSizeStackLayout();
        editorStack = new Composite(innerComposite, SWT.NONE);
        editorStack.setLayout(attributesPagesStack);

        final GridData attributesGridData = new GridData();
        attributesGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        attributesGridData.grabExcessHorizontalSpace = true;
        attributesGridData.horizontalSpan = 2;
        editorStack.setLayoutData(attributesGridData);

        for (ExtensionImpl implementation : core.getSources().getImplementations())
        {
            final ProcessingComponent source = implementation.getInstance();

            final Map<String, AttributeDescriptor> descriptors = BindableDescriptorBuilder
                .buildDescriptor(source).only(Input.class, Processing.class,
                    Required.class).not(Internal.class).flatten().attributeDescriptors;

            final String sourceID = implementation.id;
            if (!attributes.containsKey(sourceID))
            {
                this.attributes.put(sourceID, new AttributeValueSet(sourceID));
            }

            final AttributeEditorList page = new AttributeEditorList(editorStack, descriptors);
            this.editors.put(sourceID, page);
        }
    }

    /**
     * Restore state of UI components from memento.
     */
    @SuppressWarnings("unchecked")
    private void restoreState()
    {
        /*
         * Restore attribute sets for inputs.
         */
        if (state != null)
        {
            final IMemento [] mementos = state.getChildren(MEMENTO_ATTRIBUTES);
            if (mementos != null)
            {
                for (int i = 0; i < mementos.length; i++)
                {
                    final IMemento single = mementos[i];

                    final String id = single.getID();
                    final String serialized = single.getString(MEMENTO_ATTRIBUTE_SET_SERIALIZED);

                    if (id == null || serialized == null)
                    {
                        continue;
                    }

                    try
                    {
                        final AttributeValueSets sets = AttributeValueSets.deserialize(
                            new StringReader(serialized));
                        final AttributeValueSet set = sets.getDefaultAttributeValueSet();
                        if (set != null)
                        {
                            this.attributes.put(id, set);
                        }
                    }
                    catch (Exception e)
                    {
                        Utils.logError(e, false);
                    }
                }
            }
        }

        /*
         * Combo boxes state.
         */
        try
        {
            final Object [][] combos = new Object [] []
            {
                {
                    sourceViewer, MEMENTO_SOURCE_ID
                },
                {
                    algorithmViewer, MEMENTO_ALGORITHM_ID
                }
            };

            for (Object [] comboPair : combos)
            {
                ComboViewer combo = (ComboViewer) comboPair[0];
                String mementoAttr = (String) comboPair[1];

                Collection<ExtensionImpl> options = (Collection<ExtensionImpl>) combo
                    .getInput();

                /*
                 * Attempt to restore selection from memento, if not available,
                 * set the default option.
                 */
                boolean restored = false;
                if (state != null)
                {
                    String id = state.getString(mementoAttr);
                    if (id != null)
                    {
                        for (ExtensionImpl i : options)
                        {
                            if (i.id.equals(id))
                            {
                                combo.setSelection(new StructuredSelection(i), true);
                                restored = true;
                            }
                        }
                    }
                }

                if (!restored && combo.getCombo().getItemCount() > 0)
                {
                    final Object element = options.iterator().next();
                    combo.setSelection(new StructuredSelection(element));
                }
            }
        }
        catch (RuntimeException re)
        {
            Utils.logError("Could not restore search view state", re, false);
        }

        /*
         * Disable GUI if no inputs or algorithms.
         */

        final WorkbenchCorePlugin core = WorkbenchCorePlugin.getDefault();
        if (core.getSources().getImplementations().isEmpty())
        {
            disableComboWithMessage(sourceViewer.getCombo(), "No document sources found.");
            processButton.setEnabled(false);
        }

        if (core.getAlgorithms().getImplementations().isEmpty())
        {
            disableComboWithMessage(algorithmViewer.getCombo(),
                "No clustering algorithms found.");
            processButton.setEnabled(false);
        }
    }

    /**
     * 
     */
    private void disableComboWithMessage(Combo toDisable, String message)
    {
        toDisable.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        toDisable.setToolTipText(message);
        toDisable.setEnabled(false);
    }

    /**
     * 
     */
    private String getSourceId()
    {
        return getSelectedId(sourceViewer);
    }

    /**
     * 
     */
    private String getAlgorithmId()
    {
        return getSelectedId(algorithmViewer);
    }

    /**
     * 
     */
    private String getSelectedId(ComboViewer combo)
    {
        if (combo.getSelection().isEmpty())
        {
            return null;
        }

        final IStructuredSelection selection = ((IStructuredSelection) combo
            .getSelection());
        return ((ExtensionImpl) selection.getFirstElement()).id;
    }

    /*
     * 
     */
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException
    {
        state = memento;
        super.init(site, memento);
    }

    /*
     * 
     */
    @Override
    public void saveState(IMemento memento)
    {
        memento.putString(MEMENTO_SOURCE_ID, getSourceId());
        memento.putString(MEMENTO_ALGORITHM_ID, getAlgorithmId());

        for (Map.Entry<String, AttributeValueSet> entry : attributes.entrySet())
        {
            final String sourceID = entry.getKey();
            final AttributeValueSet set = entry.getValue();

            try
            {
                final StringWriter w = new StringWriter();
                final AttributeValueSets sets = new AttributeValueSets();
                sets.addAttributeValueSet(sourceID, set);
                sets.serialize(w);
                w.close();

                final String serialized = w.toString();

                final IMemento single = memento.createChild(MEMENTO_ATTRIBUTES, sourceID);
                single.putString(MEMENTO_ATTRIBUTE_SET_SERIALIZED, serialized);
            }
            catch (Exception e)
            {
                Utils.logError(e, false);
            }
        }
    }

    @Override
    public void setFocus()
    {
        this.sourceViewer.getCombo().setFocus();
    }

    @Override
    public void dispose()
    {
        for (Widget w : editors.values())
        {
            w.dispose();
        }

        super.dispose();
    }
}
