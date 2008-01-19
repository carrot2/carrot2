
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
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
import org.carrot2.filter.haog.stc.STCParameters;


/**
 * Settings class for HAOG-STC with a generic input.
 * 
 * HAOG uses settings almost identical to STC.
 */
public class HaogStcSettings extends ProcessSettingsBase implements ProcessSettings  {

    public HaogStcSettings() {
        this.params = new STCParameters().toMap();
    }

    private HaogStcSettings(Map params) {
        this.params = new HashMap(params);
    }

    public ProcessSettings createClone() {
        synchronized (this) {
            return new HaogStcSettings(params);
        }
    }

    public JComponent getSettingsComponent() {
        return new HaogStcSettingsDialog(this);
    }

	public JComponent getSettingsComponent(Frame owner) {
        return new HaogStcSettingsDialog(this);
	}

	public boolean isConfigured() {
		return true;
	}
	
	public boolean hasSettings() {
        return true;
    }

}