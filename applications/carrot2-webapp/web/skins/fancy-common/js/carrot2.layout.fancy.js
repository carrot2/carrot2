/**
 * Adds and modifies HTML markup required by this particular skin.
 */
(function($) {
  
  $(document).ready(function() {
    enableUi();
    enhance();
  });
  
  /**
   * Shows the actual UI elements. If there is no JavaScript enabled, this
   * function will not be called and the user will see the message shown
   * in <noscript> rather than the UI which requires JavaScript to run.
   */
  function enableUi() {
    setTimeout(function() { 
      $("#query").focus();
    }, 200);
    $("#loading").fadeOut(1000);
    $("div.disabled-ui").removeClass("disabled-ui");
  };

  /**
   * Dynamically adds markup required by the specific skin.
   */
  function enhance() {
    // Add placeholders for tab information on the startup screen
    $("#startup #search-area").append('<div id="extra-info"><div id="tab-info"></div><div id="example-queries"></div></div>');

    // Add pipes between entries in lists of links
    $(".example-queries a:not(:last-child)").after("<span class='pipe'>&#160;| </span>");

    // Copy util links to the startup section
    $("#util-links ul").clone().appendTo("#startup #search-area");
  };
})(jQuery);
