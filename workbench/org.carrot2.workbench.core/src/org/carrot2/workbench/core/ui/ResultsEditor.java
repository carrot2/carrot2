package org.carrot2.workbench.core.ui;

import org.apache.commons.lang.ArrayUtils;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.jobs.ProcessingJob;
import org.carrot2.workbench.core.jobs.ProcessingStatus;
import org.carrot2.workbench.core.ui.attributes.AttributeListComponent;
import org.carrot2.workbench.core.ui.clusters.ClusterTreeComponent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.*;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ResultsEditor extends EditorPart implements IPersistableEditor
{
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
    private IProcessingResultPart [] parts =
        {
            new ClusterTreeComponent(), new DocumentListBrowser(),
            new AttributeListComponent()
        };
    private int [] weights =
    {
        1, 2, 2
    };

    private FormToolkit toolkit;

    private Form rootForm;

    private SashForm sashForm;

    private Section [] sections;

    private IMemento state;

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
        rootForm.getMenuManager().add(
            new VisibilityToogleAction("Show Clusters", 0, (Boolean) sections[0]
                .getData("visible"), AbstractUIPlugin.imageDescriptorFromPlugin(
                "org.eclipse.ui", "icons/full/eview16/filenav_nav.gif")));
        rootForm.getMenuManager().add(
            new VisibilityToogleAction("Show Documents", 1, (Boolean) sections[1]
                .getData("visible"), AbstractUIPlugin.imageDescriptorFromPlugin(
                "org.eclipse.ui", "icons/full/obj16/file_obj.gif")));
        rootForm.getMenuManager().add(
            new VisibilityToogleAction("Show Attributes", 2, (Boolean) sections[2]
                .getData("visible"), AbstractUIPlugin.imageDescriptorFromPlugin(
                "org.eclipse.ui", "icons/full/obj16/generic_elements.gif")));
        rootForm.getMenuManager().update();

        IAction a = new SaveToXmlAction();
        rootForm.getToolBarManager().add(a);
        rootForm.getToolBarManager().update(true);
    }

    private int [] createControls(Composite parent)
    {
        final ProcessingJob job =
            new ProcessingJob("Processing of a query",
                (SearchParameters) getEditorInput());
        sections = new Section [parts.length];
        for (int i = 0; i < parts.length; i++)
        {
            IProcessingResultPart part = parts[i];
            Section sec =
                toolkit.createSection(parent, ExpandableComposite.EXPANDED
                    | ExpandableComposite.TITLE_BAR);
            sec.setText(part.getPartName());
            IToolBarManager manager = createToolbarManager(sec);
            part.init(getSite(), sec, toolkit, job);
            part.populateToolbar(manager);
            sec.setClient(part.getControl());
            sections[i] = sec;
        }
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
        for (int i = 0; i < parts.length; i++)
        {
            IProcessingResultPart part = parts[i];
            part.dispose();
        }
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
        for (int i = 0; i < sections.length; i++)
        {
            toogleSectionVisibility(i, true);
        }
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
}
