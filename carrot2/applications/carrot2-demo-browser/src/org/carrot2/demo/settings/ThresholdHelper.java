
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

package org.carrot2.demo.settings;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.carrot2.demo.ProcessSettingsBase;
import org.carrot2.demo.swing.util.JIntThreshold;
import org.carrot2.demo.swing.util.JThreshold;

/**
 * A small helper class for creating threshold components.
 * 
 * @author Dawid Weiss
 */
public class ThresholdHelper {

    public static JIntThreshold createIntegerThreshold(
            final ProcessSettingsBase settings, final String key, 
            String label, int min, int max, int minorTick, int majorTick)
    {
        final HashMap params = settings.getRequestParams();
        final JIntThreshold comp = new JIntThreshold(label, min, max, minorTick, majorTick);
    
        final String paramValue = (String) params.get(key);
        if (paramValue != null) {
            final int value = Integer.parseInt(paramValue);
            comp.setValue(value);
        }
        else {
            comp.setValue(min);
        }
        
        comp.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent e)
                {
                    final HashMap params = settings.getRequestParams();
                    params.put(key, Integer.toString(comp.getValue()));
                    settings.setRequestParams(params);
                }
            });
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
        return comp;
    }

    public static JComponent createDoubleThreshold(final ProcessSettingsBase settings, final String key, 
            String label, double min, double max, double minorTick, double majorTick)
    {
        final HashMap params = settings.getRequestParams();
        final double value = Double.parseDouble((String) params.get(key));
        final JThreshold comp = new JThreshold(label, min, max, minorTick, majorTick);
        comp.setValue(value);
        comp.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent e)
                {
                    final HashMap params = settings.getRequestParams();
                    params.put(key, Double.toString(comp.getValue()));
                    settings.setRequestParams(params);
                }
            });
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
        return comp;
    }

}
