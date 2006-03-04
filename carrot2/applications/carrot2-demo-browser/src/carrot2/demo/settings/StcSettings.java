
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

import java.awt.Frame;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import carrot2.demo.ProcessSettings;
import carrot2.demo.ProcessSettingsBase;

import com.dawidweiss.carrot.filter.stc.StcParameters;

/**
 * Settings class for STC with a generic input.
 * 
 * @author Dawid Weiss
 */
public class StcSettings extends ProcessSettingsBase implements ProcessSettings {

    public StcSettings() {
        this.params = new StcParameters().toMap();
    }

    private StcSettings(Map params) {
        this.params = new HashMap(params);
    }

    public boolean hasSettings() {
        return true;
    }

    public boolean isConfigured() {
        return true;
    }

    public ProcessSettings createClone() {
        synchronized (this) {
            return new StcSettings(params);
        }
    }

    public JComponent getSettingsComponent(Frame owner) {
        return new StcSettingsDialog(this);
    }
}
