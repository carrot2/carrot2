(function($) {
  /** 
   * Document-related methods exported to and imported from the outside.
   */
  jQuery.documents = { loaded: false }
  
  /**
   * Binds a handler for an event called when documents finish loading.
   */
  $(document).ready(function() {
    $("#documents-panel").bind("carrot2-documents-loaded", function() {
      loaded();
    });
    
    $("#clusters-panel").bind("carrot2-clusters-selected", function(target, clusterId, documents) {
      select(documents);
    });

    // Some actions for the results page
    if (typeof $.documents.query != 'undefined') {
      // Initiate document loading
      $.get($.unescape($.documents.url), {}, function(data) {
        jQuery.documents.loaded = true;
        var $documents = $("#documents-panel #documents");
        if ($documents.size() != 0) {
          $documents.remove();
        }
        $("#documents-panel").append(data);
        $("#documents-panel").trigger("carrot2-documents-loaded");
      });
  
      // Quick preload of some results
      if (!jQuery.documents.loaded && $.documents.source == 'web') {
        $.getJSON(
          "http://ajax.googleapis.com/ajax/services/search/web?callback=?",
          {
            v: "1.0",
            rsz: "large",
            q: $.documents.query
          },
          function(json) {
            if (!jQuery.documents.loaded) {
              $("#documents-panel").prepend(build(json));
            }
          }
        );
      }
    }
  });

  /**
   * Shows the documents with the provided documents, hides all other documents.
   */
  function select(documentIndexes) {
    
    var $temp;
    var $documents;
    
    // Detach from DOM if not on Safari
    if (!$.browser.safari) {
      var $temp = $("<span></span>");
      var $documents = $("#documents").appendTo($temp);
    } else {
      var $documents = $("#documents");
    }
    var documentsIndex = 0;
    
    $documents.children().each(function(i) {
      if (documentsIndex >= documentIndexes.length || documentIndexes[documentsIndex] > i) {
        $(this).hide();
      }
      else {
        $(this).show();
        documentsIndex++;
      }
    });
    
    // Attach back to DOM
    if (!$.browser.safari) {
      $("#documents-panel").append($documents);
    }
  }
  
  /**
   * Initializes the core class handling functionality once the 
   * clusters finish loading.
   */
  function loaded() {
    preview();
    clusters();
    
    // Finished loading
    $("#loading-documents").fadeOut(1000);
  };

  /**
   * Shows preview of the document in an iframe.
   */
  function preview() {
    $("#documents .show-preview").click(function() {
      var $this = $(this);
      var $document = $(this).parents(".document").eq(0);
      var iframeId = $document.attr("id") + "pr";
      var $iframe = $("#" + iframeId);
      
      if ($iframe.length == 0)
      {
        var url = $this.parent().find("a.title").get(0).href;
        var iframeHtml = "<div class='preview' style='display: none'>" +
                         "<iframe class='preview' id='" + iframeId + "' " +
                         "frameborder='no' src='" + url + "'></iframe></div>"
        $iframe = $(iframeHtml);
        $document.find(".url").before($iframe);
      }
      
      $iframe.animate({
        height: "toggle",
        opacity: "toggle",
        marginTop: "toggle",
        marginBottom: "toggle",
        paddingTop: "toggle",
        paddingBottom: "toggle"
      }, 200);
      
      return false;
    });
  }
  
  /**
   * Highlights clusters that contains the document being hovered on.
   */
  function clusters()
  {
    $("#documents .title-in-clusters").hover(function() {
      var documentId = $(this).parents(".document").get(0).id.substring(1) - 1;
      $.clusters.showInClusters(documentId);
      $(this).addClass("hl");
    },
    function() {
      $.clusters.clearInClusters();
      $(this).removeClass("hl");
    });
  }
  
  /**
   * Builds documents HTML based on the provided Google API JSON data.
   */
  function build(json)
  {
    var $documents = $("<div id='documents'></div>");
    var $templateDocument = $("#template-document .document");
    
    var results = json.responseData.results;
    for (var i = 0; i < results.length; i++)
    {
      var result = results[i];
      
      $document = $templateDocument.clone();
      $document.attr("id", "d" + i);
      $document.find(".rank").html(i + 1);
      $document.find("a.title, a.in-new-window").attr("href", result.url);
      $document.find("a.title").html(result.titleNoFormatting);
      $document.find(".snippet").html(result.content.replace(/<\/?b>/g, ""));
      $document.find(".url").html(result.url + "<span class='sources'>[Google]</span>");
      
      $documents.append($document);
    }
    
    $documents.append("<div id='loading-more'>Loading more results...</div>");
    
    return $documents;
  }
})(jQuery);

