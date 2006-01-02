
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.filter.stc;


public final class Document
{
    public String id;
    public String snippet;
    public String title;
    public String url;

    public void setId(String id)
    {
        this.id = id;
    }


    public void addTitleChunk(char [] buf, int start, int length)
    {
        if (title != null)
        {
            title += new String(buf, start, length);
        }
        else
        {
            title = new String(buf, start, length);
        }
    }


    public void addUrlChunk(char [] buf, int start, int length)
    {
        if (url != null)
        {
            url += new String(buf, start, length);
        }
        else
        {
            url = new String(buf, start, length);
        }
    }


    public void addSnippetChunk(char [] buf, int start, int length)
    {
        if (snippet != null)
        {
            snippet += new String(buf, start, length);
        }
        else
        {
            snippet = new String(buf, start, length);
        }
    }
}
