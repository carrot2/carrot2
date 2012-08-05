(function($) {
  $.pluginhelper.make("foldersView", function(el, options, initialized) {
    var $container = $(el);
    var hasData = false, clustersById;

    // Templates
    var folderTemplate = _.template(
      "<li class='folded <%= clazz %>'>" +
        "<a id='c<%- cluster.id %>' href='#'>" +
          "<span class='tree'></span>" +
          "<span class='phrase'><%- cluster.phrases[0] %></span>" +
          "<span class='size'>(<%- cluster.size %>)</span>" +
        "</a>" +
        "<%= clusters %>" +
      "</li>");

    // Event handlers
    $container.on("click", "a", function(e) {
      e.preventDefault();

      if ($(e.target).is("span.tree")) {
        var $li = $(this).closest("li");
        if ($li.is(".folded")) {
          unfold($li);
        } else {
          fold($li);
        }
      } else {
        // Mimic the old windows explorer behaviour for filesystem trees
        var $previousActive = $container.find("a.active");
        var $previousActiveLi = $previousActive.parent();
        var $newActive = $(this);
        var $newActiveLi = $newActive.parent();

        unfold($newActiveLi);
        $newActive.addClass("active");

        if (!e.ctrlKey) {
          fold($newActiveLi.siblings().filter($previousActiveLi));
          $previousActive.removeClass("active");
        }

        options.clusterSelectionChanged(_.map(
          $container.find("a.active").map(function() {
            return this.id;
          }),
          function(id) { return clustersById[id.substring(1)]; }));
      }

      function fold($el) {
        $el.children("ul").slideUp(function() {
          $el.addClass("folded");
        });
      }

      function unfold($el) {
        $el.children("ul").slideDown(function() {
          $el.removeClass("folded");
        });
      }
    });

    // Export public methods
    this.populate = populate;
    this.clear = clear;
    this.populated = populated;
    this.select = select;
    this.show = function() {
      $container.show();
    };
    this.hide = function() {
      $container.hide();
    };

    initialized();
    return undefined;


    //
    // Private methods
    //
    function populate(data) {
      // Generate HTML
      var html = "<a href='#'><span class='phrase'>All topics</span><span class='size'>(100)</span></a>" +
        "<ul>" +
          _.reduce(data.clusters, function reducer(html, cluster) {
            html += folderTemplate({
              cluster: cluster,
              clazz: cluster.clusters ? "branch" : "",
              clusters: cluster.clusters ? "<ul>" + _.reduce(cluster.clusters, reducer, "") + "</ul>" : ""
            });
            return html;
          }, "") +
        "</ul>";

      clustersById = _.reduce(data.clusters, function reducer(byId, cluster) {
        byId[cluster.id] = cluster;
        _.reduce(cluster.clusters || [], reducer, byId);
        return byId;
      }, {});


      $container.html(html);
      hasData = true;
      options.modelChange();
    }

    function clear() {
      $container.html("");
      hasData = false;
    }

    function populated() {
      return hasData;
    }

    function select(ids) {
      $container.find("a").removeClass("active");
      var $active = $("#c" + ids.join(", #c"));
      $active.addClass("active").parents("li").removeClass("folded");

      // Scroll to view
      var clusterOffsetTopRelative = $active.offset().top - $container.offset().top;
      $container.animate({
        scrollTop: clusterOffsetTopRelative < $container.height() - 20 ?
          0 : clusterOffsetTopRelative - ($container.height() - 20) / 2
      }, 800);
    }
  });
})(jQuery);
