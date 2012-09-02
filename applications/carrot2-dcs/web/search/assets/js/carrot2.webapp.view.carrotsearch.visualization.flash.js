(function($) {
  $.pluginhelper.make("carrotsearchFlashVisualization", function(el, options, initialized) {
    var $element = $(el);
    var flashId = $element.attr("id") + "-flash";
    var $inner = $element.html("<div id='" + flashId + "' />").find("div");
    var visualization = options.factory($.extend({}, options.options, {
      id: $inner.attr("id"),
      onInitialize: function() {
        $inner = $("#" + flashId);
        initialized();
      },
      onGroupSelectionChanged: function(groups) {
        options.clusterSelectionChanged(_.map(groups, function(id) { return clustersById[id]; }));
      },
      onModelChanged: options.modelChange
    }));
    var hasData = false, clustersById;

    // Templates
    var carrot2Document = _.template("<document id='<%- id %>'>" +
        "<title><%- title %></title>" +
        "<url>xxx</url>" +
        "<snippet><%- snippet %></snippet>" +
      "</document>");
    var carrot2Group = _.template("<group id='<%- id %>' score='<%- score %>'>" +
        "<title><%= phrases %></title>" +
        "<%= groups %>" +
        "<%= documents %>" +
      "</group>");
    var carrot2Phrase = _.template("<phrase><%- phrase %></phrase>");
    var carrot2DocumentRef = _.template("<document refid='<%- id %>'></document>");

    // Export public methods
    this.populate = populate;
    this.clear = clear;
    this.populated = populated;
    this.select = select;
    this.show = function() {
      $inner.css("visibility", "visible");
      $element.css("z-index", "0");
    };
    this.hide = function() {
      $inner.css("visibility", "hidden");
      $element.css("z-index", "-1");
    };
    return undefined;


    //
    // Private methods
    //
    function populate(data) {
      var xml = "<searchresult><query>test</query>";
      var docDefaults = { title: "", snippet: "", url: "", sources: [] };

      xml += _.reduce(data.documents, function (xml, doc) {
        xml += carrot2Document(_.defaults(doc, docDefaults));
        return xml;
      }, "");

      xml += _.reduce(data.clusters, function reducer(xml, cluster) {
        xml += carrot2Group({
          id: cluster.id,
          size: cluster.size,
          score: cluster.score,
          phrases: _.reduce(cluster.phrases, function (xml, p) { xml += carrot2Phrase({ phrase: p }); return xml; }, ""),
          documents: _.reduce(cluster.documents, function (xml, d) { xml += carrot2DocumentRef({ id: d }); return xml; }, ""),
          groups: _.reduce(cluster.clusters || [], reducer, "")
        });
        return xml;
      }, "");

      xml += "</searchresult>";

      visualization.set("dataXml", xml);

      hasData = true;
      clustersById = _.reduce(data.clusters, function reducer(byId, cluster) {
        byId[cluster.id] = cluster;
        _.reduce(cluster.clusters || [], reducer, byId);
        return byId;
      }, {});
    }

    function clear() {
      visualization.set("dataXml", null);
      hasData = false;
    }

    function populated() {
      return hasData;
    }

    function select(ids) {
      visualization.set("selection", null);
      visualization.set("selection", ids);
    }
  });
})(jQuery);
