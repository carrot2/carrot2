package org.carrot2.dcs;

/**
 * Configuration exception is thrown from subclasses of {@link AppBase}
 * when the configuration failed for some reason. 
 * 
 * @author Dawid Weiss
 */
public class ConfigurationException extends Exception
{
    public ConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConfigurationException(String message)
    {
        super(message);
    }
}
