package com.paulodev.carrot.treeSnippetMiner.treeAnalyser;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Poznań University of Technology</p>
 * @author Paweł Kowalik
 * @version 1.0
 */

import java.util.*;

import org.jdom.*;
import org.jdom.output.*;
import com.paulodev.carrot.treeExtractor.extractors.*;
import com.paulodev.carrot.treeSnippetMiner.treeAnalyser.snippetTokenizer.*;
import com.paulodev.carrot.treeSnippetMiner.treeAnalyser.tokenFeature.*;
import com.paulodev.carrot.util.html.parser.*;
import java.net.*;
import java.io.*;

public class treeAnalyser
{
    public static final String URL = "url";
    public static final String DESC = "description";
    public static final String TITLE = "title";

    private TokenFeatureCalc wordCount;
    private TokenFeatureCalc length;
    private TokenFeatureCalc tfIdf;
    private TokenFeatureCalc isURL;
    private TokenFeatureCalc URLTfIdf;

    private Element ROOT;
    private HTMLTree page;
    private String resPath;
    private double entrophyMaxLevel;

    private Hashtable results;

    private Vector featureCalcs = new Vector();

    public treeAnalyser(Element freqTreeRoot, HTMLTree page, String resPath, double entrophyMaxLevel)
    {
        this.ROOT = freqTreeRoot;
        this.page = page;
        this.resPath = resPath;
        this.entrophyMaxLevel = entrophyMaxLevel;
        results = new Hashtable();
        featureCalcs.add(length = new AvgLengthCalc());
        featureCalcs.add(wordCount = new AvgWordCount());
        featureCalcs.add(isURL = new IsURLCalc());
        featureCalcs.add(tfIdf = new TfIdf());
        featureCalcs.add(URLTfIdf = new URLTfIdf());
    }

    public boolean checkTitle(Token t, Vector results) {
        return
            t.anchor &&  t.type == Token.TYPE_NODE
            && (wordCount.calcValue(t, results) > 2)
            && (tfIdf.calcValue(t, results) > 0.01)
            && (tfIdf.calcValue(t, results) < entrophyMaxLevel);
            /*&& (isURL.calcValue(t, results) < 0.3);*/
    }

    public boolean checkDescription(Token t, Vector results) {
        return !t.anchor && (wordCount.calcValue(t, results) > 2)
            && (tfIdf.calcValue(t, results) > 0.01)
            && (tfIdf.calcValue(t, results) < entrophyMaxLevel)
            /*&& (isURL.calcValue(t, results) < 0.3)*/;
    }

    public boolean checkURL(Token t, Vector results) {
        return ((t.type == Token.TYPE_ATTR || t.type == Token.TYPE_NODE)
            && isURL.calcValue(t, results) > 0.6)
            && (URLTfIdf.calcValue(t, results) > 0.6);
    }

    public Element process()
    {
        ROOT.setName("snippet");
        snippetTokenizer tok = new snippetTokenizer();
        Enumeration temp = tok.discoverTokens(ROOT);
        while (temp.hasMoreElements())
        {
            Token token = (Token) temp.nextElement();
            System.out.println("Token: " + token.name + " [" + token.beginScope + ", " + token.endScope + "]");
            results.put(token, new Vector());
        }
        Element el = new Element("extractor");
        el.getChildren().add(ROOT);
        XMLOutputter ou = new XMLOutputter("  ", true);
        System.out.println("Invoking tree extractor");
        System.out.println(ou.outputString(el));
        TreeExtractor extr = new TreeExtractor(el, results.keys());
        Enumeration en = extr.getResults(page, 0);
        while (en.hasMoreElements())
        {
            TreeExtractor.SnippetBuilder bldr = (TreeExtractor.SnippetBuilder)
                en.nextElement();
            Enumeration items = extr.getSearchItems();
            while (items.hasMoreElements())
            {
                SearchItem it = (SearchItem)items.nextElement();
                Vector res = (Vector)results.get(it.getName());
                res.add(bldr.getValueForItem(it.getName()));
            }
        }

        clearResultTree(results.keys(), results);
        Vector tokens = new Vector(results.keySet());

        Enumeration e = tokens.elements();
        while (e.hasMoreElements()) {
            Token key = (Token) e.nextElement();
            Vector res = (Vector)results.get(key.name);
            double wc = wordCount.calcValue(key, res);
            if (wc < 1) {
                System.out.println("Removing: " + key.name);
                tokens.remove(key);
            }
        }
        Vector url = new Vector();
        Vector title = new Vector();
        Vector desc = new Vector();
        e = tokens.elements();
        while (e.hasMoreElements())
        {
            Token key = (Token) e.nextElement();
            Vector res = (Vector)results.get(key.name);
            Enumeration vals = featureCalcs.elements();
            while (vals.hasMoreElements())
            {
                TokenFeatureCalc calc = (TokenFeatureCalc)vals.nextElement();
                System.out.println(key + "[" + key.anchor + "]: " + calc.GetName() + ", value: " +
                                   calc.calcValue(key, res));
            }
            if (checkDescription(key, res)) {
                desc.add(key);
                key.important = true;
            } else if (checkTitle(key, res)) {
                title.add(key);
                key.important = true;
            } else if (checkURL(key, res)) {
                url.add(key);
                key.important = true;
            }
        }
        for (int i = 0; i < desc.size(); i++)
            System.out.println("Desc: " + desc.get(i));
        for (int i = 0; i < title.size(); i++)
            System.out.println("Title: " + title.get(i));
        for (int i = 0; i < url.size(); i++)
            System.out.println("URL: " + url.get(i));
        System.out.println("*** Postprocessing");
        // Tytul - tylko pierwszy wezel
        if (title.size() > 1) {
            Token min_url = (Token) title.get(0);
            for (int i = 1; i < title.size(); i++) {
                Token toCheck = (Token)title.get(i);
                if (min_url.beginScope > toCheck.beginScope)
                    min_url = (Token) title.get(i);
            }
            title.clear();
            title.add(min_url);
        }
        // Wywalenie opisow ktore zawieraja tytul
        for (int i = desc.size() - 1; i >= 0; i--) {
            boolean toDel = false;
            Token toCheck = (Token) desc.get(i);
            for (int j = 0; j < title.size(); j++) {
                if (toCheck.inScope((Token) title.get(j))) {
                    System.out.println("Removing: " + toCheck.name + " cause it has " + ((Token) title.get(j)).name + " inside");
                    toDel = true;
                    break;
                }
            }
            if (toDel) {
                desc.remove(i);
            }
        }
        // Sprowadzenie opisow na najnizszy poziom
        for (int i = desc.size() - 1; i >= 0; i--) {
            boolean toDel = false;
            Token toCheck = (Token) desc.get(i);
            for (int j = 0; j < desc.size(); j++) {
                  if (toCheck.inScope((Token) desc.get(j))) {
                    System.out.println("Removing: " + toCheck.name + " cause it has " + ((Token) desc.get(j)).name + " inside");
                    toDel = true;
                    break;
                }
            }
            if (toDel) {
                desc.remove(i);
            }
        }
        // Odnalezienie optymalnego url-a
        if (url.size() > 1) {
            Token min_url = (Token) url.get(0);
            for (int i = 1; i < url.size(); i++) {
                Token toCheck = (Token)url.get(i);
                if (isURL.calcValue( toCheck, (Vector)results.get(toCheck.name)) <
                    isURL.calcValue(min_url, (Vector)results.get(min_url.name)))
                {
                    min_url = (Token) url.get(i);
                }
            }
            url.clear();
            url.add(min_url);
        }

        for (int i = 0; i < desc.size(); i++)
            System.out.println("Desc: " + desc.get(i));
        for (int i = 0; i < title.size(); i++)
            System.out.println("Title: " + title.get(i));
        for (int i = 0; i < url.size(); i++)
            System.out.println("URL: " + url.get(i));

        buildFinalTree(title.elements(), url.elements(),
            desc.elements());
        removeUnnecessaryTokens(results.keys(), results);
        System.out.println("*** Final tree: ***");
        System.out.println(ou.outputString(el));
        System.out.println("*** Check: ***");
        Check(el);
        return ROOT;
   }

    private void addTokenToElement(Token t, String name) {
    switch (t.type) {
            case Token.TYPE_ATTR:
                t.start.setAttribute(name, "attribute:" + t.attrName);
                break;
            case Token.TYPE_ZONE:
                if (t.start.getAttribute(name) != null && ((Attribute)t.start.getAttribute(name)).getValue().equals("endBefore"))
 //                   t.start.removeAttribute(name);
                    ;
                else
                    t.start.setAttribute(name, "beginOn");
                if (t.end != null) {
                    t.end.setAttribute(name, "endBefore");
                }
                break;
            case Token.TYPE_NODE:
                t.start.setAttribute(name, "inside");
                break;
        }
    }

    private void clearResultTree(Enumeration tokens, Hashtable results) {
        while (tokens.hasMoreElements())
        {
            Token toCheck = (Token)tokens.nextElement();
            toCheck.start.removeAttribute(toCheck.name);
            if (toCheck.end != null)
                toCheck.end.removeAttribute(toCheck.name);
        }
    }

    private void removeUnnecessaryTokens(Enumeration tokens, Hashtable results) {
        while (tokens.hasMoreElements())
        {
            Token toCheck = (Token)tokens.nextElement();
            if (toCheck.type == Token.TYPE_NODE
                    && (!toCheck.important)
                    && wordCount.calcValue(toCheck, (Vector)results.get(toCheck.name)) < 4) {
                tryRemoveElement(toCheck.start);
                if (toCheck.type == Token.TYPE_ZONE)
                    tryRemoveElement(toCheck.end);
            }
        }
    }

    private void tryRemoveElement(Element e) {
        if ((e.getName().equalsIgnoreCase("b") ||
            e.getName().equalsIgnoreCase("i") ||
            e.getName().equalsIgnoreCase("span") ||
            e.getName().equalsIgnoreCase("u") ||
            e.getName().equalsIgnoreCase("font"))
            && e.getAttribute(TITLE) == null
            && e.getAttribute(URL) == null
            && e.getAttribute(DESC) == null) {
            e.getParent().getChildren().remove(e);
        }


    }

    private void Check(Element extractor) {
        Vector tokens = new Vector();
        tokens.add(URL);
        tokens.add(TITLE);
        tokens.add(DESC);
        try
        {
            FileOutputStream ou = new FileOutputStream(resPath + "snippets.txt");
            TreeExtractor extr = new TreeExtractor(extractor, tokens.elements());
            Enumeration en = extr.getResults(page, 0);
            while (en.hasMoreElements())
            {
                TreeExtractor.SnippetBuilder bldr = (TreeExtractor.
                    SnippetBuilder)
                    en.nextElement();
                ou.write( ("TITLE: " + bldr.getValueForItem(TITLE) + "\n").
                         getBytes());
                try
                {
                    ou.write( ("URL: " +
                               TreeExtractor.clearURL(bldr.
                        getValueForItem(URL)) + "\n").getBytes());
                }
                catch (MalformedURLException ex)
                {
                    ou.write( ("URL: \n").getBytes());
                }
                ou.write( ("DESC: " + bldr.getValueForItem(DESC) + "\n").
                         getBytes());
                ou.write( ("***\n").getBytes());
            }
        }
        catch (IOException ex1)
        {
        }
    }

    private void buildFinalTree (Enumeration title, Enumeration url, Enumeration desc) {
        while (title.hasMoreElements())
        {
            Token toAdd = (Token) title.nextElement();
            addTokenToElement(toAdd, TITLE);
        }
        while (url.hasMoreElements())
        {
            Token toAdd = (Token) url.nextElement();
            addTokenToElement(toAdd, URL);
        }
        while (desc.hasMoreElements())
        {
            Token toAdd = (Token) desc.nextElement();
            addTokenToElement(toAdd, DESC);
        }
    }
}