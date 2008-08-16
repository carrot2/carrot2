(function($) {
  /** 
   * Visualization-related variables from the outside.
   */
  jQuery.visualization = { };
  
  $(document).ready(function() {
    if (typeof $.visualization.dataUrl != 'undefined') {
      var flashvars = {
          data_sourceURL: $.visualization.dataUrl,
          callback_onGroupClick: "groupClicked",
          callback_onDocumentClick: "documentClicked",
          callback_onSelectionClear: "selectionCleared",
          openDocumentsOnClick: "true"
        };
      var params = {};
      var attributes = {};

      swfobject.embedSWF($.visualization.skinPath + "/common-dynamic/swf/org.carrotsearch.vis.circles.swf", 
          "clusters-visu", "100%", "100%", "9.0.0", $.visualization.skinPath + "/common/swf/expressInstall.swf",
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

// Callback function invoked by the visualization:
// a group has been selected.
function groupClicked(clusterId, docList) {
  var documentIndexes = docList.split(",");
  $("#clusters-panel").trigger("carrot2-clusters-selected", [ clusterId, documentIndexes ]);
}

// Callback function invoked by the visualization:
// selection has been cleared
function selectionCleared() {
  $("#clusters-panel").trigger("carrot2-clusters-selected-top");
}

// Callback function invoked by the visualization
function documentClicked(documentId) {
  // Ignore the click feedback, using flash directly to open a new
  // browser window (see openDocumentsOnClick above).
  //
  // window.open($("#d" + documentId + " a.title").attr("href"));
}
