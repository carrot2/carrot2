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

    // Make the tabs sortable
    $tabContainer.find("ul").sortable({
      change: function() {
        $tabContainer.trigger("tabStructureChanged");
      },
      sort: function() {  
        if (!window.tabDragStarted) {
          $tabContainer.find("li:last").addClass("drag");
          $tabContainer.find("li:not(.drag), #tab-lead-in").css("visibility", "visible").animate({opacity: 0.5}, 300);
          window.tabDragStarted = true;
        }
      },
      stop: function() {
        window.tabDragStarted = false;
        $tabContainer.find("li:not(.drag), #tab-lead-in").css("visibility", "visible").animate({opacity: 1.0}, 300);
        $tabContainer.trigger("tabOrderChanged");
      },
      revert: true,
      distance: 15
    });
  };
})(jQuery);
