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

public class AvgWordCount
    extends AbstractCacheCalc
{
    public String GetName()
    {
        return "Średnia liczba słów";
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
            String[] terms = ( (String)strings.get(i)).split("\\s");
            for (int j = 0; j < terms.length; j++)
            {
                String term = terms[j].toLowerCase().replaceAll("\\W", "");
                if (term.length() <= 2)
                {
                    continue;
                }
                suma++;
            }
        }
        if (docs == 0)
            return 0;
        else
            return suma / docs;
    }
}