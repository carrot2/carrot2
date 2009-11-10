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

          logo: "carrot2"
        };

      var params = {};
      var attributes = {};

      swfobject.embedSWF($.visualization.skinPath + "/common-dynamic/swf/org.carrotsearch.vis.circles.swf", 
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
     *
     * Documents from each cluster could be stored in a hash locally here,
     * but it probably makes more sense to reuse the flattenedDocuments array
     * (or jQuery.clusters.documents) function), which contains document lists 
     * for each cluster ID anyway.
     *
     * For now, that function is not properly initialized when visualization
     * panel is shown, so I comment out the code below, but you get the point.
     */
    // var clusterId = selectedClusters[0];
    // var documentIndexes = jQuery.clusters.documents(clusterId);
    // $("#clusters-panel").trigger("carrot2-clusters-selected", [ clusterId, documentIndexes ]);
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
