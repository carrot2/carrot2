
/*
 * Dynamic tree visualization
 * $Id$
 *
 * Copyright 2002 Dawid Weiss, Poznan University of Technology.
 * Use is subject to license terms. Please refer to /docs/legal/licence.txt
 */


/* -- Visualization of the groups graph ----------------------------------{{{ */

var currentRootNodeId;


// Builds a HTML fragment for a tree starting at a given node
// and expanded up to maxOpenDepth subnodes. Clicking on a
// node replaces the tree with a view from that node (replaces
// the root).
function getDynamicTreeViewForNode( rootNode, maxOpenDepth ) // {{{
{
    var html = "";
    var tmp;
    
    html += '<table width="100%" align="left" id="bubu" border="0" cellspacing="0" cellpadding="0">';

    tmp = getParentViewForNode( rootNode, maxOpenDepth );
    if (tmp != "")
    {
        // add parent stripe.
       html += 
          '<tr>'
       +    '<td width="16" style="background-position: bottom left; background-repeat: no-repeat; background-image: '
       +            "url('" + STRIPE_IMAGE_BASE + 'upstripe.jpg' + "');" + '">'
       +        '<img width="16" height="44" src="' + STRIPE_IMAGE_BASE + 'upstripetext.gif">'
       +    '</td>'
       +    '<td valign="bottom">'
       +        '<table border="0" cellspacing="0" cellpadding="0">' + tmp + '</table>'
       +    '</td>'
       +  '</tr>'
       ;
    }

    html +=
          '<tr>'
       +  '<td width="16" style="background-repeat: repeat; background-image: ' + "url('" + STRIPE_IMAGE_BASE + 'stripebluedot.gif' + "');" + '">'
       +  '</td>'
       +  '<td class="groupSelected" style="background-repeat: repeat-y; background-image: '
       +      "url('" + STRIPE_IMAGE_BASE + 'horizstripe.jpg' + "');" + '">'
         + '<a class="clickable"'
         +    ' onClick="clickMe(this);showDocumentsFromGroup(' + "'" + rootNode.id + "'" + ')"'
         +    ' onContextMenu="goToGroup(' + "'" + rootNode.id + "'" + '); return false;"'
         +    ' onMouseOver="return showStatus();"' + ' onMouseOut="return statusClear();"'
         +  '>'
         +      rootNode.getName()
            + '</a>&nbsp;&nbsp;<span class="cl_s">' + getOrderedSnippetsToLevel( rootNode, MAX_SUBGROUP_SNIPPET_DISPLAY ).length + '</span>'
       +  "</td>"
       +  "</tr>"
       ;

    tmp = getViewForNode( rootNode, maxOpenDepth );
    if (tmp != null)
    {

       html += 
          '<tr>'
       +    '<td width="16" valign="top" style="background-position: top left; background-repeat: no-repeat; background-image: '
       +        "url('" + STRIPE_IMAGE_BASE + 'downstripe.jpg' + "');" + '">'
       +        '<img width="16" height="65" src="' + STRIPE_IMAGE_BASE + 'downstripetext.gif">'
       +    "</td>"
       +    '<td valign="top">'
       +        '<table border="0" width="100%" cellspacing="0" cellpadding="0">' + tmp + "</table>"
       +    '</td>'
       +  '</tr>'
       ;
    }
    
    html += '</table>';

    return html;
} // }}}


// Returns a visualization of a node in parents axis of the groups graph
function getParentViewForNode(node, remainingLevels) // {{{
{
    var parents = node.parent;
    if (parents == null)
        return "";
        
    var html = "";
    var nodeImg;

    for (i=0;i<parents.length;i++)
    {
        var parentnode = nodes[parents[i]];
        var currentNodeId = nextNodeId++; 
        
        if (OUTPUT_TERMINAL_NODES == false && parentnode.type == TYPE_TN)
            continue;

        var last = (i==0);

        var hasParents = (parentnode.parent != null);
        var isOpened    = (remainingLevels > 0);
        
        nodeImg = getIconForNode( hasParents, isOpened, last, true );
        
        if (hasParents && isOpened)
        {
            html += expandParentFolder( currentNodeId, last, parentnode, remainingLevels );
        }

        html += '<tr id="' + ('IDN'+currentNodeId) + '">';

        if (last)
            html += '<td ' + outputImgDimensions( nodeImg ) + '>';
        else
            html += '<td ' + images['vertline'].width + ' background="'+ images['vertline'].src + '">';

        html += '<img ' + outputImgDimensions( nodeImg )+ ' src="' + images[nodeImg].src + '" ' 
                + 'onClick="javascript:switchParentState(this,' 
                + "'" + currentNodeId + "','" + parentnode.id + "'," + last + ')"';
                + '>';

        html += '</td><td class="group' + parentnode.type + '">';

        if (parentnode.type == TYPE_TN)
        {
            alert("Parent node should never be a terminal node!");
        }
        else
        if (parentnode.type == TYPE_IN)
        {
            html += '<a class="clickable"'
                 +    ' onClick="clickMe(this);showDocumentsFromGroup(' + "'" + parentnode.id + "'" + ')"'
                 +    ' onContextMenu="goToGroup(' + "'" + parentnode.id + "'" + '); return false;"'
                 +    ' onMouseOver="return showStatus();"' + ' onMouseOut="return statusClear();"'
                 +  '>'
                 +      parentnode.getName()
                 + '</a>&nbsp;&nbsp;<span class="cl_s">' + getSubdocumentsNumber(parentnode) + '</span>';
        }

        html += "</td>";
        html += "</tr>";
    }
    return html;
} // }}}


// Returns a visualization of a node in children axis of the groups graph
function getViewForNode(node, remainingLevels) // {{{
{
    var html = "";
    var children = arcs[node.id];
    
    if (children == null)
        return "";
        
    // This counts the number of children
    // nodes -- it is quite inefficient, maybe it should
    // be precached as a property?
    var i = 0;
    for (key in children)
        if (OUTPUT_TERMINAL_NODES)
            i++
        else
            if (nodes[children[key]].type != TYPE_TN)
                i++;

    var bar = false;
    for (key in children)
    {
        var subnode = nodes[children[key]];
        
        if (OUTPUT_TERMINAL_NODES == false && subnode.type == TYPE_TN)
            continue;
        
        i--;
        var last = (i==0);
        var hasSubnodes = ((OUTPUT_TERMINAL_NODES==true && arcs[subnode.id] != null) 
                            || (OUTPUT_TERMINAL_NODES==false && subnode.hasSubgroups));
        var isOpened    = (remainingLevels > 0);
        if (OUTPUT_TERMINAL_NODES==true && NO_TERMINAL_GROUPS_EXPANSION && subnode.hasSubgroups == false) {
            isOpened = false;
        }
        
        var currentNodeId = nextNodeId++;
        
        var nodeImg = getIconForNode( hasSubnodes, isOpened, last, /* parent axis? */ false );

        html += '<tr id="' + ('IDN'+currentNodeId) + '">';

        if (last) 
        {
            html   += '<td valign="top" width="' + '1' + '">'; 
        }
        else
        {
            html   += '<td valign="top" width="' + '1' + '" style="background-repeat: repeat-y;" background="'
                        + images['vertline'].src + '">';
        }

        html += '<img ' + outputImgDimensions(nodeImg) + ' class="clickable" src="';
        html += images[nodeImg].src + '" ';
        if (hasSubnodes)
        {
            html += 'onClick="javascript:switchState(this,' 
                    + "'" + currentNodeId + "','" + subnode.id + "'," + last + ')"';
        }
        html += '></td>';
        
        html += '<td class="group' + subnode.type + '"'
             + (SHADE_ODD_TERMINAL_NODES && bar && subnode.type == TYPE_TN ? 'style="background-color: #f5f5f5; border-top: solid 1px #f0f0f0; border-bottom: solid 1px #f0f0f0;"' : "")
             + '>';
        bar = !bar;

        if (subnode.type == TYPE_TN)
        {
            html += '<a href="' + subnode.url + '" target="_top">' + subnode.getName() + '</a>';
        }
        else
        if (subnode.type == TYPE_IN)
        {
            html += '<a class="clickable"'
                 +    ' onClick="clickMe(this);showDocumentsFromGroup(' + "'" + subnode.id + "'" + ')"'
                 +    ' onContextMenu="goToGroup(' + "'" + subnode.id + "'" + '); return false;"'
                 +    ' onMouseOver="return showStatus();"' + ' onMouseOut="return statusClear();"'
                 +  '>'
                 +      subnode.getName()
                    + '</a>&nbsp;&nbsp;<span class="cl_s">' + getOrderedSnippetsToLevel( subnode, MAX_SUBGROUP_SNIPPET_DISPLAY ).length + '</span>'
                 +  '';
        }

        html += "</td>";
        html += "</tr>";
        
        if (hasSubnodes && isOpened)
        {
            setTimeout("switchState(null, '" + currentNodeId + "','" + subnode.id + "'," + last + ')', 500);
            // html += expandFolder( currentNodeId, last, subnode, remainingLevels );
        }
    }
    return html;
} // }}}

var lastSelection = null;
function clickMe( object )
{
    if (lastSelection != null)
    {
        lastSelection.style.color = lastSelection.prevColor;
    }
    object.prevColor = object.style.color;
    object.style.color = 'red';
    lastSelection = object;
}


// expands/ contracts a node in children axis of the groups graph.
function switchState( imgElement, trElementId, nodeId, last ) // {{{
{
    var nodeTrElement = document.all["IDN" + trElementId];
    if (nodeTrElement == null)
    {
        alert("Error: Null TR element for IDN=" + trElementId + ". Notify authors.");
        return;
    }
    
    var isOpened = true;
    
    // expand/ contract subnode.

    var subnodeTrElement = document.all[ "IDS" + trElementId ];
    if (navigator.family=='ie4')
    {
        if (subnodeTrElement == null)
        {
            var rowHTML     = '<table width="100%" style="margin: 0px; padding: 0px;" cellspacing="0" cellpadding="0" border="0">' 
                            + expandFolder( trElementId, last, nodes[nodeId], 1 )
                            + '</table>';
            var row = nodeTrElement.parentElement.insertRow( nodeTrElement.rowIndex + 1);
            var cell = row.insertCell(0);
            cell.colSpan = 2;
            cell.innerHTML = ""; // iexplorer resize bug.
            cell.innerHTML = rowHTML;
        }
        else
        {
            var row = nodeTrElement.rowIndex + 1;
            if (nodeTrElement.parentElement.rows[row].style.display == "none")
                nodeTrElement.parentElement.rows[row].style.display = "block";
            else {
                nodeTrElement.parentElement.rows[row].style.display = "none";
                isOpened = false;
            }
        }
    }
    else
    {
        if (subnodeTrElement == null)
        {
            var rowHTML;
            
            if (nodeTrElement.subNodeHTML != null)
                rowHTML = nodeTrElement.subNodeHTML;
            else
                rowHTML = expandFolder( trElementId, last, nodes[nodeId], 1 );
                
            nodeTrElement.insertAdjacentHTML('AfterEnd', rowHTML);
        }
        else
        {
            nodeTrElement.subNodeHTML = subnodeTrElement.outerHTML;
            nodeTrElement.parentElement.deleteRow( nodeTrElement.rowIndex + 1);
            isOpened = false;
        }
    }
    
    // replace node icon image
    if (imgElement != null)
        imgElement.src = images[ getIconForNode( true, isOpened, last, false ) ].src; 
} // }}}


// expands/ contracts a node in parents axis of the groups graph
function switchParentState( imgElement, trElementId, nodeId, last ) // {{{
{
    var nodeTrElement = document.all["IDN" + trElementId];
    if (nodeTrElement == null)
    {
        alert("Error: Null TR element for IDN=" + trElementId + ". Notify authors.");
        return;
    }
    
    var isOpened = true;
    
    // expand/ contract subnode.

    var parentNodeTrElement = document.all[ "IDS" + trElementId ];
    if (navigator.family=='ie4')
    {
        if (parentNodeTrElement == null)
        {
            var rowHTML      = '<table style="margin: 0px; padding: 0px;" cellspacing="0" cellpadding="0" border="0">' 
                            + expandParentFolder( trElementId, last, nodes[nodeId], 1 )
                            + '</table>';
            var row = nodeTrElement.parentElement.insertRow( nodeTrElement.rowIndex );
            var cell = row.insertCell(0);
            cell.colSpan = 2;
            cell.innerHTML = ""; // iexplorer resize bug.
            cell.innerHTML = rowHTML;
        }
        else
        {
            var row = nodeTrElement.rowIndex - 1;
            if (nodeTrElement.parentElement.rows[row].style.display == "none")
                nodeTrElement.parentElement.rows[row].style.display = "block";
            else {
                nodeTrElement.parentElement.rows[row].style.display = "none";
                isOpened = false;
            }
        }
    }
    else
    {
        if (parentNodeTrElement == null)
        {
            var rowHTML;
            if (nodeTrElement.parentNodeHTML != null)
                rowHTML = nodeTrElement.parentNodeHTML;
            else
                rowHTML = expandParentFolder( trElementId, last, nodes[nodeId], 1 );
            nodeTrElement.insertAdjacentHTML('BeforeBegin', rowHTML);
        }
        else
        {
            nodeTrElement.parentNodeHTML = parentNodeTrElement.outerHTML;
            nodeTrElement.parentElement.deleteRow( nodeTrElement.rowIndex - 1);
            isOpened = false;
        }
    }
    
    // replace node icon image
    imgElement.src = images[ getIconForNode( true, isOpened, last, true ) ].src; 
} // }}}


// Returns an HTML fragment for a parent axis subnode
function expandParentFolder(currentNodeId, last, parentnode, remainingLevels) // {{{
{
    var html = "";
    html += '<tr id="'+ ('IDS'+currentNodeId) +'" >';
    if (last)
        html += '<td width="' + images['vertline'].width + '"></td>';
    else
        html += '<td width="' + images['vertline'].width + '" style="background-repeat: repeat-y;" background="' + images['vertline'].src + '">'
                + '<img width="' + images['hpad'].width + '" src="' + images['hpad'].src + '"></td>';
    html += '<td>';
    html += '<table width="100%" border="0" cellspacing="0" cellpadding="0">';
    html += getParentViewForNode(parentnode, remainingLevels - 1);
    html += '</table>';
    html += '</td></tr>';
    return html;
} // }}}


// Returns an HTML fragment for a children axis subnode 
function expandFolder(currentNodeId, last, subnode, remainingLevels) // {{{
{
    var html = "";
    html += '<tr id="'+ ('IDS'+currentNodeId) +'" >';
    if (last)
        html += '<td width="' + images['vertline'].width + '"></td>';
    else
        html += '<td width="' + images['vertline'].width + '" style="background-repeat: repeat-y;" background="' + images['vertline'].src + '">'
                + '<img width="' + images['hpad'].width + '" src="' + images['hpad'].src + '"></td>';
    html += '<td>';
    html += '<table width="100%" border="0" cellspacing="0" cellpadding="0">';
    html += getViewForNode(subnode, remainingLevels - 1);
    html += '</table>';
    html += '</td></tr>';
    return html;
} // }}}

// Redraws the tree, setting a certain node at root. Also
// redraws the results pane with terminal nodes reachable to a certain sublevel
// from the node

function goToGroup( nodeId ) // {{{
{
    var node = nodes[nodeId];
    
    if (node == null)
        alert("This should not happen: node ID selected does not exist (" + nodeId + ").\nNotify authors.");

    // redraw tree, setting node at root
    setTimeout("goToGroupInTree( '" + nodeId + "' )", 500);

    var treePlaceholder = document.all['treePlaceholder'];
    treePlaceholder.innerHTML = '<div class="small">Loading...</div>'; // iexplorer resize bug.
    
    showDocumentsFromGroup(nodeId);
} 

function goToGroupInTree( nodeId )
{
    var node = nodes[nodeId];
    
    if (node == null)
        alert("This should not happen: node ID selected does not exist (" + nodeId + ").\nNotify authors.");
    
    var treePlaceholder = document.all['treePlaceholder'];
    treePlaceholder.innerHTML = ""; // iexplorer resize bug.
    treePlaceholder.innerHTML = getDynamicTreeViewForNode(node, MAX_OPEN_LEVEL);
}
// }}}

// Same as goToGroup(), but does not change the root of the tree (only the displayed
// snippets set
function showDocumentsFromGroup( nodeId ) // {{{
{
    currentRootNodeId = nodeId;
    var node = nodes[nodeId];
    
    if (node == null)
        alert("This should not happen: node ID selected does not exist (" + nodeId + ").\nNotify authors.");
    
    // redraw the list of results
    var resultsPlaceholder = document.all['resultsPlaceholder'];
    
    var result;
    if (ORDER_TYPE == ORDER_DOCUMENTS_ORDER)
    {
        result = convertFromProperties(getSnippetsToLevel(node, MAX_SUBGROUP_SNIPPET_DISPLAY));
        result.sort(relevanceSort);
    }
    else
    if (ORDER_TYPE == ORDER_GROUP_DECLARATION_ORDER)
    {
        result = getOrderedSnippetsToLevel( node, MAX_SUBGROUP_SNIPPET_DISPLAY );
    }
    else
    if (ORDER_TYPE == ORDER_SCORE_ATTRIBUTE)
    {
        result = getScoreOrderedSnippetsToLevel( node, MAX_SUBGROUP_SNIPPET_DISPLAY );
    }
    else
    {
        alert("Order type unknown: " + ORDER_TYPE);
    }
    
    resultsPlaceholder.innerHTML = ""; // iexplorer resize bug.
    resultsPlaceholder.innerHTML = getSnippetsAsList(result);  
}  // }}}

function statusClear() // {{{
{
    window.status = '';
    return true;
} // }}}

function showStatus() // {{{
{
    window.status = "Click left mouse button to show documents, right mouse button to zoom to this group";
    return true;
} // }}}

// Returns snippets as an ordered html list.
function getSnippetsAsList( nodesArray ) // {{{
{
    var html = '';
    for (i=0;i<nodesArray.length;i++)
    {
        var node = nodes[ nodesArray[i] ];
        html += '<table><tr><td class="nm">'
                + node.relevanceOrderPosition + '</td>'
                + '<td class="st">'
                + '<a href="' + node.url + '" target="_top">' + node.getName() + '</a>'
                + '</td>'
                + '<td class="si">'
                + '<a href="' + node.url + '" target="_blank"><img src="' + IMAGE_BASE + '/nwin.gif" /></a>'
                + '</td>'
                + '</tr></table>'
                + '<p class="sb">' + node.snippet + '</p>'
                + '<p class="su">' + node.url
                ;
        if (node.score != null)
            html += ' <span class="extras">[score: ' + node.score + "]</span>"; 
        html += '</p>'
    }
    return html;
} // }}}



