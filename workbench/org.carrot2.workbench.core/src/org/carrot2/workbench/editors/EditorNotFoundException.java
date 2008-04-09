package org.carrot2.workbench.editors;

@SuppressWarnings("serial")
public class EditorNotFoundException extends RuntimeException
{

    public EditorNotFoundException()
    {
        super();
    }

    public EditorNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public EditorNotFoundException(String message)
    {
        super(message);
    }

    public EditorNotFoundException(Throwable cause)
    {
        super(cause);
    }

}
