/**
 * Stores user preferences (e.g. active tab, tab order) in cookies which
 * will be retrieved and interpreted on the server side.
 */
(function($) {
  /** Cookie name for storing the active source */
  var COOKIE_ACTIVE_SOURCE = "active-source";
  
  /** Cookie name for storing tab order */
  var COOKIE_SOURCE_ORDER = "source-order";
  
  /** "Forever" (10 years) storage period for cookie */
  var NEVER_EXPIRE = 30 * 12 * 10;
  
  $(document).ready(function() {
    var $tabContainer = $("#source-tabs");
    
    $tabContainer.bind("tabActivated", function(e, tabId) {
      $.cookie(COOKIE_ACTIVE_SOURCE, tabId, { expires: NEVER_EXPIRE });
    });

    $tabContainer.bind("tabOrderChanged", function(e, tabId) {
      var order = $.map($tabContainer.find(".tab:not(.drag)"), function(val) {
        return val.id;
      });
      $.cookie(COOKIE_SOURCE_ORDER, order.join("*"), { expires: NEVER_EXPIRE });
    });
  });
})(jQuery);
