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

    // Glows
//    $("#results-area").glow("glow-small", {position: "absolute", top: "65px", bottom: ($.browser.msie ? "14px" : "10px"), left: "10px", right: "10px"});
  };

  /**
   * Adds the markup required to create a glow effect.
   */
  jQuery.fn.glow = function(glowClass, containerCss, insideCss) {
    return this.each(function() {
      var $this = $(this);

      // Get content dimensions
      var contentWidth = $this.outerWidth();
      var contentHeight = $this.outerHeight();
    
      // Add glow markup
      var $glowDiv = $("<span class='" + glowClass + "'></span>");
      $this.before($glowDiv);
      $glowDiv.append($this);
      $glowDiv.append("<span class='t'></span><span class='l'></span><span class='r'></span><span class='b'></span><span class='tl'></span><span class='bl'></span><span class='tr'></span><span class='br'></span>");

      // Copy a number of CSS properties
      $glowDiv.cssFrom($this, ["float"]);

      // Extract border sizes
      var borderWidth = $glowDiv.find(".tl").width();
      var borderHeight = $glowDiv.find(".tl").height();

      // Shift the contained element
      $glowDiv.css({position: 'relative', padding: borderWidth + 'px ' + borderHeight + 'px'});
      
      if (containerCss) {
        $glowDiv.css(containerCss);
      }
      if (insideCss) {
        $this.css(insideCss);
      }
    });
  };
})(jQuery);
