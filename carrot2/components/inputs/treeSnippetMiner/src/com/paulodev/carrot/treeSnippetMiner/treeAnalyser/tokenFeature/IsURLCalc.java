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
import java.net.URL;
import com.paulodev.carrot.treeSnippetMiner.treeAnalyser.snippetTokenizer.Token;
import com.paulodev.carrot.treeExtractor.extractors.TreeExtractor;
import java.net.*;
import java.io.*;

public class IsURLCalc
    extends AbstractCacheCalc
{
    public String GetName() {
        return "Is URL";
    }

    public double innerCalcValue(Token t, Vector strings) {
        int urlCount = 0;
        System.out.println("Checking URLs: ");
        for (int i = 0; i < strings.size(); i++) {
            String toCheck = ((String)strings.get(i)).toLowerCase();
            try
            {
                toCheck = TreeExtractor.clearURL(toCheck);
                URL temp = new URL(toCheck);
                System.out.print(".");
                HttpURLConnection res = (HttpURLConnection) temp.openConnection();
                res.setFollowRedirects(false);
//                res.getContent();
                urlCount++;
            }
            catch (IOException ex1)
            {
                // not an URL :(
            }
        }
        System.out.println(" done");
        return (double) urlCount / (double) strings.size();
    }
}