package org.carrot2.workbench.core.ui;

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.forms.widgets.Section;

public class ResultsEditor extends SashFormEditorPart
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

    @Override
    protected int [] createControls(Composite parent)
    {
        sourceImage = getEditorInput().getImageDescriptor().createImage();
        final ProcessingJob job =
            new ProcessingJob("Processing of a query",
                (SearchParameters) getEditorInput());
        for (int i = 0; i < parts.length; i++)
        {
            IProcessingResultPart part = parts[i];
            Section sec =
                getToolkit().createSection(parent, Section.EXPANDED | Section.TITLE_BAR);
            sec.setText(part.getPartName());
            part.init(getSite(), sec, getToolkit(), job);
            sec.setClient(part.getControl());
        }
        job.addJobChangeListener(new JobChangeAdapter()
        {
            @Override
            public void aboutToRun(IJobChangeEvent event)
            {
                getForm().setBusy(true);
            }

            @Override
            public void done(IJobChangeEvent event)
            {
                getForm().setBusy(false);
                if (job.getResult().isOK())
                {
                    currentContent = ((ProcessingStatus) job.getResult()).result;
                    Utils.asyncExec(new Runnable()
                    {
                        public void run()
                        {
                            ResultsEditor.this.firePropertyChange(CURRENT_CONTENT);
                        }
                    });
                }
            }
        });

        job.schedule();
        return weights;
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        if (!(input instanceof SearchParameters)) throw new PartInitException(
            "Invalid Input: Must be SearchParameters");
        super.init(site, input);
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
    }

    @Override
    public boolean isDirty()
    {
        return false;
    }

    @Override
    public void dispose()
    {
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
}
