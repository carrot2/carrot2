/**
 * Adds and modifies HTML markup required by this particular skin.
 */
(function($) {
  
  $(document).ready(function() {
    // Add pipes between entries in lists of links
    $(".example-queries a:not(:last-child)").after("<span class='pipe'>&#160;| </span>");

    // Copy util links to the startup section
    $("#util-links ul").clone().appendTo("#startup #search-area");
    
    $("#source-tabs").bind("tabActivated", copyTabInfo);
    
    $("body").bind("carrot2-loaded", function() {
      if ($("#results-area").size() == 0) {
        $("#source-tabs").trigger("tabActivated", [ $.tabs.getInitialActiveTab() ]);
      }
    });
  });
  
  /**
   * Copies tab info from the tab li to the extra information area.
   */
  function copyTabInfo (e, tabId) {
    var $siblings = $("#" + tabId).find("a.label").siblings();
    $("#example-queries").html($siblings.find("span.example-queries").clone().removeClass("hide"));
  }
})(jQuery);
