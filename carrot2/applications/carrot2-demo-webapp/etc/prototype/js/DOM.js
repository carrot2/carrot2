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
