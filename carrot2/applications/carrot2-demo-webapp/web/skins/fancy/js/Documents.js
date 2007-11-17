function init()
{
  parent.afterDocsLoaded(Dom.getInnerHTML("itime"));

  YAHOO.util.Dom.batch(
    YAHOO.util.Dom.getElementsBy(
    function (element) {
      return element.id && element.id.indexOf("sic") == 0;
    }, "img"),

    function(element) {
      YAHOO.util.Event.addListener(element, "click", keepClusterHighlight, element.id.substring(3));
    });

  YAHOO.util.Dom.batch(
    YAHOO.util.Dom.getElementsBy(
    function (element) {
      return element.className == "prev";
    }, "span"),

    function(element) {
      YAHOO.util.Event.addListener(element, "click", togglePreview, element.id.substring(3));
    });

  YAHOO.util.Dom.batch(
    YAHOO.util.Dom.getElementsBy(
    function (element) {
      return element.id && element.id.indexOf("scs") == 0;
    }, "span"),

    function(element) {
      YAHOO.util.Event.addListener(element, "mouseover", highlightClusterForDocument, element.id.substring(3));
      YAHOO.util.Event.addListener(element, "mouseout", clearHighlightedClusters, element.id.substring(3));
    });
}

function togglePreview(event, id)
{
  var iframe = document.getElementById("pr" + id);
  if (!(iframe.src && iframe.src.length > 0)) {
    iframe.src = iframe.getAttribute("url");
  }
  Dom.change(iframe);
  
}

/**
 * Highlights clusters in response to the "Show in clusters" link.
 */
function highlightClusterForDocument(event, id)
{
  clearHighlightedClusters();

  parent.clusters.showInClusters(id);

  var element = document.getElementById("scs" + id);
  if (element) {
      element.className = "hl";
  }
}

function clearHighlightedClusters(event, id)
{
  if (id == highlightedDocumentId) {
    return;
  }

  if (id) {
    parent.clusters.clearInClusters(id);
  }

  var element = document.getElementById("scs" + id);
  if (element) {
      element.className = "";
  }
}

var highlightedDocumentId;

function keepClusterHighlight(event, id)
{
  var prev = highlightedDocumentId;
  if (id == highlightedDocumentId) {
    highlightedDocumentId = null;
  }
  else {
    highlightedDocumentId = id;
  }

  parent.clusters.showAllClusters(true);
  clearHighlightedClusters(null, prev);
}