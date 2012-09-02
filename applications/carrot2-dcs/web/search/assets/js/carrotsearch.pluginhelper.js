(function($) {
  /**
   * A simple static helper that facilitates creating jQuery plugins.
   * The helper takes care of attaching plugin instances to DOM elements,
   * using the stored instances on subsequent calls and dispatching method
   * calls based on the method parameter.
   */
  $.pluginhelper = {
    // Creates a prototype implementation of a plugin and registers it under 
    // the provided name. Optionally, the provided init method will be called.
    make: function(name, init) {
      var clazz = makeClass();

      var plugin = function() {
        var args = Array.prototype.slice.call(arguments);
        var jq = this;
        var result;
        var each = this.each(function() {
          // Fetch instance from DOM storage or initialize a new one
          // if the element hasn't been assigned a plugin instance yet
          var inst = $(this).data(name);
          if (inst) {
            // Dispatch method calls
            var rv = dispatch(inst, args);
            if (typeof rv !== "undefined" && jq.size() == 1) {
              result = rv;
            }
          } else {
            // Create and store instance 
            inst = clazz.call(this);
            $(this).data(name, inst);
            
            // Initialize
            if (typeof init == "function") {
              args.unshift(this);
              init.apply(inst, args);
            }
          }
        });
        
        return typeof result == "undefined" ? each : result;
      };
      $.fn[name] = plugin;
      return clazz;
      
      // Inspired by makeClass by John Resig
      function makeClass() {
        return function(args) {
          if (!(this instanceof arguments.callee)) {
            return new arguments.callee(arguments);
          }
        };
      }
      
      function dispatch(instance, args) {
        var method = args[0];
        if (typeof method == "string") {
          if (typeof instance[method] == "function") {
            return instance[method].apply(instance, 
                Array.prototype.slice.call(args, typeof args[0] == "string" ? 1 : 0));
          } else {
            $.error("Method " +  method + " does not exist.");
          }
        } else {
          $.error("Method name " +  method + " must be a string.");
        }
      }
    }
  };
})(window.jQuery);
