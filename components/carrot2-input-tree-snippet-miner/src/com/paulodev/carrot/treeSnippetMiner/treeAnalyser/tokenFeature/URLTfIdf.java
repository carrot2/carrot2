package com.paulodev.carrot.treeSnippetMiner.treeAnalyser.tokenFeature;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Poznań University of Technology</p>
 * @author Paweł Kowalik
 * @version 1.0
 */

import java.net.*;
import java.util.*;
import com.paulodev.carrot.treeSnippetMiner.treeAnalyser.snippetTokenizer.Token;
import com.paulodev.carrot.treeExtractor.extractors.TreeExtractor;

public class URLTfIdf
    extends AbstractCacheCalc
{
    public String GetName() {
        return "URL TfIdf";
    }

    protected String getHost(String line) {
        try
        {
            URL temp = new URL(TreeExtractor.clearURL(line));
            return temp.getHost();
        }
        catch (MalformedURLException ex1)
        {
            return "";
        }
    }

    public double innerCalcValue(Token t, Vector strings)
    {
        Hashtable tokens = new Hashtable();
        for (int i=0; i < strings.size(); i++) {
            String host = getHost((String) strings.get(i));
            if (!tokens.containsKey(host))
                tokens.put(host, new Integer(1));
//            else
//                tokens.put(host, new Integer(((Integer)tokens.get(host)).intValue() + 1));
        }
        return (double) tokens.size() / (double) strings.size();
    }
}