(function($) {
  $.carrot2 = {};
  
  $(document).ready(function() {
    $("body").bind("carrot2-loaded", function() {
      $("div.disabled-ui").removeClass("disabled-ui");
    });
  });
  
  $(window).load(function() {
    $("#query").focus();
    $("#loading").fadeOut(1000);
  });
})(jQuery);
