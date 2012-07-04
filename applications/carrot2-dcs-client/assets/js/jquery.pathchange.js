// Plugin that provides a "pathchange" event on the window object, notifying an application when the URL changes
// This is accomplished by watching the hash, using the hashchange event from HTML5 or a polling interval in older browsers.
// In addition, in some modern browsers, HTML5 History Management is used to support changing the URL's path without reloading the page.
// This plugin also provides a method to navigate to a URL safely, that will use HTML5 History Management to avoid a page load.
// Everything degrades gracefully, and supports RESTful client development.

// Browser Support:
//  Chrome  - Any recent version of Chrome supports everything.
//  Safari  - Any recent version of Safari supports everything.
//  Firefox - Newer versions of Firefox support the hashchange event
//            Firefox 4 betas also support HTML5 History Management
//  Internet Explorer - IE8 supports hashchange
//                      IE6 and 7 receive inferior hashchange support through a polling interval.
//  Others  - Other modern browsers probably support some subset of features.

// This plugin was authored by Ben Cherry (bcherry@gmail.com), and is released under an MIT License (do what you want with it).
// Some of the code in this plugin was adapted from Modernizr, which is also available under an MIT License.
(function($) {
  // can use $(window).bind("pathchange", fn) or $(window).pathchange(fn)
  $.fn.pathchange = function(handler) {
    return handler ? this.bind("pathchange", handler) : this.trigger("pathchange");
  };

  var my = $.pathchange = {
    // default options
    options: {
      useHistory: true, // whether we use HTML5 History Management to change the current path
      useHashchange: true, // whether we use HTML5 Hashchange to listen to the URL hash
      pollingInterval: 250, // when using Hashchange in browsers without it, how often to poll the hash (in ms)
      interceptLinks: true, // do we intercept all relative links to avoid some page reloads?
      disableHashLinks: true // do we ensure all links with href=# are not followed (this would mess with our history)?
    },

    // call this once when your app is ready to use pathchange
    init: function(options) {
      var lastHash;
      $.extend(my.options, options);

      // Listen to the HTML5 "popstate" event, if supported and desired
      if (my.options.useHistory && my.detectHistorySupport()) {
        $(window).bind("popstate", function(e) {
          $(window).trigger("pathchange");
        });
      }

      // Listen to the HTML5 "hashchange" event, if supported and desired
      if (my.options.useHashchange) {
        $(window).bind("hashchange", function(e) {
          $(window).trigger("pathchange");
        });

        // Hashchange support for older browsers (IE6/7)
        if (!my.detectHashchangeSupport()) {
          lastHash = window.location.hash;
          setInterval(function() {
            if (lastHash !== window.location.hash) {
              $(window).trigger("hashchange");
              lastHash = window.location.hash;
            }
          }, my.options.pollingInterval);
        }
      }

      // Intercept all relative links on the page, to avoid unneccesary page refreshes
      if (my.options.interceptLinks) {
        $("body").delegate("a[href^=/]", "click", function(e) {
          my.changeTo($(this).attr("href"));
          e.preventDefault();
        });
      }

      // Ensure all the href=# links on the page don't mess with things
      if (my.options.disableHashLinks) {
        $("body").delegate("a[href=#]", "click", function(e) {
          e.preventDefault();
        });
      }
    },

    // Call to manually navigate the app somewhere
    changeTo: function(path) {
      // If we're using History Management, just push an entry
      if (my.options.useHistory && my.detectHistorySupport()) {
        window.history.pushState(null, null, path);
        $(window).trigger("pathchange");
      } else {
        // Make sure there's a hash (going from foo.com#bar to foo.com would trigger a reload in Firefox, sadly)
        if (path.indexOf("#") < 0) {
          path += "#";
        }
        // Otherwise, navigate to the new URL.  Might reload the browser.  Might trigger a hashchange.
        window.location.href = window.location.protocol + "//" + window.location.hostname + (window.location.port ? ":" + window.location.port : "") + path;
      }
    },

    // Simple feature detection for History Management (borrowed from Modernizr)
    detectHistorySupport: function() {
      return !!(window.history && history.pushState);
    },

    // Simple feature detection for hashchange (adapted from Modernizr)
    detectHashchangeSupport: function() {
      var isSupported = "onhashchange" in window;
      if (!isSupported && window.setAttribute) {
        window.setAttribute("onhashchange", "return;");
        isSupported = typeof window.onhashchange === "function";
      }
      return isSupported;
    }
  };
}(jQuery));