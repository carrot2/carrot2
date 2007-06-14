/**
 * Adds a few useful methods to the default Yahoo Dom utils.
 */
Dom = function () {
};

YAHOO.lang.augment(Dom, YAHOO.util.Dom);

Dom.previousSiblingElement = function (element)
{
  while (element.previousSibling.nodeType != 1) {
    element = element.previousSibling;
  }
  return element.previousSibling;
}

Dom.nextSiblingElement = function(element)
{
  while (element.nextSibling.nodeType != 1) {
    element = element.nextSibling;
  }
  return element.nextSibling;
}

Dom.elementAt = function(element, index)
{
  for (var i = 0; i < element.childNodes.length; i++) {
    if (element.childNodes[i].nodeType == 1) {
      if (index == 0) {
        return element.childNodes[i];
      }
      else {
        index--;
      }
    }
  }
}

Dom.findParentElement = function(element, predicate)
{
  while (element && element.nodeType == 1 && !predicate(element)) {
    element = element.parentNode;
  }

  return element;
}

Dom.applyToElements = function(parent, predicate, closure, closureData) {
  for (var i = 0; i < parent.childNodes.length; i++) {
    if (parent.childNodes[i].nodeType == 1 && predicate(parent.childNodes[i])) {
      closure(parent.childNodes[i], closureData);
    }
  }
}

Dom.show = function(elementId) {
  YAHOO.util.Dom.setStyle(elementId, "display", "");
}

Dom.hide = function(elementId) {
  YAHOO.util.Dom.setStyle(elementId, "display", "none");
}

Dom.change = function(elementId) {
  var display = YAHOO.util.Dom.getStyle(elementId, "display");
  if (display == "none") {
    Dom.show(elementId);
  }
  else {
    Dom.hide(elementId);
  }
}
