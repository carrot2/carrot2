
/** SWT->JS: clear selection. */
function clearSelection()
{
    vis.set("selection", { all: true, selected: false });
}

/** SWT->JS: select a new set of groups. */ 
function selectGroupsById(ids)
{
    clearSelection();
    vis.set("selection", {
        groups: ids.map(function(id) { return "" + id; }),
        selected: true
    });
}

/** SWT->JS: model update. */
function updateDataJson(c2json) {
    // Convert clusters to groups from Carrot2/Lingo3G JSON format.
    function convert(clusters) {
      return clusters.map(function(cluster) {
        return {
          id:     cluster.id,
          label:  cluster.phrases.join(", "),
          weight: cluster.attributes && cluster.attributes["other-topics"] ? 0 : cluster.size,
          groups: cluster.clusters ? convert(cluster.clusters) : []
        }
      });
    };

    vis.set({
      dataObject: {
        groups: convert(c2json.clusters)
      }
    });
}

/** SWT->JS: view changed size. */
function updateSize() {
    // Defer actual resize until we receive resize event.
    window.addEventListener("resize", function() {
        vis.resize();
        window.removeEventListener("resize", arguments.callee);    
    });
}

/** 
 * Defer visualization embedding until we have a non 0x0 container size
 * and SWT browser functions are defined.
 */
function embedWhenReady(embeddingFunction) {
    window.addEventListener("load", function() {
      var container = document.getElementById("viscontainer");

      if (container.clientWidth == 0 || container.clientHeight == 0 || (typeof swt_log === 'undefined')) {
        window.setTimeout(arguments.callee, 500);
      } else {
        embeddingFunction();
      }
    });
}

/** On error, try to log the message to SWT. */
function installOnErrorHandler() {
    window.onerror = function(error) {
      var container = document.getElementById("viscontainer");
      if (container) {
        var hasSwtLog = (typeof swt_log !== 'undefined');
        var msg = "Error: " + JSON.stringify(arguments, null, "  ") + ", container size: " +
          container.clientWidth + "x" + container.clientHeight + 
          (hasSwtLog ? " swt_log defined." : " swt_log undefined.");

          container.innerText = msg;
          if (hasSwtLog) {
            swt_log(msg);
          }
      }
      throw error;
    }
}
