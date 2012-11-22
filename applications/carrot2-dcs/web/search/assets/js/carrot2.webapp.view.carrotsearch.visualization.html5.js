(function($) {
  $.pluginhelper.make("carrotsearchHtml5Visualization", function(el, options) {
    var $element = $(el);
    var visualization = options.factory($.extend({}, options.options, {
      id: $element.attr("id"),
      onGroupSelectionChanged: function(info) {
        options.clusterSelectionChanged(_.pluck(info.groups, "cluster"));
      },
      onRolloutStart: options.modelChange
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
    this.populate = (function() {
      // A wrapper over the target populate method that
      // delays the display if web fonts are loading.
      var fontsLoaded = false;
      var lastData = undefined;
      $("body").bind("webfontactive webfontinactive", function() {
        if (!fontsLoaded) {
          fontsLoaded = true;
          if (lastData) {
            populate(lastData);
            lastData = undefined;
          }
        }
      });
      return function(data) {
        if (fontsLoaded || !($("body").webfont("loading") === true) /* sic! */) {
          populate(data);
        } else {
          lastData = data;
        }
      };
    })();

    this.clear = clear;
    this.populated = populated;
    this.select = select;
    this.show = function() {
      $element.show();
      if (resizePending) {
        visualization.resize();
        resizePending = false;
      }
    };
    this.hide = function() {
      $element.hide();
    };

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
              weight: cluster.attributes && cluster.attributes["other-topics"] ? 0 : cluster.size,
              label: cluster.phrases[0],
              groups: _.reduce(cluster.clusters || [], reducer, []),
              cluster: cluster
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

    function select(ids) {
      visualization.set("selection", { all: true, selected: false });
      visualization.set("selection", ids);
    }
  });
})(jQuery);
