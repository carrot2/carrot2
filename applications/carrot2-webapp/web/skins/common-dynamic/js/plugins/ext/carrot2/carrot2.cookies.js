/**
 * Stores user preferences (e.g. active tab, tab order) in cookies which
 * will be retrieved and interpreted on the server side.
 */
(function($) {
  /** Cookie name for storing the active source */
  var COOKIE_ACTIVE_SOURCE = "active-source";
  
  /** Cookie name for storing tab order */
  var COOKIE_SOURCE_ORDER = "source-order";
  
  /** 
   * Cookie for tracking the application version. Sometimes after an upgrade on the
   * server, we'll need to delete some cookies and this cookie will tell us when this 
   * should happen. 
   */
  var COOKIE_APP_VERSION = "app-version";
  
  /**
   * Current application version. It needs to be incremented e.g. when we add, remove
   * or change id of a document source.
   */
  var CURRENT_VERSION = 3005;
  
  /** "Forever" (10 years) storage period for cookie */
  var NEVER_EXPIRE = 30 * 12 * 10;
  
  $(document).ready(function() {
    var appVersion = $.cookie(COOKIE_APP_VERSION);
    if (!appVersion || appVersion < CURRENT_VERSION)
    {
      $.cookie(COOKIE_ACTIVE_SOURCE, null);
      $.cookie(COOKIE_SOURCE_ORDER, null);
      $.cookie(COOKIE_APP_VERSION, CURRENT_VERSION, { expires: NEVER_EXPIRE });
    }
    
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
