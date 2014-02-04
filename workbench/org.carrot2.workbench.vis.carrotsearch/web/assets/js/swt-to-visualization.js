
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
