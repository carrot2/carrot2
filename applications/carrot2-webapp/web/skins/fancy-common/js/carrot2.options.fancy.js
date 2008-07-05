/**
 * Option handling code
 */
(function($) {
  /** Cookie name for remembering the state of the options */
  var COOKIE_OPTIONS_SHOWN = "show-options";

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
        $.cookie(COOKIE_OPTIONS_SHOWN, null);
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
        $.cookie(COOKIE_OPTIONS_SHOWN, "t", { expires: 30 * 12 * 10 });
      }
      return false;
    });
    
    if ($.cookie(COOKIE_OPTIONS_SHOWN)) {
      $("#show-options").trigger("click");
    }
  });
})(jQuery);
