

/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the license "carrot2.LICENSE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */


package fuzzyAnts;


import org.jdom.Element;
import java.io.*;
import java.util.*;
import javax.servlet.http.*;


/**
 * Performs some lexical analysis of a snippet collection and defines some (fuzzy) relations between snippets and terms
 *
 * @author Steven Schockaert
 */
public class SnippetParser
    implements Constants
{
    private List originalDocuments; //original snippets in String-representation
    private HashMap stemming; // translation: original term -> stemmed term
    private HashMap inverseStemming; // translation: stemmed term -> original ters 
    private HashSet stopwords; //set of stopwords, stemmed and in lowercase
    private HashSet queryTerms; //set of query terms, stemmed and in lowercase
    private HashMap [] snippets; //representation of the snippets as multiset, implemented as a HashMap
    private HashMap term2index; //translation: stemmed term -> term-index
    private HashMap terms; //translation: term-index -> original term 
    private HashMap df; //mapping: term-index -> document frequency (used for IDF-weighting)
    private HashMap narrowTerms; //mapping term-index -> indices of more specific terms
    private HashMap leadervalue; //mapping term-index -> leader value of the term
    private int numberOfDocuments; // number of documents
    private int numberOfTerms; //number of terms
    private HashMap [] termWeights; //contains for each snippet a HashMap term-weights
    private HashMap [] roughWeights; //idem, but for the upper approximation of a snippet
    private HashMap [] docWeights; ///contains for each term a HashMap with snippet-weights
    private HashSet zeroIndices; // set of snippet-indices that contain no term at all
    private HashSet nonZeroIndices; // set of snippet-indices that contain at least 1 term
    private boolean stemmingDesired = true; // true = stemming should be applied
    private boolean removeStopwords; // true = stopwords should be removed
    private int weightSchema; //  contains the used weighting scheme (binary, TF or TF-IDF)
    private final int MINTERMSUPP = 3;
    private final int TERMSPECSUPP = 1;
    private final double COMPLETE = 0.75;

    public SnippetParser(ArrayList documents, ArrayList meta, ArrayList query)
    {
        this(documents, meta, query, true, TFIDF);
    }


    public SnippetParser(
        List documents, List meta, List query, boolean removeStopwords, int weightSchema
    )
    {
        try
        {
            this.removeStopwords = removeStopwords;
            this.weightSchema = weightSchema;
            calculateStemming(meta);
            parseQuery(query);
            parseSnippets(documents);
            numberOfDocuments = snippets.length;
            numberOfTerms = df.size();
            calculateWeights();
            calculateNonZeroIndices();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
     * Applies stemming to to snippets
     */
    private void calculateStemming(List meta)
    {
        stemming = new HashMap();
        inverseStemming = new HashMap();
        stopwords = new HashSet();

        if (stemmingDesired)
        {
            for (ListIterator it = meta.listIterator(); it.hasNext();)
            {
                Element e = (Element) it.next();
                String original = e.getAttributeValue("t");
                String stemmed = e.getAttributeValue("s");
                String stopword = e.getAttributeValue("sw");

                if (stopword != null)
                {
                    stopwords.add(stemmed);
                }

                stemming.put(original, stemmed);

                if (inverseStemming.containsKey(stemmed))
                {
                    HashSet l = (HashSet) inverseStemming.get(stemmed);
                    l.add(original);
                    inverseStemming.put(stemmed, l);
                }
                else
                {
                    HashSet l = new HashSet();
                    l.add(original);
                    inverseStemming.put(stemmed, l);
                }
            }
        }
    }


    /*
     * Applies stemming to the query terms
     */
    private void parseQuery(List query)
    {
        queryTerms = new HashSet();

        for (Iterator it = query.listIterator(); it.hasNext();)
        {
            Element e = (Element) it.next();

            if (e != null)
            {
                StringTokenizer queryList = new StringTokenizer(e.getText());

                for (; queryList.hasMoreTokens();)
                {
                    StringBuffer buf = new StringBuffer(queryList.nextToken());

		    if ((buf.charAt(0) == '"') || (buf.charAt(0) == '+'))
			{
			    buf.deleteCharAt(0);
			}

		    if (buf.length() > 0)
			{
			    if (buf.charAt(buf.length() - 1) == '"')
				{
				    buf.deleteCharAt(buf.length() - 1);
				}
			}

		    String woord = buf.toString();

                    if (stemming.containsKey(woord))
			{
                        String s = (String) (stemming.get(woord));
                        queryTerms.add(s.toLowerCase());
                    }
                    else if (stemming.containsKey(woord.toLowerCase()))
                    {
                        String s = (String) (stemming.get(woord.toLowerCase()));
                        queryTerms.add(s.toLowerCase());
                    }
                    else
                    {
                        queryTerms.add(woord.toLowerCase());
                    }
                }
            }
        }
    }


    /*
     * snippets are parsed
     */
    private void parseSnippets(List documents)
    {
        int termIndex = 0;
        term2index = new HashMap();
        terms = new HashMap();
        df = new HashMap();
        originalDocuments = new ArrayList();
        snippets = new HashMap[documents.size()];

        for (int i = 0; i < documents.size(); i++)
        {
            Element snippet = ((Element) documents.get(i)).getChild("snippet");
            Element title = ((Element) documents.get(i)).getChild("title");
            StringBuffer text = new StringBuffer();

            if (title != null)
            {
                text.append(title.getText());
                text.append(" ");
            }

            if (snippet != null)
            {
                text.append(snippet.getText());
            }

            snippets[i] = new HashMap();

            StringTokenizer st = new StringTokenizer(text.toString());
            StringBuffer originalString = new StringBuffer(text.toString());

            for (int j = 0; j < originalString.length(); j++)
            {
                char ch = originalString.charAt(j);

                if (
                    (ch == ',') || (ch == ')') || (ch == '(') || (ch == ';') || (ch == '.')
                        || (ch == '!') || (ch == '?') || (ch == ':')
                )
                {
                    originalString.setCharAt(j, ' ');
                }
            }

            originalDocuments.add(originalString.toString());

            for (; st.hasMoreTokens();)
            {
                String s = st.nextToken().toLowerCase();
                StringBuffer buf = new StringBuffer(s);

                if (
                    (buf.charAt(0) == '.') || (buf.charAt(0) == ',') || (buf.charAt(0) == ';')
                        || (buf.charAt(0) == '(') || (buf.charAt(0) == ')')
                        || (buf.charAt(0) == '?') || (buf.charAt(0) == '!')
                        || (buf.charAt(0) == ':')
                )
                {
                    buf.deleteCharAt(0);
                }

                if (buf.length() > 0)
                {
                    if (
                        (buf.charAt(buf.length() - 1) == '.')
                            || (buf.charAt(buf.length() - 1) == ',')
                            || (buf.charAt(buf.length() - 1) == ';')
                            || (buf.charAt(buf.length() - 1) == '(')
                            || (buf.charAt(buf.length() - 1) == ')')
                            || (buf.charAt(buf.length() - 1) == '?')
                            || (buf.charAt(buf.length() - 1) == '!')
                            || (buf.charAt(buf.length() - 1) == ':')
                    )
                    {
                        buf.deleteCharAt(buf.length() - 1);
                    }
                }

                s = buf.toString();

                String originalS = s;

                if (stemming.containsKey(s))
                {
                    s = (String) stemming.get(s.toString());
                }

                boolean validWord = true;

                if (s.length() < 2)
                {
                    validWord = false;
                }
                else if (queryTerms.contains(s))
                {
                    validWord = false;
                }
                else if (removeStopwords && stopwords.contains(s))
                {
                    validWord = false;
                }
                else
                {
                    for (int k = 0; k < s.length(); k++)
                    {
                        if (!Character.isLetter(s.charAt(k)))
                        {
                            validWord = false;

                            break;
                        }
                    }
                }

                if (validWord)
                {
                    if (snippets[i].containsKey(s))
                    {
                        snippets[i].put(
                            s, new Integer(((Integer) snippets[i].get(s)).intValue() + 1)
                        );
                    }
                    else
                    {
                        snippets[i].put(s, new Integer(1));
                    }

                    if (term2index.containsKey(s))
                    {
                        int index = ((Integer) term2index.get(s)).intValue();
                        int value = ((Integer) df.get(new Integer(index))).intValue();
                        df.put(new Integer(index), new Integer(value + 1));
                    }
                    else
                    {
                        terms.put(new Integer(termIndex), originalS);
                        term2index.put(s, new Integer(termIndex));
                        df.put(new Integer(termIndex), new Integer(1));
                        termIndex++;
                    }
                }
            }
        }
    }


    /*
     * term-weights in each snippet are calculated
     */
    private void calculateWeights()
    {
        termWeights = new HashMap[numberOfDocuments];
        docWeights = new HashMap[numberOfTerms];

        for (int i = 0; i < numberOfTerms; i++)
        {
            docWeights[i] = new HashMap();
        }

        for (int i = 0; i < numberOfDocuments; i++)
        {
            termWeights[i] = new HashMap();

            double sum = 0;

            for (Iterator it = snippets[i].keySet().iterator(); it.hasNext();)
            {
                String term = (String) it.next();
                int termIndex = ((Integer) term2index.get(term)).intValue();
                int freq = ((Integer) snippets[i].get(term)).intValue();
                int idf = ((Integer) df.get(new Integer(termIndex))).intValue();
                double value = 0;

                if (idf >= MINTERMSUPP)
                {
                    if (weightSchema == BINARY)
                    {
                        value = (freq > 0) ? 1
                                            : 0;
                    }
                    else if (weightSchema == TF)
                    {
                        value = freq;
                    }
                    else if (weightSchema == TFIDF)
                    {
                        value = freq * Math.log(numberOfDocuments / idf);
                    }
                    else
                    {
                        System.err.println("WRONG WEIGHTSCHEMA");
                    }
                }

                sum += (value * value);
                termWeights[i].put(new Integer(termIndex), new Double(value));

                if (weightSchema == BINARY)
                {
                    docWeights[termIndex].put(new Integer(i), new Double(value));
                }
            }

            if (weightSchema != BINARY)
            { // normalisation -> all weigths in [0,1]
                sum = Math.sqrt(sum);

                for (Iterator it = termWeights[i].keySet().iterator(); it.hasNext();)
                {
                    Integer termIndex = (Integer) it.next();
                    double value = ((Double) termWeights[i].get(termIndex)).doubleValue();
                    value /= sum;
                    termWeights[i].put(termIndex, new Double(value));
                    docWeights[termIndex.intValue()].put(new Integer(i), new Double(value));
                }
            }
        }
    }


    /*
     * Determine the indices of the document that contain no term at all
     */
    private void calculateNonZeroIndices()
    {
        zeroIndices = new HashSet();
        nonZeroIndices = new HashSet();

        for (int i = 0; i < termWeights.length; i++)
        {
            if (termWeights[i].size() == 0)
            {
                zeroIndices.add(new Integer(i));
            }
            else
            {
                nonZeroIndices.add(new Integer(i));
            }
        }
    }


    /*
     * Calculate the upper approximation of the snippets in the sense of rough set theory
     */
    public void calculateRoughWeights()
    {
        roughWeights = new HashMap[numberOfDocuments];

        for (int i = 0; i < numberOfDocuments; i++)
        {
            HashMap roughTerms = new HashMap();

            for (Iterator it1 = termWeights[i].keySet().iterator(); it1.hasNext();)
            {
                //itererate over all terms in the document i
                int term = ((Integer) it1.next()).intValue();
                double termValue = ((Double) termWeights[i].get(new Integer(term))).doubleValue();
                HashMap spec = narrowTerms(term);

                for (Iterator it2 = spec.keySet().iterator(); it2.hasNext();)
                {
                    //iterate over all terms which are more specific than "term"
                    int newTerm = ((Integer) it2.next()).intValue();
                    double oldValue = 0;

                    if (roughTerms.containsKey(new Integer(newTerm)))
                    {
                        oldValue = ((Double) roughTerms.get(new Integer(newTerm))).doubleValue();
                    }

                    double specValue = ((Double) spec.get(new Integer(newTerm))).doubleValue();
                    double newValue = Math.min(termValue, specValue);

                    if (newValue > oldValue)
                    {
                        roughTerms.put(new Integer(newTerm), new Double(newValue));
                    }
                }
            }

            roughWeights[i] = roughTerms;
        }
    }


    /*
     * Calculate the leader value of the term "t"
     */
    public double leadervalue(int t)
    {
        if (leadervalue == null)
        {
            leadervalue = new HashMap();
        }

        if (leadervalue.keySet().contains(new Integer(t)))
        {
            return ((Double) leadervalue.get(new Integer(t))).intValue();
        }

        HashMap spec = narrowTerms(t);
        double res = 0;

        for (Iterator it = spec.keySet().iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            res += ((Double) spec.get(index)).doubleValue();
        }

        leadervalue.put(new Integer(t), new Double(res));

        return res;
    }


    /*
     * Determine the terms that are more specific than "t"
     */
    private HashMap narrowTerms(int t)
    {
        if (narrowTerms == null)
        {
            narrowTerms = new HashMap();
        }

        if (narrowTerms.keySet().contains(new Integer(t)))
        {
            return (HashMap) narrowTerms.get(new Integer(t));
        }

        HashSet universe = new HashSet();
        HashMap result = new HashMap();

        for (Iterator it = docWeights[t].keySet().iterator(); it.hasNext();)
        {
            int doc = ((Integer) it.next()).intValue();
            universe.addAll(termWeights[doc].keySet());
        }

        for (Iterator it = universe.iterator(); it.hasNext();)
        {
            Integer termIndex = (Integer) it.next();

            if (t == termIndex.intValue())
            {
                result.put(termIndex, new Double(1));
            }
            else
            {
                double specValue = narrowTerm(termIndex.intValue(), t);

                if (specValue > 0.30)
                {
                    result.put(termIndex, new Double(specValue));
                }
            }
        }

        narrowTerms.put(new Integer(t), result);

        return result;
    }


    /*
     * Calculate to which extend "t1" is more specific than "t2"
     */
    private double narrowTerm(int t1, int t2)
    {
        double sum = sumTerm(t1);
        double sumMin = sumTermMin(t1, t2);

        if ((sum > 0) && (sumMin >= TERMSPECSUPP))
        {
            return sumMin / sum;
        }
        else
        {
            return 0;
        }
    }


    /*
     * calculates the sum over all document of the minimum of the weight of "t1" and the weight of "t2" in that document
     */
    public double sumTermMin(int t1, int t2)
    {
        double sum = 0;
        Set s1 = docWeights[t1].keySet();
        Set s2 = docWeights[t2].keySet();
        Set s = (s1.size() > s2.size()) ? s2
                                        : s1;

        for (Iterator it = s.iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            double w1 = 0;
            double w2 = 0;

            if (docWeights[t1].containsKey(index))
            {
                w1 = ((Double) docWeights[t1].get(index)).doubleValue();
            }

            if (docWeights[t2].containsKey(index))
            {
                w2 = ((Double) docWeights[t2].get(index)).doubleValue();
            }

            sum += Math.min(w1, w2);
        }

        return sum;
    }


    /*
     * calculates the sum over all terms of the minimum of the weight of that term in "d1" and in "d2"
     */
    public double sumDocMin(int d1, int d2)
    {
        double sum = 0;
        Set s1 = termWeights[d1].keySet();
        Set s2 = termWeights[d2].keySet();
        Set s = (s1.size() > s2.size()) ? s2
                                        : s1;

        for (Iterator it = s.iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            double w1 = 0;
            double w2 = 0;

            if (termWeights[d1].containsKey(index))
            {
                w1 = ((Double) termWeights[d1].get(index)).doubleValue();
            }

            if (termWeights[d2].containsKey(index))
            {
                w2 = ((Double) termWeights[d2].get(index)).doubleValue();
            }

            sum += Math.min(w1, w2);
        }

        return sum;
    }


    /*
     * same as "sumDocMin" but for the upper approximation
     */
    public double sumRoughDocMin(int d1, int d2)
    {
        if (roughWeights == null)
        {
            calculateRoughWeights();
        }

        double sum = 0;
        Set s1 = roughWeights[d1].keySet();
        Set s2 = roughWeights[d2].keySet();
        Set s = (s1.size() > s2.size()) ? s2
                                        : s1;

        for (Iterator it = s.iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            double w1 = 0;
            double w2 = 0;

            if (roughWeights[d1].containsKey(index))
            {
                w1 = ((Double) roughWeights[d1].get(index)).doubleValue();
            }

            if (roughWeights[d2].containsKey(index))
            {
                w2 = ((Double) roughWeights[d2].get(index)).doubleValue();
            }

            sum += Math.min(w1, w2);
        }

        return sum;
    }


    /*
     * calculates the sum over all document of the maximum of the weight of "t1" and the weight of "t2" in that document
     */
    public double sumTermMax(int t1, int t2)
    {
        double sum = 0;
        HashSet s = new HashSet(docWeights[t1].keySet());
        s.addAll(docWeights[t2].keySet());

        for (Iterator it = s.iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            double w1 = 0;
            double w2 = 0;

            if (docWeights[t1].containsKey(index))
            {
                w1 = ((Double) docWeights[t1].get(index)).doubleValue();
            }

            if (docWeights[t2].containsKey(index))
            {
                w2 = ((Double) docWeights[t2].get(index)).doubleValue();
            }

            sum += Math.max(w1, w2);
        }

        return sum;
    }


    /*
     * calculates the sum over all terms of the maximum of the weight of that term in "d1" and in "d2"
     */
    public double sumDocMax(int d1, int d2)
    {
        double sum = 0;
        HashSet s = new HashSet(termWeights[d1].keySet());
        s.addAll(termWeights[d2].keySet());

        for (Iterator it = s.iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            double w1 = 0;
            double w2 = 0;

            if (termWeights[d1].containsKey(index))
            {
                w1 = ((Double) termWeights[d1].get(index)).doubleValue();
            }

            if (termWeights[d2].containsKey(index))
            {
                w2 = ((Double) termWeights[d2].get(index)).doubleValue();
            }

            sum += Math.max(w1, w2);
        }

        return sum;
    }


    /*
     * same as "sumDocMax" but for the upper approximation
     */
    public double sumRoughDocMax(int d1, int d2)
    {
        if (roughWeights == null)
        {
            calculateRoughWeights();
        }

        double sum = 0;
        HashSet s = new HashSet(roughWeights[d1].keySet());
        s.addAll(roughWeights[d2].keySet());

        for (Iterator it = s.iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            double w1 = 0;
            double w2 = 0;

            if (roughWeights[d1].containsKey(index))
            {
                w1 = ((Double) roughWeights[d1].get(index)).doubleValue();
            }

            if (roughWeights[d2].containsKey(index))
            {
                w2 = ((Double) roughWeights[d2].get(index)).doubleValue();
            }

            sum += Math.max(w1, w2);
        }

        return sum;
    }


    /*
     * calculates the sum over all documents of the weight of the term "t"
     */
    public double sumTerm(int t)
    {
        double sum = 0;
        HashSet s = new HashSet(docWeights[t].keySet());

        for (Iterator it = s.iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            double w = 0;

            if (docWeights[t].containsKey(index))
            {
                ;
            }

            w = ((Double) docWeights[t].get(index)).doubleValue();

            sum += w;
        }

        return sum;
    }


    /*
     * calculates the sum over all terms of the weight of the document "d"
     */
    public double sumDoc(int d)
    {
        double sum = 0;
        Set s = termWeights[d].keySet();

        for (Iterator it = s.iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            double w = 0;

            if (termWeights[d].containsKey(index))
            {
                ;
            }

            w = ((Double) termWeights[d].get(index)).doubleValue();
            sum += w;
        }

        return sum;
    }


    /*
     * same as "sumDoc" but for the upper approximation
     */
    public double sumRoughDoc(int d)
    {
        if (roughWeights == null)
        {
            calculateRoughWeights();
        }

        double sum = 0;
        Set s = roughWeights[d].keySet();

        for (Iterator it = s.iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            double w = 0;

            if (roughWeights[d].containsKey(index))
            {
                ;
            }

            w = ((Double) roughWeights[d].get(index)).doubleValue();
            sum += w;
        }

        return sum;
    }


    /*
     * returns the weight of "term" in "doc"
     */
    public double weight(int doc, int term)
    {
        if (termWeights[doc].containsKey(new Integer(term)))
        {
            return ((Double) termWeights[doc].get(new Integer(term))).doubleValue();
        }
        else
        {
            return 0.0;
        }
    }


    /*
     * returns the number of terms
     */
    public int numberOfTerms()
    {
        return numberOfTerms;
    }


    /*
     * returns the original term that corresponds with the index "t"
     */
    public String originalTerm(int t)
    {
        return (String) terms.get(new Integer(t));
    }


    /*
     * returns a set with all document-indices
     */
    public Set getAllIndices()
    {
        HashSet s = new HashSet();

        for (int i = 0; i < numberOfDocuments; i++)
        {
            s.add(new Integer(i));
        }

        return s;
    }


    /*
     * returns a set with the indices of all documents containing at least 1 term
     */
    public Set getNonZeroIndices()
    {
        return nonZeroIndices;
    }


    /*
     * returns a set with all term-indices
     */
    public Set getTermIndices()
    {
        HashSet s = new HashSet();

        for (Iterator it = df.keySet().iterator(); it.hasNext();)
        {
            Integer index = (Integer) it.next();
            int number = ((Integer) df.get(index)).intValue();

            if (number >= MINTERMSUPP)
            {
                s.add(index);
            }
        }

        return s;
    }


    /*
     * Return a term->weight mapping for the document with index "index"
     */
    public Map getDocument(int index)
    {
        return termWeights[index];
    }


    /*
     * idem, for upper approximation
     */
    public Map getRoughDocument(int index)
    {
        if (roughWeights == null)
        {
            calculateRoughWeights();
        }

        return roughWeights[index];
    }


    /*
     * Returns the extend to which document "i" is more specific than document "j"
     */
    public double roughDocNT(int i, int j)
    {
        if (roughWeights == null)
        {
            calculateRoughWeights();
        }

        double sum = sumRoughDoc(i);

        if (sum > 0)
        {
            return sumRoughDocMin(i, j) / sum;
        }
        else
        {
            return 0;
        }
    }


    /*
     * Returns the extend to which term "i" is more specific than term "j"
     */
    public double termRT(int i, int j)
    {
        double sum = sumTermMax(i, j);

        if (sum > 0)
        {
            return sumTermMin(i, j) / sum;
        }
        else
        {
            return 0;
        }
    }


    /*
     * implements a softer variant of the completion of the term "term", in the sense of Zhang and Dong
     */
    public String complete(String term, Collection indices)
    {
        LabelTree links = new LabelTree();
        LabelTree rechts = new LabelTree();
        int totalNumber = 0;

        for (Iterator it1 = indices.iterator(); it1.hasNext();)
        {
            String s = (String) originalDocuments.get(((Integer) it1.next()).intValue());
            StringTokenizer tok = new StringTokenizer(s);
            LinkedList tokList = new LinkedList();

            for (; tok.hasMoreTokens();)
            {
                tokList.add(tok.nextToken().toLowerCase());
            }

            for (int i = 0; i < tokList.size(); i++)
            {
                String huidig = (String) tokList.get(i);

                if (huidig.equals(term))
                {
                    totalNumber++;
                    links.add(reverse(tokList.subList(Math.max(0, i - 6), i)));
                    rechts.add(
                        tokList.subList(
                            Math.min(tokList.size() - 1, i + 1), Math.min(tokList.size(), i + 6)
                        )
                    );
                }
            }
        }

        String l = links.getReverseComplete(totalNumber);
        String r = rechts.getComplete(totalNumber);

        if (totalNumber > 3)
        {
            return (l + " " + term + " " + r);
        }
        else
        {
            return term;
        }
    }


    private List reverse(List l)
    {
        LinkedList res = new LinkedList();

        for (ListIterator it = l.listIterator(); it.hasNext();)
        {
            res.add(0, it.next());
        }

        return res;
    }
}
