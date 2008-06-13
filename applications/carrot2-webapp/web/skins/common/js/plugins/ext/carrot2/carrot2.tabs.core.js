(function($) {
  $(document).ready(function() {
    sourceTabs();
    
    $("body").bind("carrot2.loaded", function() {
      $("#source-tabs").trigger("tabActivated", [ $("#source-tabs li.active").eq(0).attr("id") ]);
    });
  });

  /**
   * Adds dynamic behaviour to tabs, including click response and sorting by
   * drag&drop.
   */
  function sourceTabs() {
    $tabContainer = $("#source-tabs");

    // Make tabs respond to clicks
    $tabContainer.click($.delegate({
      ".label": function(e) {
        activateTab(e, $tabContainer);
        return false;
      }
    }));

    // Initialize active tab
    updateTabs();
  
    // When tab structure changes, update the CSS classes
    $tabContainer.bind("tabStructureChanged", $.tabs.updateTabs);
  };

  /**
   * Activates provided tab.
   */
  activateTab = function(e, $tabContainer) {
    $tabContainer.find("li.tab").removeClass("active")
    $(e.target).parents("li.tab").addClass("active").removeClass("passive");
    $tabContainer.trigger("tabStructureChanged");
    
    var tabId = $(e.target).parents("li").eq(0).attr("id");
    $tabContainer.trigger("tabActivated", [ tabId ]);
    
    $("#source").val(tabId);
    if ($("#results-area").size() > 0) {
      $("#source").parents("form")[0].submit();
    }
  };

  /**
   * Updates the look of tabs after the active tab or tab order has changed.
   */
  updateTabs = function(e)
  {
    $tabContainer = $("#source-tabs")
    var $tabs = $tabContainer.find("li:visible:not(.drag)");
    $tabs.removeClass("passive-first passive-last active-last before-active");
    $("li:not(.active)").addClass("passive");

    $.each($tabs, function(i, tab) {
      $tab = $(tab);
      var status = tabStatus(tab);
      var nextStatus = tabStatus($tabs[i + 1]);
      var orderSuffix = "";
      if (i == 0) {
        if (status == "active") {
          $tabContainer.addClass("first-active");
        }
        else {
          $tabContainer.removeClass("first-active");
        }
      } else if (i == $tabs.length - 1) {
        orderSuffix = "-last";
      }

      $tab.addClass(status + orderSuffix);
      if (nextStatus == "active") {
        $tab.addClass("before-active");
      }
    });
  };

  /**
   * Returns the status ("active" or "passive") of the provided tab.
   */
  tabStatus = function(tabElement) {
    if (!tabElement) {
      return null;
    }
    return (tabElement && tabElement.className.indexOf("passive") >= 0 ? "passive" : "active");
  };

  copyTabInfo = function(e) {
    var $siblings = $(e.target).siblings();
    $("#tab-info").html($siblings.find("span.tab-info").clone().removeClass("hide"));
    $("#example-queries").html($siblings.find("span.example-queries").clone().removeClass("hide"));
  }

  /**
   * Core functions for handling tabs exported to the outside.
   */
  jQuery.tabs = {
    updateTabs: updateTabs
  };
})(jQuery);
