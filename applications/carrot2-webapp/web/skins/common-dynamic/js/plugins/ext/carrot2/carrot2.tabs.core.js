(function($) {
  $(document).ready(function() {
    sourceTabs();
    
    $("body").bind("carrot2-loaded", function() {
      $("#source-tabs").trigger("tabActivated", [ $.tabs.getInitialActiveTab() ]);
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
      ".label, .label u, li": function(e) {
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
    
    var $target = $(e.target);
    var $tabLi;
    if ($target.is("li.tab")) {
      $tabLi = $target;
    } else {
      $tabLi = $target.parents("li.tab").eq(0);
    }
    
    $tabLi.addClass("active").removeClass("passive");
    $tabContainer.trigger("tabStructureChanged");
    
    var tabId = $tabLi.attr("id");
    $tabContainer.trigger("tabActivated", [ tabId ]);
    
    $("#source").val(tabId);
    if ($("#results-area").size() > 0) {
      $("#source").parents("form")[0].submit();
    } else {
      $("#query").focus();
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
      }
      
      if (i == $tabs.length - 1) {
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

  /**
   * Returns the id of the initially selected tab.
   */
  getInitialActiveTab = function() {
    return $("#source-tabs li.active").eq(0).attr("id");
  }
  
  /**
   * Core functions for handling tabs exported to the outside.
   */
  jQuery.tabs = {
    updateTabs: updateTabs,
    getInitialActiveTab: getInitialActiveTab
  };

})(jQuery);