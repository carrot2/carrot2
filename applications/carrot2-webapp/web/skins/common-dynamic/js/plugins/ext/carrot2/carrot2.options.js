/**
 * Option handling code
 */
(function($) {
  /** Cookie name for remembering the state of the options */
  var COOKIE_OPTIONS_SHOWN = "show-options";
  var COOKIE_ADVANCED_OPTIONS_SHOWN = "show-advanced-options";
  
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
   * Returns identifier of the currently selected source.
   */
  function getSourceId()
  {
    return $("#source").val();
  }
  
  /**
   * Core functions for handling tabs exported to the outside.
   */
  jQuery.options = {
    updateResultsArea: updateResultsArea,
    getSourceId: getSourceId
  };

  $(document).ready( function() {
    $("#show-options").click( function() {
      showOptions();
      $.cookie(COOKIE_OPTIONS_SHOWN, "t", {
        expires: 30 * 12 * 10
      });
      if ($("#advanced-options").is(":visible") && $("#options").is(":visible")) {
        updateAdvancedOptions($.options.getSourceId());
      }
      return false;
    });

    $("#hide-options").click( function() {
      fixResultsAreaSize(false, "#options");
      var $resultsArea = $("#results-area");

      if ($resultsArea.size() == 0) {
        $("#main-info").show();
      }
      $("#example-queries, #show-options").show();
      $("#options, #hide-options").hide();
      $("#query").focus();
      
      $.cookie(COOKIE_OPTIONS_SHOWN, null);
      return false;
    });
    
    $("#show-advanced-options").click(function(event) {
      var $link = $(this);
      updateAdvancedOptions($.options.getSourceId(), function () {
        showAdvancedOptions();
        $.cookie(COOKIE_ADVANCED_OPTIONS_SHOWN, "t", {
          expires: 30 * 12 * 10
        });
      });
      return false;
    });
    
    $("#hide-advanced-options").click(function() {
      fixResultsAreaSize(false, "#advanced-options");
      $(this).hide();
      $("#show-advanced-options").show();
      $("#advanced-options").hide() 
      $.cookie(COOKIE_ADVANCED_OPTIONS_SHOWN, null);
      return false;
    });
    
    $("#source-tabs").bind("tabActivated", function(event, sourceId) {
      if ($("#advanced-options").is(":visible") && $("#options").is(":visible")) {
        updateAdvancedOptions(sourceId);
      }
    });
    
    $("#advanced-options :checkbox").submitCheckboxAsHidden();
    
    if ($("#options").is(":visible")) {
      fixResultsAreaSize(true, "#options");
    } 
  });

  function showOptions()
  {
    fixResultsAreaSize(true, "#options");
    
    $("#example-queries, #main-info, #show-options").hide();
    $("#options, #hide-options").show();
    $("#query").focus();
  }
  
  function showAdvancedOptions()
  {
    $("#advanced-options").show();
    $("#show-advanced-options").hide();
    $("#hide-advanced-options").show();
    fixResultsAreaSize(true, "#advanced-options");
  }
  
  function fixResultsAreaSize(showing, updatedElementSelector)
  {
    var $resultsArea = $("#results-area");
    if ($resultsArea.size() != 0) {
      var optionsHeight = $(updatedElementSelector).innerHeight();
      jQuery.options.updateResultsArea($resultsArea, optionsHeight, (showing ? 1 : -1));
    }
  }
  
  function updateAdvancedOptions(sourceId, callback)
  {
    if ($("#results-area").size() == 0) {
      $.get($.options.url, {source: sourceId, v: $.carrot2.build}, function(data) {
        var $opts = $("#advanced-options");
        $opts.html(data).find(":checkbox").submitCheckboxAsHidden();
        if (callback) {
          callback.call($opts);
        }
      });
    } else {
      if (callback) {
        callback.call($("#advanced-options"));
      }
    }
  }
})(jQuery);
