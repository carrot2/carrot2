package com.paulodev.carrot.treeSnippetMiner.treeAnalyser.snippetTokenizer;

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

public class snippetTokenizer
{
    public snippetTokenizer()
    {
    }

    public Enumeration discoverTokens(Element treeRoot)
    {
        Vector tokens = new Vector();
        int[] indexes =
            {
            0, 0, 0};
        int[] num = {0};
        Token[] lastZoneToken = {null};
        discoverTokens(treeRoot, tokens, indexes,
                       treeRoot.getChildren().size() > 1 ? 1 : 0, num, lastZoneToken, false);
        if (lastZoneToken[0] != null)
            lastZoneToken[0].endScope = num[0] + 1;
        return tokens.elements();
    }

    private void discoverTokens(Element treeNode, Vector tokens, int[] indexes,
                                int level, int[] pos, Token[] lastZoneToken, boolean anchor)
    {
        pos[0]++;
        anchor = anchor || treeNode.getName().equalsIgnoreCase("a");
        Token nodeToken = null;
        if (level > 1)
        {
            indexes[0]++;
            nodeToken = new Token("node_" + indexes[0], Token.TYPE_NODE, pos[0], treeNode, anchor, null);
            tokens.add(nodeToken);
            treeNode.getAttributes().add(new Attribute("node_" +
                indexes[0],
                "inside"));
            if (treeNode.getName().equalsIgnoreCase("div") ||
                treeNode.getName().equalsIgnoreCase("a") ||
                treeNode.getName().equalsIgnoreCase("dl") ||
                treeNode.getName().equalsIgnoreCase("dt") ||
                treeNode.getName().equalsIgnoreCase("dd") ||
                treeNode.getName().equalsIgnoreCase("li") ||
                treeNode.getName().equalsIgnoreCase("tr") ||
                treeNode.getName().equalsIgnoreCase("td") ||
                treeNode.getName().equalsIgnoreCase("table") ||
                treeNode.getName().equalsIgnoreCase("font") ||
                treeNode.getName().equalsIgnoreCase("span") ||
                treeNode.getName().equalsIgnoreCase("br") ||
                treeNode.getName().equalsIgnoreCase("p"))
            {
                if (!(treeNode.getAttributeValue("optional") == null ? false :
                    treeNode.getAttributeValue("optional").equalsIgnoreCase("true"))
                    || treeNode.getName().equalsIgnoreCase("br")
                    || treeNode.getName().equalsIgnoreCase("p")
                    || treeNode.getName().equalsIgnoreCase("div")) {
                        if (lastZoneToken[0] != null) {
                            lastZoneToken[0].endScope = pos[0] - 1;
                            lastZoneToken[0].end = treeNode;
                            treeNode.getAttributes().add(new Attribute("zone_" +
                                indexes[2], "endBefore"));
                        }
                        indexes[2]++;
                        lastZoneToken[0] = new Token("zone_" + indexes[2], Token.TYPE_ZONE, pos[0], treeNode, anchor, null);
                        tokens.add(lastZoneToken[0]);
                        treeNode.getAttributes().add(new Attribute("zone_" +
                            indexes[2], "beginOn"));
                }
            }
            if (treeNode.getName().equalsIgnoreCase("a"))
            {
                indexes[1]++;
                tokens.add(new Token("attr_" + indexes[1], Token.TYPE_ATTR, pos[0], treeNode, anchor, "href"));
                treeNode.getAttributes().add(new Attribute("attr_" + indexes[1],
                    "attribute:href"));
            }
        }

        for (int i = 0; i < treeNode.getChildren().size(); i++)
        {
            discoverTokens( (Element)treeNode.getChildren().get(i), tokens,
                           indexes, level + 1, pos, lastZoneToken, anchor);
        }
        if (nodeToken != null)
            nodeToken.endScope = pos[0];
    }
}