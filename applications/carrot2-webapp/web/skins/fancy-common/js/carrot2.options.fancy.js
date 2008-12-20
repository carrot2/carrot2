/**
 * Option handling code
 */
(function($) {
  function updateResultsAreaParent($resultsArea, optionsHeight, multiplier)
  {
    var top = $resultsArea.parent().position().top;
    $resultsArea.parent().css("top", top + optionsHeight * multiplier);
  }
  
  jQuery.options.updateResultsArea = updateResultsAreaParent;
})(jQuery);
