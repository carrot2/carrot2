(function($) {

  $(document).ready(function() {
    $("#documents-panel").bind("carrot2-documents-loaded", function() {
      $("#source-time").html(window.sourceTime);

      if (window.fetchedDocumentsCount) {
        $("#document-count").html(window.fetchedDocumentsCount).parent().show();
        $("#status-fetched-documents").html(window.fetchedDocumentsCount);
      }

      if (window.totalDocumentsCount && window.totalDocumentsCount > 0) {
        $("#status-total-documents").html(window.totalDocumentsCount);
        $("#status-total").show();
        $("#documents-status-overall").show();
      }

      $("#status-query").text($("#query").val());
    });
    
    $("#clusters-panel").bind("carrot2-clusters-loaded", function() {
      if (window.algorithmTime) {
        $("#algorithm-time").html(window.algorithmTime).parent().show();
      }
    });
    
    $("#clusters-panel").bind("carrot2-clusters-selected", function(target, clusterId, documents) {
      $("#status-cluster-label").html($("#" + clusterId + " > a > .label").text());
      $("#status-cluster-size").html(documents.length);
      
      $("#documents-status-overall").hide();
      $("#documents-status-cluster").show();
    });

    $("#clusters-panel").bind("carrot2-clusters-selected-top", function() {
      $("#documents-status-overall").show();
      $("#documents-status-cluster").hide();
    });
  });

})(jQuery);

