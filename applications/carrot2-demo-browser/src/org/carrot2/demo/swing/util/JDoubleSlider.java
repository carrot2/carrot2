
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

package org.carrot2.demo.swing.util;

import java.text.MessageFormat;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;


/**
 * A threshold slider for double values.
 * 
 * @author Dawid Weiss
 */
public class JDoubleSlider extends JSlider {
    private final MessageFormat labelFormat = new MessageFormat("{0,number,#.##}");
    private RangeScaler scaler;

    private double tickScale;
    
    public JDoubleSlider(double min, double max, double minorTicks, double majorTicks) {
        this.scaler = new RangeScaler(min, max, 0, 1000);

        super.setMinimum(scaler.getIntMin());
        super.setMaximum(scaler.getIntMax());

        tickScale = Math.min(minorTicks, majorTicks);

        setMinorTickSpacing(scaler.scale(minorTicks));
        setMajorTickSpacing(scaler.scale(majorTicks));
        setPaintLabels(true);
        setPaintTicks(true);
        setSnapToTicks(false);

        setLabelTable(createLabels(scaler.getIntMin(), scaler.getIntMax(), scaler.scale(majorTicks)));
    }

    public void setValue(double value) {
        super.setValue(scaler.to(value));
    }

    public double getDoubleValue() {
        double value = scaler.from(super.getValue());

        if (getSnapToTicks()) {
            final double n = Math.round(value / tickScale);
            value = tickScale * n;
        }

        return Math.round(value * 100.0) / 100.0;
    }

    public boolean getSnapToTicks() {
        return super.getSnapToTicks();
    }

    public void setSnapToTicks(boolean flag) {
        super.setSnapToTicks(flag);
        this.setLabelTable(createLabels(scaler.getIntMin(), scaler.getIntMax(), getMajorTickSpacing()));
    }

    private Hashtable createLabels(int min, int max, int step) {
        final Hashtable table = new Hashtable();

        for (int i = min; i <= max; i = i + step) {
            double value = scaler.from(i);
            if (getSnapToTicks()) {
                final double n = Math.round(value / tickScale);
                value = tickScale * n;
            }
            final String label = labelFormat.format(new Object [] {new Double(value)});
            table.put(new Integer(i), createLabel(label));
        }

        String label;

        label = labelFormat.format(new Object [] {new Double(scaler.from(min))});
        table.put(new Integer(min), createLabel(label));

        label = labelFormat.format(new Object [] {new Double(scaler.from(max))});
        table.put(new Integer(max), createLabel(label));

        return table;
    }

    private JLabel createLabel(String label) {
        return new JLabel(label);
    }
}
