package org.carrot2.workbench.core.ui;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.EditorPart;

public abstract class SashFormEditorPart extends EditorPart implements IPersistableEditor
{
    private FormToolkit toolkit;
    private Form rootForm;

    private SashForm sashForm;
    private IMemento state;

    /**
     * Should not be called by subclasses. Call createControls().
     */
    @Override
    public void createPartControl(Composite parent)
    {
        toolkit = new FormToolkit(parent.getDisplay());
        rootForm = toolkit.createForm(parent);
        rootForm.setText("Results");
        toolkit.decorateFormHeading(rootForm);
        sashForm = new SashForm(rootForm.getBody(), getSashFormOrientation());
        toolkit.adapt(sashForm);
        //        weights = new ArrayList<Integer>();
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

    protected FormToolkit getToolkit()
    {
        return toolkit;
    }

    /**
     * Default value is SWT.HORIZONTAL.
     * 
     * @return orientation in which SashForm should be.
     * @see SWT#HORIZONTAL
     * @see SWT#VERTICAL
     */
    protected int getSashFormOrientation()
    {
        return SWT.HORIZONTAL;
    }

    /**
     * Creates controls to be put o SashForm. <b> Subclasses must implement this method!
     * </B>
     */
    protected abstract int [] createControls(Composite parent);

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        setSite(site);
        setInput(input);
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

    @Override
    public void dispose()
    {
        toolkit.dispose();
        super.dispose();
    }
}
