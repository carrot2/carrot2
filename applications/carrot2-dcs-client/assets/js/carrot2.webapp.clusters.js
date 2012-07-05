(function($) {
  $.pluginhelper.make("clusters", function(el, options) {
    var $views = $(options.components.views);

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


    this.view = setActiveView;
    this.algorithm = setActiveAlgorithm;
    return;

    function setActiveView(view) {
      actiateByClassAndId($views, "view", view);
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
  });
})(jQuery);
