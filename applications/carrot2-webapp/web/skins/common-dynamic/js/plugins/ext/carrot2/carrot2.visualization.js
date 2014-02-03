(function($) {
  /** 
   * Visualization-related variables from the outside.
   */
  jQuery.visualization = { };
  
  $(document).ready(function() {
    if (typeof $.visualization.dataUrl != 'undefined') {
      if (!CarrotSearchFoamTree.supported | !CarrotSearchCircles.supported) {
        $("#clusters-panel").text("Oops! Visualizations require a modern, HTML5-capable browser.");
        return;
      }

      var visualization;
      if ($.visualization.visualization == 'foamtree') {
        visualization = new CarrotSearchFoamTree({
          id: "clusters-panel",
          captureMouseEvents: false,
          pixelRatio: Math.min(1.5, window.devicePixelRatio || 1),

          rainbowStartColor: "hsla(  0, 100%, 70%, 1)",
          rainbowEndColor:   "hsla(359, 100%, 70%, 1)",
          "groupFillGradientCenterLightnessShift": 12,

          groupFontFamily: "Tahoma, Arial, sans-serif"
        });
      }

      if ($.visualization.visualization == 'circles') {
        // configure inner title box, animation speed, colors. 
        visualization = new CarrotSearchCircles({
          id: "clusters-panel",
          captureMouseEvents: false,
          pixelRatio: Math.min(1.5, window.devicePixelRatio || 1),
          titleBar: "inscribed",
          titleBarTextColor: "#555",
          groupOutlineColor: "#fff",
          groupFontFamily: "Tahoma, Arial, sans-serif",
          groupMinFontSize: "8",
          groupMaxFontSize: "25",
          ratioAngularPadding: 0.2,
          labelColorThreshold: 0.2,
          groupLinePadding: 1,
          groupLabelDecorator: function(opts, props, vars) {
            vars.labelText = vars.labelText.toLocaleUpperCase();
          },

          rainbowStartColor: "hsla(  0, 100%, 50%, 0.6)",
          rainbowEndColor:   "hsla(300, 100%, 50%, 0.6)",

          onModelChanged: function() {
            this.set("open", {all: true, open: true});
          }
        });
      }

      // Attach listeners.
      if (visualization != null) {
        visualization.set({
          onGroupSelectionChanged: function(selection) {
            if (selection.groups.length == 0) {
              $("#clusters-panel").trigger("carrot2-clusters-selected-top");
            } else {
              if (selection.groups.length > 1) {
                var docUnion = {}
                var labels = []
                selection.groups.forEach(function(g) {
                  g.documents.forEach(function(d) { docUnion[d] = true; });
                  labels.push(g.label);
                });
                $("#clusters-panel").trigger("carrot2-clusters-selected", [undefined, Object.keys(docUnion), labels.join(" (and) ")]);
              } else {
                var group = selection.groups[0];
                $("#clusters-panel").trigger("carrot2-clusters-selected", [group.id, group.documents, group.label]);
              }
            }
          }
        });
      }

      // Attach container resize listener.
      window.addEventListener("resize", (function() {
        var timeout;
        return function() {
          window.clearTimeout(timeout);
          timeout = window.setTimeout(visualization.resize, 300);
        };
      })());

      // Load the data model (convert from legacy XML).
      function convert(clusters) {
        var $foo = $(clusters).map(function(index, cluster) {
          cluster = $(cluster); // destroys the model, simplifies processing.
          // Traversal is child-first and we detach children so that 'title > phrase' selector later on
          // only captures the current cluster's phrases. We want all documents though, so the flattened list
          // is collected before remove().
          var flattened = jQuery.makeArray(cluster.find("document").map(function(i, e) { return $(e).attr("refid")}));
          cluster.remove();
          var subgroups = cluster.children("group").size() > 0 ? convert(cluster.children("group")) : [];

          var weight = cluster.attr("size") ? cluster.attr("size") : 0;
          if (cluster.find("attribute[key ~= 'other-topics']").length > 0) {
            weight = 0;
          } 

          return {
            id:     cluster.attr("id"),
            label:     jQuery.makeArray(cluster.find("title > phrase").map(function(i, e) { return $(e).text(); })).join(", "),
            documents: flattened,
            weight: Number(weight),
            groups: subgroups
          }
        });

        console.log($foo);

        return jQuery.makeArray($foo);
      }

      $.ajax({
        url: decodeURIComponent($.visualization.dataUrl),
        dataType: "xml",
        success: function(data) {
          visualization.set({
            dataObject: {
              groups: convert($(data).find("searchresult > group"))
            }
          });
        }
      });
    }
  });

  /** Safari layout bug hack */
  $(window).load(function() {
    if ($.browser.safari) {
      $("#clusters-panel").css("display", "block");
    }
  });
})(jQuery);
