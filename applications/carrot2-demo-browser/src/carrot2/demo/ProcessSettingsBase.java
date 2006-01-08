
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package carrot2.demo;

import java.util.*;

import javax.swing.JComponent;


/**
 * Base class for process settings.
 *  
 * @author Stanislaw Osinski
 */
public abstract class ProcessSettingsBase implements ProcessSettings
{
    private boolean liveUpdate = true;

    protected Map params;
    protected Vector listeners = new Vector();

    public boolean hasSettings() {
        return false;
    }

    public abstract JComponent getSettingsComponent();

    public abstract boolean isConfigured();

    public HashMap getRequestParams() {
        synchronized (this) {
            return new HashMap(params);
        }
    }

    public abstract ProcessSettings createClone();

    protected void fireParamsUpdated() {
        for (Iterator i = listeners.iterator(); i.hasNext();)
        {
            ((ProcessSettingsListener) i.next()).settingsChanged(this);
        }
    }

    public void addListener(ProcessSettingsListener listener) {
        this.listeners.add(listener);
    }

    public void setRequestParams(Map params) {
        synchronized (this) {
            this.params = params;
            if (liveUpdate) {
                fireParamsUpdated();
            }
        }
    }

    public boolean isLiveUpdate() {
        return liveUpdate;
    }

    public void setLiveUpdate(boolean liveUpdate) {
        this.liveUpdate = liveUpdate;
        if (liveUpdate) {
            fireParamsUpdated();
        }
    }
}