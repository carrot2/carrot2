(function($) {
  $.pluginhelper.make("foldersView", function(el, options, initialized) {
    $(el).text("folders");

    // Export public methods
    this.populate = populate;
    this.clear = clear;
    this.populated = populated;
    this.shown = function() { };

    initialized();
    return undefined;


    //
    // Private methods
    //
    function populate(data) {
    }

    function clear() {
    }

    function populated() {
      return false;
    }
  });
})(jQuery);
