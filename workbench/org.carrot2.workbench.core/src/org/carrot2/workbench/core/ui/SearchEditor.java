package org.carrot2.workbench.core.ui;

import java.util.*;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.DisposeBin;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.editors.AttributeChangedEvent;
import org.carrot2.workbench.editors.IAttributeListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.forms.widgets.*;
import org.eclipse.ui.part.EditorPart;

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
    public static final String ID = "org.carrot2.workbench.core.editors.results";

    /*
     * Memento attributes and sections.
     */

    private static final String MEMENTO_SECTIONS = "sections";
    private static final String MEMENTO_SECTION = "section";
    private static final String SECTION_NAME = "name";
    private static final String SECTION_WEIGHT = "weight";
    private static final String SECTION_VISIBLE = "visible";

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
         * Schedule initial processing.
         */

        reprocess();
    }

    /**
     * Update part name and root form's title
     */
    private void updatePartHeaders()
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

        final int MAX_WIDTH = 20;
        final String full = (String) query;
        final String abbreviated = StringUtils.abbreviate(full, MAX_WIDTH);

        setPartName(abbreviated);
        setTitleToolTip(full);

        rootForm.setText(abbreviated);
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
        // Ignore.
    }

    /*
     * 
     */
    public void doSaveAs()
    {
        // TODO: Implement save and forward to {@link SaveAsXMLAction}?
    }

    /*
     * 
     */
    public boolean isSaveAsAllowed()
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
        setDirty(false);

        final SearchJob job = new SearchJob(searchResult);

        /*
         * Add a job listener to update root form's busy state.
         */
        job.addJobChangeListener(new JobChangeAdapter()
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
                        rootForm.setBusy(busy);
                    }
                });
            }
        });

        job.schedule();
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

        final IAction saveToXML = new SaveAsXMLAction();
        toolbar.add(saveToXML);

        toolbar.add(new Separator());

        /*
         * Add reprocess action and hook it to the dirty state of the editor.
         */
        final IAction reprocess = new ReprocessAction();
        toolbar.add(reprocess);

        this.addPropertyListener(new IPropertyListener()
        {
            public void propertyChanged(Object source, int propId)
            {
                if (propId == PROP_DIRTY)
                {
                    reprocess.setEnabled(isDirty());
                }
            }
        });

        /*
         * Install live-update trigger. 
         */

        /*
         * TODO: Add live-update action button and event trigger here.
         * The trigger should be hooked up to SearchInput's event stream
         * and update a timer that would spawn a job when the countdown reaches zero.
         */

        toolbar.add(new Separator());

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
