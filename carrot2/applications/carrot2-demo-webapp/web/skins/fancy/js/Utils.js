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
  if (parent == null)
  {
    return;
  }
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

Dom.getInnerHTML = function(elementId) {
  var element = document.getElementById(elementId);
  if (element) {
    return element.innerHTML;
  }
  else {
    return null;
  }
}

/**
 * Adds a few useful Cookie handling methods taken from:
 * http://www.quirksmode.org/js/cookies.html
 */
Cookies = function () {
};

Cookies.createCookieForever = function(name, value) {
  Cookies.createCookie(name, value, 365 * 100);
} 

Cookies.createCookie = function(name, value, days) {
	if (days) {
		var date = new Date();
		date.setTime(date.getTime()+(days*24*60*60*1000));
		var expires = "; expires="+date.toGMTString();
	}
	else var expires = "";
	document.cookie = name+"="+value+expires+"; path=/";
}

Cookies.readCookie = function(name) {
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for(var i=0;i < ca.length;i++) {
		var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	}
	return null;
}

Cookies.eraseCookie = function(name) {
	Cookies.createCookie(name, "", -1);
}

