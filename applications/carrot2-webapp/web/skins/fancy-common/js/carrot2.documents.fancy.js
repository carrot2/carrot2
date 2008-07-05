(function($) {

  $(document).ready(function() {
    $("#documents-panel").bind("carrot2.documents.loaded", function() {
      $("#source-time").html(window.sourceTime);
      $("#document-count").html(window.fetchedDocumentsCount).parent().show();
    });
  });

})(jQuery);

