package com.dawidweiss.carrot.remote.controller.util;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Exception utilities.
 */
public class ExceptionHelper
{

   /** Hide public constructor */
   private ExceptionHelper()
   {
   }



   /**
    * Creates stack trace string of a given exception
    */
    public static String getStackTrace(Throwable e)
    {
        StringWriter sw = new StringWriter();
        PrintWriter  pw = new PrintWriter( sw );

        e.printStackTrace( pw );

        pw.close();
        return sw.toString();
    }



    /**
     * Returns the stack trace of the invoking thread. This method throws an exception inside its body,
     * which is a time-consuming operation. Use only when really needed.
     */
    public static String getCurrentStackTrace()
    {
        try
        {
            throw new RuntimeException();
        }
        catch (RuntimeException e)
        {
            String currentSt = getStackTrace( e );
            return currentSt;
        }
    }

}