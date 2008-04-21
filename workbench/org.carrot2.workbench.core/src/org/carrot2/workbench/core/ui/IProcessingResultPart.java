package org.carrot2.workbench.core.ui;

import org.carrot2.workbench.core.jobs.ProcessingJob;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchSite;

public interface IProcessingResultPart
{

    void init(IWorkbenchSite site, Composite parent, ProcessingJob job);

    Control getControl();

    void dispose();

}
