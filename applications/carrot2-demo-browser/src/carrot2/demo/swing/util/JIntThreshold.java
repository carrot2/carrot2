
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package carrot2.demo.swing.util;

import java.awt.*;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * A threshold visualization component (integer range).
 * 
 * @author Dawid Weiss
 */
public class JIntThreshold extends JPanel {
    private final Vector listeners = new Vector();
    private final JSlider slider;
    private final JSpinner spinner;

    public JIntThreshold(String labelText, int min, int max, int minorTicks, int majorTicks) {
        GridBagConstraints cc;

        final GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);

        cc = new GridBagConstraints(0, 0, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
        final JLabel label = new JLabel(labelText);
        layout.setConstraints(label, cc);
        this.add(label);

        this.slider = new JSlider(min, max);

        int ticks;
        if (minorTicks > 0) {
            ticks = minorTicks;
            slider.setMinorTickSpacing(minorTicks);
        } else {
            if (majorTicks > 0) {
                ticks = majorTicks;
            } else {
                ticks = 0;
            }
        }

        if (majorTicks > 0) {
            ticks = majorTicks;
            slider.setMajorTickSpacing(majorTicks);
        }
        
        if (ticks > 0) {
            slider.setLabelTable(slider.createStandardLabels(ticks));
            slider.setPaintLabels(true);
            slider.setPaintTicks(true);
        }

        this.slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                final JSlider slider = (JSlider) e.getSource();
                updateSpinnerValue(slider.getValue());
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
        this.spinner.setModel(new SpinnerNumberModel(min, min, max, (ticks > 0 ? ticks : 1)));
        this.spinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                final JSpinner spinner = (JSpinner) e.getSource();
                final SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
                updateSliderValue(model.getNumber().intValue());
            }
        });
        cc = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
        layout.setConstraints(spinner, cc);
        this.add(spinner);
    }
    
    public void setSnapToTicks(boolean flag) {
        slider.setSnapToTicks(flag);
    }

    public void setValue(int value) {
    	updateSliderValue(value);
    	updateSpinnerValue(value);
    }

    public void addChangeListener(ChangeListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public int getValue() {
        return slider.getValue();
    }

    protected void updateSliderValue(int value) {
        this.slider.setValue(value);
    }

    protected void updateSpinnerValue(int doubleValue) {
        this.spinner.setValue(new Integer(doubleValue));
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
