

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.paulodev.carrot.util.html.parser;


/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2002 Dawid Weiss, Institute of Computing Science, Poznan University of
 * Technology
 * </p>
 * 
 * <p>
 * Company: Institute of Computing Science, Poznan University of Technology
 * </p>
 *
 * @author Paweł Kowalik
 * @version 1.0
 */
public class HTMLTextNode
    extends HTMLNode
{
    private StringBuffer text;

    public HTMLTextNode(HTMLTree owner, StringBuffer text, HTMLNode parent, int level, int [] num)
    {
        super(owner, parent, true, level, num);
        this.maxNum = this.num;
        this.text = text;
        this.children = null;
        this.name = "content";
        this.classedName = "content";
    }

    public StringBuffer getText()
    {
        return text;
    }


    public String toString()
    {
        StringBuffer res = new StringBuffer(text.length() + level + 2);
        buildTabs(res);
        res.append(text);
        res.append('\n');

        return res.toString();
    }
}
