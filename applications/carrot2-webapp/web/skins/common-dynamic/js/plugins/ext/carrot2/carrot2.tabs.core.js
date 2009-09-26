(function($) {
  $(document).ready(function() {
    sourceTabs();
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

    // Add autosubmit on tab activation
    $tabContainer.bind("tabActivated", function() {
      if ($("#results-area").size() > 0) {
        // A little-hack, dependency-wise: we remove attribute names from advanced options
        // so that when we submit the form after tab change, common attribute values
        // are not passed between sources (e.g. site between boss-wiki and boss-images).
        if ($tabContainer.data("previous-source") != $("#source").val()) {
          $("#advanced-options :input").removeAttr("name");
        }
        $("#search-form")[0].submit();
      }
    });
    
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
    var $sourceHidden = $("#source");
    $tabContainer.data("previous-source", $sourceHidden.val());
    $sourceHidden.val(tabId);
    $tabContainer.trigger("tabActivated", [ tabId ]);
    
    $("#query").focus();
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
