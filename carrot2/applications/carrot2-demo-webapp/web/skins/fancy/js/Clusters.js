var needToShowAllClusters = false;
var currentClusterId;

function init()
{
  parent.afterClustersLoaded(Dom.getInnerHTML("ctime"));

  YAHOO.util.Dom.batch(
    YAHOO.util.Dom.getElementsBy(
    function (element) {
      return element.className && element.className.indexOf("lgr") == 0;
    }, "tr"),

    function(element) {
      YAHOO.util.Event.addListener(element, "click", leafClusterClicked, element.className.substring(3));
    });

  YAHOO.util.Dom.batch(
    YAHOO.util.Dom.getElementsBy(
    function (element) {
      return element.className && element.className.indexOf("ngr") == 0;
    }, "tr"),

    function(element) {
      if (element && element.className) { YAHOO.util.Event.addListener(element, "click", nonLeafClusterClicked, element.className.substring(3)); }
    });

  YAHOO.util.Dom.batch(
    YAHOO.util.Dom.getElementsBy(
    function (element) {
      return element.className && element.className.indexOf("sac") == 0;
    }, "span"),

    function(element) {
      if (element) { YAHOO.util.Event.addListener(element, "click", showAllClustersClicked); }
    });

  YAHOO.util.Dom.batch(
    YAHOO.util.Dom.getElementsBy(
    function (element) {
      return element.className && element.className.indexOf("more") == 0;
    }, "tr"),

    function(element) {
      if (element && element.id) {
        var id = element.id.substring(0, element.id.indexOf("more"));

        var td = element.getElementsByTagName("td")[1];
        YAHOO.util.Event.addListener(td, "click", moreClicked, {id: id, ft: element.className.substring(4)});

        var span = element.getElementsByTagName("span")[0];
        YAHOO.util.Event.addListener(span, "click", moreClicked, {id: id, ft: element.className.substring(4)});
      }
    });

  YAHOO.util.Event.addListener("always-all-link", "click", alwaysShowAllClusters);
  YAHOO.util.Event.addListener("top-link", "click", topClusterClicked);
}

function initHistory()
{
  var bookmarkedCluster = YAHOO.util.History.getBookmarkedState("c");
  var initialCluster = bookmarkedCluster || "";

  YAHOO.util.History.register("c", initialCluster, function(state) {
    selectCluster(state, false);
  });


  YAHOO.util.History.onLoadEvent.subscribe(function() {
    var state = YAHOO.util.History.getCurrentState("c");
    selectCluster(state, true);
  });
}

function leafClusterClicked(event, id)
{
  highlightCluster(id);
  selectDocuments(clusterDocs[id]);
  highlightWords(clusterWords[id]);
  updateUrl(id);
}

function nonLeafClusterClicked(event, id)
{
  fold(id);
  selectDocuments(clusterDocs[id]);
  highlightWords(clusterWords[id]);
  updateUrl(id);
}

function moreClicked(event, param)
{
  var split = param.ft.split("-");
  foldRange(param.id, "more", split[0] - 0, split[1] - 0);
}

function showAllClustersClicked()
{
  showAllClusters();
}

function topClusterClicked(event)
{
  highlightCluster('top');
  highlightWords([]);
  showAll();
  updateUrl('top');
}

function fold(elementId)
{
  Dom.change("cld" + elementId);
  highlightCluster(elementId);
};

function highlightCluster(elementId)
{
  clearHighlights();
  var textTd = document.getElementById('t' + elementId);
  if (textTd)
  {
    if (textTd.className.indexOf("sic") >= 0)
    {
      textTd.className = 'text hl sic';
    }
    else
    {
      textTd.className = 'text hl';
    }
    return true;
  }
  else
  {
    return false;
  }
}

function foldRange(prefix, morePrefix, start, end)
{
  for(var i = start; i <= end; i++)
  {
    Dom.show(prefix + "|" + i);
  }

  Dom.hide(prefix + morePrefix + (start-1));
  Dom.show(prefix + morePrefix + (end));
};

function clearHighlights()
{
  var nodes = document.getElementsByTagName('td');
  for (var i = 0; i < nodes.length; i++)
  {
    if (nodes[i].className && nodes[i].className.indexOf('text hl') >= 0)
    {
      if (nodes[i].className.indexOf('sic') >= 0) {
        nodes[i].className = 'text sic';
      }
      else {
        nodes[i].className = 'text';
      }
    }
  }
}

function contains(array, value)
{
  for (var i = 0; i < array.length; i++)
  {
    if (array[i]+"" == value+"")
    {
      return true;
    }
  }

  return false;
}

function selectDocuments(refids)
{
  var documentElements = parent.documents.document.getElementsByTagName('table');
  for (var i = 0; i < documentElements.length; i++)
  {
    if (documentElements[i].className == 'd')
    {
      if (contains(refids, documentElements[i].id))
      {
        documentElements[i].style.display = "block";
      }
      else
      {
      	if (documentElements[i].style.display != "none") {
        	documentElements[i].style.display = "none";
        }
      }
    }
  }

  parent.documents.scrollBy(0,-10000);
}

function highlightWords(clusterIds)
{
  var docs = parent.documents.document;
  if (!docs.styleSheets) {
    hlwSlow(clusterIds);
    return;
  }

  for (var s = 0; s < docs.styleSheets.length; s++)
  {
    var sheet = docs.styleSheets[s];
    if (sheet.href.indexOf(".css") >= 0)
    {
      continue;
    }

    var rules;
    if (sheet.cssRules) {
      rules = sheet.cssRules;
    }
    else if (sheet.rules) {
      rules = sheet.rules;
    }
    else {
      hlwSlow(clusterIds);
      return;
    }

    for (var i = 0; i < rules.length; i++)
    {
      if (contains(clusterIds, rules[i].selectorText.toLowerCase().substring(2))) {
        rules[i].style.fontWeight = 'bold';
      }
      else {
        if (rules[i].style.fontWeight == 'bold') {
        	rules[i].style.fontWeight = 'normal';
        }
      }
    }
  }
}

function hlwSlow(wordids)
{
  var documentElements = parent.documents.document.getElementsByTagName('b');

  for (var i = 0; i < documentElements.length; i++)
  {
    if (documentElements[i].className) {
      for (var j = 0; j < wordids.length; j++)
      {
        if (documentElements[i].className.indexOf('w' + wordids[j]) >= 0)
        {
          documentElements[i].style.fontWeight = "bold";
        }
        else
        {
	        if (rules[i].style.fontWeight == 'bold') {
	          documentElements[i].style.fontWeight = "normal";
	        }
        }
      }
    }
  }
}

function showAll()
{
  var documentElements = parent.documents.document.getElementsByTagName('table');
  for (var i = 0; i < documentElements.length; i++)
  {
    if (documentElements[i].className == 'd')
    {
      documentElements[i].style.display = "block";
    }
  }
  parent.documents.scrollBy(0,-10000);
}

function showAllClusters(dontShowAlwaysAllClusters)
{
  var trElements = document.getElementsByTagName('tr');
  for (var i = 0; i < trElements.length; i++)
  {
    if (!trElements[i].id || trElements[i].id.lastIndexOf('cld') != 0) {
      if (trElements[i].id && trElements[i].id.indexOf('more') >= 0)
      {
        trElements[i].style.display = "none";
      }
      else
      {
        trElements[i].style.display = "";
      }
    }
  }

  if (!dontShowAlwaysAllClusters && !Cookies.readCookie("always-all-clusters"))
  {
    document.getElementById("always-all").style.display = "block";
  }

}

function alwaysShowAllClusters()
{
  Cookies.createCookieForever("always-all-clusters", "true");
  document.getElementById("always-all-done").style.display = "inline";
  setTimeout(function () {
    Dom.hide("always-all");
  }, 2000);
}

function showInClusters(id)
{
  var clusters = docClusters[id];
  for (var i = 0; i < clusters.length; i++) {
    var textTd = document.getElementById('t' + clusters[i]);
    if (textTd)
    {
    	if (textTd.parentNode.style.display == 'none')
    	{
        needToShowAllClusters = true;
        setTimeout(function () {
          if (needToShowAllClusters) {
            showAllClusters();
          }
        }, 500);
    	}

      if (textTd.className.indexOf("hl") >= 0)
      {
        textTd.className = 'text hl sic';
      }
      else
      {
        textTd.className = 'text sic';
      }
    }
  }
}

function clearInClusters(id)
{
  needToShowAllClusters = false;
  var clusters = docClusters[id];
  for (var i = 0; i < clusters.length; i++) {
    var textTd = document.getElementById('t' + clusters[i]);
    if (textTd.className.indexOf('hl') >= 0) {
      textTd.className = 'text hl';
    }
    else {
      textTd.className = 'text';
    }
  }
}

function updateUrl(id)
{
  var currentState = YAHOO.util.History.getCurrentState("c");
  var newState = id;
  if (newState != currentState &&
      navigator.appName.indexOf("Internet Explorer") < 0) {
    currentClusterId = id;
    YAHOO.util.History.navigate("c", newState);
  }
}

function scrollToCluster(id)
{
  var hashIndex = (document.location.href.indexOf("#") >= 0 ?
    document.location.href.indexOf("#") : document.location.href.length);
  var newURL = document.location.href.substring(0, hashIndex) + "#" + id;
  document.location.replace(newURL);

  window.scrollBy(0, -25);
}

function selectCluster(id, showAll)
{
  if (id == "" || currentClusterId == id) {
    return;
  }

  if (id == "top")
  {
    highlightCluster('top');
    highlightWords([]);
    showAll();
  }
  else
  {
    if (highlightCluster(id))
    {
      if (showAll)
      {
        showAllClusters(true);
      }
      unfoldToTop(document.getElementById("t" + id).parentNode.id);
      selectDocuments(clusterDocs[id]);
      highlightWords(clusterWords[id]);
      scrollToCluster(id);
    }
  }
  currentClusterId = id;
}

function unfoldToTop(id)
{
  id = id.substring(0, id.indexOf("|"));
  var cldTr = document.getElementById("cld" + id);
  if (!cldTr)
  {
    return;
  }

  cldTr.style.display = "";
  var prevTr = Dom.previousSiblingElement(cldTr);
  if (prevTr.id && prevTr.id.length > 0)
  {
    unfoldToTop(prevTr.id);
  }
}

function computeDocClusters(clusterDocs)
{
  var result = new Array();

  for (var t in clusterDocs) {
    var docs = clusterDocs[t];
    for (var i = 0; i < docs.length; i++) {
      if (!result[docs[i]]) {
        result[docs[i]] = new Array();
      }
      result[docs[i]].push(t);
    }
  }

  return result;
}