
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

package org.carrot2.workbench.core.ui;

import java.io.IOException;
import java.util.*;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.*;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.*;
import org.carrot2.workbench.core.preferences.PreferenceConstants;
import org.carrot2.workbench.core.ui.actions.ActiveSearchEditorActionDelegate;
import org.carrot2.workbench.core.ui.actions.GroupingMethodAction;
import org.carrot2.workbench.core.ui.widgets.CScrolledComposite;
import org.carrot2.workbench.editors.AttributeEvent;
import org.carrot2.workbench.editors.AttributeListenerAdapter;
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
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;

import org.carrot2.shaded.guava.common.base.Predicate;
import org.carrot2.shaded.guava.common.base.Predicates;
import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

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
     * Filter showing only required attributes.
     */
    private final static Predicate<AttributeDescriptor> SHOW_REQUIRED = new AnnotationsPredicate(
        false, Required.class);

    /**
     * Filter showing all attributes.
     */
    private final static Predicate<AttributeDescriptor> SHOW_ALL = Predicates
        .alwaysTrue();

    /**
     * State persistence.
     */
    private SearchInputViewMemento state;

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
    private AttributeGroups attributeGroups;

    /**
     * A joint set of attributes for all sources from and default attribute values for
     * algorithms.
     */
    private final AttributeValueSet attributes = new AttributeValueSet("global");

    /**
     * A map of {@link BindableDescriptor} for each document source ID and
     * algorithm ID from {@link WorkbenchCorePlugin#getComponentSuite()}.
     */
    private Map<String, BindableDescriptor> descriptors = Maps.newHashMap();

    /**
     * All {@link DocumentSourceDescriptor}s related to {@link IDocumentSource}s in
     * {@link #sourceViewer}.
     */
    private HashMap<String, DocumentSourceDescriptor> sources;

    /**
     * All {@link ProcessingComponentDescriptor}s related to {@link IClusteringAlgorithm}s
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
            final DocumentSourceDescriptor impl = (DocumentSourceDescriptor) ((IStructuredSelection) event
                .getSelection()).getFirstElement();
            setSourceId(impl.getId());
        }
    };

    /*
     * 
     */
    private IAction linkWithEditorAction;

    /**
     * Validation validationStatus.
     */
    private CLabel validationStatus;

    /**
     * If <code>true</code>, validation validationStatus is immediately propagated to
     * the user interface (this happens after the first click on {@link #processButton}.
     */
    private boolean showValidationStatus;

    /*
     * 
     */
    private Image errorStatusImage = WorkbenchCorePlugin.getImageDescriptor(
        "icons/error.png").createImage();

    /**
     * Link the GUI of {@link SearchInputView} with the currently active
     * {@link SearchEditor}.
     */
    private class LinkWithEditorActionDelegate extends ActiveSearchEditorActionDelegate
    {
        /* */
        @Override
        protected void run(SearchEditor editor)
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
            attributes.setAttributeValues(input.getAttributeValueSet()
                .getAttributeValues());
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

            setImageDescriptor(WorkbenchCorePlugin.getImageDescriptor("icons/optional.png"));

            /*
             * Subscribe to change events on the global preference property and update
             * initial state.
             */
            final IPreferenceStore preferenceStore = WorkbenchCorePlugin.getDefault()
                .getPreferenceStore();
            preferenceStore.addPropertyChangeListener(new IPropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent event)
                {
                    if (PreferenceConstants.SHOW_OPTIONAL.equals(event.getProperty()))
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
            final boolean state = WorkbenchCorePlugin.getDefault().getPreferenceStore()
                .getBoolean(PreferenceConstants.SHOW_OPTIONAL);

            setChecked(state);
        }

        @Override
        public void run()
        {
            final IPreferenceStore preferenceStore = WorkbenchCorePlugin.getDefault()
                .getPreferenceStore();

            final boolean state = preferenceStore
                .getBoolean(PreferenceConstants.SHOW_OPTIONAL);

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
        createToolbar(bars.getToolBarManager());
        bars.updateActionBars();
    }
    
    /*
     * 
     */
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException
    {
        super.init(site, memento);

        this.state = null;
        try
        {
            if (memento != null)
            {
                this.state = SimpleXmlMemento.getChild(SearchInputViewMemento.class, memento);
            }
        }
        catch (IOException e)
        {
            Utils.logError(e, false);
            this.state = null;
        }
    }

    /*
     * 
     */
    private void createToolbar(IToolBarManager toolBarManager)
    {
        // Link with editor action.
        final IAction linkWithEditor = new ActionDelegateProxy(
            new LinkWithEditorActionDelegate(), IAction.AS_CHECK_BOX);
        linkWithEditor.setImageDescriptor(WorkbenchCorePlugin
            .getImageDescriptor("icons/link_e.png"));
        linkWithEditor.setToolTipText("Link the interface with current editor");
        toolBarManager.add(linkWithEditor);
        this.linkWithEditorAction = linkWithEditor;

        // Optional attributes action toggle.
        final IAction showRequiredOnly = new ShowOptionalAction();
        toolBarManager.add(showRequiredOnly);

        // Grouping method action.
        toolBarManager.add(new GroupingMethodAction(
            PreferenceConstants.GROUPING_INPUT_VIEW));

        // Add save attributes action.
        toolBarManager.add(new SaveDocumentSourceAttributesAction(this));
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
                final String source = getSourceId();
                if (hasAllRequiredAttributes(source))
                {
                    fireProcessing();
                }
                else
                {
                    showValidationStatus = true;
                    checkAllRequiredAttributes();
                }
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
        final IPreferenceStore preferenceStore = WorkbenchCorePlugin.getDefault()
            .getPreferenceStore();

        preferenceStore.addPropertyChangeListener(new PropertyChangeListenerAdapter(
            PreferenceConstants.SHOW_OPTIONAL)
        {
            public void propertyChangeFiltered(PropertyChangeEvent event)
            {
                displayEditorSet();
            }
        });

        preferenceStore.addPropertyChangeListener(new PropertyChangeListenerAdapter(
            PreferenceConstants.GROUPING_INPUT_VIEW)
        {
            public void propertyChangeFiltered(PropertyChangeEvent event)
            {
                displayEditorSet();
            }
        });
    }

    /**
     * Display a new composite with editors for the current combination of the source,
     * algorithm and grouping/ filtering flags.
     */
    private void displayEditorSet()
    {
        final IPreferenceStore preferenceStore = WorkbenchCorePlugin.getDefault()
            .getPreferenceStore();

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
        Map<String, Boolean> expansionState = Collections.emptyMap();
        if (this.attributeGroups != null)
        {
            expansionState = this.attributeGroups.getExpansionStates();
            this.attributeGroups.dispose();
            this.attributeGroups = null;
        }

        /*
         * Create a new editor set.
         */
        final IPreferenceStore prefStore = WorkbenchCorePlugin.getDefault()
            .getPreferenceStore();

        final String sourceID = getSourceId();
        final GroupingMethod groupingMethod = GroupingMethod.valueOf(prefStore
            .getString(PreferenceConstants.GROUPING_INPUT_VIEW));
        final BindableDescriptor sourceDescriptor = this.descriptors.get(sourceID);

        if (StringUtils.isEmpty(getSourceId()))
        {
            final Label label = new Label(editorCompositeContainer, SWT.CENTER);
            label.setText("No active sources");
            final GridData gd = new GridData();
            gd.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
            gd.grabExcessHorizontalSpace = true;
            label.setLayoutData(gd);
        }
        else
        {
            this.attributeGroups = new AttributeGroups(editorCompositeContainer,
                sourceDescriptor, groupingMethod, filter, attributes.getAttributeValues());

            final GridData gd = new GridData();
            gd.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
            gd.grabExcessHorizontalSpace = true;
            attributeGroups.setLayoutData(gd);
            attributeGroups.setExpanded(expansionState);

            /*
             * Set default values for those attributes that do not have any assigned.
             */
            Map<String,Object> defaultValues = sourceDescriptor.getDefaultValues();
            for (Map.Entry<String, Object> e : defaultValues.entrySet())
            {
                if (attributes.getAttributeValue(e.getKey()) == null)
                {
                    attributes.setAttributeValue(e.getKey(), e.getValue());
                }
            }

            /*
             * Set initial values of editors.
             */
            attributeGroups.setAttributes(filterAttributesOf(sourceID));

            /*
             * Hook up listeners updating attributes on changes in editors.
             */
            attributeGroups.addAttributeListener(new AttributeListenerAdapter()
            {
                public void valueChanged(AttributeEvent event)
                {
                    if (event.key.equals(AttributeList.ENABLE_VALIDATION_OVERLAYS))
                    {
                        return;
                    }

                    attributes.setAttributeValue(event.key, event.value);
                    checkAllRequiredAttributes();
                }

                public void valueChanging(AttributeEvent event)
                {
                    /*
                     * On content changing, eagerly substitute the value of the given
                     * attribute with the new value. In the input view, early commit of
                     * attribute values should not trigger any additional consequences, so
                     * we can do it.
                     */
                    valueChanged(event);
                }
            });
        }

        /*
         * Redraw GUI.
         */
        this.editorCompositeContainer.setRedraw(true);
        this.editorCompositeContainer.layout(true);

        checkAllRequiredAttributes();
        scroller.reflow(true);
    }

    /**
     * Creates permanent GUI elements (source, algorithm combos, placeholder for the
     * editors).
     */
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
        final ProcessingComponentSuite suite = core.getComponentSuite();

        sourceViewer = createComboViewer(innerComposite, "Source", suite.getSources());
        sourceViewer.addSelectionChangedListener(sourceSelectionListener);

        sources = Maps.newHashMap();
        for (DocumentSourceDescriptor e : suite.getSources())
        {
            try
            {
                sources.put(e.getId(), e);
                addFilteredDescriptor(e);
            }
            catch (Exception x)
            {
                Utils.logError("Could not initialize source: " + e.getId(), false);
            }
        }

        // Initialize algorithms and algorithm combo.
        algorithmViewer = createComboViewer(innerComposite, "Algorithm", suite
            .getAlgorithms());

        algorithms = Maps.newHashMap();
        for (ProcessingComponentDescriptor e : suite.getAlgorithms())
        {
            algorithms.put(e.getId(), e);
            addFilteredDescriptor(e);
        }

        final Label l = new Label(innerComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
        l.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(
            2, 1).minSize(SWT.DEFAULT, 10).create());

        // Initialize a place holder for the editors.
        this.editorCompositeContainer = new Composite(innerComposite, SWT.NONE);
        this.editorCompositeContainer.setLayoutData(GridDataFactory.fillDefaults().span(
            2, 1).create());
        this.editorCompositeContainer
            .setLayout(GridLayoutFactory.fillDefaults().create());

        final Composite processStatus = new Composite(innerComposite, SWT.NONE);
        processStatus.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true,
            false).create());
        processStatus.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).spacing(3,
            0).create());

        this.validationStatus = new CLabel(processStatus, SWT.LEFT);
        validationStatus.setLayoutData(GridDataFactory.fillDefaults().hint(250,
            SWT.DEFAULT).grab(true, false).create());

        // Initialize process button.
        processButton = new Button(processStatus, SWT.PUSH);
        processButton.setText("Process");

        final GridData processButtonGridData = new GridData();
        processButtonGridData.horizontalAlignment = GridData.END;
        processButtonGridData.verticalAlignment = GridData.END;
        processButtonGridData.horizontalSpan = 1;
        processButton.setLayoutData(processButtonGridData);

        // Restore view state.
        restoreState();

        // Initial editor display for the current input.
        displayEditorSet();

        // Restore initial expansion state for groups.
        if (attributeGroups != null)
        {
	        if (state != null)
	        {
	        	if (state.sectionsExpansionState != null)
	        	{
	        		this.attributeGroups.setExpanded(state.sectionsExpansionState);
	        	}
	        }
	        else
	        {
	            // Set the default expansion state.
	            this.attributeGroups.setExpanded(false);
	            this.attributeGroups.setExpanded(AttributeLevel.BASIC.toString(), true);
	        }
        }
    }

    /**
     * Adds a {@link BindableDescriptor} to {@link #descriptors}, filtering
     * to only {@link Input} and {@link Processing} attributes.
     */
    private void addFilteredDescriptor(ProcessingComponentDescriptor e)
    {
        final WorkbenchCorePlugin core = WorkbenchCorePlugin.getDefault();

        descriptors.put(e.getId(),
            core.getComponentDescriptor(e.getId()).only(
                Input.class, Processing.class));        
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

        final Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
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
         * Clone current attribute values so that they can be freely modified in the
         * editor and in the input view.
         */
        final AttributeValueSet requestAttrs = new AttributeValueSet("request");
        requestAttrs.setAttributeValues(filterAttributesOf(getSourceId()));
        requestAttrs.setAttributeValues(filterAttributesOf(getAlgorithmId()));

        final SearchInput input = new SearchInput(
            getSourceId(), getAlgorithmId(), requestAttrs);
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
     * Check if all required {@link Input} attributes of a given source are properly
     * initialized.
     */
    private boolean hasAllRequiredAttributes(String sourceId)
    {
        if (StringUtils.isEmpty(sourceId))
        {
            return false;
        }

        return getEmptyRequiredAttributes(sourceId).isEmpty();
    }

    /**
     * Returns descriptors of all required attributes that still have no values or have
     * invalid values.
     */
    private Collection<AttributeDescriptor> getEmptyRequiredAttributes(String sourceId)
    {
        final Collection<AttributeDescriptor> desc = descriptors.get(sourceId).flatten().attributeDescriptors
            .values();

        final ArrayList<AttributeDescriptor> remaining = Lists.newArrayList();
        for (AttributeDescriptor d : desc)
        {
            final Object value = attributes.getAttributeValue(d.key);

            if (!d.isValid(value))
            {
                remaining.add(d);
            }
        }

        return remaining;
    }

    /**
     * Check if all required attributes for the current configuration are available and
     * update the {@link #processButton}.
     */
    private void checkAllRequiredAttributes()
    {
        if (!showValidationStatus)
        {
            this.validationStatus.setText("");
            this.validationStatus.setImage(null);
        }
        else
        {
            attributeGroups.setAttribute(
                AttributeList.ENABLE_VALIDATION_OVERLAYS, true);

            final String source = getSourceId();
            if (!StringUtils.isEmpty(source))
            {
                final Collection<AttributeDescriptor> remaining = getEmptyRequiredAttributes(source);
                if (remaining.size() > 0)
                {
                    final String firstBad = remaining.iterator().next().metadata.getLabelOrTitle();
                    validationStatus.setText("Invalid attribute value: " + firstBad);
                    validationStatus.setImage(errorStatusImage);
                }
                else
                {
                    this.validationStatus.setText("");
                    this.validationStatus.setImage(null);
                }
            }
        }
    }

    /**
     * Restore state of UI components from saved state.
     */
    private void restoreState()
    {
        if (state != null)
        {
            this.attributes.setAttributeValues(state.attributes.getAttributeValues());
            this.linkWithEditor = state.linkWithEditor;
        }
        this.linkWithEditorAction.setChecked(linkWithEditor);

        ProcessingComponentDescriptor source = null;
        ProcessingComponentDescriptor algorithm = null;

        if (state != null)
        {
            source = sources.get(state.sourceId);
            algorithm = algorithms.get(state.algorithmId);
        }
        
        if (source == null)
        {
            // Try to select the default set in preferences.
            String id = WorkbenchCorePlugin.getDefault().getPreferenceStore().getString(
                PreferenceConstants.DEFAULT_SOURCE_ID);
            source = sources.get(id);
        }

        if (algorithm == null)
        {
            // Try to select the default set in preferences.
            String id = WorkbenchCorePlugin.getDefault().getPreferenceStore().getString(
                PreferenceConstants.DEFAULT_ALGORITHM_ID);
            algorithm = algorithms.get(id);
        }

        restoreState(sourceViewer, source);
        restoreState(algorithmViewer, algorithm);

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

    /**
     * Attempt to set selection to the given descriptor, if failed, select the first
     * available element. 
     */
    @SuppressWarnings("unchecked")
    private void restoreState(ComboViewer combo, ProcessingComponentDescriptor descriptor)
    {
        if (descriptor == null)
        {
            Collection<ProcessingComponentDescriptor> options = 
                (Collection<ProcessingComponentDescriptor>) combo.getInput();

            if (options.size() > 0)
            {
                descriptor = options.iterator().next();
            }
        }

        if (descriptor != null)
        {
            combo.setSelection(new StructuredSelection(descriptor), true);
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
    String getSourceId()
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
        showValidationStatus = false;

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
        algorithmViewer
            .setSelection(new StructuredSelection(algorithms.get(algorithmID)));
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

    /**
     * Filter only those keys from {@link #attributes} that belong to source
     * <code>sourceID</code>.
     */
    Map<String, Object> filterAttributesOf(String componentId)
    {
        final Map<String, Object> filtered = Maps.newLinkedHashMap(
            attributes.getAttributeValues());

        filtered.keySet().retainAll(
            descriptors.get(componentId).attributeDescriptors.keySet());

        return filtered;
    }

    /**
     * Update an attribute stored in the input view. 
     */
    void setAttribute(String key, Object value)
    {
        /*
         * Force update of attributes map directly because this method
         * updates algorithm attributes that are not covered by editorComposite.
         */
        this.attributes.setAttributeValue(key, value);
        this.attributeGroups.setAttribute(key, value);
    }

    /*
     * 
     */
    @Override
    public void saveState(IMemento memento)
    {
        final SearchInputViewMemento state = new SearchInputViewMemento();

        state.sourceId = getSourceId();
        state.algorithmId = getAlgorithmId();
        state.linkWithEditor = linkWithEditor;
        state.attributes = attributes;
        
        if (attributeGroups != null) {
        	state.sectionsExpansionState = attributeGroups.getExpansionStates();
        }

        try
        {
            SimpleXmlMemento.addChild(memento, state);
        }
        catch (IOException e)
        {
            Utils.logError(e, false);
        }
    }

    /**
     * We set the focus to the current {@link #attributeGroups}'s default element if
     * possible. Otherwise, set the focus to the input source combo.
     */
    @Override
    public void setFocus()
    {
        if (attributeGroups != null)
        {
            attributeGroups.setFocus();
        }
        else
        {
            this.sourceViewer.getCombo().setFocus();
        }
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        if (this.attributeGroups != null) attributeGroups.dispose();
        if (this.errorStatusImage != null) this.errorStatusImage.dispose();
        
        super.dispose();
    }

    /**
     * Returns the active page's view instance.
     */
    public static SearchInputView getView()
    {
        final IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();

        return (SearchInputView) page.findView(SearchInputView.ID);
    }
}
