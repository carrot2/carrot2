/** SWT-JS-SWF link (clear selection). */
function clearSelection()
{
    vis.set("selection", null);
}

/** SWT-JS-SWF link (select group by ID). */ 
function selectGroupById(id, selected)
{
    vis.set("selection", {
        groups: [id],
        selected: selected
    });
}

/** SWT-JS-SWF link (reload data from an URL). */
function loadDataFromURL(url)
{
    vis.set("dataUrl", url);
}

/** SWT-JS-SWF link (reload data from an string). */
function loadDataFromXML(data)
{
    vis.set("dataXml", data);
}

/** 
 Deferred method calls to SWT-defined methods, see
 http://issues.carrot2.org/browse/CIRCLES-62
*/

/** SWF-JS-SWT callback */
function proxy_groupClicked()
{
    swt_groupClicked.apply(this, arguments);
}

/** SWF-JS-SWT callback */
function proxy_documentClicked()
{
    swt_documentClicked.apply(this, arguments);
}

/** SWF-JS-SWT callback */
function proxy_onModelChanged()
{
    swt_onModelChanged.apply(this, arguments);
}
