package com.paulodev.carrot.treeSnippetMiner.treeAnalyser.tokenFeature;

import com.paulodev.carrot.treeSnippetMiner.treeAnalyser.snippetTokenizer.Token;
import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Poznań University of Technology</p>
 * @author Paweł Kowalik
 * @version 1.0
 */

public abstract class AbstractCacheCalc implements TokenFeatureCalc
{
    private Hashtable cache = new Hashtable();

    public String GetName()
    {
        return "Abstract cache Feature Calc";
    }

    public double calcValue(Token t, Vector strings)
    {
        if (cache.containsKey(t))
            return ((Double) cache.get(t)).doubleValue();
        else {
            double tmp =innerCalcValue(t, strings);
            cache.put(t, new Double(tmp));
            return tmp;
        }
    }

    protected abstract double innerCalcValue(Token t, Vector strings);

}