

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
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
    private List origineleDocumenten; //original snippets in String-representation
    private HashMap stemming; // translation: original term -> stemmed term
    private HashMap inverseStemming; // translation: stemmed term -> original ters 
    private HashSet stopwoorden; //set of stopwords, stemmed and in lowercase
    private HashSet queryTermen; //set of query terms, stemmed and in lowercase
    private HashMap [] snippets; //representation of the snippets as multiset, implemented as a HashMap
    private HashMap term2index; //translation: stemmed term -> term-index
    private HashMap termen; //translation: term-index -> original term 
    private HashMap df; //mapping: term-index -> document frequency (used for IDF-weighting)
    private HashMap specifiekereTermen; //mapping term-index -> indices of more specific terms
    private HashMap leiderwaarde; //mapping term-index -> leader value of the term
    private int aantalDocumenten; // number of documents
    private int aantalTermen; //number of terms
    private HashMap [] termGewichten; //contains for each snippet a HashMap term-weights
    private HashMap [] ruwGewichten; //idem, but for the upper approximation of a snippet
    private HashMap [] docGewichten; ///contains for each term a HashMap with snippet-weights
    private HashSet nulIndices; // set of snippet-indices that contain no term at all
    private HashSet nietNulIndices; // set of snippet-indices that contain at least 1 term
    private boolean stemmingGewenst = true; // true = stemming should be applied
    private boolean verwijderStopwoorden; // true = stopwords should be removed
    private int gewichtSchema; //  contains the used weighting scheme (binary, TF or TF-IDF)
    private final int MINTERMSUPP = 3;
    private final int TERMSPECSUPP = 1;
    private final double COMPLEET = 0.75;

    public SnippetParser(ArrayList documenten, ArrayList meta, ArrayList query)
    {
        this(documenten, meta, query, true, TFIDF);
    }


    public SnippetParser(
        List documenten, List meta, List query, boolean verwijderStopwoorden, int gewichtSchema
    )
    {
        try
        {
            this.verwijderStopwoorden = verwijderStopwoorden;
            this.gewichtSchema = gewichtSchema;
            bepaalStemming(meta);
            parseQuery(query);
            parseSnippets(documenten);
            aantalDocumenten = snippets.length;
            aantalTermen = df.size();
            bepaalGewichten();
            bepaalNietNulIndices();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
     * Applies stemming to to snippets
     */
    private void bepaalStemming(List meta)
    {
        stemming = new HashMap();
        inverseStemming = new HashMap();
        stopwoorden = new HashSet();

        if (stemmingGewenst)
        {
            for (ListIterator it = meta.listIterator(); it.hasNext();)
            {
                Element e = (Element) it.next();
                String origineel = e.getAttributeValue("t");
                String gestemd = e.getAttributeValue("s");
                String stopwoord = e.getAttributeValue("sw");

                if (stopwoord != null)
                {
                    stopwoorden.add(gestemd);
                }

                stemming.put(origineel, gestemd);

                if (inverseStemming.containsKey(gestemd))
                {
                    HashSet l = (HashSet) inverseStemming.get(gestemd);
                    l.add(origineel);
                    inverseStemming.put(gestemd, l);
                }
                else
                {
                    HashSet l = new HashSet();
                    l.add(origineel);
                    inverseStemming.put(gestemd, l);
                }
            }
        }
    }


    /*
     * Applies stemming to the query terms
     */
    private void parseQuery(List query)
    {
        queryTermen = new HashSet();

        for (Iterator it = query.listIterator(); it.hasNext();)
        {
            Element e = (Element) it.next();

            if (e != null)
            {
                StringTokenizer queryList = new StringTokenizer(e.getText());

                for (; queryList.hasMoreTokens();)
                {
                    String woord = queryList.nextToken();

                    if (stemming.containsKey(woord))
                    {
                        String s = (String) (stemming.get(woord));
                        queryTermen.add(s.toLowerCase());
                    }
                    else if (stemming.containsKey(woord.toLowerCase()))
                    {
                        String s = (String) (stemming.get(woord.toLowerCase()));
                        queryTermen.add(s.toLowerCase());
                    }
                    else
                    {
                        queryTermen.add(woord.toLowerCase());
                    }
                }
            }
        }
    }


    /*
     * snippets are parsed
     */
    private void parseSnippets(List documenten)
    {
        int termIndex = 0;
        term2index = new HashMap();
        termen = new HashMap();
        df = new HashMap();
        origineleDocumenten = new ArrayList();
        snippets = new HashMap[documenten.size()];

        for (int i = 0; i < documenten.size(); i++)
        {
            Element snippet = ((Element) documenten.get(i)).getChild("snippet");
            Element title = ((Element) documenten.get(i)).getChild("title");
            StringBuffer tekst = new StringBuffer();

            if (title != null)
            {
                tekst.append(title.getText());
                tekst.append(" ");
            }

            if (snippet != null)
            {
                tekst.append(snippet.getText());
            }

            snippets[i] = new HashMap();

            StringTokenizer st = new StringTokenizer(tekst.toString());
            StringBuffer origineleString = new StringBuffer(tekst.toString());

            for (int j = 0; j < origineleString.length(); j++)
            {
                char ch = origineleString.charAt(j);

                if (
                    (ch == ',') || (ch == ')') || (ch == '(') || (ch == ';') || (ch == '.')
                        || (ch == '!') || (ch == '?') || (ch == ':')
                )
                {
                    origineleString.setCharAt(j, ' ');
                }
            }

            origineleDocumenten.add(origineleString.toString());

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

                String origineleS = s;

                if (stemming.containsKey(s))
                {
                    s = (String) stemming.get(s.toString());
                }

                boolean geldigwoord = true;

                if (s.length() < 2)
                {
                    geldigwoord = false;
                }
                else if (queryTermen.contains(s))
                {
                    geldigwoord = false;
                }
                else if (verwijderStopwoorden && stopwoorden.contains(s))
                {
                    geldigwoord = false;
                }
                else
                {
                    for (int k = 0; k < s.length(); k++)
                    {
                        if (!Character.isLetter(s.charAt(k)))
                        {
                            geldigwoord = false;

                            break;
                        }
                    }
                }

                if (geldigwoord)
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
                        int waarde = ((Integer) df.get(new Integer(index))).intValue();
                        df.put(new Integer(index), new Integer(waarde + 1));
                    }
                    else
                    {
                        termen.put(new Integer(termIndex), origineleS);
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
    private void bepaalGewichten()
    {
        termGewichten = new HashMap[aantalDocumenten];
        docGewichten = new HashMap[aantalTermen];

        for (int i = 0; i < aantalTermen; i++)
        {
            docGewichten[i] = new HashMap();
        }

        for (int i = 0; i < aantalDocumenten; i++)
        {
            termGewichten[i] = new HashMap();

            double som = 0;

            for (Iterator it = snippets[i].keySet().iterator(); it.hasNext();)
            {
                String term = (String) it.next();
                int termIndex = ((Integer) term2index.get(term)).intValue();
                int freq = ((Integer) snippets[i].get(term)).intValue();
                int idf = ((Integer) df.get(new Integer(termIndex))).intValue();
                double waarde = 0;

                if (idf >= MINTERMSUPP)
                {
                    if (gewichtSchema == BINAIR)
                    {
                        waarde = (freq > 0) ? 1
                                            : 0;
                    }
                    else if (gewichtSchema == TF)
                    {
                        waarde = freq;
                    }
                    else if (gewichtSchema == TFIDF)
                    {
                        waarde = freq * Math.log(aantalDocumenten / idf);
                    }
                    else
                    {
                        System.err.println("FOUTIEF GEWICHTSCHEMA");
                    }
                }

                som += (waarde * waarde);
                termGewichten[i].put(new Integer(termIndex), new Double(waarde));

                if (gewichtSchema == BINAIR)
                {
                    docGewichten[termIndex].put(new Integer(i), new Double(waarde));
                }
            }

            if (gewichtSchema != BINAIR)
            { // normalisation -> all weigths in [0,1]
                som = Math.sqrt(som);

                for (Iterator it = termGewichten[i].keySet().iterator(); it.hasNext();)
                {
                    Integer termIndex = (Integer) it.next();
                    double waarde = ((Double) termGewichten[i].get(termIndex)).doubleValue();
                    waarde /= som;
                    termGewichten[i].put(termIndex, new Double(waarde));
                    docGewichten[termIndex.intValue()].put(new Integer(i), new Double(waarde));
                }
            }
        }
    }


    /*
     * Determine the indices of the document that contain no term at all
     */
    private void bepaalNietNulIndices()
    {
        nulIndices = new HashSet();
        nietNulIndices = new HashSet();

        for (int i = 0; i < termGewichten.length; i++)
        {
            if (termGewichten[i].size() == 0)
            {
                nulIndices.add(new Integer(i));
            }
            else
            {
                nietNulIndices.add(new Integer(i));
            }
        }
    }


    /*
     * Calculate the upper approximation of the snippets in the sense of rough set theory
     */
    public void bepaalRuwGewichten()
    {
        ruwGewichten = new HashMap[aantalDocumenten];

        for (int i = 0; i < aantalDocumenten; i++)
        {
            HashMap ruwTermen = new HashMap();

            for (Iterator it1 = termGewichten[i].keySet().iterator(); it1.hasNext();)
            {
                //itereer over alle termen in het document i
                int term = ((Integer) it1.next()).intValue();
                double termWaarde = ((Double) termGewichten[i].get(new Integer(term))).doubleValue();
                HashMap spec = specifiekereTermen(term);

                for (Iterator it2 = spec.keySet().iterator(); it2.hasNext();)
                {
                    //itereer over alle termen die specifieker zijn dan term
                    int nieuweTerm = ((Integer) it2.next()).intValue();
                    double oudeWaarde = 0;

                    if (ruwTermen.containsKey(new Integer(nieuweTerm)))
                    {
                        oudeWaarde = ((Double) ruwTermen.get(new Integer(nieuweTerm))).doubleValue();
                    }

                    double specWaarde = ((Double) spec.get(new Integer(nieuweTerm))).doubleValue();
                    double nieuweWaarde = Math.min(termWaarde, specWaarde);

                    if (nieuweWaarde > oudeWaarde)
                    {
                        ruwTermen.put(new Integer(nieuweTerm), new Double(nieuweWaarde));
                    }
                }
            }

            ruwGewichten[i] = ruwTermen;
        }
    }


    /*
     * Calculate the leader value of the term "t"
     */
    public double leiderwaarde(int t)
    {
        if (leiderwaarde == null)
        {
            leiderwaarde = new HashMap();
        }

        if (leiderwaarde.keySet().contains(new Integer(t)))
        {
            return ((Double) leiderwaarde.get(new Integer(t))).intValue();
        }

        HashMap spec = specifiekereTermen(t);
        double res = 0;

        for (Iterator it = spec.keySet().iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            res += ((Double) spec.get(index)).doubleValue();
        }

        leiderwaarde.put(new Integer(t), new Double(res));

        return res;
    }


    /*
     * Determine the terms that are more specific than "t"
     */
    private HashMap specifiekereTermen(int t)
    {
        if (specifiekereTermen == null)
        {
            specifiekereTermen = new HashMap();
        }

        if (specifiekereTermen.keySet().contains(new Integer(t)))
        {
            return (HashMap) specifiekereTermen.get(new Integer(t));
        }

        HashSet universum = new HashSet();
        HashMap resultaat = new HashMap();

        for (Iterator it = docGewichten[t].keySet().iterator(); it.hasNext();)
        {
            int doc = ((Integer) it.next()).intValue();
            universum.addAll(termGewichten[doc].keySet());
        }

        for (Iterator it = universum.iterator(); it.hasNext();)
        {
            Integer termIndex = (Integer) it.next();

            if (t == termIndex.intValue())
            {
                resultaat.put(termIndex, new Double(1));
            }
            else
            {
                double specWaarde = specifiekerTerm(termIndex.intValue(), t);

                if (specWaarde > 0.30)
                {
                    resultaat.put(termIndex, new Double(specWaarde));
                }
            }
        }

        specifiekereTermen.put(new Integer(t), resultaat);

        return resultaat;
    }


    /*
     * Calculate to which extend "t1" is more specific than "t2"
     */
    private double specifiekerTerm(int t1, int t2)
    {
        double som = somTerm(t1);
        double somMin = somTermMin(t1, t2);

        if ((som > 0) && (somMin >= TERMSPECSUPP))
        {
            return somMin / som;
        }
        else
        {
            return 0;
        }
    }


    /*
     * calculates the sum over all document of the minimum of the weight of "t1" and the weight of "t2" in that document
     */
    public double somTermMin(int t1, int t2)
    {
        double som = 0;
        Set s1 = docGewichten[t1].keySet();
        Set s2 = docGewichten[t2].keySet();
        Set s = (s1.size() > s2.size()) ? s2
                                        : s1;

        for (Iterator it = s.iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            double w1 = 0;
            double w2 = 0;

            if (docGewichten[t1].containsKey(index))
            {
                w1 = ((Double) docGewichten[t1].get(index)).doubleValue();
            }

            if (docGewichten[t2].containsKey(index))
            {
                w2 = ((Double) docGewichten[t2].get(index)).doubleValue();
            }

            som += Math.min(w1, w2);
        }

        return som;
    }


    /*
     * calculates the sum over all terms of the minimum of the weight of that term in "d1" and in "d2"
     */
    public double somDocMin(int d1, int d2)
    {
        double som = 0;
        Set s1 = termGewichten[d1].keySet();
        Set s2 = termGewichten[d2].keySet();
        Set s = (s1.size() > s2.size()) ? s2
                                        : s1;

        for (Iterator it = s.iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            double w1 = 0;
            double w2 = 0;

            if (termGewichten[d1].containsKey(index))
            {
                w1 = ((Double) termGewichten[d1].get(index)).doubleValue();
            }

            if (termGewichten[d2].containsKey(index))
            {
                w2 = ((Double) termGewichten[d2].get(index)).doubleValue();
            }

            som += Math.min(w1, w2);
        }

        return som;
    }


    /*
     * same as "somDocMin" but for the upper approximation
     */
    public double somRuwDocMin(int d1, int d2)
    {
        if (ruwGewichten == null)
        {
            bepaalRuwGewichten();
        }

        double som = 0;
        Set s1 = ruwGewichten[d1].keySet();
        Set s2 = ruwGewichten[d2].keySet();
        Set s = (s1.size() > s2.size()) ? s2
                                        : s1;

        for (Iterator it = s.iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            double w1 = 0;
            double w2 = 0;

            if (ruwGewichten[d1].containsKey(index))
            {
                w1 = ((Double) ruwGewichten[d1].get(index)).doubleValue();
            }

            if (ruwGewichten[d2].containsKey(index))
            {
                w2 = ((Double) ruwGewichten[d2].get(index)).doubleValue();
            }

            som += Math.min(w1, w2);
        }

        return som;
    }


    /*
     * calculates the sum over all document of the maximum of the weight of "t1" and the weight of "t2" in that document
     */
    public double somTermMax(int t1, int t2)
    {
        double som = 0;
        HashSet s = new HashSet(docGewichten[t1].keySet());
        s.addAll(docGewichten[t2].keySet());

        for (Iterator it = s.iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            double w1 = 0;
            double w2 = 0;

            if (docGewichten[t1].containsKey(index))
            {
                w1 = ((Double) docGewichten[t1].get(index)).doubleValue();
            }

            if (docGewichten[t2].containsKey(index))
            {
                w2 = ((Double) docGewichten[t2].get(index)).doubleValue();
            }

            som += Math.max(w1, w2);
        }

        return som;
    }


    /*
     * calculates the sum over all terms of the maximum of the weight of that term in "d1" and in "d2"
     */
    public double somDocMax(int d1, int d2)
    {
        double som = 0;
        HashSet s = new HashSet(termGewichten[d1].keySet());
        s.addAll(termGewichten[d2].keySet());

        for (Iterator it = s.iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            double w1 = 0;
            double w2 = 0;

            if (termGewichten[d1].containsKey(index))
            {
                w1 = ((Double) termGewichten[d1].get(index)).doubleValue();
            }

            if (termGewichten[d2].containsKey(index))
            {
                w2 = ((Double) termGewichten[d2].get(index)).doubleValue();
            }

            som += Math.max(w1, w2);
        }

        return som;
    }


    /*
     * same as "somDocMax" but for the upper approximation
     */
    public double somRuwDocMax(int d1, int d2)
    {
        if (ruwGewichten == null)
        {
            bepaalRuwGewichten();
        }

        double som = 0;
        HashSet s = new HashSet(ruwGewichten[d1].keySet());
        s.addAll(ruwGewichten[d2].keySet());

        for (Iterator it = s.iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            double w1 = 0;
            double w2 = 0;

            if (ruwGewichten[d1].containsKey(index))
            {
                w1 = ((Double) ruwGewichten[d1].get(index)).doubleValue();
            }

            if (ruwGewichten[d2].containsKey(index))
            {
                w2 = ((Double) ruwGewichten[d2].get(index)).doubleValue();
            }

            som += Math.max(w1, w2);
        }

        return som;
    }


    /*
     * calculates the sum over all documents of the weight of the term "t"
     */
    public double somTerm(int t)
    {
        double som = 0;
        HashSet s = new HashSet(docGewichten[t].keySet());

        for (Iterator it = s.iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            double w = 0;

            if (docGewichten[t].containsKey(index))
            {
                ;
            }

            w = ((Double) docGewichten[t].get(index)).doubleValue();

            som += w;
        }

        return som;
    }


    /*
     * calculates the sum over all terms of the weight of the document "d"
     */
    public double somDoc(int d)
    {
        double som = 0;
        Set s = termGewichten[d].keySet();

        for (Iterator it = s.iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            double w = 0;

            if (termGewichten[d].containsKey(index))
            {
                ;
            }

            w = ((Double) termGewichten[d].get(index)).doubleValue();
            som += w;
        }

        return som;
    }


    /*
     * same as "somDoc" but for the upper approximation
     */
    public double somRuwDoc(int d)
    {
        if (ruwGewichten == null)
        {
            bepaalRuwGewichten();
        }

        double som = 0;
        Set s = ruwGewichten[d].keySet();

        for (Iterator it = s.iterator(); it.hasNext();)
        {
            Integer index = ((Integer) it.next());
            double w = 0;

            if (ruwGewichten[d].containsKey(index))
            {
                ;
            }

            w = ((Double) ruwGewichten[d].get(index)).doubleValue();
            som += w;
        }

        return som;
    }


    /*
     * returns the weight of "term" in "doc"
     */
    public double gewicht(int doc, int term)
    {
        if (termGewichten[doc].containsKey(new Integer(term)))
        {
            return ((Double) termGewichten[doc].get(new Integer(term))).doubleValue();
        }
        else
        {
            return 0.0;
        }
    }


    /*
     * returns the number of terms
     */
    public int aantalTermen()
    {
        return aantalTermen;
    }


    /*
     * returns the original term that corresponds with the index "t"
     */
    public String origineleTerm(int t)
    {
        return (String) termen.get(new Integer(t));
    }


    /*
     * returns a set with all document-indices
     */
    public Set geefAlleIndices()
    {
        HashSet s = new HashSet();

        for (int i = 0; i < aantalDocumenten; i++)
        {
            s.add(new Integer(i));
        }

        return s;
    }


    /*
     * returns a set with the indices of all documents containing at least 1 term
     */
    public Set geefNietNulIndices()
    {
        return nietNulIndices;
    }


    /*
     * returns a set with all term-indices
     */
    public Set geefTermIndices()
    {
        HashSet s = new HashSet();

        for (Iterator it = df.keySet().iterator(); it.hasNext();)
        {
            Integer index = (Integer) it.next();
            int aantal = ((Integer) df.get(index)).intValue();

            if (aantal >= MINTERMSUPP)
            {
                s.add(index);
            }
        }

        return s;
    }


    /*
     * Return a term->weight mapping for the document with index "index"
     */
    public Map geefDocument(int index)
    {
        return termGewichten[index];
    }


    /*
     * idem, for upper approximation
     */
    public Map geefRuwDocument(int index)
    {
        if (ruwGewichten == null)
        {
            bepaalRuwGewichten();
        }

        return ruwGewichten[index];
    }


    /*
     * Returns the extend to which document "i" is more specific than document "j"
     */
    public double ruwDocNT(int i, int j)
    {
        if (ruwGewichten == null)
        {
            bepaalRuwGewichten();
        }

        double som = somRuwDoc(i);

        if (som > 0)
        {
            return somRuwDocMin(i, j) / som;
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
        double som = somTermMax(i, j);

        if (som > 0)
        {
            return somTermMin(i, j) / som;
        }
        else
        {
            return 0;
        }
    }


    /*
     * implements a softer variant of the completion of the term "term", in the sense of Zhang and Dong
     */
    public String compleet(String term, Collection indices)
    {
        LabelBoom links = new LabelBoom();
        LabelBoom rechts = new LabelBoom();
        int totaalAantal = 0;

        for (Iterator it1 = indices.iterator(); it1.hasNext();)
        {
            String s = (String) origineleDocumenten.get(((Integer) it1.next()).intValue());
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
                    totaalAantal++;
                    links.add(reverse(tokList.subList(Math.max(0, i - 6), i)));
                    rechts.add(
                        tokList.subList(
                            Math.min(tokList.size() - 1, i + 1), Math.min(tokList.size(), i + 6)
                        )
                    );
                }
            }
        }

        String l = links.geefReverseCompleet(totaalAantal);
        String r = rechts.geefCompleet(totaalAantal);

        if (totaalAantal > 3)
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
