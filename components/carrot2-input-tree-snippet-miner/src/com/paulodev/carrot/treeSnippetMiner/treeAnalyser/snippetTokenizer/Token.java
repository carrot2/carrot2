package com.paulodev.carrot.treeSnippetMiner.treeAnalyser.snippetTokenizer;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Poznań University of Technology</p>
 * @author Paweł Kowalik
 * @version 1.0
 */

import org.jdom.*;

public class Token
{
    public String name;
    public String attrName;
    public boolean anchor;
    public int type;
    public int beginScope;
    public int endScope = 0;

    public boolean important = false;

    public Element start;
    public Element end = null;

    public static final int TYPE_ZONE = 1;
    public static final int TYPE_NODE = 2;
    public static final int TYPE_ATTR = 3;

    public Token(String name, int type, int beginScope, Element start, boolean anchor, String attrName)
    {
        this.name = name;
        this.type = type;
        this.anchor = anchor;
        this.beginScope = beginScope;
        this.start = start;
        this.attrName = attrName;
    }

    public String  toString() {
        return name;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object o) {
        return this.toString().equals(o.toString());
    }

    public boolean inScope(Token toCheck) {
        if (toCheck == this)
            return false;
        if (type == TYPE_ATTR)
            return false;
        else if (type == TYPE_NODE) {
            return (toCheck.beginScope >= beginScope) && (toCheck.endScope <= endScope);
        }
        else if (type == TYPE_ZONE) {
            if (toCheck.type == TYPE_ZONE)
                return (toCheck.beginScope >= beginScope) && (toCheck.endScope <= endScope);
            else if (toCheck.type == TYPE_NODE)
                return (toCheck.beginScope > beginScope) && (toCheck.beginScope <= endScope);
            else
                return false;
        }
        else
            return false;
    }
}