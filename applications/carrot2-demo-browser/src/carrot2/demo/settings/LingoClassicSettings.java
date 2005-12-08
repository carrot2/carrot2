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
