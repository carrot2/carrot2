
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

package org.carrot2.demo;

import java.awt.Frame;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

/**
 * Empty project settings with no extra configuration.
 * 
 * @author Dawid Weiss
 */
public final class EmptyProcessSettings implements ProcessSettings {

    public boolean isConfigured() {
        return true;
    }

    public Map getRequestParams() {
        return new HashMap();
    }

    public boolean hasSettings() {
        return false;
    }

    public ProcessSettings createClone() {
        return this;
    }

    public JComponent getSettingsComponent(Frame owner) {
        throw new RuntimeException("Should not be invoked.");
    }

    public void addListener(ProcessSettingsListener listener) {
    }

    public void setLiveUpdate(boolean liveUpdate) {
    }

    public boolean isLiveUpdate() {
        return false;
    }
    
    public void dispose() {
    }
}
