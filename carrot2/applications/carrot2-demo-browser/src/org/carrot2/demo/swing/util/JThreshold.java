
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.demo.swing.util;

import java.awt.*;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * A threshold visualization component.
 * 
 * @author Dawid Weiss
 */
public class JThreshold extends JPanel {

    private final Vector listeners = new Vector();
    private final JDoubleSlider slider;
    private final JSpinner spinner;

    public JThreshold(String labelText, double min, double max, double minorTicks, double majorTicks) {
        GridBagConstraints cc;

        final GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);

        cc = new GridBagConstraints(0, 0, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
        final JLabel label = new JLabel(labelText);
        layout.setConstraints(label, cc);
        this.add(label);

        this.slider = new JDoubleSlider(min, max, minorTicks, majorTicks);
        this.slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                final JDoubleSlider slider = (JDoubleSlider) e.getSource();
                updateSpinnerValue(slider.getDoubleValue());
                if (slider.getValueIsAdjusting() == false) {
                    fireValueChange(new ChangeEvent(this));
                }
            }
        });
        cc = new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0);
        layout.setConstraints(slider, cc);
        this.add(slider);
        
        this.spinner = new JSpinner();
        final Dimension prefSize = spinner.getPreferredSize();
        prefSize.width = Math.max(prefSize.width, 50);
        this.spinner.setPreferredSize(prefSize);
        this.spinner.setModel(new SpinnerNumberModel(min, min, max, minorTicks));
        this.spinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                final JSpinner spinner = (JSpinner) e.getSource();
                final SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
                updateSliderValue(model.getNumber().doubleValue());
            }
        });
        cc = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
        layout.setConstraints(spinner, cc);
        this.add(spinner);
    }
    
    public void setSnapToTicks(boolean flag) {
        slider.setSnapToTicks(flag);
    }

    public void setValue(double value) {
    	updateSliderValue(value);
    	updateSpinnerValue(value);
    }

    public void addChangeListener(ChangeListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public double getValue() {
        return slider.getDoubleValue();
    }

    protected void updateSliderValue(double value) {
        this.slider.setValue(value);
    }

    protected void updateSpinnerValue(double doubleValue) {
        this.spinner.setValue(new Double(doubleValue));
    }

    protected void fireValueChange(ChangeEvent event) {
        synchronized (listeners) {
            for (Iterator i = listeners.iterator(); i.hasNext();) {
                ((ChangeListener) i.next()).stateChanged(event);
            }
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#getToolTipText()
     */
    public void setToolTipText(String text)
    {
        super.setToolTipText(text);
        if (slider != null)
        {
            slider.setToolTipText(text);
        }
        
        if (spinner != null)
        {
            spinner.setToolTipText(text);
        }
    }
}
