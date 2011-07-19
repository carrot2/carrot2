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

/** Shows tips in case of embedding failures. */
function showTip(success) {
  if (!success) {
    (document.getElementById("ie64") || document.getElementById("alt")).style.display = "block";
  }
}

/** Inserts a notice for IE64 users. */
function ie64Notice() {
  if (navigator.userAgent.indexOf("Win64") >= 0) {
    document.write('<div id="ie64">');
    document.write('<p>To see the visualization, run a 32-bit Workbench in a 32-bit JVM.</p>');
    document.write('<p><small>Currently, your 64-bit Workbench is running in a 64-bit JVM and ');
    document.write('embeds a 64-bit version of Internet Explorer, in which Flash is not supported. ');
    document.write('If you install a 32-bit JVM (which runs fine on 64-bit Windows) and run 32-bit Workbench in it, ');
    document.write('a 32-bit version of Internet Explorer will be used and you will be able to see the visualization.</small></p>');
    document.write('<p><small>Instructions:</small></p>');
    document.write('<small><ol>');
    document.write('<li>Download and install a JVM for 32-bit Windows, e.g. from <a href="http://www.oracle.com/technetwork/java/javase/downloads/index.html">Oracle</a>.</li>');
    document.write('<li>Download Workbench for 32-bit Windows.</li>');
    document.write('<li>Edit the workbench ini file, e.g. <tt>carrot2-workbench.ini</tt>, to provide the path to the 32-bit JVM. At the beginning of the file, insert:');
    document.write('<pre>-vm\nc:\\Program Files (x86)\\Java\\jdk1.6.0_24\\bin\\javaw.exe</pre>');
    document.write('Note: the JVM path may be different on your system.</li>');
    document.write('<li>Run Workbench. You should be able to see the visualizations.</li>');
    document.write('</ol></small>');
    document.write('</div>');
  }
}

/** Invoked by the workbench when updating XML. Defers the update until
 *  the visualization is initialized. */
function updateDataXml(xml) {
  try {
      vis.set('dataXml', xml);
  } catch (e) {
      // alert("Error: " + e.message);
	  throw e;
  }
}

/** Visualization callback (on initialized) */
function onInitialized() {
  swt_onVisualizationLoaded();
}

