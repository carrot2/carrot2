(function($) {
  $.pluginhelper.make("clusters", function(el, options) {
    var $views = $(options.components.views); // view tabs
    var $data = $(options.components.data); // documents and clusters container
    var $clusters = $(options.components.clusters); // container of all views

    // Data
    var viewsById = _.reduce(options.views, function (byId, v) { byId[v.id] = v; return byId; }, {});
    var activeView, currentData, viewPlugins = { };

    // Compiled templates
    var viewTemplate = _.template('<li><a href="#<%- id %>" class="view"><i class="icon icon-<%- id %>" title="<%- label %>"></i></a></li>');
    var algorithmTemplate = _.template(
      '<li class="<%- clazz %>"><%= by %><a class="algorithm" href="#<%- algorithm.id %>" title="<%- algorithm.description %>"><%- algorithm.label %> <%= icon %></a></li>');

    // Generate view tabs
    var html = _.reduce(options.views,
      function(html, view) {
        return html += viewTemplate(view);
      }, "");

    // Primary algorithm tabs
    options.algorithms[0].first = true;
    html += _.reduce(_.filter(options.algorithms, function(a) { return !a.other; }),
      function(html, algorithm) {
        return html += algorithmTemplate({
          algorithm: algorithm,
          by: algorithm.first ? "<span>by</span>" : "",
          clazz: algorithm.first ? "grouping" : "",
          icon: ""
        });
      }, "");

    // Other algorithms dropdown
    var otherAlgorithms = _.filter(options.algorithms, function(a) { return a.other; });
    if (otherAlgorithms.length > 0) {
      html += '<li class="dropdown">\
                 <a class="dropdown-toggle" data-toggle="dropdown" href="#">other<b class="caret"></b></a>\
                 <ul class="dropdown-menu">';

      html += _.reduce(otherAlgorithms,
        function(html, algorithm) {
          return html += algorithmTemplate({
            algorithm: algorithm,
            clazz: "",
            by: "",
            icon: "<i class='icon-ok'></i>"
          });
        }, "");

      html += '</ul></li>';
    }

    $views.html(html);

    // Bind listeners
    $views.on("click", "a.view", function(e) {
      var id = $(this).attr("href").substring(1);
      setActiveView(id);
      options.viewChanged(id);
      e.preventDefault();
    });
    $views.on("click", "a.algorithm", function(e) {
      var id = $(this).attr("href").substring(1);
      setActiveAlgorithm(id);
      options.algorithmChanged(id);
      e.preventDefault();
    });

    // Export public methods
    this.view = setActiveView;
    this.algorithm = setActiveAlgorithm;
    this.populate = populate;
    return undefined;


    //
    // Private methods
    //

    function setActiveView(view) {
      activeView = view;

      // Show active tab
      actiateByClassAndId($views, "view", view);

      // Show the actual view element
      embedOrShow(view);
    }

    function setActiveAlgorithm(algorithm) {
      $views.find(".dropdown").removeClass("active");
      var $li = actiateByClassAndId($views, "algorithm", algorithm);
      $li.parents(".dropdown").addClass("active");
    }

    function actiateByClassAndId($list, clazz, id) {
      $list.find("a." + clazz).parent().removeClass("active");
      return $list.find("a." + clazz + "[href = '#" + id + "']").parent().addClass("active");
    }

    function populate(data) {

      // Clear inactive views so that old data doesn't flash when switching views
      viewsById[activeView].plugin
    }

    function embedOrShow(id) {
      var view = viewsById[id];
      var $v = $("#" + id);

      if ($v.size() == 0) {
        // Create an element
        $v = $("<div id='" + id + "' />").appendTo($clusters);

        // Let the plugin do the embedding
        $v[view.plugin](view.config);
      }

      // TODO: a smoother transition here?
      $data.toggleClass("narrow-view", view.type == "narrow");
      $clusters.children().not($v).hide();
      $v.show();
    }
  });
})(jQuery);
