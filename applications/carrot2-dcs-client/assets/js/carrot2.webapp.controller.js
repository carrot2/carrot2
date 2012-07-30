(function($) {
  var ControllerDesktop = $.pluginhelper.make("controller", function(el, options) {
    var $container = $(el);

    // Initialize application state
    state.source = options.defaults.source;
    state.results = options.defaults.results;
    state.view = options.defaults.view;
    state.algorithm = options.defaults.algorithm;

    var currentData;

    // Initialize and wire the components
    var $search = null, $clusters = null, $documents = null;

    // Search form
    $search = $(options.search.container).search($.extend({}, options.search, {
      sourceChanged: function(source) {
        state.source = source;
        doSearch();
      },
      resultsCountChanged: function(count) {
        state.results = count;
        doSearch();
      },
      searchTriggered: function(query) {
        state.query = query;
        doSearch();
      }
    }));
    $search.search("source", state.source);
    $search.search("results", state.results);

    // Cluster views
    $clusters = $(options.clusters.container).clusters($.extend({}, options.clusters, {
      viewChanged: function (view) {
        state.view = view;
      },
      algorithmChanged: function (algorithm) {
        state.algorithm = algorithm;
        doSearch();
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
    $documents = $(options.documents.container).documents($.extend({}, options.documents, {
    }));

    // Show the UI when initialization complete
    $container.attr("class", "startup");
    return undefined;

    /**
     * Queries the searcher and updates the views with results.
     */
    function doSearch() {
      if ($.trim(state.query).length == 0) {
        $search.search("focus");
        return;
      }

      $container.attr("class", "results");
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

    decode: function(string) {
      var s = string || window.location.href;
      var split = (s.split("#")[1] || "").split("/");
      var decoded = { };
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
      delete decoded.encode;
      delete decoded.decode;
      delete decoded.push;
      $.extend(this, decoded);

      function convert(val) {
        if (val == "false") {
          return false;
        } else if (val == "true") {
          return true;
        } else {
          return val;
        }
      }
    },

    push: function() {
      var prev = window.location.hash;
      window.location.hash = "#" + this.encode();
      return prev != window.location.hash;
    }
  };
})(jQuery);
