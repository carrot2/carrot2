package carrot2.demo.settings;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.JOptionPane;

import carrot2.demo.ProcessSettings;

import com.stachoodev.carrot.filter.lingo.lsicluster.LsiConstants;

/**
 * Settings class for Lingo classic with a generic
 * input (Yahoo for example).
 * 
 * @author Dawid Weiss
 */
public class LingoClassicSettings implements ProcessSettings {
    private HashMap params; 

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

    public void showDefaultSettings(Object guiContainer) {
        final LingoSettingsDialog dlg = new LingoSettingsDialog(params);
        int result = JOptionPane.showConfirmDialog((Component) guiContainer, 
                new Object[] {dlg}, "Lingo settings", 
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            this.params = dlg.getParams();
        }
    }

    public boolean isConfigured() {
        return true;
    }

    public HashMap getRequestParams() {
        return new HashMap(params);
    }

    public ProcessSettings createClone() {
        return new LingoClassicSettings(params);
    }
}
