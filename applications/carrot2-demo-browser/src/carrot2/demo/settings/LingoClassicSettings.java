
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

import java.util.*;

import javax.swing.*;

import carrot2.demo.*;

import com.stachoodev.carrot.filter.lingo.lsicluster.*;

/**
 * Settings class for Lingo classic with a generic input.
 * 
 * @author Dawid Weiss
 */
public class LingoClassicSettings extends ProcessSettingsBase implements ProcessSettings {

    public LingoClassicSettings() {
        params = new HashMap();
        params.put(LsiConstants.CLUSTER_ASSIGNMENT_THRESHOLD,
                Double.toString(LsiConstants.DEFAULT_CLUSTER_ASSIGNMENT_THRESHOLD));
        params.put(LsiConstants.CANDIDATE_CLUSTER_THRESHOLD,
                Double.toString(LsiConstants.DEFAULT_CANDIDATE_CLUSTER_THRESHOLD));
    }

    private LingoClassicSettings(Map params) {
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
            return new LingoClassicSettings(params);
        }
    }

    public JComponent getSettingsComponent() {
        LingoSettingsDialog dlg = new LingoSettingsDialog(this);
        return dlg;
    }
}
