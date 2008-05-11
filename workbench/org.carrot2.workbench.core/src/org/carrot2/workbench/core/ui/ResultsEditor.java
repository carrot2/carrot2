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
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.forms.widgets.*;
import org.eclipse.ui.part.EditorPart;

public class ResultsEditor extends EditorPart
{
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
        1, 3, 3
    };

    private FormToolkit toolkit;

    private Form rootForm;

    private SashForm sashForm;

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
            sashForm.setWeights(weights);
        }
        GridLayout layout = GridLayoutFactory.swtDefaults().create();
        rootForm.getBody().setLayout(layout);
        sashForm.SASH_WIDTH = 5;
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    private int [] createControls(Composite parent)
    {
        final ProcessingJob job =
            new ProcessingJob("Processing of a query",
                (SearchParameters) getEditorInput());
        for (int i = 0; i < parts.length; i++)
        {
            IProcessingResultPart part = parts[i];
            Section sec =
                toolkit.createSection(parent, Section.EXPANDED | Section.TITLE_BAR);
            sec.setText(part.getPartName());
            part.init(getSite(), sec, toolkit, job);
            sec.setClient(part.getControl());
        }
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
                            //in case 'query' attribute is Output attribute (xml source e.g.)
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
            return "no query attribute";
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
        return "Results";
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
        if (state == null)
        {
            return null;
        }
        int weightsAmount = state.getInteger("weights-amount");
        if (weightsAmount != sashForm.getChildren().length)
        {
            return null;
        }
        int [] weights = new int [0];
        for (int i = 0; i < weightsAmount; i++)
        {
            ArrayUtils.add(weights, state.getInteger("w" + i));
        }
        return weights;
    }

    public void saveState(IMemento memento)
    {
        memento.putInteger("weights-amount", this.sashForm.getWeights().length);
        for (int i = 0; i < this.sashForm.getWeights().length; i++)
        {
            int weight = this.sashForm.getWeights()[i];
            memento.putInteger("w" + i, weight);
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
