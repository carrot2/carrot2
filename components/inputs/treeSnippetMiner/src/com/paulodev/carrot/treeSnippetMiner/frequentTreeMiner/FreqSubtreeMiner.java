package com.paulodev.carrot.treeSnippetMiner.frequentTreeMiner;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Poznań University of Technology</p>
 * @author Paweł Kowalik
 * @version 1.0
 */

import java.io.*;
import java.util.*;

import org.jdom.*;
import com.paulodev.carrot.util.html.parser.*;

public class FreqSubtreeMiner
{
    private HTMLTree page;
    private String resPath;
    private int numSnippets;
    private int minSnippetsToSupport;
    private int maxLevelDistance = 5;
    private int maxSize = 0;
    private int maxSnippetSize;
    private double minSupport;
    private TreeExpansion maxExpansion = null;
    public static final double START_SUPPORT = 1;
    public double setupMinSupport = 0.8;
    public static final double SNIPPET_SIZE_MUL = 2;

    private Hashtable dictionary = new Hashtable();

    public FreqSubtreeMiner(HTMLTree page, int numSnippets, double minSupport, String resPath)
    {
        this.page = page;
        this.resPath = resPath;
        this.numSnippets = numSnippets;
        this.setupMinSupport = minSupport;
        this.minSupport = 1;
        this.minSnippetsToSupport = (int)Math.ceil(numSnippets * minSupport);
        this.maxSnippetSize = (int) (SNIPPET_SIZE_MUL *
                                     ( (double)page.getAllNodes().size() /
                                      (double)numSnippets));
    }

    private void setMinSupport(double minSup) {
        this.minSupport = minSup;
        this.minSnippetsToSupport = (int)Math.ceil(numSnippets * minSup);
    }

    public int getNumSnippets()
    {
        return numSnippets;
    }

    public int getMaxSnippetSize()
    {
        return maxSnippetSize;
    }

    public int getMinSnippetsToSupprt()
    {
        return minSnippetsToSupport;
    }

    public int getMaxLevelDistance()
    {
        return maxLevelDistance;
    }

    public int getMaxSize()
    {
        return maxSize;
    }

    public void setMaxExpansion(TreeExpansion ex)
    {
        if (ex.getTreeSize() >
            (maxExpansion != null ? maxExpansion.getTreeSize() : 0))
        {
            maxExpansion = ex;
            maxSize = ex.getTreeSize();
        }
    }

    public Enumeration getDictionary()
    {
        return dictionary.elements();
    }

    public DictNodeOccurence getDictionaryForTag(String name)
    {
        return (DictNodeOccurence)dictionary.get(name);
    }

    public Element mineFrequentSubtree()
    {
        boolean direction = true;
        // set support to build dictionary
        setMinSupport(setupMinSupport);
        for (int i = 0; i < page.getAllNodes().size(); i++)
        {
            HTMLNode node = (HTMLNode)page.getAllNodes().get(i);

            // Skip empty and root nodes
            if (node == null || node.getLevel() < 2)
            {
                continue;
            }
            DictNodeOccurence n = (DictNodeOccurence)dictionary.get(node.
                getClassedName());
            if (n == null)
            {
                // first time in dictionary
                n = new DictNodeOccurence( ( (HTMLNode)page.getAllNodes().get(i)).
                                          getClassedName(), false);
                dictionary.put(n.getName(), n);
            }
            n.addOccurence( (HTMLNode)page.getAllNodes().get(i));
        }
        Enumeration e = dictionary.elements();
        while (e.hasMoreElements())
        {
            DictNodeOccurence n = (DictNodeOccurence)e.nextElement();
            n.setMaxOccurences(n.getValue() / minSnippetsToSupport);
            // delete all non-frequent tags from dictionary and the <content> (but leave the root node)
            if (n.getValue() < minSnippetsToSupport ||
                n.getName().equals("content") || n.getName().equals("!--"))
            {
                dictionary.remove(n.getName());
            }
        }

        constructL2Dictionary();
        // Create artificial root node's dictionary
        DictNodeOccurence rootNodeDict = new DictNodeOccurence("__ROOT__", true);
        rootNodeDict.addOccurence(page.getRootNode());

        Vector L1Expansions = new Vector();
        //initialize 1st level trees
        e = dictionary.elements();
        while (e.hasMoreElements())
        {
            DictNodeOccurence n = (DictNodeOccurence)e.nextElement();
            TreeExpansion nowe = new TreeExpansion(direction, this, n, rootNodeDict);
            L1Expansions.add(nowe);
        }

        // add connections from root node dict to any other
        e = dictionary.elements();
        while (e.hasMoreElements())
        {
            rootNodeDict.addPossibleChild( ( (DictNodeOccurence)e.nextElement()).
                                          getName());
        }
        dictionary.put(rootNodeDict.getName(), rootNodeDict);

        // only 100% support
        setMinSupport(START_SUPPORT);

        e = L1Expansions.elements();
        while (e.hasMoreElements())
        {
            TreeExpansion toEnum = (TreeExpansion)e.nextElement();

            if ( (maxExpansion == null) ||
                ( ( (DictNodeOccurence)dictionary.get(toEnum.getName())).
                 getMaxOccurences() -
                 maxExpansion.getTagUsageCount(toEnum.getName()) >
                 0))
            {
                toEnum.expandTrees();
            }
        }

        if (maxExpansion != null)
        {
            resetPageStyle();
            System.out.println("*** MAX ROOTED***");
            printResult(maxExpansion);
            // allow zero level extension
            maxExpansion.setZeroLevelExpands(true);
            maxExpansion.calcBound();
            maxExpansion.expandTrees();
            System.out.println("*** MAX 2nd Stage***");
            printResult(maxExpansion);
            maxExpansion.truncateOccurences();
            System.out.println("*** MAX 2nd Stage Truncated***");
            printResult(maxExpansion);
//            saveProcessedPage("c:/result.html");
/*            TreeExpansion ex = maxExpansion.getTreeInDirection(this,  false);
            System.out.println("*** Rotated Tree***");
            printResult(ex);
            ex.calcBound();
            ex.expandTrees();
            maxExpansion = ex.getTreeInDirection(this, true);*/
            discoverOptionalElements(maxExpansion);
            printResult(maxExpansion);
//            saveProcessedPage("c:/result.html");
            return processStructure(maxExpansion);
        }
        return null;
    }

    private void discoverOptionalElements(TreeExpansion e) {
//		maxExpansion.removeIrrelevantOccurences();
        TreeExpansion lastMaxExp = maxExpansion;
        ResultNode res = maxExpansion.getResult();
        setMinSupport(setupMinSupport);
        TreeExpansion last = e;
        TreeExpansion curr = e.getPrevExpansion();
        while (!curr.isRoot()) {
            last.boundPreviousExpansion();
            maxExpansion = curr;
            maxSize = curr.getTreeSize();
            curr.expandTrees();
            if (maxExpansion != curr) {
                maxExpansion.markResults();
                res = maxExpansion.MergeResult(res, curr);
            }
            last = curr;
            curr = curr.getPrevExpansion();
        }
        System.out.println("AFTER OPTIONAL>>");
        System.out.println(res);
        maxExpansion = res.getRoot().constructTreeExpansion(this, true);
    }

    private void constructL2Dictionary()
    {
        Enumeration eOut = getDictionary();
        while (eOut.hasMoreElements())
        {
            DictNodeOccurence outer = (DictNodeOccurence)eOut.nextElement();
            outer.clearPossibleChildren();
            Enumeration eIn = getDictionary();
            while (eIn.hasMoreElements())
            {
                DictNodeOccurence inner = (DictNodeOccurence)eIn.nextElement();
                if (outer.getChildrenCount(inner, maxLevelDistance) >=
                    minSnippetsToSupport)
                {
                    outer.addPossibleChild(inner.getName());
                }
            }
        }
    }

    private Element processStructure(TreeExpansion max)
    {
        Element res = max.getResult().toXML();
        return res;
    }

    private void printResult(TreeExpansion t)
    {
        ResultNode node = t.getResult();
        System.out.println(node.toString());
    }

    public HTMLNode getPageRoot()
    {
        return page.getRootNode();
    }

    private void resetPageStyle()
    {
        Enumeration e = page.getAllNodes().elements();
        while (e.hasMoreElements())
        {
            Object o = e.nextElement();
            if (o instanceof HTMLNode)
            {
                ( (HTMLNode)o).addAttribute("STYLE", "background: white");
            }
        }

    }

    private void saveProcessedPage(String name)
    {
        Vector resultNodes = maxExpansion.markResults();
        Enumeration e = resultNodes.elements();
        while (e.hasMoreElements())
        {
            ( (TreeExpansionElementOccurence)e.nextElement()).getNode().
                addAttribute("STYLE", "background: green");
        }
        try
        {
            OutputStream os = new FileOutputStream(name);
            os.write(page.toString().getBytes());
            os.close();
        }
        catch (IOException ex1)
        {
            System.err.println("File could not be saved: " + ex1.toString());
        }
    }
}