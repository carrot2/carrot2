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
import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.DisposeBin;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.core.helpers.PostponableJob;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.preferences.PreferenceConstants;
import org.carrot2.workbench.core.ui.actions.SaveAsXMLActionDelegate;
import org.carrot2.workbench.core.ui.widgets.CScrolledComposite;
import org.carrot2.workbench.editors.AttributeChangedEvent;
import org.carrot2.workbench.editors.AttributeListenerAdapter;
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
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
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

    /**
     * All attributes of a single panel.
     */
    public final static class SectionReference
    {
        public final Section section;
        public final int sashIndex;
        public boolean visibility;
        public int weight;

        SectionReference(Section self, int sashIndex, boolean v, int w)
        {
            this.section = self;
            this.sashIndex = sashIndex;
            this.visibility = v;
            this.weight = w;
        }

        public SectionReference(SectionReference other)
        {
            this(null, -1, other.visibility, other.weight);
        }
    }
    
    /**
     * Sections (panels) present inside the editor.
     */
    private EnumMap<SearchEditorSections, SectionReference> sections;

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

    /**
     * This editor's restore state information.
     */
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
     * Auto-update listener calls {@link #reprocess()} after 
     * {@link PreferenceConstants#AUTO_UPDATE} property changes.
     */
    private IAttributeListener autoUpdateListener = new AttributeListenerAdapter()
    {
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
    private IPropertyChangeListener autoUpdateListener2 = new IPropertyChangeListener() {
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

        sashForm = new SashForm(rootForm.getBody(), SWT.HORIZONTAL);
        toolkit.adapt(sashForm);

        final GridLayout layout = GridLayoutFactory.swtDefaults()
            .margins(sashForm.SASH_WIDTH, sashForm.SASH_WIDTH).create();
        rootForm.getBody().setLayout(layout);        

        createControls(sashForm);
        updatePartHeaders();

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
        if (memento != null)
        {
            saveSectionsState(memento, sections);
        }
    }

    /**
     * Creates a custom child in a given memento and persists information from
     * a set of {@link SectionReference}s.
     */
    final static void saveSectionsState(IMemento memento, 
        EnumMap<SearchEditorSections, SectionReference> sections)
    {
        final IMemento sectionsMemento = memento.createChild(MEMENTO_SECTIONS);
        for (SearchEditorSections section : sections.keySet())
        {
            final SectionReference sr = sections.get(section);

            final IMemento sectionMemento = sectionsMemento.createChild(MEMENTO_SECTION);
            sectionMemento.putString(SECTION_NAME, section.name());
            sectionMemento.putInteger(SECTION_WEIGHT, sr.weight);
            sectionMemento.putString(SECTION_VISIBLE, Boolean.toString(sr.visibility));
        }
    }
    
    /**
     * Restores partial attributes saved by {@link #saveSectionsState()}
     */
    final static void restoreSectionsState(IMemento memento, 
        EnumMap<SearchEditorSections, SectionReference> sections)
    {
        final IMemento sectionsMemento = memento.getChild(MEMENTO_SECTIONS);
        if (sectionsMemento != null)
        {
            for (IMemento sectionMemento : sectionsMemento.getChildren(MEMENTO_SECTION))
            {
                final SearchEditorSections section = SearchEditorSections.valueOf(
                    sectionMemento.getString(SECTION_NAME));

                if (sections.containsKey(section))
                {
                    final SectionReference r = sections.get(section);
                    r.weight = sectionMemento.getInteger(SECTION_WEIGHT);
                    r.visibility = Boolean.valueOf(sectionMemento.getString(SECTION_VISIBLE));
                }
            }
        }
    }

    /*
     * 
     */
    private void restoreState()
    {
        /*
         * Assign default section weights.
         */
        for (SearchEditorSections s : sections.keySet())
        {
            sections.get(s).weight = s.weight;
        }

        /*
         * Restore global sections attributes, if possible.
         */
        final WorkbenchCorePlugin core = WorkbenchCorePlugin.getDefault();
        core.restoreSectionsState(sections);

        /*
         * Restore weights from editor's memento, if possible.
         */
        if (state != null)
        {
            restoreSectionsState(state, sections);
        }

        /*
         * Update weights and visibility.
         */
        final int [] weights = sashForm.getWeights();
        for (SearchEditorSections s : sections.keySet())
        {
            final SectionReference sr = sections.get(s);
            weights[sr.sashIndex] = sr.weight;
            setSectionVisibility(s, sr.visibility);
        }
        sashForm.setWeights(weights);
        
        /*
         * Unfortunately SashForm does not propagate layout events,
         * so we need to acquire these events from sash form elements directly.
         */
        for (final SearchEditorSections section : sections.keySet())
        {
            final SectionReference sr = sections.get(section);
            sr.section.addControlListener(new ControlListener() {
                public void controlMoved(ControlEvent e)
                {
                    sr.weight = sashForm.getWeights()[sr.sashIndex];
                }

                public void controlResized(ControlEvent e)
                {
                     controlMoved(e);
                }
            });
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
     * {@link SearchEditor} adaptations.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter(Class adapter)
    {
        return super.getAdapter(adapter);
    }

    /**
     * Returns a map of this editor's panels ({@link SectionReference}s). This
     * map and its objects are considered <b>read-only</b>.
     * 
     * @see #setSectionVisibility(SearchEditorSections, boolean)
     */
    EnumMap<SearchEditorSections, SectionReference> getSections()
    {
        return sections;
    }

    /**
     * Shows or hides a given panel.
     */
    public void setSectionVisibility(SearchEditorSections section, boolean visible)
    {
        sections.get(section).visibility = visible;
        sections.get(section).section.setVisible(visible);
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
        /*
         * Create and add sections in order of their declaration in the enum type.
         */
        this.sections = new EnumMap<SearchEditorSections, SectionReference>(SearchEditorSections.class);

        int index = 0;
        for (final SearchEditorSections s : EnumSet.allOf(SearchEditorSections.class))
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

            final SectionReference sr = new SectionReference(section, index, true, 0);            
            sections.put(s, sr);

            index++;
        }

        /*
         * Set up selection event forwarding. Install the editor as selection provider for
         * the part.
         */
        final ClusterTree tree = (ClusterTree) getSections().get(
            SearchEditorSections.CLUSTERS).section.getClient();

        this.selectionProvider = tree;
        this.getSite().setSelectionProvider(this);

        /*
         * Set up an event callback making editor dirty when attributes change. 
         */
        this.getSearchResult().getInput().addAttributeChangeListener(new AttributeListenerAdapter() {
            public void attributeChange(AttributeChangedEvent event)
            {
                setDirty(true);
            }
        });

        /*
         * Set up an event callback to spawn auto-update jobs on changes to attributes.
         */
        resources.registerAttributeChangeListener(
            this.getSearchResult().getInput(), autoUpdateListener);

        /*
         * Set up an event callback to restart processing after auto-update is
         * enabled and the editor is dirty.
         */
        resources.registerPropertyChangeListener(
            WorkbenchCorePlugin.getDefault().getPreferenceStore(), autoUpdateListener2);

        /*
         * Install a synchronization agent between the current selection in the editor and
         * the document list panel.
         */
        final DocumentList documentList = (DocumentList) getSections().get(
            SearchEditorSections.DOCUMENTS).section.getClient();
        documentListSelectionSync = new DocumentListSelectionSync(documentList, this);
        resources.registerPostSelectionChangedListener(this, documentListSelectionSync);

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

        final CScrolledComposite scroller = new CScrolledComposite(sec, 
            SWT.H_SCROLL | SWT.V_SCROLL);
        resources.add(scroller);
        
        final Composite spacer = GUIFactory.createSpacer(scroller);
        resources.add(spacer);

        final AttributeGroups attributesPanel = new AttributeGroups(
            spacer, descriptor, GroupingMethod.GROUP);
        resources.add(attributesPanel);

        toolkit.paintBordersFor(scroller);
        toolkit.adapt(scroller);
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(false);
        scroller.setContent(spacer);

        /*
         * Link attribute value changes:
         * attribute panel -> search result
         */
        final IAttributeListener panelToEditorSync = new AttributeListenerAdapter() {
            public void attributeChange(AttributeChangedEvent event)
            {
                getSearchResult().getInput().setAttribute(event.key, event.value);
            }
        };
        attributesPanel.addAttributeChangeListener(panelToEditorSync);

        /*
         * Link attribute value changes:
         * search result -> attribute panel
         */
        final IAttributeListener editorToPanelSync = new AttributeListenerAdapter() {
            public void attributeChange(AttributeChangedEvent event)
            {
                /*
                 * temporarily unsubscribe from events from the attributes
                 * list to avoid event looping.
                 */
                attributesPanel.removeAttributeChangeListener(panelToEditorSync);
                attributesPanel.setAttribute(event.key, event.value);
                attributesPanel.addAttributeChangeListener(panelToEditorSync);
            }
        };
        getSearchResult().getInput().addAttributeChangeListener(editorToPanelSync);

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
