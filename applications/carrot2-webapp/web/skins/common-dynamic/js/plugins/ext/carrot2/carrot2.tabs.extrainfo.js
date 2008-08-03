/**
 * Adds and modifies HTML markup required by this particular skin.
 */
(function($) {
  
  $(document).ready(function() {
    // Add placeholders for tab information on the startup screen
    $("#startup #search-area").append('<div id="extra-info"><div id="tab-info"></div><div id="example-queries"></div></div>');

    // Add pipes between entries in lists of links
    $(".example-queries a:not(:last-child)").after("<span class='pipe'>&#160;| </span>");

    // Copy util links to the startup section
    $("#util-links ul").clone().appendTo("#startup #search-area");
    
    $("#source-tabs").bind("tabActivated", copyTabInfo);
  });
  
  /**
   * Copies tab info from the tab li to the extra information area.
   */
  function copyTabInfo (e, tabId) {
    var $siblings = $("#" + tabId).find("a.label").siblings();
    $("#tab-info").html($siblings.find("span.tab-info").clone().removeClass("hide"));
    $("#example-queries").html($siblings.find("span.example-queries").clone().removeClass("hide"));
  }
})(jQuery);
