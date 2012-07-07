(function($) {
  $.pluginhelper.make("carrotsearchHtml5Visualization", function(el, options, initialized) {
    var visualization = options.factory($.extend({}, options.options, {
      id: $(el).attr("id")
    }));

    // Export public methods
    this.populate = populate;
    this.clear = clear;
    this.populated = populated;

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
  });
})(jQuery);
