
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

import org.carrot2.filter.fuzzyAnts.FuzzyAntsParameters;

import carrot2.demo.ProcessSettings;
import carrot2.demo.ProcessSettingsBase;

import com.dawidweiss.carrot.filter.stc.StcParameters;


/**
 * Settings class for FuzzyAnts.
 * 
 * @author Dawid Weiss
 */
public class FuzzyAntsSettings extends ProcessSettingsBase implements ProcessSettings {

    public FuzzyAntsSettings() {
        this.params = new FuzzyAntsParameters().toMap();
    }

    private FuzzyAntsSettings(Map params) {
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
            return new FuzzyAntsSettings(params);
        }
    }

    public JComponent getSettingsComponent(Frame owner) {
        return new FuzzyAntsSettingsDialog(this);
    }
}
