
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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.carrot2.core.Cluster;
import org.carrot2.core.ProcessingComponentDescriptor;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.InternalAttributePredicate;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.util.attribute.Input;
import org.carrot2.workbench.core.WorkbenchActionFactory;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.ActionDelegateProxy;
import org.carrot2.workbench.core.helpers.DisposeBin;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.core.helpers.PartListenerAdapter;
import org.carrot2.workbench.core.helpers.PostponableJob;
import org.carrot2.workbench.core.helpers.SimpleXmlMemento;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.preferences.PreferenceConstants;
import org.carrot2.workbench.core.ui.actions.GroupingMethodAction;
import org.carrot2.workbench.core.ui.actions.SaveAsXMLActionDelegate;
import org.carrot2.workbench.core.ui.sash.SashForm;
import org.carrot2.workbench.core.ui.widgets.CScrolledComposite;
import org.carrot2.workbench.editors.AttributeEvent;
import org.carrot2.workbench.editors.AttributeListenerAdapter;
import org.carrot2.workbench.editors.IAttributeListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableEditor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.progress.UIJob;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import org.carrot2.shaded.guava.common.base.Predicates;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Editor accepting {@link SearchInput} and performing operations on it. The editor also
 * exposes the model of processing results.
 */
public final class SearchEditor extends EditorPart implements IPersistableEditor
{
    /**
     * Public identifier of this editor.
     */
    public static final String ID = "org.carrot2.workbench.core.editors.searchEditor";

    /**
     * Options required for {@link #doSave(IProgressMonitor)}.
     */
    @Root(name = "save-dialog-options")
    public static final class SaveOptions implements Cloneable
    {
        /**
         * Preference key for global options.
         */
        @org.simpleframework.xml.Transient
        private static final String GLOBAL_OPTIONS_PREFKEY = 
            SearchEditorSaveAsDialog.class.getName() + ".saveOptions";

        @org.simpleframework.xml.Attribute(required = false)
        public String directory;

        @org.simpleframework.xml.Attribute(required = false)
        public String fileName;

        @org.simpleframework.xml.Attribute(required = false)
        public Boolean includeDocuments = true;

        @org.simpleframework.xml.Attribute(required = false)
        public Boolean includeClusters = true;

        @org.simpleframework.xml.Attribute(required = false)
        public Boolean includeAttributes = false;
        
        public String getFullPath()
        {
            Path path = FileDialogs.checkOrDefault(directory);
            return new File(path.toFile(), fileName).getAbsolutePath();
        }
        
        /**
         * Preserve save options in a global preference key.
         */
        public static SaveOptions getGlobalOrDefault()
        {
            try
            {
                String xml = 
                    WorkbenchCorePlugin.getPreferences().get(GLOBAL_OPTIONS_PREFKEY, null);
                if (xml != null)
                {
                    return SimpleXmlMemento.fromString(SaveOptions.class, xml);
                }
            }
            catch (IOException e)
            {
                // fall through
            }

            return new SaveOptions();
        }

        /**
         * Preserve save options in a global preference key.
         */
        public void saveGlobal()
        {
            try
            {
                WorkbenchCorePlugin.getPreferences().put(
                    GLOBAL_OPTIONS_PREFKEY, SimpleXmlMemento.toString(this));
            }
            catch (IOException e)
            {
                Utils.logError(e, false);
            }
        }
    }

    /**
     * Most recent save options.
     */
    private SaveOptions saveOptions;

    /**
     * Part property indicating current grouping of attributes on the
     * {@link PanelName#ATTRIBUTES}.
     */
    private static final String GROUPING_LOCAL = 
        PreferenceConstants.GROUPING_EDITOR_PANEL + ".local";

    /**
     * Global memento key.
     */
    private static final String GLOBAL_MEMENTO_KEY = SearchEditor.class + ".memento";

    /**
     * {@link SearchEditor} has several panels. These panels are identifier with constants
     * in this enum. Their visual attributes and preference keys are also configured here.
     * <p>
     * These panels are <b>required</b> by {@link SearchEditor} and you should not remove
     * any of these constants.
     */
    public static enum PanelName
    {
        CLUSTERS("Clusters", ClusterTreeView.ID), 
        DOCUMENTS("Documents", DocumentListView.ID), 
        ATTRIBUTES("Attributes", AttributeView.ID);

        /** Default name. */
        public final String name;

        /** Icon identifier. */
        public final String iconID;
        
        /** Visibility preference key. */
        public final String prefKeyVisibility;

        /** Width preference key. */
        public final String prefKeyWeight;

        private PanelName(String name, String iconID)
        {
            this.name = name;
            this.iconID = iconID;

            final String prefKey = PanelName.class.getName() + "." + name;
            this.prefKeyVisibility = prefKey + ".visibility";
            this.prefKeyWeight = prefKey + ".weight";
        }
    }    

    /**
     * All attributes of a single panel.
     */
    final static class PanelReference
    {
        private final Section section;
        private final int sashIndex;
        PanelState state;

        PanelReference(Section self, int sashIndex)
        {
            this.section = self;
            this.sashIndex = sashIndex;
        }
    }
    
    /**
     * Panel state.
     */
    @Root
    public final static class PanelState
    {
        @Element
        public int weight;
        
        @Element
        public boolean visibility;
    }

    /**
     * Panels inside the editor.
     */
    private EnumMap<PanelName, PanelReference> panels;

    /**
     * Search result model is the core model around which all other views revolve
     * (editors, views, actions). It can perform transformation of {@link SearchInput}
     * into a {@link ProcessingResult} and inform listeners about changes going on in the
     * model.
     */
    private SearchResult searchResult;

    /**
     * Resources to be disposed of in {@link #dispose()}.
     */
    private DisposeBin resources;

    /**
     * Image from the {@link SearchInput} used to run the query.
     */
    private Image sourceImage;

    /**
     * If <code>true</code>, then the editor's {@link #searchResult} contain a stale value
     * with regard to its input.
     */
    private boolean dirty = true;

    /*
     * GUI layout, state restoration.
     */

    private FormToolkit toolkit;
    private Form rootForm;
    private SashForm sashForm;

    /**
     * This editor's restore state.
     */
    private SearchEditorMemento state;

    /**
     * Selection handling.
     */
    private SearchEditorSelectionProvider selectionProvider;

    /**
     * There is only one {@link SearchJob} assigned to each editor. The job is
     * re-scheduled when re-processing is required.
     * 
     * @see #reprocess()
     */
    private SearchJob searchJob;

    /**
     * Auto-update listener calls {@link #reprocess()} after
     * {@link PreferenceConstants#AUTO_UPDATE} property changes.
     */
    private IAttributeListener autoUpdateListener = new AttributeListenerAdapter()
    {
        /** Postponable reschedule job. */
        private PostponableJob job = new PostponableJob(new UIJob("Auto update...")
        {
            public IStatus runInUIThread(IProgressMonitor monitor)
            {
                reprocess();
                return Status.OK_STATUS;
            }
        });

        public void valueChanged(AttributeEvent event)
        {
            final IPreferenceStore store = WorkbenchCorePlugin.getDefault().getPreferenceStore();
            if (store.getBoolean(PreferenceConstants.AUTO_UPDATE))
            {
                final int delay = store.getInt(PreferenceConstants.AUTO_UPDATE_DELAY);
                job.reschedule(delay);
            }
        }
    };

    /**
     * When auto-update key in the preference store changes, force re-processing in case
     * the editor is dirty.
     */
    private IPropertyChangeListener autoUpdateListener2 = new IPropertyChangeListener()
    {
        public void propertyChange(PropertyChangeEvent event)
        {
            if (PreferenceConstants.AUTO_UPDATE.equals(event.getProperty()))
            {
                if (isDirty())
                {
                    reprocess();
                }
            }
        }
    };

    /**
     * Attribute editors.
     */
    private AttributeGroups attributesPanel;

    /**
     * Create main GUI components, hook up events, schedule initial processing.
     */
    @Override
    public void createPartControl(Composite parent)
    {
        this.resources = new DisposeBin(WorkbenchCorePlugin.getDefault());

        sourceImage = getEditorInput().getImageDescriptor().createImage();
        resources.add(sourceImage);

        toolkit = new FormToolkit(parent.getDisplay());
        resources.add(toolkit);

        rootForm = toolkit.createForm(parent);
        rootForm.setText(getPartName());
        rootForm.setImage(getTitleImage());

        toolkit.decorateFormHeading(rootForm);

        sashForm = new SashForm(rootForm.getBody(), SWT.HORIZONTAL)
        {
            protected boolean onDragSash(Event event)
            {
                final boolean modified = super.onDragSash(event);
                if (modified)
                {
                    /*
                     * Update globally remembered weights.
                     */
                    final int [] weights = sashForm.getWeights();
                    for (PanelReference sr : panels.values())
                    {
                        sr.state.weight = weights[sr.sashIndex];
                    }

                    saveGlobalPanelsState(getPanelState());
                }
                return modified;
            }
        };
        toolkit.adapt(sashForm);

        final GridLayout layout = GridLayoutFactory.swtDefaults().margins(
            sashForm.SASH_WIDTH, sashForm.SASH_WIDTH).create();
        rootForm.getBody().setLayout(layout);

        createControls(sashForm);
        createActions();
        
        updatePartHeaders();
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        /*
         * Hook to post-update events.
         */
        this.searchResult.addListener(new SearchResultListenerAdapter()
        {
            public void processingResultUpdated(ProcessingResult result)
            {
                updatePartHeaders();
            }
        });

        /*
         * Create jobs and schedule initial processing after the editor is shown.
         */
        createJobs();

        getSite().getPage().addPartListener(new PartListenerAdapter()
        {
            public void partClosed(IWorkbenchPart part)
            {
                getSite().getPage().removePartListener(this);
            }

            public void partOpened(IWorkbenchPart part)
            {
                if (part == SearchEditor.this)
                {
                    reprocess();
                }
            }
        });
    }

    /**
     * Update part name and root form's title
     */
    private void updatePartHeaders()
    {
        final SearchResult searchResult = getSearchResult();
        final SearchInput input = getSearchResult().getInput();

        final String full = getFullInputTitle(searchResult);
        final String abbreviated = getAbbreviatedInputTitle(searchResult);

        setPartName(abbreviated);
        setTitleToolTip(full);

        /*
         * Add the number of documents and clusters to the root form's title.
         */
        final ProcessingResult result = searchResult.getProcessingResult();
        if (result != null)
        {
            final int documents = result.getDocuments().size();
            final int clusters = result.getClusters().size();

            rootForm.setText(abbreviated + " (" + pluralize(documents, "document")
                + " from " + componentName(input.getSourceId()) + ", "
                + pluralize(clusters, "cluster") + " from "
                + componentName(input.getAlgorithmId()) + ")");
        }
        else
        {
            rootForm.setText(abbreviated);
        }
    }

    /**
     * Returns the component label or id if not available
     */
    private String componentName(String componentId)
    {
        final ProcessingComponentDescriptor component = WorkbenchCorePlugin.getDefault()
            .getComponent(componentId);

        if (component != null && !StringUtils.isEmpty(component.getLabel()))
        {
            return component.getLabel();
        }

        return componentId;
    }

    /**
     * Pluralize a given number.
     */
    private String pluralize(int value, String title)
    {
        return Integer.toString(value) + " " + title + (value != 1 ? "s" : "");
    }

    /**
     * Abbreviates the input's title (and adds an ellipsis at end if needed).
     */
    private String getAbbreviatedInputTitle(SearchResult searchResult)
    {
        final int MAX_WIDTH = 40;
        return StringUtils.abbreviate(getFullInputTitle(searchResult), MAX_WIDTH);
    }

    /**
     * Attempts to construct an input title from either query attribute or attributes
     * found in processing results.
     */
    private String getFullInputTitle(SearchResult searchResult)
    {
        /*
         * Initially, set to dummy name.
         */
        String title = ObjectUtils.toString(this.searchResult.getInput().getAttribute(
            AttributeNames.QUERY), null);

        /*
         * If we have a processing result...
         */
        if (searchResult.hasProcessingResult())
        {
            final ProcessingResult result = searchResult.getProcessingResult();

            /*
             * Check if there is a query in the output attributes (may be different from
             * the one set on input).
             */

            title = ObjectUtils.toString(
                result.getAttributes().get(AttributeNames.QUERY), null);

            /*
             * Override with custom title, if present.
             */

            title = ObjectUtils.toString(result.getAttributes().get(
                AttributeNames.PROCESSING_RESULT_TITLE), title);
        }

        if (StringUtils.isEmpty(title))
        {
            title = "(empty query)";
        }

        return title;
    }

    /*
     * 
     */
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        if (!(input instanceof SearchInput)) throw new PartInitException(
            "Invalid input: must be an instance of: " + SearchInput.class.getName());

        setSite(site);
        setInput(input);

        /*
         * Set default local grouping if not already restored. We must set it here because
         * it is used to create components later (and before restoreState()).
         */
        if (StringUtils.isEmpty(getPartProperty(GROUPING_LOCAL)))
        {
            final IPreferenceStore preferenceStore = WorkbenchCorePlugin.getDefault()
                .getPreferenceStore();

            setPartProperty(GROUPING_LOCAL, preferenceStore
                .getString(PreferenceConstants.GROUPING_EDITOR_PANEL));
        }

        this.searchResult = new SearchResult((SearchInput) input);
    }

    /*
     * 
     */
    @Override
    public Image getTitleImage()
    {
        return sourceImage;
    }

    /*
     * 
     */
    @Override
    public void setFocus()
    {
        rootForm.setFocus();
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        this.resources.dispose();
        super.dispose();
    }

    /*
     *
     */
    public void saveState(IMemento memento)
    {
        if (memento == null) return;

        try
        {
            final SearchEditorMemento state = new SearchEditorMemento();
            state.panels = getPanelState(); 
            state.sectionsExpansionState = this.attributesPanel.getExpansionStates();
            state.saveOptions = this.saveOptions;
            SimpleXmlMemento.addChild(memento, state);
        }
        catch (IOException e)
        {
            Utils.logError(e, false);
        }
    }

    /*
     * 
     */
    public void restoreState(IMemento memento)
    {
        if (memento == null) return;

        try
        {
            this.state = SimpleXmlMemento.getChild(SearchEditorMemento.class, memento);
        }
        catch (IOException e)
        {
            Utils.logError(e, false);
        }
    }

    /**
     * Save this editor's state as global.
     */
    void saveAsGlobalState()
    {
        saveGlobalPanelsState(getPanelState());

        // Save global sections expansion state (serialized).
        this.state = new SearchEditorMemento();
        this.state.panels = getPanelState();
        this.state.sectionsExpansionState = this.attributesPanel.getExpansionStates();
        SimpleXmlMemento.toPreferenceStore(GLOBAL_MEMENTO_KEY, state);
    }

    /**
     * Save the given panels state as global state. 
     */
    public static void saveGlobalPanelsState(Map<PanelName, PanelState> globalState)
    {
        final IPreferenceStore prefStore = 
            WorkbenchCorePlugin.getDefault().getPreferenceStore();

        for (Map.Entry<PanelName, PanelState> e : globalState.entrySet())
        {
            prefStore.setValue(e.getKey().prefKeyVisibility, e.getValue().visibility);
            prefStore.setValue(e.getKey().prefKeyWeight, e.getValue().weight);
        }
    }

    /**
     * Restore global state shared among editors. 
     */
    private static SearchEditorMemento restoreGlobalState()
    {
        SearchEditorMemento memento = SimpleXmlMemento.fromPreferenceStore(
            SearchEditorMemento.class, GLOBAL_MEMENTO_KEY);

        if (memento == null)
        {
            memento = new SearchEditorMemento();
            memento.sectionsExpansionState = Maps.newHashMap();            
        }

        final IPreferenceStore prefStore = 
            WorkbenchCorePlugin.getDefault().getPreferenceStore();
        final Map<PanelName, PanelState> panels = Maps.newEnumMap(PanelName.class);
        for (PanelName n : EnumSet.allOf(PanelName.class))
        {
            final PanelState s = new PanelState();
            s.visibility = prefStore.getBoolean(n.prefKeyVisibility);
            s.weight = prefStore.getInt(n.prefKeyWeight);
            panels.put(n, s);
        }
        memento.panels = panels;

        return memento;
    }

    /*
     * 
     */
    private void restoreState()
    {
        /*
         * Restore from global state, if possible.
         */
        final SearchEditorMemento globalState = restoreGlobalState();
        for (Map.Entry<PanelName, PanelState> e : globalState.panels.entrySet())
        {
            panels.get(e.getKey()).state = e.getValue();
        }
        this.attributesPanel.setExpanded(globalState.sectionsExpansionState);

        /*
         * Restore state from this editor's memento, if possible.
         */
        if (state != null)
        {
            for (Map.Entry<PanelName, PanelState> e : state.panels.entrySet())
            {
                panels.get(e.getKey()).state = e.getValue();
            }
            this.attributesPanel.setExpanded(state.sectionsExpansionState);
            this.saveOptions = state.saveOptions;
        }

        /*
         * Update weights and visibility.
         */
        final int [] weights = sashForm.getWeights();
        for (Map.Entry<PanelName, PanelReference> e : panels.entrySet())
        {
            final PanelReference p = e.getValue();
            weights[p.sashIndex] = p.state.weight;
            setPanelVisibility(e.getKey(), p.state.visibility);
        }
        sashForm.setWeights(weights);
    }
    
    /**
     * Returns the current state of all editor panels.
     */
    Map<PanelName, PanelState> getPanelState()
    {
        final HashMap<PanelName, PanelState> m = Maps.newHashMap();
        for (Map.Entry<PanelName, PanelReference> e : panels.entrySet())
        {
            m.put(e.getKey(), e.getValue().state);
        }
        return m;
    }

    /*
     * 
     */
    public void doSave(IProgressMonitor monitor)
    {
        if (saveOptions == null)
        {
            doSaveAs();
            return;
        }

        doSave(saveOptions);
    }

    /**
     * Show a dialog prompting for file name and options and save the result to an XML
     * file.
     */
    public void doSaveAs()
    {
        if (isDirty() || this.searchJob.getState() == Job.RUNNING)
        {
            final MessageDialog dialog = new MessageDialog(getEditorSite().getShell(),
                "Modified parameters", null, "Search parameters"
                    + " have been changed. Save stale results?", MessageDialog.WARNING,
                new String []
                {
                    IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL
                }, 0);

            if (dialog.open() == MessageDialog.CANCEL)
            {
                return;
            }
        }

        SaveOptions newOptions = saveOptions;
        if (newOptions == null)
        {
            newOptions = SaveOptions.getGlobalOrDefault();
            newOptions.fileName = 
                FileDialogs.sanitizeFileName(getFullInputTitle(getSearchResult())) + ".xml";
        }

        final Shell shell = this.getEditorSite().getShell();
        if (new SearchEditorSaveAsDialog(shell, newOptions).open() == Window.OK)
        {
            this.saveOptions = newOptions;
            doSave(saveOptions);
        }
    }

    /**
     * 
     */
    private void doSave(SaveOptions options)
    {
        final ProcessingResult result = getSearchResult().getProcessingResult();
        if (result == null)
        {
            Utils.showError(new Status(Status.ERROR, WorkbenchCorePlugin.PLUGIN_ID,
                "No search result yet."));
            return;
        }

        final IAction saveAction = new SaveAsXMLActionDelegate(result, options);
        final Job job = new Job("Saving search result...")
        {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
                saveAction.run();
                return Status.OK_STATUS;
            }
        };
        job.setPriority(Job.SHORT);
        job.schedule();
    }

    /*
     * 
     */
    public boolean isSaveAsAllowed()
    {
        return true;
    }

    /*
     * Don't require save-on-close.
     */
    @Override
    public boolean isSaveOnCloseNeeded()
    {
        return false;
    }

    /*
     * 
     */
    @Override
    public boolean isDirty()
    {
        return dirty;
    }

    /**
     * Mark the editor status as dirty (input parameters changed, for example).
     */
    private void setDirty(boolean value)
    {
        this.dirty = value;
        firePropertyChange(PROP_DIRTY);
    }

    /**
     * {@link SearchEditor} adaptations.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter)
    {
        return super.getAdapter(adapter);
    }

    /**
     * Shows or hides a given panel.
     */
    public void setPanelVisibility(PanelName panel, boolean visible)
    {
        panels.get(panel).state.visibility = visible;
        panels.get(panel).section.setVisible(visible);
        sashForm.layout();
    }

    /**
     * Schedule a re-processing job to update {@link #searchResult}.
     */
    public void reprocess()
    {
        /*
         * There is a race condition between the search job and the dirty flag. The editor
         * becomes 'clean' when the search job is initiated, so that further changes of
         * parameters will simply re-schedule another job after the one started before
         * ends. This may lead to certain inconsistencies between the view and the
         * attributes (temporal), but is much simpler than trying to pool/ cache/ stack
         * dirty tokens and manage them in synchronization with running jobs.
         */
        setDirty(false);
        searchJob.schedule();
    }

    /**
     * 
     */
    public SearchResult getSearchResult()
    {
        return searchResult;
    }

    /**
     * Add actions to the root form's toolbar.
     */
    private void createActions()
    {
        final IToolBarManager toolbar = rootForm.getToolBarManager();

        // Choose visible panels.
        final IAction selectPanelsAction = new SearchEditorPanelsAction(
            "Choose visible panels", this);
        toolbar.add(selectPanelsAction);

        toolbar.update(true);
    }

    /**
     * Update grouping state of the {@link #attributesPanel} and reset its editor values.
     */
    private void updateGroupingState(GroupingMethod grouping)
    {
        attributesPanel.setGrouping(grouping);
        attributesPanel.setAttributes(getSearchResult().getInput().getAttributeValueSet()
            .getAttributeValues());
    }

    /**
     * Create internal panels and hook up listener infrastructure.
     */
    private void createControls(SashForm parent)
    {
        /*
         * Create and add panels in order of their declaration in the enum type.
         */
        this.panels = Maps.newEnumMap(PanelName.class);

        int index = 0;
        for (final PanelName s : EnumSet.allOf(PanelName.class))
        {
            final Section section;
            switch (s)
            {
                case CLUSTERS:
                    section = createClustersPart(parent, getSite());
                    break;

                case DOCUMENTS:
                    section = createDocumentsPart(parent, getSite());
                    break;

                case ATTRIBUTES:
                    section = createAttributesPart(parent, getSite());
                    break;

                default:
                    throw new RuntimeException("Unknown section: " + s);
            }

            final PanelReference sr = new PanelReference(section, index++);
            panels.put(s, sr);
        }

        /*
         * Set up selection event forwarding.
         */
        this.selectionProvider = new SearchEditorSelectionProvider(this);
        this.getSite().setSelectionProvider(selectionProvider);

        final ClusterTree tree = 
            (ClusterTree) panels.get(PanelName.CLUSTERS).section.getClient();

        /* Link bidirectional selection synchronization. */
        new ClusterTreeSelectionAdapter(selectionProvider, tree);

        /*
         * Set up an event callback making editor dirty when attributes change.
         */
        this.getSearchResult().getInput().addAttributeListener(
            new AttributeListenerAdapter()
            {
                public void valueChanged(AttributeEvent event)
                {
                    setDirty(true);
                }
            });

        /*
         * Set up an event callback to spawn auto-update jobs on changes to attributes.
         */
        resources.registerAttributeChangeListener(this.getSearchResult().getInput(),
            autoUpdateListener);

        /*
         * Set up an event callback to restart processing after auto-update is enabled and
         * the editor is dirty.
         */
        resources.registerPropertyChangeListener(WorkbenchCorePlugin.getDefault()
            .getPreferenceStore(), autoUpdateListener2);

        /*
         * Install a synchronization agent between the current selection in the editor and
         * the document list panel.
         */
        final DocumentList documentList = 
            (DocumentList) panels.get(PanelName.DOCUMENTS).section.getClient();
        new DocumentListSelectionAdapter(selectionProvider, documentList, this);

        /*
         * Restore state information.
         */
        restoreState();
    }

    /*
     * 
     */
    private Section createSection(Composite parent)
    {
        return toolkit.createSection(parent, ExpandableComposite.EXPANDED
            | ExpandableComposite.TITLE_BAR);
    }

    /**
     * Create internal panel with the list of clusters (if present).
     */
    private Section createClustersPart(Composite parent, IWorkbenchSite site)
    {
        final PanelName section = PanelName.CLUSTERS;
        final Section sec = createSection(parent);
        sec.setText(section.name);

        final ClusterTree clustersTree = new ClusterTree(sec, SWT.NONE);
        resources.add(clustersTree);

        /*
         * Hook the clusters tree to search result's events.
         */
        this.searchResult.addListener(new SearchResultListenerAdapter()
        {
            public void processingResultUpdated(ProcessingResult result)
            {
                final List<Cluster> clusters = result.getClusters();
                if (clusters != null && clusters.size() > 0)
                {
                    clustersTree.show(clusters);
                }
                else
                {
                    clustersTree.show(Collections.<Cluster> emptyList());
                }
            }
        });

        sec.setClient(clustersTree);

        // Add expand/collapse action to the toolbar.
        final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
        final ToolBar toolbar = toolBarManager.createControl(sec);

        toolBarManager.add(new ActionDelegateProxy(
            new ClusterTreeExpanderAction(
                ClusterTreeExpanderAction.CollapseAction.EXPAND, 
                clustersTree, this.getSearchResult()), 
            IAction.AS_PUSH_BUTTON));

        toolBarManager.add(new ActionDelegateProxy(
            new ClusterTreeExpanderAction(
                ClusterTreeExpanderAction.CollapseAction.COLLAPSE,
                clustersTree, this.getSearchResult()), 
            IAction.AS_PUSH_BUTTON));

        toolBarManager.update(true);
        sec.setTextClient(toolbar);

        return sec;
    }

    /**
     * Create internal panel with the document list.
     */
    private Section createDocumentsPart(Composite parent, IWorkbenchSite site)
    {
        final PanelName section = PanelName.DOCUMENTS;
        final Section sec = createSection(parent);
        sec.setText(section.name);

        final DocumentList documentList = new DocumentList(sec, SWT.NONE);
        resources.add(documentList);

        /*
         * Hook the document list to search result's events.
         */
        this.searchResult.addListener(new SearchResultListenerAdapter()
        {
            public void processingResultUpdated(ProcessingResult result)
            {
                documentList.show(result);
            }
        });

        sec.setClient(documentList);
        return sec;
    }

    /**
     * Create internal panel with the set of algorithm component's attributes.
     */
    private Section createAttributesPart(Composite parent, IWorkbenchSite site)
    {
        final PanelName section = PanelName.ATTRIBUTES;
        final Section sec = createSection(parent);
        sec.setText(section.name);

        final BindableDescriptor descriptor = getAlgorithmDescriptor();

        final CScrolledComposite scroller = new CScrolledComposite(sec, SWT.H_SCROLL
            | SWT.V_SCROLL);
        resources.add(scroller);

        final Composite spacer = GUIFactory.createSpacer(scroller);
        resources.add(spacer);

        final String groupingValue = getPartProperty(GROUPING_LOCAL);
        final GroupingMethod grouping = GroupingMethod.valueOf(groupingValue);
        final Map<String, Object> defaultValues = getSearchResult().getInput()
            .getAttributeValueSet().getAttributeValues();

        attributesPanel = new AttributeGroups(spacer, descriptor, grouping, null,
            defaultValues);
        attributesPanel.setLayoutData(GridDataFactory.fillDefaults().grab(true, true)
            .create());
        resources.add(attributesPanel);

        toolkit.paintBordersFor(scroller);
        toolkit.adapt(scroller);
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(false);
        scroller.setContent(spacer);

        /*
         * Link attribute value changes: attribute panel -> search result
         */
        final IAttributeListener panelToEditorSync = new AttributeListenerAdapter()
        {
            public void valueChanged(AttributeEvent event)
            {
                getSearchResult().getInput().setAttribute(event.key, event.value);
            }
        };
        attributesPanel.addAttributeListener(panelToEditorSync);

        /*
         * Link attribute value changes: search result -> attribute panel
         */
        final IAttributeListener editorToPanelSync = new AttributeListenerAdapter()
        {
            public void valueChanged(AttributeEvent event)
            {
                /*
                 * temporarily unsubscribe from events from the attributes list to avoid
                 * event looping.
                 */
                attributesPanel.removeAttributeListener(panelToEditorSync);
                attributesPanel.setAttribute(event.key, event.value);
                attributesPanel.addAttributeListener(panelToEditorSync);
            }
        };
        getSearchResult().getInput().addAttributeListener(editorToPanelSync);

        /*
         * Add toolbar actions.
         */
        final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
        final ToolBar toolbar = toolBarManager.createControl(sec);

        // Auto update.
        final IWorkbenchWindow window = getSite().getWorkbenchWindow();
        toolBarManager.add(WorkbenchActionFactory.AUTO_UPDATE_ACTION.create(window));

        // Attribute grouping.
        final IAction attributesAction = new GroupingMethodAction(GROUPING_LOCAL, this);
        toolBarManager.add(attributesAction);
        
        // Save/ load attributes.
        final IAction saveLoadAction = new SaveAlgorithmAttributesAction(getSearchResult().getInput());
        toolBarManager.add(saveLoadAction);

        toolBarManager.update(true);
        sec.setTextClient(toolbar);

        /*
         * Update global preferences when local change.
         */
        final String globalPreferenceKey = PreferenceConstants.GROUPING_EDITOR_PANEL;
        addPartPropertyListener(new PropertyChangeListenerAdapter(GROUPING_LOCAL)
        {
            protected void propertyChangeFiltered(PropertyChangeEvent event)
            {
                final IPreferenceStore prefStore = WorkbenchCorePlugin.getDefault()
                    .getPreferenceStore();

                final String currentValue = getPartProperty(GROUPING_LOCAL);
                prefStore.setValue(globalPreferenceKey, currentValue);

                updateGroupingState(GroupingMethod.valueOf(currentValue));
                Utils.adaptToFormUI(toolkit, attributesPanel);

                if (!panels.get(PanelName.ATTRIBUTES).state.visibility)
                {
                    setPanelVisibility(PanelName.ATTRIBUTES, true);
                }
            }
        });
        
        /*
         * Perform GUI adaptations.
         */
        Utils.adaptToFormUI(toolkit, attributesPanel);
        Utils.adaptToFormUI(toolkit, scroller);

        sec.setClient(scroller);
        return sec;
    }

    /**
     * Get hold of the algorithm instance, extract its attribute descriptors.
     */
    BindableDescriptor getAlgorithmDescriptor()
    {
        final WorkbenchCorePlugin core = WorkbenchCorePlugin.getDefault();
        final String algorithmID = getSearchResult().getInput().getAlgorithmId();

        BindableDescriptor componentDescriptor = core.getComponentDescriptor(algorithmID);
        if (componentDescriptor == null) {
            throw new RuntimeException("No descriptor for algorithm: " + algorithmID);
        }
        return componentDescriptor.only(Input.class,
            Processing.class).only(Predicates.not(new InternalAttributePredicate(false)));
    }

    /**
     * Creates reusable jobs used in the editor.
     */
    private void createJobs()
    {
        /*
         * Create search job.
         */

        final String title = getAbbreviatedInputTitle(searchResult);
        this.searchJob = new SearchJob("Searching for '" + title + "'...", searchResult);

        // Try to push search jobs into the background, if possible.
        this.searchJob.setPriority(Job.DECORATE);

        /*
         * Add a job listener to update root form's busy state.
         */
        searchJob.addJobChangeListener(new JobChangeAdapter()
        {
            @Override
            public void aboutToRun(IJobChangeEvent event)
            {
                setBusy(true);
            }

            @Override
            public void done(IJobChangeEvent event)
            {
                setBusy(false);
            }

            private void setBusy(final boolean busy)
            {
                Utils.asyncExec(new Runnable()
                {
                    public void run()
                    {
                        if (!rootForm.isDisposed())
                        {
                            rootForm.setBusy(busy);
                        }
                    }
                });
            }
        });
    }

    /*
     * 
     */
    @SuppressWarnings("unused")
    private IToolBarManager createToolbarManager(Section section)
    {
        ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
        ToolBar toolbar = toolBarManager.createControl(section);

        final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
        toolbar.setCursor(handCursor);

        // Cursor needs to be explicitly disposed
        toolbar.addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent e)
            {
                if ((handCursor != null) && (handCursor.isDisposed() == false))
                {
                    handCursor.dispose();
                }
            }
        });

        section.setTextClient(toolbar);
        return toolBarManager;
    }
}
