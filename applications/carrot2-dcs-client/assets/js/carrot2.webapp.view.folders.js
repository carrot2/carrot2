(function($) {
  $.pluginhelper.make("foldersView", function(el, options, initialized) {
    var $container = $(el);
    var hasData = false;

    // Templates
    var folderTemplate = _.template(
      "<li class='folded <%= clazz %>'>" +
        "<a id='<%- cluster.id %>' href='#'>" +
          "<span class='tree'></span>" +
          "<span class='phrase'><%- cluster.phrases[0] %></span>" +
          "<span class='size'>(<%- cluster.size %>)</span>" +
        "</a>" +
        "<%= clusters %>" +
      "</li>");


    // Export public methods
    this.populate = populate;
    this.clear = clear;
    this.populated = populated;
    this.select = select;
    this.shown = function() { };

    initialized();
    return undefined;


    //
    // Private methods
    //
    function populate(data) {
      // Generate HTML
      var html = "<a href='#'><span class='phrase'>All topics</span><span class='size'>(100)</span></a>" +
        "<ul>" +
          _.reduce(data.clusters, function reducer(html, cluster) {
            html += folderTemplate({
              cluster: cluster,
              clazz: cluster.clusters ? "branch" : "",
              clusters: cluster.clusters ? "<ul>" + _.reduce(cluster.clusters, reducer, "") + "</ul>" : ""
            });
            return html;
          }, "") +
        "</ul>";

      $container.html(html).on("click", "a", function() {
        $(this).parent().toggleClass("folded");
      });

      hasData = true;
    }

    function clear() {
      $container.html("");
      hasData = false;
    }

    function populated() {
      return hasData;
    }

    function select(ids) {
    }
  });
})(jQuery);
