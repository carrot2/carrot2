/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * All rights reserved.
 *
 * Refer to full text of the license "carrot2.LICENSE" in the root folder
 * of CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.dawidweiss.carrot.util;


public class EscapeString
{

    public EscapeString()
    {
    }

    public static String escapeString(String s)
    {
        int i = 0;
        int j = 0;
        int max = s.length();
        char chars[] = new char[max * 2];
        while(i < max) 
        {
            char x = s.charAt(i++);
            switch(x)
            {
            case 9: // '\t'
            case 10: // '\n'
            case 13: // '\r'
            case 34: // '"'
                chars[j++] = '\\';
                chars[j++] = x;
                break;

            default:
                chars[j++] = x;
                break;
            }
        }
        return new String(chars, 0, j);
    }
}
