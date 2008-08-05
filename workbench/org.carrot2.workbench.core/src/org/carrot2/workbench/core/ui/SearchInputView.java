package org.carrot2.workbench.core.ui;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.*;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.util.attribute.constraint.ConstraintValidator;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.*;
import org.carrot2.workbench.core.preferences.PreferenceConstants;
import org.carrot2.workbench.core.ui.actions.ActiveSearchEditorActionDelegate;
import org.carrot2.workbench.core.ui.actions.GroupingMethodAction;
import org.carrot2.workbench.core.ui.widgets.CScrolledComposite;
import org.carrot2.workbench.editors.*;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.*;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;


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
     * Memento attribute for persisting {@link #linkWithEditor}.
     */
    private static final String MEMENTO_EDITOR_LINK = "link-with-editor";

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
     * Filter showing only required attributes.
     */
    @SuppressWarnings("unchecked")
    private final static Predicate<AttributeDescriptor> SHOW_REQUIRED = new AnnotationsPredicate(false, Required.class);

    /**
     * Filter showing all attributes.
     */
    private final static Predicate<AttributeDescriptor> SHOW_ALL = Predicates.alwaysTrue();

    /**
     * State persistence.
     */
    private IMemento state;

    private ComboViewer sourceViewer;
    private ComboViewer algorithmViewer;
    private Button processButton;

    /**
     * Scroller composite container.
     */
    private CScrolledComposite scroller;

    /**
     * A composite holding holding an {@link AttributeGroups} (editors for the current
     * combination of input/ source).
     */
    private Composite editorCompositeContainer;

    /**
     * The current editor composite.
     */
    private AttributeGroups editorComposite;

    /**
     * A joint set of attributes for all sources from {@link WorkbenchCorePlugin#getSources()}.
     */
    private AttributeValueSet attributes = new AttributeValueSet("all-inputs");

    /**
     * A map of {@link BindableDescriptor} for each document source ID from {@link
     * WorkbenchCorePlugin#getSources()}.
     */
    private Map<String, BindableDescriptor> descriptors = Maps.newHashMap();

    /**
     * All {@link DocumentSourceDescriptor}s related to {@link DocumentSource}s in 
     * {@link #sourceViewer}. 
     */
    private HashMap<String, DocumentSourceDescriptor> sources;

    /**
     * All {@link ProcessingComponentDescriptor}s related to {@link ClusteringAlgorithm}s 
     * in {@link #algorithmViewer}.
     */
    private HashMap<String, ProcessingComponentDescriptor> algorithms;

    /**
     * Link the GUI with currently selected editor.
     */
    private boolean linkWithEditor = false;

    /**
     * Selection listener on {@link #sourceViewer}.
     */
    private ISelectionChangedListener sourceSelectionListener = new ISelectionChangedListener()
    {
        public void selectionChanged(SelectionChangedEvent event)
        {
            final DocumentSourceDescriptor impl = (DocumentSourceDescriptor) 
                ((IStructuredSelection) event.getSelection()).getFirstElement();
            setSourceId(impl.getId());
        }
    };
    
    /**
     * Link the GUI of {@link SearchInputView} with the currently 
     * active {@link SearchEditor}.
     */
    private class LinkWithEditorActionDelegate extends ActiveSearchEditorActionDelegate
    {
        /*
         * 
         */
        @Override
        public void run(SearchEditor editor)
        {
            linkWithEditor = !linkWithEditor;

            super.getAction().setChecked(linkWithEditor);
            if (linkWithEditor)
            {
                if (isEnabled(getEditor()))
                {
                    linkWith((SearchEditor) getEditor());
                }
            }
        }

        /**
         * Is this action enabled for the given editor?
         */
        protected boolean isEnabled(IEditorPart activeEditor)
        {
            return activeEditor != null && activeEditor instanceof SearchEditor;
        }

        /**
         * Detect editor switch.
         */
        @Override
        protected void switchingEditors(IEditorPart previous, IEditorPart activeEditor)
        {
            super.switchingEditors(previous, activeEditor);

            if (activeEditor != null && linkWithEditor)
            {
                final SearchEditor editor = (SearchEditor) activeEditor;
                linkWith(editor);
            }
        }

        /**
         * Synchronize the view with the given editor.
         */
        private void linkWith(SearchEditor editor)
        {
            final SearchInput input = editor.getSearchResult().getInput();

            setAlgorithmId(input.getAlgorithmId());
            attributes.setAttributeValues(
                input.getAttributeValueSet().getAttributeValues());
            setSourceId(input.getSourceId());
        }
    }

    /**
     * Toggles between {@link SearchInputView#SHOW_REQUIRED} and
     * {@link SearchInputView#SHOW_ALL}. 
     */
    private static class ShowOptionalAction extends Action
    {
        public ShowOptionalAction()
        {
            super("Show optional attributes", SWT.TOGGLE);
            
            /*
             * Subscribe to change events on the global preference
             * property and update initial state.
             */
            final IPreferenceStore preferenceStore = 
                WorkbenchCorePlugin.getDefault().getPreferenceStore();
            preferenceStore.addPropertyChangeListener(new IPropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent event)
                {
                    if (PreferenceConstants.SHOW_OPTIONAL.equals(
                        event.getProperty()))
                    {
                        updateState();
                    }
                }
            });

            updateState();
        }

        /*
         * Update selection state.
         */
        private void updateState()
        {
            final boolean state = 
                WorkbenchCorePlugin.getDefault().getPreferenceStore().getBoolean(
                PreferenceConstants.SHOW_OPTIONAL);

            setChecked(state);
        }

        @Override
        public void run()
        {
            final IPreferenceStore preferenceStore = 
                WorkbenchCorePlugin.getDefault().getPreferenceStore();

            final boolean state =  preferenceStore.getBoolean(
                PreferenceConstants.SHOW_OPTIONAL);

            preferenceStore.setValue(PreferenceConstants.SHOW_OPTIONAL, !state);
        }
    }

    /*
     * 
     */
    @Override
    public void init(IViewSite site) throws PartInitException
    {
        super.init(site);

        /*
         * Create toolbar and menu contributions.
         */
        final IActionBars bars = getViewSite().getActionBars();
        createMenu(bars.getMenuManager());
        createToolbar(bars.getToolBarManager());
        bars.updateActionBars();        
    }
    
    /*
     * 
     */
    private void createToolbar(IToolBarManager toolBarManager)
    {
        final IAction linkWithEditor = new ActionDelegateProxy(
            new LinkWithEditorActionDelegate(), IAction.AS_CHECK_BOX);
        linkWithEditor.setImageDescriptor(WorkbenchCorePlugin.getImageDescriptor("icons/link_e.gif"));
        linkWithEditor.setToolTipText("Link the interface with current editor");
        toolBarManager.add(linkWithEditor);

        toolBarManager.add(new GroupingMethodAction(PreferenceConstants.GROUPING_INPUT_VIEW));
    }

    /*
     * 
     */
    private void createMenu(IMenuManager menuManager)
    {
        final IAction showRequiredOnly = new ShowOptionalAction();

        menuManager.add(showRequiredOnly);
        menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    /**
     * Create user interface for the view.
     */
    @Override
    public void createPartControl(Composite parent)
    {
        /*
         * Create GUI components.
         */
        createComponents(parent);

        /*
         * Hook processing event to the processing button and on traversal (when all
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
        
        /*
         * Hook global preference updates.
         */
        final IPreferenceStore preferenceStore = 
            WorkbenchCorePlugin.getDefault().getPreferenceStore();

        preferenceStore.addPropertyChangeListener(
            new PropertyChangeListenerAdapter(PreferenceConstants.SHOW_OPTIONAL)
        {
            public void propertyChangeFiltered(PropertyChangeEvent event)
            {
                displayEditorSet();
            }
        });
        
        preferenceStore.addPropertyChangeListener(
            new PropertyChangeListenerAdapter(PreferenceConstants.GROUPING_INPUT_VIEW)
        {
            public void propertyChangeFiltered(PropertyChangeEvent event)
            {
                displayEditorSet();
            }
        });
    }

    /**
     * Display a new composite with editors for the current combination of the source, algorithm
     * and grouping/ filtering flags.
     */
    private void displayEditorSet()
    {
        final IPreferenceStore preferenceStore = 
            WorkbenchCorePlugin.getDefault().getPreferenceStore();

        final Predicate<AttributeDescriptor> filter;
        if (preferenceStore.getBoolean(PreferenceConstants.SHOW_OPTIONAL))
        {
            filter = SHOW_ALL;
        }
        else
        {
            filter = SHOW_REQUIRED;
        }

        this.editorCompositeContainer.setRedraw(false);

        /*
         * Dispose of last editor.
         */
        if (this.editorComposite != null)
        {
            this.editorComposite.dispose();
            this.editorComposite = null;
        }
        
        /*
         * Create a new editor set.
         */
        final IPreferenceStore prefStore = 
            WorkbenchCorePlugin.getDefault().getPreferenceStore();

        final String sourceID = getSourceId();
        final GroupingMethod groupingMethod = GroupingMethod.valueOf(
            prefStore.getString(PreferenceConstants.GROUPING_INPUT_VIEW));
        final BindableDescriptor sourceDescriptor = this.descriptors.get(sourceID);

        if (StringUtils.isEmpty(getSourceId()))
        {
            // No active source.
            return;
        }

        this.editorComposite = new AttributeGroups(editorCompositeContainer, 
            sourceDescriptor, groupingMethod, filter);
        
        final GridData gd = new GridData();
        gd.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        editorComposite.setLayoutData(gd);

        /*
         * Set initial values for editors.
         */
        for (Map.Entry<String, Object> e : filterAttributesOf(sourceID).entrySet())
        {
            editorComposite.setAttribute(e.getKey(), e.getValue());
        }

        /*
         * Hook up listeners updating attributes on changes in editors. 
         */
        editorComposite.addAttributeChangeListener(new AttributeListenerAdapter() {
            public void attributeChange(AttributeChangedEvent event)
            {
                attributes.setAttributeValue(event.key, event.value);
                checkAllRequiredAttributes();
            }

            public void contentChanging(IAttributeEditor editor, Object value)
            {
                /*
                 * On content changing, eagerly substitute the value of the
                 * given attribute with the new value. In the input view, early
                 * commit of attribute values should not trigger any additional 
                 * consequences, so we can do it.
                 */
                final String attributeKey = editor.getAttributeKey();
                attributes.setAttributeValue(attributeKey, value);
                checkAllRequiredAttributes();
            }
        });

        /*
         * Redraw GUI. 
         */
        this.editorCompositeContainer.setRedraw(true);
        checkAllRequiredAttributes();
        scroller.reflow(true);
    }

    /**
     * Filter only those keys from {@link #attributes} that belong to source
     * <code>sourceID</code>. 
     */
    private Map<String, Object> filterAttributesOf(String sourceID)
    {
        final Map<String, Object> filtered = Maps.newLinkedHashMap(
            attributes.getAttributeValues());

        filtered.keySet().retainAll(descriptors.get(sourceID).attributeDescriptors.keySet());
        
        return filtered;
    }

    /**
     * Creates permanent GUI elements (source, algorithm combos, placeholder for
     * the editors).
     */
    @SuppressWarnings("unchecked")
    private void createComponents(Composite parent)
    {
        final WorkbenchCorePlugin core = WorkbenchCorePlugin.getDefault();
        parent.setLayout(new FillLayout());

        this.scroller = new CScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(false);

        final Composite innerComposite = GUIFactory.createSpacer(scroller);
        final GridLayout gridLayout = (GridLayout) innerComposite.getLayout();
        gridLayout.numColumns = 2;
        gridLayout.makeColumnsEqualWidth = false;

        scroller.setContent(innerComposite);

        // Initialize sources, descriptors and source combo.
        ProcessingComponentSuite suite = core.getComponentSuite();
        
        sourceViewer = createComboViewer(innerComposite, "Source", suite.getSources());
        sourceViewer.addSelectionChangedListener(sourceSelectionListener);

        sources = Maps.newHashMap();
        for (DocumentSourceDescriptor e : suite.getSources())
        {
            try
            {
                final ProcessingComponent pc = e.getComponentClass().newInstance();
    
                sources.put(e.getId(), e);
    
                final BindableDescriptor descriptor = BindableDescriptorBuilder
                    .buildDescriptor(pc).only(Input.class, Processing.class);
            
                descriptors.put(e.getId(), descriptor);
            }
            catch (Exception x)
            {
                Utils.logError("Could not initialize source: " + e.getId(), false);
            }
        }

        // Initialize algorithms and algorithm combo.
        algorithmViewer = createComboViewer(innerComposite, "Algorithm", suite.getAlgorithms());

        algorithms = Maps.newHashMap();
        for (ProcessingComponentDescriptor e : suite.getAlgorithms())
        {
            algorithms.put(e.getId(), e);
        }

        final Label l = new Label(innerComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
        l.setLayoutData(GridDataFactory.fillDefaults()
            .align(SWT.FILL, SWT.CENTER).span(2, 1).minSize(SWT.DEFAULT, 10).create());

        // Initialize a place holder for the editors.
        this.editorCompositeContainer = new Composite(innerComposite, SWT.NONE);
        this.editorCompositeContainer.setLayoutData(
            GridDataFactory.fillDefaults().span(2, 1).create());
        this.editorCompositeContainer.setLayout(
            GridLayoutFactory.fillDefaults().create());

        // Initialize process button.
        processButton = new Button(innerComposite, SWT.PUSH);
        processButton.setText("Process");

        final GridData processButtonGridData = new GridData();
        processButtonGridData.horizontalAlignment = GridData.END;
        processButtonGridData.verticalAlignment = GridData.END;
        processButtonGridData.horizontalSpan = 2;
        processButton.setLayoutData(processButtonGridData);

        /*
         * Restore state and push initial values to editors.
         */
        restoreState();
        displayEditorSet();
    }
    
    /**
     * Creates a JFace ComboViewer around a collection of extension point implementations.
     */
    private ComboViewer createComboViewer(Composite parent, String comboLabel, 
        List<? extends ProcessingComponentDescriptor> components)
    {
        final Label label = new Label(parent, SWT.CENTER);
        label.setLayoutData(new GridData());
        label.setText(comboLabel);

        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY
            | SWT.BORDER);
        combo.setLayoutData(gridData);
        combo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

        final ComboViewer viewer = new ComboViewer(combo);
        viewer.setLabelProvider(new LabelProvider()
        {
            public String getText(Object element)
            {
                return ((ProcessingComponentDescriptor) element).getLabel();
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
        viewer.setInput(components);

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
        final AttributeValueSet requestAttrs = new AttributeValueSet("request");
        requestAttrs.setAttributeValues(filterAttributesOf(getSourceId()));

        final SearchInput input = new SearchInput(getSourceId(), getAlgorithmId(), requestAttrs);
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
        final Collection<AttributeDescriptor> desc = descriptors.get(sourceId)
            .flatten().attributeDescriptors.values();

        for (AttributeDescriptor d : desc)
        {
            final Object value = attributes.getAttributeValue(d.key);

            if (!isValid(d, value))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if all required attributes for the current configuration are available and
     * update the {@link #processButton}.
     */
    private void checkAllRequiredAttributes()
    {
        processButton.setEnabled(hasAllRequiredAttributes(getSourceId()));
    }
    
    /**
     * Returns <code>true</code> if the value described by a given attribute descriptor
     * is valid (non-<code>null</code> for {@link Required} attributes and fulfilling
     * all other constraints.
     */
    private boolean isValid(AttributeDescriptor d, Object value)
    {
        if (d.requiredAttribute && value == null)
        {
            return false;
        }

        if (value == null)
        {
            value = d.defaultValue;
        }

        final Annotation [] constraints = d.constraints.toArray(
            new Annotation [d.constraints.size()]);

        return ConstraintValidator.isMet(value, constraints).length == 0;
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
                            this.attributes.setAttributeValues(set.getAttributeValues());
                        }
                    }
                    catch (Exception e)
                    {
                        Utils.logError(e, false);
                    }
                }
            }

            this.linkWithEditor = Boolean.valueOf(state.getString(MEMENTO_EDITOR_LINK));
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

                Collection<ProcessingComponentDescriptor> options = 
                    (Collection<ProcessingComponentDescriptor>) combo.getInput();

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
                        for (ProcessingComponentDescriptor i : options)
                        {
                            if (i.getId().equals(id))
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
        if (sources.isEmpty())
        {
            disableComboWithMessage(sourceViewer.getCombo(), "No document sources.");
            processButton.setEnabled(false);
        }

        if (algorithms.isEmpty())
        {
            disableComboWithMessage(algorithmViewer.getCombo(), "No clustering algorithms.");
            processButton.setEnabled(false);
        }
    }

    /*
     *
     */
    private void disableComboWithMessage(Combo toDisable, String message)
    {
        toDisable.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        toDisable.setToolTipText(message);
        toDisable.setEnabled(false);
    }

    /**
     * Return the current source component identifier.
     */
    private String getSourceId()
    {
        return getSelectedId(sourceViewer);
    }

    /**
     * Switch to user interface to the given source.
     */
    private void setSourceId(String sourceID)
    {
        sourceViewer.removeSelectionChangedListener(sourceSelectionListener);
        sourceViewer.setSelection(new StructuredSelection(sources.get(sourceID)));
        sourceViewer.addSelectionChangedListener(sourceSelectionListener);

        displayEditorSet();
    }

    /*
     * 
     */
    private String getAlgorithmId()
    {
        return getSelectedId(algorithmViewer);
    }
    
    /*
     * 
     */
    private void setAlgorithmId(String algorithmID)
    {
        algorithmViewer.setSelection(
            new StructuredSelection(algorithms.get(algorithmID)));
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
        return ((ProcessingComponentDescriptor) selection.getFirstElement()).getId();
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
        memento.putString(MEMENTO_EDITOR_LINK, Boolean.toString(linkWithEditor));

        /*
         * Save attributes for each input separately.
         */
        
        for (String sourceID : this.descriptors.keySet())
        {
            final AttributeValueSet set = new AttributeValueSet(sourceID);
            set.setAttributeValues(filterAttributesOf(sourceID));

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

    /*
     * 
     */
    @Override
    public void setFocus()
    {
        this.sourceViewer.getCombo().setFocus();
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        this.editorComposite.dispose();
        super.dispose();
    }
}
