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
import com.paulodev.carrot.treeSnippetMiner.treeAnalyser.snippetTokenizer.Token;

public interface TokenFeatureCalc
{
    public String GetName();

    public double calcValue(Token t, Vector strings);
}