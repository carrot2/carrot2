(function($) {
  var WebFont = $.pluginhelper.make("webfont", function(el, options) {
    var self = this;
    self.loadingInProgress = typeof options.google != "undefined";

    window.WebFontConfig = $.extend( {}, options, {
      active: function() {
        self.loadingInProgress = false;
        $(el).trigger("webfontactive");
      },
      inactive: function() {
        self.loadingInProgress = false;
        $(el).trigger("webfontinactive");
      }
    });
    (function() {
      var wf = document.createElement('script');
      wf.src = ('https:' == document.location.protocol ? 'https' : 'http') +
          '://ajax.googleapis.com/ajax/libs/webfont/1/webfont.js';
      wf.type = 'text/javascript';
      wf.async = 'true';
      var s = document.getElementsByTagName('script')[0];
      s.parentNode.insertBefore(wf, s);
    })();
  });

  WebFont.prototype.loading = function() {
    return this.loadingInProgress;
  };
})(jQuery);
