package carrot2.demo;

import java.util.HashMap;

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

    public void showSettings() {
        throw new RuntimeException("Should not be invoked.");
    }

    public void showDefaultSettings(Object guiContainer) {
        throw new RuntimeException("Should not be invoked.");
    }

    public ProcessSettings createClone() {
        return this;
    }
}
