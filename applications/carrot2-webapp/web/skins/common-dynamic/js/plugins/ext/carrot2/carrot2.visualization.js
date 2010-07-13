(function($) {
  /** 
   * Visualization-related variables from the outside.
   */
  jQuery.visualization = { };
  
  $(document).ready(function() {
    if (typeof $.visualization.dataUrl != 'undefined') {
      var flashvars = {
          startup_data_URL: $.visualization.dataUrl,

          callback_onGroupSelection: "onGroupSelection",
          callback_onDocumentSelection: "onDocumentSelection",
          openDocumentsOnClick: true,

          documentsPanel: "AUTO",
          maxVisibleDocuments: 400,

          logo: jQuery.visualization.logo
        };

      var params = {};
      var attributes = {};

      swfobject.embedSWF($.visualization.skinPath + "/common-dynamic/swf/com.carrotsearch.visualizations.circles.swf",
          "clusters-visu", "100%", "100%", "10.0.0", $.visualization.skinPath + "/common/swf/expressInstall.swf",
          flashvars, params, attributes);
    }
  });

  /** Safari layout bug hack */
  $(window).load(function() {
    if ($.browser.safari) {
      $("#clusters-visu").css("display", "block");
    }
  });
})(jQuery);

/**
 * An array of selected cluster IDs in the visualization SWF.
 */
var selectedClusters = [];

/**
 * A cache of document identifiers for each cluster ID, as given by
 * the visualization. The same data is created in <code>carrot2.clusters.core.js</code>,
 * so perhaps we could reuse it?
 */
var flattenedDocuments0 = {};

// Callback function invoked by the visualization:
// a group has been selected or deselected.
function onGroupSelection(clusterId, isSelected, docList) {
  var i  = jQuery.inArray(clusterId, selectedClusters);

  // If re-selected, re-push at the top of the stack.
  if (i != -1) selectedClusters.splice(i, 1);
  if (isSelected) selectedClusters.unshift(clusterId);

  if (selectedClusters.length == 0) {
    $("#clusters-panel").trigger("carrot2-clusters-selected-top");
  } else {
    /*
     * TODO: selectedClusters contains identifiers of clusters to be shown.
     * one could either merge their documents, or display one cluster after
     * another in the documents panel (my pick).
     */
    flattenedDocuments0[clusterId] = docList.split(",");

    var cluster = selectedClusters[0];
    var documentIndexes = flattenedDocuments0[cluster];
    $("#clusters-panel").trigger("carrot2-clusters-selected", [ cluster, documentIndexes ]);
  }
}

// Callback function invoked by the visualization:
// selection has been cleared
function selectionCleared() {
  $("#clusters-panel").trigger("carrot2-clusters-selected-top");
}

// Callback function invoked by the visualization
function onDocumentSelection(documentId) {
  // Ignore the click feedback, using flash directly to open a new
  // browser window (see openDocumentsOnClick above).
  //
  // window.open($("#d" + documentId + " a.title").attr("href"));
}
