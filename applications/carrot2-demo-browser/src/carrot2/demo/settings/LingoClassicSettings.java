package carrot2.demo.settings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComponent;

import carrot2.demo.ProcessSettings;
import carrot2.demo.ProcessSettingsListener;

import com.stachoodev.carrot.filter.lingo.lsicluster.LsiConstants;

/**
 * Settings class for Lingo classic with a generic
 * input (Yahoo for example).
 * 
 * @author Dawid Weiss
 */
public class LingoClassicSettings implements ProcessSettings {
    private HashMap params;
    private Vector listeners = new Vector();

    public LingoClassicSettings() {
        params = new HashMap();
        params.put(LsiConstants.CLUSTER_ASSIGNMENT_THRESHOLD,
                Double.toString(LsiConstants.DEFAULT_CLUSTER_ASSIGNMENT_THRESHOLD));
        params.put(LsiConstants.CANDIDATE_CLUSTER_THRESHOLD,
                Double.toString(LsiConstants.DEFAULT_CANDIDATE_CLUSTER_THRESHOLD));
    }

    private LingoClassicSettings(HashMap params) {
        this.params = new HashMap(params);
    }

    public boolean hasSettings() {
        return true;
    }

    public void showSettings() {
    }

    public boolean isConfigured() {
        return true;
    }

    public HashMap getRequestParams() {
        synchronized (this) {
            return new HashMap(params);
        }
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

    public void setRequestParams(HashMap params) {
        synchronized (this) {
            this.params = params;
            fireParamsUpdated();
        }
    }

    private void fireParamsUpdated() {
        for (Iterator i = listeners.iterator(); i.hasNext(); ) {
            ((ProcessSettingsListener) i.next()).settingsChanged(this); 
        }
    }

    public void addListener(ProcessSettingsListener listener) {
        this.listeners.add(listener);
    }
}
