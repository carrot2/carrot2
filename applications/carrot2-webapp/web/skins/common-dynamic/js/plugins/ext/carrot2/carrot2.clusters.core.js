(function($) {
  /** Cookie name for showing all clusters right away */
  var COOKIE_ALWAYS_SHOW_ALL_CLUSTERS = "always-all-clusters";

  /** 
   * A private reference to the flattened cluster-document structure. 
   * Key: cluster id, Value: array of documents from the cluster and its
   * subclusters.
   */
  var flattenedDocuments;
  
  /**
   * An array containing ids of clusters for each document. Index: document index, 
   * Value: array of ids of clusters to which the document belongs.
   */
  var documentClusters;

  /**
   * Binds a handler for an event called when clusters finish loading.
   */
  $(document).ready(function() {
    $("#clusters-panel").bind("carrot2-clusters-loaded", function() {
      loaded();
    });
    
    if (typeof $.clusters.url != 'undefined') {
      $.ajax({
        url: $.unescape($.clusters.url), 
        success: function(data) {
          $("#clusters-panel").prepend(data);
          $("#clusters-panel").trigger("carrot2-clusters-loaded");
        },
        // Firefox somehow does not execute JS that comes in a cached AJAX response...
        cache: !$.browser.mozilla 
      });
    }
  });

  /**
   * Core functions for handling clusters exported to the outside.
   */
  jQuery.clusters = { 
    segmentSize: 10
  };

  /**
   * Initializes the core class handling functionality once the 
   * clusters finish loading.
   */
  function loaded() {
    // Enhance makup, install listeners and cluster segmentation
    enhance();
    listeners();
    segmentation();
    $("#tree-top").addClass("selected");
    
    // Finished loading
    $("#loading-clusters").fadeOut(1000);
  };

  /**
   * Dynamically adds markup required by the specific skin.
   */
  function enhance() {
    $("#clusters li a").prepend("<span class='tree'></span>");
    $("#clusters li:not(:last-child):has(ul)").addClass("branch");
  };

  /**
   * Adds listeners for tree nodes.
   */
  function listeners() {
    $("#clusters li > a").click($.delegate({
      ".tree": function() {
        $(this).parent().toggleFolding();
        return false;
      },
      "span, a": function() {
        $(this).parent().toggleSelection();
        return false;
      }
    }));

    $("#tree-top").click(function () {
      $("#clusters .selected").removeClass("selected");
      $("#tree-top").addClass("selected");
      $("#clusters-panel").trigger("carrot2-clusters-selected-top");
    });
  };

  /**
   * Installs cluster segmentation (showing in groups of e.g. 10 clusters).
   */
  function segmentation() {
    if ($.cookie(COOKIE_ALWAYS_SHOW_ALL_CLUSTERS)) {
      return;
    }
    
    $("#clusters").segment(10);
  };
 
  jQuery.fn.segment = function(size) {
    var $lis = this.children("ul").children("li");
    if ($lis.size() <= size) {
      return this;
    }

    // Call recursively on subclusters
    $lis.each(function() {
      $(this).segment(size);
    });

    // Add segments
    var $more = $("<a href='#'><span class='tree'></span>more</a>");
    $more.one("click", function() {
      var $li = $(this).parent();
      $.clusters.toggle($li.nextAll().slice(0, size + 1), "show");
      $.clusters.toggle($li, "hide");
      $("#clusters").trigger("carrot2-clusters-more", [ $li ]);
    });

    var $showAll = $("<a class='show-all' href='#'>show all</a>");
    $showAll.one("click", function() {
      $("#clusters li:not(.more)").show();
      $("#clusters li.more").hide();
      $alwaysShowAll = $("<span id='always-show-all'><a href='#'>Always show all clusters</a></span>");
      $("#clusters").append($alwaysShowAll);
      $alwaysShowAll.find("a").fadeIn(500).one("click", function() {
        $.cookie(COOKIE_ALWAYS_SHOW_ALL_CLUSTERS, "t", { expires: 30 * 12 * 10 });
        $(this).after(" (saved)");
        $(this).parent().fadeOut(5000);
      });
    });

    $li = $("<li class='more'></li>");
    $li.append($more);
    $li.append(" | ");
    $li.append($showAll);

    $lis.slice(0, $lis.size() - 1).filter(":nth-child(" + size + "n)").after($li);
    this.children("ul").children("li").slice(size + 1).hide();

    return this;
  };

  /**
   * Handles folding of clusters. There are two pairs of complementary states a
   * cluster can be in:
   *
   * 1. Unfolded   -- cluster's subclusters are shown
   * 2. Folded     -- cluster's subclusters are not shown
   *
   * 3. Selected   -- cluster's documents are shown, cluster is marked as selected
   * 4. Unselected -- cluster's documents are not shown, cluster is not marked as selected
   *
   * Cluster toggling algorithm:
   *
   * 1. When cluster's label or icon is clicked:
   *    a. if the cluster was unselected, it gets selected and unfolded
   *    b. if the cluster was selected, it remains selected and gets unfolded
   *    c. if a sibling cluster was selected, it gets unselected and folded
   * 2. When cluster's [+] element is clicked:
   *    a. selection does not change if the selected cluster is not a child of
   *       the cluster being folded
   *    b. if the selection was on a subcluster of the cluster being folded,
   *       the folded cluster gets selected
   *    c. if the cluster was folded, it gets unfolded
   *    d. if the cluster was unfolded, it gets folded
   */
  jQuery.fn.toggleFolding = function(mode, callback) {
    if (!$.modern()) {
      if (callback) {
        window.setTimeout(callback, 10);
      }
      return this;
    }
    
    var action = mode || "toggle";
    $this = $(this);

    if (action == "toggle") {
      $this.toggleClass("folded").toggleClass("unfolded");
    } else if (action == "show") {
      $this.removeClass("folded").addClass("unfolded");
    } else if (action == "hide") {
      $this.addClass("folded").removeClass("unfolded");
    }

    // Folding a node whose child is selected?
    if ($this.hasClass("folded")) {
      var $selected = $this.find(".selected");
      if ($selected.size() > 0) {
        $selected.removeClass("selected");
        $this.addClass("selected");
        
        callback = function() {
          $this.selectDocuments();
        };
      }
    }

    // Unfold groups and show documents
    var $ul = $(this).children("ul");
    jQuery.clusters.toggle($ul, action, callback);
    if ($ul.size() == 0) {
      if (callback) {
        window.setTimeout(callback, 10);
      }
    }

    // Notify listeners
    $("#clusters").trigger("carrot2-clusters-folded", [ $this, action ]);

    return this;
  };

  /**
   * Shows or hides folded subclusters.
   */
  jQuery.clusters.toggle = function($element, action, callback) {
    if (action == "toggle") {
      $element.toggle();
    } else if (action == "show") {
      $element.show();
    } else if (action == "hide") {
      $element.hide();
    }

    if (callback) {
      callback.call(this);
    }
  };

  /** Keep an extra reference to the original toggle function. */
  jQuery.clusters.toggleDefault = jQuery.clusters.toggle;

  /**
   * Handles selection of clusters.
   */
  jQuery.fn.toggleSelection = function() {
    $this = $(this);

    if ($this.hasClass("selected")) {
      $this.toggleFolding("show");
    } else {
      // Remove selection from a sibling, if any
      if ($this.siblings(".selected").toggleFolding("hide").removeClass("selected").size() == 0) {
        // Selection was not on sibling
        $("#clusters .selected").not($this).removeClass("selected");
      }

      $(this).addClass("selected");
      $(this).toggleFolding("show", function() {
        $this.selectDocuments();
      });
    }

    return this;
  };

  /**
   * Shows documents from the cluster receiving this call.
   */
  jQuery.fn.selectDocuments = function() {
    var clusterId = $(this).attr("id");
    var clusterDocuments = $.clusters.documents(clusterId);

    // Notify listeners
    $("#clusters-panel").trigger("carrot2-clusters-selected", [ clusterId, clusterDocuments ]);

    return this;
  };

  /**
   * Returns documents contained in a cluster and its subclusters.
   */
  jQuery.clusters.documents = function(clusterId) {
    return flattenedDocuments[clusterId];
  }

  /**
   * Called from the AJAX-loaded clusters HTML part to pass cluster-document assignments.
   */
  jQuery.clusters.setDocuments = function(documents) {
    flattenedDocuments = $.clusters.flatten(documents);
    documentClusters = $.clusters.documentClusters(flattenedDocuments);
  }
  
  /**
   * Flatten a structure of cluster documents.
   */ 
  jQuery.clusters.flatten = function (clusters) {
    var flattened = { };
  
    if (clusters) {
      $.each(clusters, function(key, value) {
        flattenDocsInternal(key, value, flattened);
      });
  
      $.each(flattened, function(key, value) {
        $.sortUnique(value);
      });
    }

    return flattened;
  };

  /**
   * Internal flattening function allowing recursion.
   */
  function flattenDocsInternal(clusterId, cluster, flattened) {
    var flattenedCluster = flattened[clusterId];
    if (!flattenedCluster) {
      flattenedCluster = [];
      flattened[clusterId] = flattenedCluster;
    }

    if (cluster.d) {
      $.pushAll(flattenedCluster, cluster.d);
    }

    if (cluster.c) {
      $.each(cluster.c, function(key, val) {
        var docs = flattenDocsInternal(key, val, flattened);
        $.pushAll(flattenedCluster, docs);
      });
    }

    return flattenedCluster;
  }
  
  /**
   * Builds an array of cluster ids for each document.
   */
  jQuery.clusters.documentClusters = function(flattenedDocuments)
  {
    var documentClusters = [];
    $.each(flattenedDocuments, function(clusterId, documentIndexes) {
      $.each(documentIndexes, function(j, documentIndex) {
        if (!documentClusters[documentIndex - 1]) {
          documentClusters[documentIndex - 1] = [];
        }
        documentClusters[documentIndex - 1].push(clusterId);
      });
    });
    
    return documentClusters;
  }
  
  /**
   * Highlights those clusters that contain the provided document
   */
  jQuery.clusters.showInClusters = function(documentIndex) {
    if (documentClusters) {
      $.each(documentClusters[documentIndex], function(i, clusterId) {
        $("#" + clusterId).addClass("hl");
      });
    }
  }
  
  /**
   * Removes cluster highlights.
   */
  jQuery.clusters.clearInClusters = function() {
    $("#clusters .hl").removeClass("hl");
  }  
})(jQuery);
