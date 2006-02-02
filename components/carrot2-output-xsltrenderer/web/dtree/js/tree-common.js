
/*
 * Dynamic tree visualization
 * $Id$
 *
 * Copyright 2002 Dawid Weiss, Poznan University of Technology.
 * Use is subject to license terms. Please refer to /docs/legal/licence.txt
 */


    // Array of nodes (associative - node_id --> node instance) 
    var nodes   = new Array();
    
    // Array of outer-arcs of nodes (associative -
    // node_id --> array of ids of nodes connected by outer arcs)
    var arcs    = new Array();
    
    var scores  = new Array();
    
    // A list of INodes, which have been marked as roots of their own
    // subhierarchy. Roots have no direct links between themselves, so must
    // be shown explicitly when 'show roots' button is pressed (or some
    // other action is taken).
    var roots   = new Array();

    // Synthetic root for all top-level groups    
    var syntheticRoot;
    
    // A counter for terminal nodes (documents), reflecting
    // search engine's relevance ranking.
    var relevanceOrderPosition = 1;
    
    // Unique node identification number generator
    var nextNodeId = 1;
    
    // Node type: TYPE_IN (Internal Node), TYPE_TN (Terminal Node).         
    var TYPE_IN = "IN";
    var TYPE_TN = "TN";
    
    var images = new Array(
        "lastmnode",
        "lastpnode",
        "lastnode",
        "mnode",
        "pnode",
        "node",
        "parentlastmnode",
        "parentlastpnode",
        "parentlastnode",
        "parentmnode",
        "parentpnode",
        "parentnode",
        "vertline",
        "hpad");

    // Initialize static data (synthetic root)
    syntheticRoot = new INode("__SYNTHETIC_ROOT__", DEFAULT_ALL_ROOTS_TITLE);


/* -- Images handling ----------------------------------------------------{{{ */


// Starts loading images and initializes 'images' array with associative names
// and Image objects.
function preloadImages(imagesBase, width, height) // {{{
{
    STRIPE_IMAGE_BASE = imagesBase + "/stripe/";
    IMAGE_BASE        = imagesBase + "/icons/";
    IMAGE_WIDTH = width;
    IMAGE_HEIGHT = height;
    
    for (i=0;i<images.length;i++)
    {
        var imageName = images[i];
        var image = new Image();
        image.src = IMAGE_BASE + imageName + IMAGES_EXTENSION;
        image.width = IMAGE_WIDTH;
        image.height = IMAGE_HEIGHT;
        images[ imageName ] = image;
    }
} // }}}


// Outputs image dimensions part of an IMG tag
function outputImgDimensions( imageName ) // {{{
{
    if (images[imageName] == null)
        alert("Image not defined: " + imageName );
    return 'width="' + images[imageName].width + '" height="' + images[imageName].height + '" ';
} // }}}


// Returns an appropriate visualization of a node (icon)
function getIconForNode(hasSubnodes, isOpened, isLast, isParentAxis) // {{{
{
    var nodeImg;

    if (!hasSubnodes) nodeImg = "node";
    else
    {
        if (isOpened) nodeImg = "mnode";
        else nodeImg = "pnode";
    }
    
    if (isLast)       nodeImg = 'last' + nodeImg;    
    if (isParentAxis) nodeImg = 'parent' + nodeImg;

    return nodeImg;
} // }}}



/* -- Node, INode and TNode class ----------------------------------------{{{ */


// Creates a node instance (either internal or terminal). 
function Node(id, name) // {{{
{
    this.id = id;
    this.name = name;
    this.parent = null;
    this._fullname = name;
    this._name = null; // lazy init.

    this.getName = function() {
        if (this._name == null) {
            this._name = this.splitLongSequences(this._fullname);
        }
        return this._name;
    };

    // Ensures the text has word breaks in character sequences longer
    // then the given input argument.
    //
    // This solution is a bit temporary -- we will have to rewrite
    // it to a solution which uses frames someday, otherwise it is simply
    // unsolvable.
    this.splitLongSequences = function(text) {
        var MAX_CHUNK_LENGTH = 30;
        var newText = text.split(/[\ \t\n]/);
        var i;
        for (i = 0; i < newText.length; i++) {
            if (newText[i].length > MAX_CHUNK_LENGTH) {
                newText[i] = newText[i].replace(/([\/\=\?\-\.])/g, "$1<wbr/>");
            }
        }
        return newText.join(" ");
    }
} // }}}


// creates an Internal Node (representing groups)
function INodeRoot(id, title) // {{{
{
    node = new INode(id, title);
    roots[roots.length] = node;

    if (arcs[syntheticRoot.id] == null)
        arcs[syntheticRoot.id] = new Array();
    var nodeIdArcs = arcs[syntheticRoot.id];
    nodeIdArcs[ nodeIdArcs.length ] = node.id;
    node.parent = new Array();
    node.parent[ node.parent.length ] = syntheticRoot.id;
} // }}}


function INode(id, title) // {{{
{
    node = new Node(id, title);
    node.type = TYPE_IN;
    nodes[id] = node;
    return node;
} // }}}


// creates a Terminal Node (representing a document)
function TNode(id, title, url, snippet) // {{{
{
    node = new Node(id, title);
    node.type = TYPE_TN;
    node.snippet = snippet;
    node.url = url;
    node.relevanceOrderPosition = relevanceOrderPosition++;
    nodes[id] = node;
    return node;
} // }}}


/* -- Decodes information about edges in groups graph --------------------{{{ */



// Decodes information about edges in the tree (graph)
// of internal/ terminal nodes. The input parameter is a string
// which must follow this format:
//      node_id;[outer-edges];node_id;[outer-edges]...
// where outer-edges is a list of node ids separated by commas.
// Example:
// "1;2,3;2;3,4" stands for (1-->2,3)(2-->3,4)
//
// 1. Such information is decoded into arc[] map, where keys are
//    nodeId's and values are Arrays of nodeId's (after-lists).
// 2. A property 'parent' is associated with each node. This
//    property is an Array of nodeId's of nodes which have an edge
//    to the node.
// 3. Every node is given a boolean property 'hasSubgroups' which, if
//    present (and set to true), indicates the node has subnodes of
//    type TYPE_IN (internal nodes).
function decodeEdges( list ) // {{{
{
    var outEdgesList = list.split(';');
    for (i=0;i<outEdgesList.length;i+=2)
    {
        var nodeId = outEdgesList[i];
        var outEdges = outEdgesList[i+1].split(',');

        if (arcs[nodeId] == null)
            arcs[nodeId] = new Array();
        var nodeIdArcs = arcs[nodeId];
        var hasSubgroups = nodes[nodeId].hasSubgroups;

        for (j=0;j<outEdges.length;j++)
        {
            // add an arc
            nodeIdArcs[nodeIdArcs.length] = outEdges[j];
            // add parent link
            var edgeEndNode = nodes[outEdges[j]];
            if (edgeEndNode.parent == null)
                edgeEndNode.parent = new Array();
            edgeEndNode.parent[ edgeEndNode.parent.length ] = nodeId;
            // cache the information about subgroups presence for future use in
            // displaying icons.
            if (nodes[outEdges[j]].type==TYPE_IN)
                hasSubgroups = true;
        }
        
        if (hasSubgroups==null)
            hasSubgroups = false;

        nodes[nodeId].hasSubgroups = hasSubgroups;
    }
} // }}}



/* 
 * This function is basically the same as decodeEdges, but it also takes into
 * account the weight (score) assigned to each relationship. The score is encoded
 * as a float number, according to the following:
 *
 * "1;2,1.2,3,0.12;2;3,1,4,2" stands for (1-->2[score:1.2],3[score:0.12])(2-->3[score:1],4[score:2])
 */
function decodeScoredEdges( list ) // {{{
{
    var outEdgesList = list.split(';');
    for (i=0;i<outEdgesList.length;i+=2)
    {
        var nodeId = outEdgesList[i];
        var outEdges = outEdgesList[i+1].split(',');

        if (arcs[nodeId] == null)
            arcs[nodeId] = new Array();
        var nodeIdArcs = arcs[nodeId];
        
        if (scores[nodeId] == null)
            scores[nodeId] = new Array();
        var nodeIdScores = scores[nodeId];

        var hasSubgroups = nodes[nodeId].hasSubgroups;

        for (j=0;j<outEdges.length;j+=2)
        {
            var edgeEndNode = nodes[outEdges[j]];
            // add an arc
            nodeIdArcs[nodeIdArcs.length] = edgeEndNode.id;
            nodeIdScores[nodeIdArcs.length - 1] = new Number(outEdges[j+1]);

            // add parent link
            if (edgeEndNode.parent == null)
                edgeEndNode.parent = new Array();
            edgeEndNode.parent[ edgeEndNode.parent.length ] = nodeId;
            // cache the information about subgroups presence for future use in
            // displaying icons.
            if (nodes[outEdges[j]].type==TYPE_IN)
                hasSubgroups = true;
        }
        
        if (hasSubgroups==null)
            hasSubgroups = false;

        nodes[nodeId].hasSubgroups = hasSubgroups;
    }
} // }}}

function ScoredNode( node, score )
{
    this.node = node;
    this.score = score;
    this.node.score = score;
}

function scoreSort(a,b) // {{{
{
    if (a.score < b.score)
        return 1;
    else 
    if (a.score == b.score)
        return 0;
    else
        return -1;
} // }}}

function getScoreOrderedSnippetsToLevel( node, remainingDepth ) // {{{
{
    var allSnippets = new Array();
    var orderedSnippets = new Array();

    var nextLevelNodes = new Array();
    nextLevelNodes[nextLevelNodes.length] = node.id;
    
    do {
        var nonTerminalSubnodes = new Array();
        var thisTurnTerminalNodes = new Array();
        
        for (j = 0; j < nextLevelNodes.length; j++)
        {
            var children = arcs[nextLevelNodes[j]];
            if (children == null)
                continue;

            for (i = 0;i<children.length;i++)
            {
                var subnode = nodes[children[ i ]];
                if (subnode.type == TYPE_TN) {
                    if (allSnippets[subnode.id] == null)
                    {
                        thisTurnTerminalNodes[ thisTurnTerminalNodes.length ] = 
                            new ScoredNode(subnode, scores[nextLevelNodes[j]][i]);
                    }
                }
                else
                {
                    nonTerminalSubnodes[ nonTerminalSubnodes.length ] = subnode.id;
                }
            }
        }
        
        for (i=0;i<thisTurnTerminalNodes.length;i++)
        {
            allSnippets[ thisTurnTerminalNodes[i].node.id ] = true;
            orderedSnippets[ orderedSnippets.length ] = thisTurnTerminalNodes[i]; 
        }

        nextLevelNodes = nonTerminalSubnodes;
        remainingDepth--;
    } while ((thisTurnTerminalNodes.length > 0 || FORCE_FULL_DEPTH) && remainingDepth > 0);

    orderedSnippets.sort(scoreSort);
    
    var result = new Array();
    for (i=0;i<orderedSnippets.length;i++)
    {
        result[ result.length ] = orderedSnippets[i].node.id;
    }
    
    return result;
} // }}}


function getSubdocumentsNumber( node )
{
    if (node.cachedSubdocuments == null)
    {
        node.cachedSubdocuments = getOrderedSnippetsToLevel( node, MAX_SUBGROUP_SNIPPET_DISPLAY ).length; 
    }
    return node.cachedSubdocuments;
}




function getOrderedSnippetsToLevel( node, remainingDepth ) // {{{
{
    var allSnippets = new Array();
    var orderedSnippets = new Array();

    var nextLevelNodes = new Array();
    nextLevelNodes[nextLevelNodes.length] = node.id;
    
    do {
        var nonTerminalSubnodes = new Array();
        var thisTurnTerminalNodes = new Array();
        
        for (j = 0; j < nextLevelNodes.length; j++)
        {
            var children = arcs[nextLevelNodes[j]];
            if (children == null)
                continue;

            for (i = 0;i<children.length;i++)
            {
                var subnode = nodes[children[ i ]];
                if (subnode.type == TYPE_TN) {
                    if (allSnippets[subnode.id] == null)
                    {
                        thisTurnTerminalNodes[ thisTurnTerminalNodes.length ] = subnode.id;
                    }
                }
                else
                {
                    nonTerminalSubnodes[ nonTerminalSubnodes.length ] = subnode.id;
                }
            }
        }
        
        for (i=0;i<thisTurnTerminalNodes.length;i++)
        {
            allSnippets[ thisTurnTerminalNodes[i] ] = true;
            orderedSnippets[ orderedSnippets.length ] = thisTurnTerminalNodes[i]; 
        }

        nextLevelNodes = nonTerminalSubnodes;
        remainingDepth--;
    } while ((thisTurnTerminalNodes.length > 0 || FORCE_FULL_DEPTH) && remainingDepth > 0);

    return orderedSnippets;
} // }}}



// Returns an array (associative - IDs) of terminal nodes
// reachable from a certain node up to remainingDepth in the
// hierarchy.
function getSnippetsToLevel( node, remainingDepth) // {{{
{
    var allSnippets = new Array();

    var nextLevelNodes = new Array();
    nextLevelNodes[node.id] = true;
    
    do {
        var nonTerminalSubnodes = new Array();
        var thisTurnTerminalNodes = new Array();
        
        for (i in nextLevelNodes)
        {
            var children = arcs[i];
            for (key in children)
            {
                subnode = nodes[children[key]];
                if (subnode.type == TYPE_TN) {
                    if (allSnippets[subnode.id] == null)
                        thisTurnTerminalNodes[ subnode.id ] = true;
                }
                else
                    nonTerminalSubnodes[ subnode.id ] = true;
            }
        }
        
        var newAdded = false;
        for (key in thisTurnTerminalNodes)
        {
            allSnippets[ key ] = true;
            newAdded = true;
        }

        nextLevelNodes = nonTerminalSubnodes;

        remainingDepth--;
    } while ((newAdded == true || FORCE_FULL_DEPTH) && remainingDepth > 0);

    return allSnippets;
} // }}}


// Converts an associative array of nodes (array[node.id]) to a sequential
// array - array[sequential_counter] = node.id
// this is necessary because array.sort() otherwise doesn't work.        
function convertFromProperties(nodesArray) // {{{
{
    var i = 0;
    var result = new Array();
    for (key in nodesArray)
    {
        result[i] = key;
        i++;
    }
    return result;
} // }}}


// comparison function for array.sort().
// Returns terminal nodes ordered according to their order as they
// were added to the noeds list.
function relevanceSort(a,b) // {{{
{
    if (nodes[a].relevanceOrderPosition < nodes[b].relevanceOrderPosition)
        return -1;
    else 
    if (nodes[a].relevanceOrderPosition == nodes[b].relevanceOrderPosition)
        return 0;
    else
        return 1;
} // }}}



