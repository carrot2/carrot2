package com.paulodev.carrot.treeSnippetMiner.treeAnalyser.tokenFeature;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Poznań University of Technology</p>
 * @author Paweł Kowalik
 * @version 1.0
 */

import java.util.*;
import java.net.HttpURLConnection;
import java.net.URL;
import com.paulodev.carrot.treeSnippetMiner.treeAnalyser.snippetTokenizer.Token;
import com.paulodev.carrot.treeExtractor.extractors.TreeExtractor;
import java.io.*;

public class IsURLCalc
    extends AbstractCacheCalc
{
    public String GetName() {
        return "Is URL";
    }

    public double innerCalcValue(Token t, Vector strings) {
        int urlCount = 0;
        for (int i = 0; i < strings.size(); i++) {
            String toCheck = ((String)strings.get(i)).toLowerCase();
            try
            {
                toCheck = TreeExtractor.clearURL(toCheck);
                URL temp = new URL(toCheck);
                HttpURLConnection res = (HttpURLConnection) temp.openConnection();
                HttpURLConnection.setFollowRedirects(false);
                urlCount++;
            }
            catch (IOException ex1)
            {
                // not an URL :(
            }
        }
        return (double) urlCount / (double) strings.size();
    }
}