(function($) {
  /** 
   * Visualization-related variables from the outside.
   */
  jQuery.visualization = { };
  
  $(document).ready(function() {
    if (typeof $.visualization.dataUrl != 'undefined') {
      var flashvars = {
          startup_data_URL: $.visualization.dataUrl,

          callback_onSelectionChanged: "onGroupSelection",
          callback_onDocumentSelection: "onDocumentSelection",
          openDocumentsOnClick: true,

          documentsPanel: "AUTO",
          maxVisibleDocuments: 400,
          disableLogging: true,

          logo: jQuery.visualization.logo
      };

      if ($.visualization.visualization == 'foamtree') {
        flashvars["gui_hsv_start"] = "0.8, 0, 0.9, 0.95";
        flashvars["gui_hsv_end"] = "0.8, 0.83, 0.9, 0.95";
        flashvars["gui_hsv_text_dark"] = 0xff202020;
        flashvars["gui_hsv_text_light"] = 0xffe0e0e0;
        flashvars["cornerRoundness"] = 0.2;
      }

      if ($.visualization.visualization == 'circles') {
        flashvars["gui_hsv_text_dark"] = 0xd0000000;
        flashvars["gui_hsv_text_light"] = 0xd0ffffff;
        flashvars["expandAll"] = true;
        // swfobject wtf... values are interpreted as URL encoded? Passing % doesn't work.
        flashvars["minOutsideFontSize"] = '2%25';
        flashvars["maxOutsideFontSize"] = '30%25';
      }

      var params = {};
      var attributes = {};

      swfobject.embedSWF($.visualization.skinPath + "/common-dynamic/swf/com.carrotsearch.visualizations." +
          $.visualization.visualization + ".swf",
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

// Callback function invoked by the visualization:
// a group has been selected or deselected.
function onGroupSelection(clusterIds, documentIds) {
  if (clusterIds.length == 0) {
    $("#clusters-panel").trigger("carrot2-clusters-selected-top");
  } else {
    // For the time being, we're showing only the first selected cluster.
    $("#clusters-panel").trigger("carrot2-clusters-selected", [ clusterIds[0], documentIds[0], $("#clusters-visu").get(0).groupInfo(clusterIds[0])[0] ]);
  }
}
