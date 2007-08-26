
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

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Quite dirty, but should do for now: a process settings
 * instance aggregating one or more instances of {@link ProcessSettings}. 
 * 
 * @author Dawid Weiss
 */
final class CompoundProcessSettings extends ProcessSettingsBase implements ProcessSettingsListener {

    private ProcessSettings[] settings;

    public CompoundProcessSettings(ProcessSettings[] settings) {
        this.settings = settings;
        for (int i = 0; i < settings.length; i++) {
            if (settings[i] instanceof ProcessSettingsBase) {
                ((ProcessSettingsBase) settings[i]).addListener(this);                
            }
        }
    }

    public final ProcessSettings createClone() {
        ProcessSettings [] clone = new ProcessSettings [settings.length];
        for (int i = 0; i < clone.length; i++) {
            clone[i] = settings[i].createClone();
        }
        return new CompoundProcessSettings(clone);
    }

    public void dispose() {
        for (int i = 0; i < settings.length; i++) {
            settings[i].dispose();
        }
    }

    public Map getRequestParams() {
        HashMap all = new HashMap();
        for (int i = 0; i < settings.length; i++) {
            all.putAll(settings[i].getRequestParams());
        }
        return all;
    }

    public JComponent getSettingsComponent(Frame owner) {
        final DefaultFormBuilder builder = new DefaultFormBuilder(
                new FormLayout("pref", ""));

        for (int i = 0; i < settings.length; i++) {
            builder.append(settings[i].getSettingsComponent(owner));
            builder.nextLine();
        }

        return builder.getPanel();
    }

    public final boolean hasSettings() {
        for (int i = 0; i < settings.length; i++) {
            if (settings[i].hasSettings()) {
                return true;
            }
        }
        return false;
    }

    public final boolean isConfigured() {
        for (int i = 0; i < settings.length; i++) {
            if (settings[i].isConfigured() == false) {
                return false;
            }
        }
        return true;
    }

    public void settingsChanged(ProcessSettings settings) {
        super.fireParamsUpdated();
    }
}
