(function($) {
  var ControllerDesktop = $.pluginhelper.make("controller", function(el, options) {
    var $container = $(el);

    // Initialize application state
    state.source = options.defaults.source;
    state.results = options.defaults.results;
    state.view = options.defaults.view;
    state.algorithm = options.defaults.algorithm;
    state.decode(); // capture initial state

    var currentData;

    // Initialize and wire the components
    var $search = null, $clusters = null, $documents = null;

    // Search form
    $search = $(options.search.container).search($.extend({}, options.search, {
      sourceChanged: function(source) {
        state.source = source;
        state.push();
      },
      resultsCountChanged: function(count) {
        state.results = count;
        state.push();
      },
      searchTriggered: function(query) {
        state.query = query;
        if ($.trim(state.query).length == 0) {
          $search.search("focus");
        } else {
          state.push();
        }
      }
    }));
    $search.search("source", state.source);
    $search.search("results", state.results);

    // Cluster views
    $clusters = $(options.clusters.container).clusters($.extend({}, options.clusters, {
      viewChanged: function (view) {
        state.view = view;
        state.push();
      },
      algorithmChanged: function (algorithm) {
        state.algorithm = algorithm;
        state.push();
      },
      clusterSelectionChanged: function (selectedClusters) {
        if (selectedClusters.length == 0) {
          // select all docs
          $documents.documents("clearSelection");
        } else {
          var docsToSelect = _.reduce(selectedClusters, function reducer(docsToSelect, cluster) {
            _.each(cluster.documents, function(docId) { docsToSelect[docId] = true; } );
            _.reduce(cluster.clusters || [], reducer, docsToSelect);
            return docsToSelect;
          }, {});
          $documents.documents("select", docsToSelect, _.chain(selectedClusters).pluck("phrases").flatten().value());
        }
      }
    }));
    $clusters.clusters("view", state.view);
    $clusters.clusters("algorithm", state.algorithm);

    // Document view
    $documents = $(options.documents.container).documents($.extend({}, options.documents));

    // React to path changes
    $(window).pathchange(function () {
      var changed = state.decode();
      if (state.query.length == 0) {
        $container.attr("class", "startup");
      }

      $clusters.clusters("algorithm", state.algorithm);
      $clusters.clusters("view", state.view);
      $search.search("source", state.source);
      $search.search("results", state.results);
      $search.search("query", state.query);
      if (changed.query || changed.results || changed.algorithm || changed.source) {
        doSearch();
      }
    });
    $.pathchange.init({
      useHistory: false
    });

    // Initialization complete, show the UI
    if (window.location.hash) {
      $(window).pathchange(); // decode and refresh
    } else {
      $container.attr("class", "startup");
    }
    return undefined;

    /**
     * Queries the searcher and updates the views with results.
     */
    function doSearch() {
      if ($.trim(state.query).length == 0) {
        $search.search("focus");
        return;
      }

      // TODO: cancel running search if we get another one
      $container.attr("class", "results");
      $documents.documents("loading");
      $clusters.clusters("loading");

      options.searcher({
        query: state.query,
        results: state.results,
        source: state.source,
        algorithm: state.algorithm
      }, function (data) {
        currentData = data;
        $clusters.clusters("populate", data);
        $documents.documents("populate", data);
      });
    }
  });

  // Application state management
  var state = {
    source: "",
    results: "",
    algorithm: "",
    view: "",
    query: "",

    encode: function() {
      var url = [];
      process(null, this);
      return url.join("/");

      function process(property, value) {
        if ($.isFunction(value)) {
          return;
        }

        if ($.isArray(value)) {
          $.each(value, function(i, v) {
            process(property + "[]", v);
          });
        } else if ($.isPlainObject(value)) {
          $.each(value, function(key, v) {
            process((property ? property + "." : "") + key, v);
          });
        } else {
          url.push(property, encodeURIComponent(value));
        }
      }
    },

    decode: (function(string) {
      var previousState = {};
      var initial;

      return function() {
        if (!initial) {
          initial = $.extend({}, this);
          return;
        }

        var s = string || window.location.href;
        var split = (s.split("#")[1] || "").split("/");

        var decoded;
        if (split.length > 1) {
          decoded = { };
          outer: for (var i = 0; i < split.length / 2; i++) {
            var path = decodeURIComponent(split[i*2] || "").split(/\./);
            var property = path.shift();
            var target = decoded;
            while (path.length > 0) {
              if (!$.isPlainObject(target)) {
                break outer;
              }
              if (typeof target[property] == 'undefined') {
                target[property] = { };
              }
              target = target[property];
              property = path.shift();
            }

            var val = convert(decodeURIComponent(split[i*2 + 1] || ""));
            if (property.indexOf("[]") > 0) {
              property = property.replace(/[\[\]]/g, "");
              if (typeof target[property] == 'undefined') {
                target[property] = [];
              }
              target[property].push(val);
            } else {
              target[property] = val;
            }
          }
        } else {
          decoded = initial;
        }
        delete decoded.encode;
        delete decoded.decode;
        delete decoded.push;

        var changed = {};
        $.extend(this, decoded);
        $.each(decoded, function(key, value) {
          if (previousState[key] != value) {
            changed[key] = true;
          }
        });
        $.extend(previousState, decoded);
        return changed;

        function convert(val) {
          if (val == "false") {
            return false;
          } else if (val == "true") {
            return true;
          } else {
            return val;
          }
        }
      };
    })(),

    push: function() {
      var prev = window.location.hash;
      window.location.hash = "#" + this.encode();
      return prev != window.location.hash;
    }
  };
})(jQuery);
