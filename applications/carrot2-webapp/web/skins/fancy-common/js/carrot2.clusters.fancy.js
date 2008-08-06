(function($) {

  $(document).ready(function() {
    $("#clusters-panel").bind("carrot2-clusters-loaded", function() {
      enhance();
      if ($.browser.msie) {
        $("#clusters").bind("carrot2-clusters-folded", fixLayout);
        $("#clusters").bind("carrot2-clusters=more", fixLayout);
      }
    });
  });

  /**
   * Adds markup required by the "fancy" skin.
   */
  function enhance() {
    $("#clusters li:last-child:not(:has(ul))").addClass("leaf-last");
    $("#clusters li:last-child:has(ul)").addClass("branch-last");
    $("#clusters li.more").addClass("leaf-last");
  };

  /**
   * Fixes IE7 rendering problems on the fancy skin.
   */
  function fixLayout(event, $element, action) {
    window.setTimeout(function() {
      $("#clusters span.tree").remove();
      $("#clusters li:not(.more)").children("a").prepend("<span class='tree'></span>");
    }, 0);
  };

  /**
   * Overrides the default toggle function to add some animation.
   */
  jQuery.clusters.toggle = function($element, action, callback) {
    if ($.browser.msie) {
      jQuery.clusters.toggleDefault($element, action, callback);
    } else {
      $element.animate({
        height: action,
        opacity: action,
        marginTop: action,
        marginBottom: action,
        paddingTop: action,
        paddingBottom: action
      }, 200, callback);
    }
  };

})(jQuery);

