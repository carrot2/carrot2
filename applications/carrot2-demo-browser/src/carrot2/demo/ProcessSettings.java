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

    /** Display per-query settings for the process. Should display a modeless dialog. */
    public void showSettings();

    /** Display default settings for the process. Should display a modal dialog. */
    public void showDefaultSettings(Object guiContainer);

    public boolean isConfigured();

    public HashMap getRequestParams();

    /** Create a clone of yourself (and default settings) for use in a query */
    public ProcessSettings createClone();
}
