package com.paulodev.carrot.treeSnippetMiner.treeAnalyser.tokenFeature;

import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Poznań University of Technology</p>
 * @author Paweł Kowalik
 * @version 1.0
 */

import com.paulodev.carrot.treeSnippetMiner.treeAnalyser.snippetTokenizer.Token;

public class AvgLengthCalc
    extends AbstractCacheCalc
{
    public String GetName()
    {
        return "Srednia dlugosc";
    }

    public double innerCalcValue(Token t, Vector strings)
    {
        int suma = 0;
        int docs = 0;
        for (int i = 0; i < strings.size(); i++)
        {
            if (((String)strings.get(i)).length() == 0)
                continue;
            else
                docs++;
            suma += ( (String)strings.get(i)).length();
        }
        if (docs == 0)
            return 0;
        else
            return suma / docs;
    }
}