
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

import java.awt.Frame;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.carrot2.demo.ProcessSettings;
import org.carrot2.demo.ProcessSettingsBase;
import org.carrot2.filter.haog.fi.FIParameters;


/**
 * Settings class for HAOG-FI with a generic input.
 * @author Karol Gołembniak
 */
public class HaogFiSettings extends ProcessSettingsBase implements ProcessSettings {

    public HaogFiSettings() {
        this.params = new FIParameters().toMap();
    }

    private HaogFiSettings(Map params) {
        this.params = new HashMap(params);
    }

    public ProcessSettings createClone() {
        synchronized (this) {
            return new HaogFiSettings(params);
        }
    }

    public JComponent getSettingsComponent() {
        return new HaogFiSettingsDialog(this);
    }

	public JComponent getSettingsComponent(Frame owner) {
        return new HaogFiSettingsDialog(this);
	}

	public boolean isConfigured() {
		return true;
	}

	public boolean hasSettings() {
        return true;
    }
}
