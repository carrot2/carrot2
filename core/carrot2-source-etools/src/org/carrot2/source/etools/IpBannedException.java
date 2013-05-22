package org.carrot2.source.etools;


@SuppressWarnings("serial")
public class IpBannedException extends Exception
{
    public IpBannedException(Exception cause)
    {
        super(cause);
    }
}
