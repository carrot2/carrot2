package carrot2.demo;

import java.util.HashMap;

/**
 * Classes implementing this interface manage settings for a given process
 * (identified by a process identifier).
 * 
 * @author Dawid Weiss
 */
public interface ProcessSettings {
    public boolean hasSettings();
    public void showSettings();
    public boolean isConfigured();

    public HashMap getRequestParams();
}
