package org.carrot2.launcher;

/**
 * Launching exception in {@link Launcher}.
 * 
 * @author Dawid Weiss
 */
final class LaunchException extends RuntimeException
{
    public LaunchException(String message)
    {
        super(message);
    }   
}
