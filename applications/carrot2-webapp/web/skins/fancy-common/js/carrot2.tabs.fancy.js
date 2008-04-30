(function($) {
  
  $(document).ready(function() {
    sourceTabs();
  });

  /**
   * Adds dynamic behaviour to tabs, including click response and sorting by
   * drag&drop.
   */
  function sourceTabs() {
    $tabContainer = $("#source-tabs")

    // Bind listener for copying tab description
    $tabContainer.bind("tabsChanged", $.tabs.updateTabs);
    $tabContainer.find("li.tab").bind("tabActivated", copyTabInfo);
  };

  copyTabInfo = function(e) {
    var $siblings = $(e.target).find("a.label").siblings();
    $("#tab-info").html($siblings.find("span.tab-info").clone().removeClass("hide"));
    $("#example-queries").html($siblings.find("span.example-queries").clone().removeClass("hide"));
  }
})(jQuery);
