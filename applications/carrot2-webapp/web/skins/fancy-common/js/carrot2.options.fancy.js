/**
 * Option handling code
 */
(function($) {
  $(document).ready(function() {
    $("#show-options").click(function() {
      var optionsShowing = $(this).data("optionsShowing");
      
      if (optionsShowing) {
        $("#extra-info").show();
        if ($resultsArea.size() != 0) {
          var optionsHeight = $("#options").innerHeight();
          var top = $resultsArea.parent().position().top;
          $resultsArea.parent().css("top", top - optionsHeight);
        }
        $("#options").hide();
        $(this).data("optionsShowing", false);
      }
      else {
        $("#extra-info").hide();
        $("#options").show();
        
        $resultsArea = $("#results-area");
        if ($resultsArea.size() != 0) {
          var optionsHeight = $("#options").innerHeight();
          var top = $resultsArea.parent().position().top;
          $resultsArea.parent().css("top", top + optionsHeight);
        }
        $(this).data("optionsShowing", true);
      }
      return false;
    });
  });
})(jQuery);
