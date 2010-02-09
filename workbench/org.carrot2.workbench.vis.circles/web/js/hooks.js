
/** SWT-JS-SWF link (clear selection). */
function clearSelection(notifyListeners)
{
    getSWF().clearSelection(notifyListeners);
}

/** SWT-JS-SWF link (select group by ID). */ 
function selectGroupById(id, selected, fireEvent)
{
    getSWF().selectGroupById(id, selected, fireEvent);
}

/** SWT-JS-SWF link (reload data from an URL). */
function loadDataFromURL(url)
{
    getSWF().loadDataFromURL(url);
}

/** SWT-JS-SWF link (reload data from an string). */
function loadDataFromXML(data)
{
	getSWF().loadDataFromXML(data);
}

/** Returns the embedded SWF object. */
function getSWF()
{
	return $("#content")[0];
}
