/**
 * Option handling code
 */
(function($) {
  /** Cookie name for remembering the state of the options */
  var COOKIE_OPTIONS_SHOWN = "show-options";

  $(document).ready( function() {
    $("#show-options").click( function() {
      $resultsArea = $("#results-area");
      if ($resultsArea.size() != 0) {
        var optionsHeight = $("#options").innerHeight();
        jQuery.options.updateResultsArea($resultsArea, optionsHeight, 1);
      }
      
      $("#example-queries, #main-info, #show-options").hide();
      $("#options, #hide-options").show();
      $("#query").focus();

      $.cookie(COOKIE_OPTIONS_SHOWN, "t", {
        expires :30 * 12 * 10
      });
      return false;
    });

    $("#hide-options").click( function() {
      $resultsArea = $("#results-area");
      if ($resultsArea.size() != 0) {
        var optionsHeight = $("#options").innerHeight();
        jQuery.options.updateResultsArea($resultsArea, optionsHeight, -1);
      } else {
        $("#main-info").show();
      }
      
      $("#example-queries, #show-options").show();
      $("#options, #hide-options").hide();
      $("#query").focus();
      
      $.cookie(COOKIE_OPTIONS_SHOWN, null);
      return false;
    });
    
    if ($.cookie(COOKIE_OPTIONS_SHOWN)) {
      $("#show-options").trigger("click");
    }
  });

  /**
   * Updates the top position of the results area after showing/ hiding options.
   * Skins are likely to provide their own implementation here.
   */
  function updateResultsArea($resultsArea, optionsHeight, multiplier)
  {
    var top = $resultsArea.position().top;
    $resultsArea.css("top", top + optionsHeight * multiplier);
  }
  
  /**
   * Core functions for handling tabs exported to the outside.
   */
  jQuery.options = {
    updateResultsArea: updateResultsArea
  };
})(jQuery);
