
/**
 * Callback function (from visualization code).
 */
function groupClicked(clusterId, docList) {
	$.post(selectionCallback, { group: clusterId } );
}

/**
 * Externally 'select' a given group (id-based) in the visualisation circle.
 */
function selectGroupById(id) {
	var circles = getSWF();
	circles.selectGroupById(id);
}

/**
 * Reload data from an external URL.
 */
function loadDataFromURL(url) {
	var circles = getSWF();
	circles.loadDataFromURL(url);
}

/**
 * Reload data from XML string.
 */
function loadDataFromXML(data) {
	var circles = getSWF();
	var xml = circles.loadDataFromXML(data);
}

/**
 * Returns the embedded SWF object.
 */
function getSWF() {
	return $("#content")[0];
}