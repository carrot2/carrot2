package org.carrot2.workbench.core.ui;

import org.carrot2.workbench.core.jobs.ProcessingJob;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public interface IProcessingResultPart
{

    void init(IWorkbenchSite site, Composite parent, FormToolkit toolkit,
        ProcessingJob job);

    Control getControl();

    String getPartName();

    void dispose();

}
