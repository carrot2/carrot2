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


public class TfIdf
    extends AbstractCacheCalc
{
    private class Term
    {
        public String term;
        public int totalCount = 0;
        public GlobalTerm global;

        public Term(String term)
        {
            this.term = term;
        }
    }

    private class GlobalTerm
        extends Term
    {
        public int distinctCount = 0;
        public boolean DfReady = false;
        public double Df;
        public double Entrophy = 0;

        public GlobalTerm(String term)
        {
            super(term);
        }

        public double GetDf(int docCount)
        {
            if (!DfReady)
            {
                Df = Math.log((double)docCount / (double)distinctCount);
                DfReady = true;
            }
            return Df;
        }
    }

    private class Document
    {
        public Hashtable terms = new Hashtable();
        public int termCount = 0;
        public double weightSum = 0;
    }

    public String GetName()
    {
        return "Entropia zawartości";
    }

    protected String[] getTerms(String line) {
        return line.split("\\s");
    }

    public double innerCalcValue(Token t, Vector parStrings)
    {
        Hashtable globalTerms = new Hashtable();
        Vector strings = new Vector();
        for (int i = 0; i < parStrings.size(); i++)
            if (((String)parStrings.get(i)).length() == 0)
                continue;
            else
                strings.add(parStrings.get(i));

        Document[] documents = new Document[strings.size()];
        int docs = 0;
        for (int i = 0; i < strings.size(); i++)
        {
            documents[i] = new Document();
            String line = (String)strings.get(i);
            String[] terms = getTerms(line);
            for (int j = 0; j < terms.length; j++)
            {
                String term = terms[j].toLowerCase().replaceAll("\\W", "");
                if (term.length() <= 2)
                {
                    continue;
                }
                documents[i].termCount++;
                Term lok;
                GlobalTerm glob;
                if (documents[i].terms.containsKey(terms[j]))
                {
                    lok = (Term)documents[i].terms.get(term);
                    glob = (GlobalTerm)globalTerms.get(lok.term);
                }
                else
                {
                    lok = new Term(term);
                    documents[i].terms.put(lok.term, lok);
                    if (globalTerms.containsKey(lok.term))
                    {
                        glob = (GlobalTerm)globalTerms.get(lok.term);
                    }
                    else
                    {
                        glob = new GlobalTerm(lok.term);
                        globalTerms.put(glob.term, glob);
                    }
                    lok.global = glob;
                    glob.distinctCount++;
                }
                glob.totalCount++;
                lok.totalCount++;
            }
        }
        GlobalTerm[] terms = (GlobalTerm[])globalTerms.values().toArray(new
            GlobalTerm[0]);
        double[][] DTMatrix = new double[documents.length][terms.length];
        for (int i = 0; i < documents.length; i++)
        {
            for (int j = 0; j < terms.length; j++)
            {
                if (documents[i].terms.containsKey(terms[j].term))
                {
/*                    DTMatrix[i][j] = ( (double) ( (Term)documents[i].terms.get(
                        terms[j].term)).totalCount)
                        * terms[j].GetDf(documents.length);
                    documents[i].weightSum += DTMatrix[i][j];*/
                    Term toTest = (Term)documents[i].terms.get(terms[j].term);
                    DTMatrix[i][j] = (double) ( toTest.totalCount) /
                        (double) toTest.global.distinctCount;
                    documents[i].weightSum += DTMatrix[i][j];
                }
                else
                {
                    DTMatrix[i][j] = 0;
                }
            }
            for (int j = 0; j < terms.length; j++)
            {
                DTMatrix[i][j] = DTMatrix[i][j] / documents[i].weightSum;
            }
        }

        double docLenLog = Math.log(documents.length);
        for (int i = 0; i < terms.length; i++)
        {
            terms[i].Entrophy = 0;
            for (int j = 0; j < documents.length; j++)
            {
                if (DTMatrix[j][i] > 0)
                {
                    terms[i].Entrophy -= DTMatrix[j][i] *
                        (Math.log(DTMatrix[j][i]) / docLenLog);
                }
            }
        }

/*        for (int i = 0; i < terms.length; i++)
        {
            System.out.print(terms[i].term + "(" + Math.round(terms[i].Entrophy * 100.0)/100.0 + ")    \t");
            for (int j =0; j < documents.length; j++)
                System.out.print( Math.round(DTMatrix[j][i]*100.0)/100.0 + "\t");
            System.out.println();
        }*/

        double res = 0;
        for (int i = 0; i < documents.length; i++)
        {
            Enumeration e = documents[i].terms.elements();
            while (e.hasMoreElements())
            {
                Term term = (Term)e.nextElement();
                res += /*(double)t.totalCount * */ term.global.Entrophy /
                    (double)documents[i].termCount;
            }
        }
        return res / (double)documents.length;
    }
}