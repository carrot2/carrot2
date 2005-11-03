package carrot2.demo;

import java.util.HashMap;

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

    public HashMap getRequestParams() {
        return new HashMap();
    }

    public boolean hasSettings() {
        return false;
    }

    public ProcessSettings createClone() {
        return this;
    }

    public JComponent getSettingsComponent() {
        throw new RuntimeException("Should not be invoked.");
    }

    public void addListener(ProcessSettingsListener listener) {
    }
}
