(function($) {
  $.pluginhelper.make("search", function(el, options) {
    // Elements
    var $search = $(el);
    var $query = $(options.components.query);
    var $sources = $(options.components.sources);
    var $examples = $(options.components.examples);
    var $queries = $(options.components.queries);
    var $results = $(options.components.results);

    // Data
    var sources = options.sources;
    var sourcesById = _.reduce(sources, function(byId, obj) { byId[obj.id] = obj; return byId; }, {});

    // Compiled templates
    var sourceTemplate = _.template("<li><a href='#<%- id %>'><%- label %></a></li>");
    var exampleQueryTemplate = _.template("<a href='#<%- id %>'><%- query %></a>");
    var resultsCountTemplate = _.template("<li><a href='#<%- count %>'><%- count %> results <i class='icon-ok'></i></a></li>");

    // Generate source tabs
    $sources.html(_.reduce(sources,
      function(html, source) {
        return html += sourceTemplate(source);
      }, ""));

    // Generate results counts
    $results.html(_.reduce(options.results,
      function(html, count) {
        return html += resultsCountTemplate({ count: count });
      }, ""));

    // Bind listeners
    $sources.on("click", "a", function(e) {
      var id = $(this).attr("href").substring(1);
      setActiveSource(id);
      options.sourceChanged(id);
      e.preventDefault();
    });
    $results.on("click", "a", function(e) {
      var count = $(this).attr("href").substring(1);
      setActiveResults(count);
      options.resultsCountChanged(count);
      e.preventDefault();
    });
    $search.find("form").submit(function(e) {
      options.searchTriggered($.trim($query.val()));
      e.preventDefault();
    });

    // Focus query field
    $query.focus();

    // Export public methods
    this.source = setActiveSource;
    this.results = setActiveResults;
    this.focus = function() {
      $query.focus();
    };
    return;

    // Private methods
    function setActiveSource(id) {
      // Activate tab
      actiateById($sources, id);

      // Update example queries
      var examples = sourcesById[id].examples;
      if (examples) {
        $queries.html(_.reduce(examples,
          function(html, query) {
            return html += exampleQueryTemplate({ id: id, query: query })
          }, ""));
        $examples.show();
      } else {
        $examples.hide();
      }
    }

    function setActiveResults(count) {
      actiateById($results, count);
    }

    function actiateById($list, id) {
      $list.children().removeClass("active");
      $list.find("a[href = '#" + id + "']").parent().addClass("active");
    }
  });
})(jQuery);
