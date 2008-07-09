package org.carrot2.workbench.core.ui;

import java.io.File;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.Cluster;
import org.carrot2.core.ProcessingComponent;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.util.attribute.BindableDescriptorBuilder;
import org.carrot2.util.attribute.Input;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.DisposeBin;
import org.carrot2.workbench.core.helpers.PostponableJob;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.ui.actions.SaveAsXMLActionDelegate;
import org.carrot2.workbench.editors.AttributeChangedEvent;
import org.carrot2.workbench.editors.IAttributeListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableEditor;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.progress.UIJob;

/**
 * Editor accepting {@link SearchInput} and performing operations on it. The editor also
 * exposes the model of processing results.
 */
public final class SearchEditor extends EditorPart implements IPersistableEditor,
    IPostSelectionProvider
{
    /**
     * Public identifier of this editor.
     */
    public static final String ID = "org.carrot2.workbench.core.editors.searchEditor";

    /**
     * Options required for {@link #doSave(IProgressMonitor)}.
     */
    public static final class SaveOptions implements Cloneable
    {
        public String directory;
        public String fileName;
        
        public boolean includeDocuments = true;
        public boolean includeClusters = true;

        public String getFullPath()
        {
            return new File(new File(directory), fileName).getAbsolutePath();
        }

        public static String sanitizeFileName(String anything)
        {
            String result = anything.replaceAll("[^a-zA-Z0-9_\\-.\\s]", "");
            result = result.trim().replaceAll("[\\s]+", "-");
            result = result.toLowerCase();
            if (StringUtils.isEmpty(result))
            {
                result = "unnamed";
            }
            return result;
        }
    }

    /**
     * A public event identifier related to auto-update property.
     * 
     * @see #isAutoUpdate()
     */
    public static final int PROP_AUTO_UPDATE = 0;

    /**
     * Most recent save options.
     */
    private SaveOptions saveOptions;

    /*
     * Memento attributes and sections.
     */

    private static final String MEMENTO_SECTIONS = "sections";
    private static final String MEMENTO_SECTION = "section";
    private static final String SECTION_NAME = "name";
    private static final String SECTION_WEIGHT = "weight";
    private static final String SECTION_VISIBLE = "visible";
    private static final String SECTION_AUTO_UPDATE = "auto-update";

    /**
     * Sections (panels) present inside the editor.
     */
    private EnumMap<SearchEditorSections, Section> sections;

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
     * Synchronization between {@link DocumentList} and the current workbench's selection.
     */
    private DocumentListSelectionSync documentListSelectionSync;

    /**
     * Image from the {@link SearchInput} used to run the query.
     */
    private Image sourceImage;

    /**
     * If <code>true</code>, then the editor's {@link #searchResult} contain
     * a stale value with regard to its input.
     */
    private boolean dirty = true;

    /*
     * GUI layout, state restoration.
     */

    private FormToolkit toolkit;
    private Form rootForm;
    private SashForm sashForm;

    private IMemento state;

    /**
     * {@link SearchEditor} forwards its selection provider methods to this component 
     * ({@link SearchEditorSections#CLUSTERS} panel).
     */
    private IPostSelectionProvider selectionProvider;

    /**
     * There is only one {@link SearchJob} assigned to each editor. The job
     * is re-scheduled when re-processing is required.
     * 
     * @see #reprocess()
     */
    private SearchJob searchJob;

    /**
     * If <code>true</code>, then changes on attributes automatically trigger re-processing
     * of the search result.
     */
    private boolean autoUpdate = true;

    /**
     * Auto-update listener calls {@link #reprocess()} after attributes change.
     */
    private IAttributeListener autoUpdateListener = new IAttributeListener()
    {
        /*
         * TODO: [CARROT-276] Make this setting a global preference. 
         */
        private final int AUTO_UPDATE_DELAY = 1000;

        /** Postponable reschedule job. */
        private PostponableJob job = new PostponableJob(new UIJob("Auto update...") {
            public IStatus runInUIThread(IProgressMonitor monitor)
            {
                reprocess();
                return Status.OK_STATUS;
            }
        });

        public void attributeChange(AttributeChangedEvent event)
        {
            if (isAutoUpdate())
            {
                job.reschedule(AUTO_UPDATE_DELAY);
            }
        }
    };

    /*
     * 
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
        final GridLayout layout = GridLayoutFactory.swtDefaults().create();
        rootForm.getBody().setLayout(layout);

        rootForm.setText(getPartName());
        rootForm.setImage(getTitleImage());

        toolkit.decorateFormHeading(rootForm);

        sashForm = new SashForm(rootForm.getBody(), SWT.HORIZONTAL);
        toolkit.adapt(sashForm);

        createControls(sashForm);
        updatePartHeaders();

        sashForm.SASH_WIDTH = 5;
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createActions();

        /*
         * Hook to post-update events.
         */
        final ISearchResultListener rootFormTitleUpdater = new SearchResultListenerAdapter()
        {
            public void processingResultUpdated(ProcessingResult result)
            {
                updatePartHeaders();
            }
        };
        this.searchResult.addListener(rootFormTitleUpdater);
        
        /*
         * Create jobs and schedule initial processing.
         */
        createJobs();
        reprocess();
    }

    /**
     * Update part name and root form's title
     */
    private void updatePartHeaders()
    {
        final String full = getFullInputTitle(getSearchResult().getInput());
        final String abbreviated = getAbbreviatedInputTitle(getSearchResult().getInput());

        setPartName(abbreviated);
        setTitleToolTip(full);

        rootForm.setText(abbreviated);
    }

    /**
     * Abbreviates the input's title (and adds an ellipsis at end if needed).
     */
    private String getAbbreviatedInputTitle(SearchInput input)
    {
        final int MAX_WIDTH = 40;
        return StringUtils.abbreviate(getFullInputTitle(input), MAX_WIDTH);
    }

    /**
     * Attempts to construct an input title from either query attribute
     * or attributes found in processing results.
     */
    private String getFullInputTitle(SearchInput input)
    {
        Object query = this.searchResult.getInput().getAttribute(AttributeNames.QUERY);

        if (query == null)
        {
            final ProcessingResult result = this.searchResult.getProcessingResult();
            if (result != null)
            {
                query = this.searchResult.getProcessingResult().getAttributes().get(
                    AttributeNames.QUERY);
            }
        }

        if (query != null)
        {
            query = query.toString();
        }
        else
        {
            query = "(empty query)";
        }

        return query.toString();
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

        this.searchResult = new SearchResult((SearchInput) input);
    }

    @Override
    public Image getTitleImage()
    {
        return sourceImage;
    }

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
        this.removePostSelectionChangedListener(documentListSelectionSync);
        this.resources.dispose();
        super.dispose();
    }

    /*
     * 
     */
    public void saveState(IMemento memento)
    {
        if (memento != null)
        {
            memento.putString(SECTION_AUTO_UPDATE, Boolean.toString(autoUpdate));

            final IMemento sectionsMemento = memento.createChild(MEMENTO_SECTIONS);
            final int [] weights = this.sashForm.getWeights();
            int i = 0;
            for (SearchEditorSections section : EnumSet.allOf(SearchEditorSections.class))
            {
                final IMemento sectionMemento = sectionsMemento
                    .createChild(MEMENTO_SECTION);
                sectionMemento.putString(SECTION_NAME, section.name());
                sectionMemento.putInteger(SECTION_WEIGHT, weights[i++]);
                sectionMemento.putString(SECTION_VISIBLE, sections.get(section).getData(
                    "visible").toString());
            }
        }
    }

    /*
     * 
     */
    private void restoreState(EnumMap<SearchEditorSections, Integer> weights)
    {
        if (state != null && state.getChild(MEMENTO_SECTIONS) != null)
        {
            this.autoUpdate = Boolean.valueOf(state.getString(SECTION_AUTO_UPDATE)); 
            
            final IMemento sectionsMemento = state.getChild(MEMENTO_SECTIONS);
            for (IMemento sectionMemento : sectionsMemento.getChildren(MEMENTO_SECTION))
            {
                final SearchEditorSections section = SearchEditorSections
                    .valueOf(sectionMemento.getString(SECTION_NAME));
                final int weight = sectionMemento.getInteger(SECTION_WEIGHT);
                final boolean visible = Boolean.valueOf(sectionMemento
                    .getString(SECTION_VISIBLE));

                this.toogleSectionVisibility(section, visible);
                weights.put(section, weight);
            }
        }
    }

    /*
     * 
     */
    public void restoreState(IMemento memento)
    {
        state = memento;
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
     * Show a dialog prompting for file name and options and save the result to
     * an XML file. 
     */
    public void doSaveAs()
    {
        if (isDirty() || this.searchJob.getState() == Job.RUNNING)
        {
            final MessageDialog dialog = new MessageDialog(getEditorSite().getShell(),
                "Modified parameters", null, "Search parameters" +
                		" have been changed. Save stale results?", MessageDialog.WARNING,
                		new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);

            if (dialog.open() == MessageDialog.CANCEL)
            {
                return;
            }
        }

        SaveOptions newOptions = saveOptions;
        if (newOptions == null)
        {
            newOptions = new SaveOptions();
            newOptions.fileName = SaveOptions.sanitizeFileName(
                getFullInputTitle(getSearchResult().getInput())) + ".xml";
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
     * Returns the current auto-update property value.
     */
    public boolean isAutoUpdate()
    {
        return this.autoUpdate;
    }

    /**
     * Sets the new auto-update property value. Note that setting
     * auto-update to <code>true</code> will cause re-processing
     * if the editor is in the dirty state.
     */
    public void setAutoUpdate(boolean autoUpdate)
    {
        if (this.autoUpdate == autoUpdate)
            return;

        this.autoUpdate = autoUpdate;

        if (isDirty() && autoUpdate)
        {
            reprocess();
        }

        firePropertyChange(PROP_AUTO_UPDATE);
    }

    /**
     * {@link SearchEditor} adaptations.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter(Class adapter)
    {
        return super.getAdapter(adapter);
    }

    /**
     * Returns a map of this editor's panels ({@link Section}s).
     */
    EnumMap<SearchEditorSections, Section> getSections()
    {
        return sections;
    }

    /*
     * 
     */
    void toogleSectionVisibility(SearchEditorSections section, boolean visible)
    {
        sections.get(section).setVisible(visible);
        sections.get(section).setData("visible", visible);
        sashForm.layout();
    }

    /**
     * Schedule a re-processing job to update {@link #searchResult}.
     */
    public void reprocess()
    {
        /*
         * There is a race condition between the search job and the dirty flag. The editor
         * becomes 'clean' when the search job is initiated, so that further changes of parameters
         * will simply re-schedule another job after the one started before ends.
         * 
         * This may lead to certain inconsistencies between the view
         * and the attributes (temporal), but is much simpler than 
         * trying to pool/ cache/ stack dirty tokens and manage them 
         * in synchronization with running jobs.
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

    /*
     * 
     */
    private void createActions()
    {
        final IToolBarManager toolbar = rootForm.getToolBarManager();

        /*
         * TODO: Add toggle buttons instead of a menu here?
         */

        final IAction selectSectionsAction = new SearchEditorPanelSelectorAction(
            "Choose visible panels", this);
        toolbar.add(selectSectionsAction);
        toolbar.update(true);
    }

    /**
     * Create internal panels and hook up listener infrastructure.
     */
    private void createControls(SashForm parent)
    {
        final IPreferenceStore preferenceStore = WorkbenchCorePlugin.getDefault()
            .getPreferenceStore();

        this.sections = new EnumMap<SearchEditorSections, Section>(
            SearchEditorSections.class);

        /*
         * Create and add sections in order of their declaration in the enum type.
         */
        for (SearchEditorSections s : EnumSet.allOf(SearchEditorSections.class))
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
                    throw new RuntimeException("Unhandled section: " + s);
            }

            sections.put(s, section);
            toogleSectionVisibility(s, preferenceStore.getBoolean(s.defaultVisibility));
        }

        /*
         * Set up selection event forwarding. Install the editor as selection provider for
         * the part.
         */
        final ClusterTree tree = (ClusterTree) getSections().get(
            SearchEditorSections.CLUSTERS).getClient();

        this.selectionProvider = tree;
        this.getSite().setSelectionProvider(this);

        /*
         * Set up an event callback making editor dirty when attributes change. 
         */
        this.getSearchResult().getInput().addAttributeChangeListener(new IAttributeListener() {
            public void attributeChange(AttributeChangedEvent event)
            {
                setDirty(true);
            }
        });

        /*
         * Set up an event callback to spawn auto-update jobs on changes to attributes. This may
         * look a bit over-the-top, but it's a pattern taken from Eclipse SDK... 
         */
        this.getSearchResult().getInput().addAttributeChangeListener(autoUpdateListener);

        /*
         * Install a synchronization agent between the current selection in the editor and
         * the document list panel.
         */
        final DocumentList documentList = (DocumentList) getSections().get(
            SearchEditorSections.DOCUMENTS).getClient();
        documentListSelectionSync = new DocumentListSelectionSync(documentList, this);
        this.addPostSelectionChangedListener(documentListSelectionSync);

        /*
         * Assign default weights.
         */
        final EnumMap<SearchEditorSections, Integer> weights = new EnumMap<SearchEditorSections, Integer>(
            SearchEditorSections.class);

        for (SearchEditorSections s : EnumSet.allOf(SearchEditorSections.class))
        {
            weights.put(s, s.weight);
        }

        /*
         * Restore state information.
         */
        restoreState(weights);
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
        final SearchEditorSections section = SearchEditorSections.CLUSTERS;
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
        return sec;
    }

    /**
     * Create internal panel with the document list.
     */
    private Section createDocumentsPart(Composite parent, IWorkbenchSite site)
    {
        final SearchEditorSections section = SearchEditorSections.DOCUMENTS;
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
    @SuppressWarnings("unchecked")
    private Section createAttributesPart(Composite parent, IWorkbenchSite site)
    {
        final SearchEditorSections section = SearchEditorSections.ATTRIBUTES;
        final Section sec = createSection(parent);
        sec.setText(section.name);

        final BindableDescriptor descriptor = getAlgorithmDescriptor();
        final AttributeEditorGroups attributesList = new AttributeEditorGroups(sec, descriptor);
        resources.add(attributesList);

        /*
         * Link attribute value changes:
         * attribute panel -> search result
         */
        final IAttributeListener panelToEditorSync = new IAttributeListener() {
            public void attributeChange(AttributeChangedEvent event)
            {
                getSearchResult().getInput().setAttribute(event.key, event.value);
            }
        };
        attributesList.addAttributeChangeListener(panelToEditorSync);

        /*
         * Link attribute value changes:
         * search result -> attribute panel
         */
        final IAttributeListener editorToPanelSync = new IAttributeListener() {
            public void attributeChange(AttributeChangedEvent event)
            {
                /*
                 * temporarily unsubscribe from events from the attributes
                 * list to avoid event looping.
                 */
                attributesList.removeAttributeChangeListener(panelToEditorSync);
                attributesList.setAttribute(event.key, event.value);
                attributesList.addAttributeChangeListener(panelToEditorSync);
            }
        };
        getSearchResult().getInput().addAttributeChangeListener(editorToPanelSync);

        /*
         * Perform GUI adaptations.
         */
        toolkit.adapt(attributesList);
        toolkit.paintBordersFor(attributesList);
        Utils.adaptToFormUI(toolkit, attributesList);
        
        sec.setClient(attributesList);
        return sec;
    }

    /**
     * Get hold of the algorithm instance, extract its attribute descriptors.
     */
    @SuppressWarnings("unchecked")
    BindableDescriptor getAlgorithmDescriptor()
    {
        final WorkbenchCorePlugin core = WorkbenchCorePlugin.getDefault();
        final String algorithmID = getSearchResult().getInput().getAlgorithmId();

        final ProcessingComponent component = core.getAlgorithms().getImplementation(
            algorithmID).getInstance();

        return BindableDescriptorBuilder.buildDescriptor(component)
            .only(Input.class, Processing.class)
            .not(Internal.class);
    }

    /**
     * Creates reusable jobs used in the editor.
     */
    private void createJobs()
    {
        /*
         * Create search job.
         */

        final String title = getAbbreviatedInputTitle(searchResult.getInput());
        this.searchJob = new SearchJob(
            "Searching for '" + title + "'...", searchResult);

        // I assume search jobs qualify as 'long' jobs (over one second).
        this.searchJob.setPriority(Job.LONG);

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

    /*
     * Selection provider implementation forwards to the internal tree panel.
     */

    public void addPostSelectionChangedListener(ISelectionChangedListener listener)
    {
        this.selectionProvider.addPostSelectionChangedListener(listener);
    }

    public void removePostSelectionChangedListener(ISelectionChangedListener listener)
    {
        this.selectionProvider.removePostSelectionChangedListener(listener);
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
        this.selectionProvider.addSelectionChangedListener(listener);
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener)
    {
        this.selectionProvider.removeSelectionChangedListener(listener);
    }

    public ISelection getSelection()
    {
        return this.selectionProvider.getSelection();
    }

    public void setSelection(ISelection selection)
    {
        this.selectionProvider.setSelection(selection);
    }
}
