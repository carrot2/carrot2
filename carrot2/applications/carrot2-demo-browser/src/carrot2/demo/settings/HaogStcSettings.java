
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

package carrot2.demo.settings;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import carrot2.demo.ProcessSettings;

import com.dawidweiss.carrot.filter.stc.StcParameters;

/**
 * Settings class for HAOG-STC with a generic input.
 * 
 * HAOG currently uses settings identical to STC, so we extend
 * from {@link carrot2.demo.settings.StcSettings} and return
 * {@link carrot2.demo.settings.StcSettingsDialog}.
 */
public class HaogStcSettings extends StcSettings implements ProcessSettings {

    public HaogStcSettings() {
        this.params = new StcParameters().toMap();
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
        return new StcSettingsDialog(this);
    }
}
