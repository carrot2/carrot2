(function($) {
  $.pluginhelper.make("carrotsearchHtml5Visualization", function(el, options, initialized) {
    var $element = $(el);
    var visualization = options.factory($.extend({}, options.options, {
      id: $element.attr("id")
    }));

    var resizePending = false;
    $(window).resize((function() {
      var to;
      return function() {
        window.clearTimeout(to);
        to = window.setTimeout(function() {
          if ($element.is(":visible")) {
            visualization.resize();
          } else {
            resizePending = true;
          }
        }, 250);
      };
    })());

    // Export public methods
    this.populate = populate;
    this.clear = clear;
    this.populated = populated;
    this.shown = shown;

    initialized();
    return undefined;


    //
    // Private methods
    //
    function populate(data) {
      visualization.set("dataObject", {
        groups: _.reduce(data.clusters,
          function reducer(arr, cluster) {
            arr.push({
              id: cluster.id,
              weight: cluster.attributes["other-topics"] ? 0 : cluster.size,
              label: cluster.phrases[0],
              groups: _.reduce(cluster.clusters || [], reducer, [])
            });
            return arr;
          }, [])
      });
    }

    function clear() {
      visualization.set("dataObject", null);
    }

    function populated() {
      return visualization.get("dataObject") !== null;
    }

    function shown() {
      if (resizePending) {
        visualization.resize();
        resizePending = false;
      }
    }
  });
})(jQuery);
