package org.carrot2.workbench.core.ui;

import java.util.*;

import org.apache.commons.lang.ArrayUtils;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.CorePlugin;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.jobs.ProcessingJob;
import org.carrot2.workbench.core.jobs.ProcessingStatus;
import org.carrot2.workbench.core.preferences.PreferenceConstants;
import org.carrot2.workbench.core.ui.attributes.AttributeListComponent;
import org.carrot2.workbench.core.ui.clusters.ClusterTreeComponent;
import org.carrot2.workbench.core.ui.views.*;
import org.carrot2.workbench.editors.AttributeChangeEvent;
import org.carrot2.workbench.editors.AttributeChangeListener;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.*;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.forms.widgets.*;
import org.eclipse.ui.part.EditorPart;

public class ResultsEditor extends EditorPart implements IPersistableEditor
{
    private final class SectionChooserAction extends Action
    {
        private IMenuCreator creator = new IMenuCreator()
        {
            private Menu menu;
            private Collection<Image> images = new ArrayList<Image>();

            public Menu getMenu(Control parent)
            {
                menu = new Menu(parent);
                createItems();
                return menu;
            }

            public Menu getMenu(Menu parent)
            {
                menu = new Menu(parent);
                createItems();
                return menu;
            }

            private void createItems()
            {
                IExtension ext =
                    Platform.getExtensionRegistry().getExtension("org.eclipse.ui.views",
                        "org.carrot2.workbench.core.views");
                final Map<String, ImageDescriptor> icons =
                    new HashMap<String, ImageDescriptor>();
                for (int i = 0; i < ext.getConfigurationElements().length; i++)
                {
                    IConfigurationElement view = ext.getConfigurationElements()[i];
                    if (view.getName().equals("view")
                        && view.getAttribute("icon") != null)
                    {
                        icons.put(view.getAttribute("id"), CorePlugin
                            .getImageDescriptor(view.getAttribute("icon")));
                    }
                }

                MenuItem mi1 = new MenuItem(menu, SWT.CHECK);
                linkToAction(mi1, new VisibilityToogleAction("Show Clusters", 0,
                    (Boolean) sections[0].getData("visible"), icons
                        .get(ClusterTreeView.ID)));
                MenuItem mi2 = new MenuItem(menu, SWT.CHECK);
                linkToAction(mi2, new VisibilityToogleAction("Show Documents", 1,
                    (Boolean) sections[1].getData("visible"), icons
                        .get(DocumentListView.ID)));
                MenuItem mi3 = new MenuItem(menu, SWT.CHECK);
                linkToAction(mi3, new VisibilityToogleAction("Show Attributes", 2,
                    (Boolean) sections[2].getData("visible"), icons
                        .get(AttributesView.ID)));
            }

            private void linkToAction(final MenuItem mi, final Action action)
            {
                mi.setText(action.getText());
                Image icon = action.getImageDescriptor().createImage();
                images.add(icon);
                mi.setImage(icon);
                mi.setSelection(action.isChecked());
                mi.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        action.setChecked(mi.getSelection());
                        action.run();
                        e.doit = true;
                    }
                });
            }

            public void dispose()
            {
                for (Image icon : images)
                {
                    icon.dispose();
                }
                //menu can be null, if action was never executed and menu was not shown
                if (menu != null)
                {
                    menu.dispose();
                }
            }
        };

        private SectionChooserAction(String text)
        {
            super(text, IAction.AS_DROP_DOWN_MENU);
        }

        @Override
        public IMenuCreator getMenuCreator()
        {
            return creator;
        }

        @Override
        public ImageDescriptor getImageDescriptor()
        {
            return CorePlugin.getImageDescriptor("icons/sections.gif");
        }

    }

    private class VisibilityToogleAction extends Action
    {
        private int sectionIndex;

        public VisibilityToogleAction(String title, int index, boolean checked,
            ImageDescriptor imageDescriptor)
        {
            super(title, IAction.AS_CHECK_BOX);
            this.sectionIndex = index;
            this.setChecked(checked);
            this.setImageDescriptor(imageDescriptor);
        }

        @Override
        public void run()
        {
            toogleSectionVisibility(sectionIndex, isChecked());
        }

    }

    public static final String ID = "org.carrot2.workbench.core.editors.results";

    public static final int CURRENT_CONTENT = 1;

    private ProcessingResult currentContent;
    private Image sourceImage;
    private int [] weights =
    {
        1, 2, 2
    };
    private AttributeListComponent attributeList = new AttributeListComponent();

    private FormToolkit toolkit;

    private Form rootForm;

    private SashForm sashForm;

    private Section [] sections;

    private IMemento state;

    private ClusterTreeComponent tree;

    private DocumentListBrowser browser;

    @Override
    public void createPartControl(Composite parent)
    {
        toolkit = new FormToolkit(parent.getDisplay());
        sourceImage = getEditorInput().getImageDescriptor().createImage();
        rootForm = toolkit.createForm(parent);
        rootForm.setText(getPartName());
        rootForm.setImage(getTitleImage());
        toolkit.decorateFormHeading(rootForm);
        sashForm = new SashForm(rootForm.getBody(), SWT.HORIZONTAL);
        toolkit.adapt(sashForm);
        int [] weights = createControls(sashForm);
        int [] storedWeights = restoreWeightsFromState();
        if (storedWeights == null)
        {
            sashForm.setWeights(weights);
        }
        else
        {
            sashForm.setWeights(storedWeights);
        }
        GridLayout layout = GridLayoutFactory.swtDefaults().create();
        rootForm.getBody().setLayout(layout);
        sashForm.SASH_WIDTH = 5;
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        createActions();
    }

    private void createActions()
    {

        IAction saveToXmlAction = new SaveToXmlAction();
        rootForm.getToolBarManager().add(saveToXmlAction);
        IAction selectSectionsAction = new SectionChooserAction("Choose sections");
        rootForm.getToolBarManager().add(selectSectionsAction);
        rootForm.getToolBarManager().update(true);
    }

    private int [] createControls(Composite parent)
    {
        final ProcessingJob job =
            new ProcessingJob("Processing of a query",
                (SearchParameters) getEditorInput());
        sections = new Section [3];
        for (int i = 0; i < 3; i++)
        {
            Section sec =
                toolkit.createSection(parent, ExpandableComposite.EXPANDED
                    | ExpandableComposite.TITLE_BAR);
            sections[i] = sec;
        }
        IToolBarManager manager = createToolbarManager(sections[0]);
        createClustersPart(sections[0], manager, job, getSite());
        manager = createToolbarManager(sections[1]);
        createDocumentsPart(sections[1], manager, job, getSite());
        manager = createToolbarManager(sections[2]);
        createAttributesPart(sections[2], manager, job, getSite());

        restorePartVisibilityFromState();
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
                if (job.getResult().isOK())
                {
                    currentContent = ((ProcessingStatus) job.getResult()).result;
                    Utils.asyncExec(new Runnable()
                    {
                        public void run()
                        {
                            // in case 'query' attribute is Output attribute (xml source
                            // e.g.)
                            rootForm.setText(getPartName());
                            ResultsEditor.this.firePropertyChange(CURRENT_CONTENT);
                        }
                    });
                }
            }
        });

        job.schedule();
        return weights;
    }

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

    private void createClustersPart(Section sec, IToolBarManager manager,
        ProcessingJob job, IWorkbenchSite site)
    {
        tree = new ClusterTreeComponent();
        sec.setText("Clusters");
        tree.init(site, sec);
        job.addJobChangeListener(new JobChangeAdapter()
        {
            @Override
            public void done(IJobChangeEvent event)
            {
                if (event.getResult().getSeverity() == IStatus.OK)
                {
                    final ProcessingResult result =
                        ((ProcessingStatus) event.getResult()).result;
                    tree.setClusters(result.getClusters());
                }
            }
        });
        tree.populateToolbar(manager);
        sec.setClient(tree.getControl());
    }

    private void createDocumentsPart(Section sec, IToolBarManager manager,
        ProcessingJob job, IWorkbenchSite site)
    {
        browser = new DocumentListBrowser();
        sec.setText("Documents");
        browser.init(site, sec);
        job.addJobChangeListener(new JobChangeAdapter()
        {
            @Override
            public void done(IJobChangeEvent event)
            {
                if (event.getResult().getSeverity() == IStatus.OK)
                {
                    final ProcessingResult result =
                        ((ProcessingStatus) event.getResult()).result;
                    Utils.asyncExec(new Runnable()
                    {
                        public void run()
                        {
                            browser.updateBrowserText(result);
                        }
                    });
                }
            }
        });
        browser.populateToolbar(manager);
        sec.setClient(browser.getControl());
    }

    @SuppressWarnings("unchecked")
    private void createAttributesPart(Section sec, IToolBarManager manager,
        final ProcessingJob job, IWorkbenchSite site)
    {
        attributeList = new AttributeListComponent();
        GroupingMethod method = GroupingMethod.GROUP;
        BindableDescriptor desc =
            BindableDescriptorBuilder.buildDescriptor(job.algorithm
                .getExecutableComponent());
        desc = desc.only(Input.class, Processing.class).not(Internal.class).group(method);

        attributeList.init(sec, desc);
        attributeList.addAttributeChangeListener(new AttributeChangeListener()
        {
            public void attributeChange(AttributeChangeEvent event)
            {
                job.attributes.put(event.key, event.value);
                if (attributeList.isLiveUpdateEnabled())
                {
                    job.schedule();
                }
            }
        });
        attributeList.addPropertyChangeListener(new IPropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent event)
            {
                if (event.getProperty().equals(AttributeListComponent.LIVE_UPDATE))
                {
                    if ((Boolean) event.getNewValue())
                    {
                        job.schedule();
                    }
                }
            }
        });
        toolkit.adapt((Composite) attributeList.getControl());
        toolkit.paintBordersFor((Composite) attributeList.getControl());
        UiFormUtils.adaptToFormUI(toolkit, attributeList.getControl());
        attributeList.populateToolbar(manager);
        sec.setClient(attributeList.getControl());
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

    private void toogleSectionVisibility(int sectionIndex, boolean visible)
    {
        sections[sectionIndex].setVisible(visible);
        sections[sectionIndex].setData("visible", visible);
        sashForm.layout();
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        if (!(input instanceof SearchParameters)) throw new PartInitException(
            "Invalid Input: Must be SearchParameters");
        setSite(site);
        setInput(input);
    }

    /*
     * 
     */
    @Override
    public String getPartName()
    {
        Object query =
            ((SearchParameters) this.getEditorInput()).getAttributes().get(
                AttributeNames.QUERY);
        if (query != null)
        {
            return query.toString();
        }
        else
        {
            return "loading...";
        }
    }

    @Override
    public Image getTitleImage()
    {
        return sourceImage;
    }

    /*
     * 
     */
    @Override
    public String getTitleToolTip()
    {
        return getPartName();
    }

    @Override
    public void setFocus()
    {
        rootForm.setFocus();
    }

    @Override
    public void dispose()
    {
        toolkit.dispose();
        sourceImage.dispose();
        tree.dispose();
        browser.dispose();
        attributeList.dispose();
        super.dispose();
    }

    public ProcessingResult getCurrentContent()
    {
        return currentContent;
    }

    private int [] restoreWeightsFromState()
    {
        IMemento weightsState =
            getChildIfCorrect("weights", "weights-amount", sashForm.getChildren().length);
        int [] weights = new int [0];
        if (weightsState != null)
        {
            for (int i = 0; i < sashForm.getChildren().length; i++)
            {
                weights = ArrayUtils.add(weights, weightsState.getInteger("w" + i));
            }
            return weights;
        }
        return null;
    }

    private void restorePartVisibilityFromState()
    {
        toogleSectionVisibility(0, CorePlugin.getDefault().getPreferenceStore()
            .getBoolean(PreferenceConstants.P_SHOW_CLUSTERS));
        toogleSectionVisibility(1, CorePlugin.getDefault().getPreferenceStore()
            .getBoolean(PreferenceConstants.P_SHOW_DOCUMENTS));
        toogleSectionVisibility(2, CorePlugin.getDefault().getPreferenceStore()
            .getBoolean(PreferenceConstants.P_SHOW_ATTRIBUTES));
        IMemento partsState =
            getChildIfCorrect("visibility", "parts-amount", sections.length);
        if (partsState != null)
        {
            for (int i = 0; i < sections.length; i++)
            {
                toogleSectionVisibility(i, Boolean.parseBoolean(partsState
                    .getString("visible-part" + i)));
            }
        }
    }

    private IMemento getChildIfCorrect(String childName, String amountAttribute,
        int correctAmount)
    {
        if (state == null)
        {
            return null;
        }
        IMemento partsState = state.getChild(childName);
        if (partsState == null)
        {
            return null;
        }
        int partsAmount = partsState.getInteger(amountAttribute);
        if (partsAmount != correctAmount)
        {
            return null;
        }
        return partsState;
    }

    public void saveState(IMemento memento)
    {
        IMemento weightsMemento = memento.createChild("weights");
        weightsMemento.putInteger("weights-amount", this.sashForm.getWeights().length);
        for (int i = 0; i < this.sashForm.getWeights().length; i++)
        {
            int weight = this.sashForm.getWeights()[i];
            weightsMemento.putInteger("w" + i, weight);
        }
        IMemento partsMemento = memento.createChild("visibility");
        partsMemento.putInteger("parts-amount", sections.length);
        for (int i = 0; i < sections.length; i++)
        {
            partsMemento.putString("visible-part" + i, sections[i].getData("visible")
                .toString());
        }
    }

    public void restoreState(IMemento memento)
    {
        state = memento;
    }

    /*
     * 
     */
    public void doSave(IProgressMonitor monitor)
    {
    }

    /*
     * 
     */
    public void doSaveAs()
    {
    }

    /*
     * 
     */
    public boolean isSaveAsAllowed()
    {
        return false;
    }

    @Override
    public boolean isDirty()
    {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter(Class adapter)
    {
        if (AttributeListComponent.class.equals(adapter))
        {
            return attributeList;
        }
        if (ClusterTreeComponent.class.equals(adapter))
        {
            return tree;
        }
        return super.getAdapter(adapter);
    }
}
