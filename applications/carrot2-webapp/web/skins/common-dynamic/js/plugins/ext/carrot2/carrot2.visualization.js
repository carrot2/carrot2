(function($) {
  /** 
   * Visualization-related variables from the outside.
   */
  jQuery.visualization = { };
  
  $(document).ready(function() {
    if (typeof $.visualization.dataUrl != 'undefined') {
      var flashvars = {
          data_sourceURL: $.visualization.dataUrl,
          callback_onGroupClick: "groupClicked"
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

// Callback function invoked by the visualization
function groupClicked(clusterId, docList) {
  var documentIndexes = docList.split(",");
  $("#clusters-panel").trigger("carrot2-clusters-selected", [ clusterId, documentIndexes ]);
}
