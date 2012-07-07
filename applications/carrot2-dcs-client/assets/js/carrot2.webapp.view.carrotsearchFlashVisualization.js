(function($) {
  $.pluginhelper.make("carrotsearchFlashVisualization", function(el, options) {
    var $element = $(el);
    var $inner = $element.html("<div id='" + $element.attr("id") + "-flash' />").find("div");
    var visualization = options.constructor($.extend({}, options.options, {
      id: $inner.attr("id")
    }));

    // Export public methods
    return undefined;


    //
    // Private methods
    //
  });
})(jQuery);
