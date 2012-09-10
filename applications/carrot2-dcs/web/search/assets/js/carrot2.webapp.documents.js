(function($) {
  $.pluginhelper.make("documents", function(el, options) {
    var $listing = $(options.components.listing);
    var $summary = $(options.components.summary);
    var $container = $(el);

    var currentData;

    // Compiled templates
    var summaryRootTemplate = _.template('Top <%- count %> results for <b><%- query %></b>');
    var summaryOneGroupTemplate = _.template('<%- count %> results in group <b><%- label %></b>');
    var summaryMoreGroupsTemplate = _.template('<%- count %> results in groups <%= groups %> ');

    // Export public methods
    this.populate = populate;
    this.select = select;
    this.clearSelection = clearSelection;
    this.loading = function () { $container.addClass("loading"); };
    return undefined;

    //
    // Private methods
    //
    function populate(data, source) {
      var template = options.templates.custom[source] || options.templates.fallback;

      var docDefaults = { title: "", snippet: "", url: "", sources: [] };
      $listing.html(_.reduce(data.documents, function (html, doc) {
        html += template(_.defaults(doc, docDefaults));
        return html;
      }, ""));

      $summary.html(summaryRootTemplate({
        count: data.documents.length,
        query: data.query
      }));

      currentData = data;
      $container.removeClass("loading");
    }

    function select(ids, labels) {
      $listing.scrollTop(0).children().hide().filter(function() {
        return ids[this.id.substring(1)];
      }).show();

      if (labels.length == 1) {
        $summary.html(summaryOneGroupTemplate({
          count: _.size(ids),
          label: labels[0]
        }));
      } else {
        $summary.html(summaryMoreGroupsTemplate({
          count: _.size(ids),
          groups: "<b>" + labels.join("</b>, <b>") + "</b>"
        }));
      }
    }

    function clearSelection() {
      $listing.children().show();

      $summary.html(summaryRootTemplate({
        count: currentData.documents.length,
        query: currentData.query
      }));
    }
  });
})(jQuery);
