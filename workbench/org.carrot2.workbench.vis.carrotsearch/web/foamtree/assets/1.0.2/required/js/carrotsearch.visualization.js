/*!
 * Generic JavaScript API for Carrot Search visualizations.
 *
 * Copyright 2002-2010, Carrot Search s.c.
 * 
 * This file is licensed under Apache License 2.0:
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * @constructor
 */
function CarrotSearchVisualization() {
  // Version of Flash required
  var FLASH_REQUIRED_VERSION = "10.0.0";
  
  // Visualization-specific configuration of this API:
  // 
  // visualizationName:      human-readable name of the visualization, e.g. to use in logs
  // settings:               runtime option values provided by the user
  // defaults:               default option values
  // reembedinglessSettings: settings whose change does not requires reembedding
  // transformers:           option value transformers
  // jsToSwfPropertyName:    JavaScript API to Flash API property name translation
  // aliases:                mappings between old and new settings names for backward compatibility
  // getOptionMethods:       method handling specific readable options, e.g. dataXml
  var config;
  
  // User option values provided for the specific visualization, merged with defaults
  var options;
  
  // A reference to this available though a closure to private functions
  var thisVisualization = this;

  // DOM element containing this visualization
  var element = null;

  
  // Public methods
  this.get = get;
  this.set = set;
  this.init = init;
  this.embed = embed;
  this.print = print;
  this.saveAsImage = saveAsImage;
  
  // Converters to use by specific visualizations
  this.negate = negate;
  this.toFlashGroupColorModel = toFlashGroupColorModel;
  this.toHexString = toHexString;
  this.toStringArray = toStringArray;
  this.colorArrayToString = colorArrayToString;
  this.toCallbackWithTransformedResult = toCallbackWithTransformedResult;
  this.toCallbackOnThisVisualization = toCallbackOnThisVisualization; 

  // Utility methods
  this.has = has;
  
  /**
   * Initializes the generic JavaScript API with visualization-specific configuration.
   */
  function init(visualizationConfig) 
  {
    config = visualizationConfig;
    
    extend(getOptionMethods, config.getOptionMethods);
    
    // Merge user options with defaults
    options = extend({}, config.defaults, config.settings);
  }
  
  /**
   * Embeds the Flash visualization according to the internal options.
   */
  function embed(initial) {
    if (initial) {
      if (canLog()) {
        window.console.group(config.visualizationName + ": initial embedding");
      }
      validateOptions(options, config.reembedinglessSettings, null);
    }
    
    // Map JS to SWF parameter names
    var flashvars = prepareFlashVars(initial);
    for (var name in flashvars) {
      flashvars[name] = escape(flashvars[name]);
    }
    
    var params = {
      // Enable transparency with respect to other DHTML elements.
      wmode: "transparent"
    };

    if (canLog()) {
      window.console.info("flashvars: ", flashvars);
    }

    window.swfobject.embedSWF(options.visualizationSwfLocation, 
      options.id, 
      options.width,
      options.height,
      FLASH_REQUIRED_VERSION, 
      options.flashPlayerInstallerSwfLocation,
      flashvars, params, {}, 
      function(e) {
        if (e.success) {
          element = e.ref;
          if (canLog()) {
            window.console.info("element: ", element);
          }
        }
        if (options.onLoad) {
          options.onLoad.call(thisVisualization, e.success);
        }
        if (canLog()) {
          window.console.groupEnd();
        }
      }
    );
    
    if (initial && canLog()) {
      window.console.groupEnd();
    }
  };

  /**
   * Converts JS API options to Flash API options. 
   */
  function prepareFlashVars(skipNulls) {
    var flashvars = { };
    for (var name in options) {
      var key = config.jsToSwfPropertyName[name];
      if (key) {
        var value = config.transformers[name] ? config.transformers[name].call(this, options[name], name) : options[name];
        if (!skipNulls || value !== null) {
          flashvars[key] = value;
        }
      }
    }
    
    if (options.groupSizeWeighting == "score") {
      flashvars.zeroScoreReplacement = -100000;
    }
    
    return flashvars;
  }
  
  var setOptionMethods = {
      "documentPanelPosition": documentsPanel,
      "selection": select
  };
  
  /**
   * Sets options. Re-embeds Flash if needed.
   */
  function set() {
    var opts;
    if (arguments.length == 0) {
      return;
    } else if (arguments.length == 1) {
      opts = arguments[0];
    } else if (arguments.length == 2) {
      opts = { };
      opts[arguments[0]] = arguments[1];
    }

    var logging = options.logging;
    
    // Merge new options with the current ones
    extend(options, opts);
    if (has(opts, "dataUrl")) {
      delete options.dataXml;
    } else if (has(opts, "dataXml")) {
      delete options.dataUrl;
    }

    // Open debug group
    if (canLog()) {
      window.console.group(config.visualizationName + ": setting options");
    }
    
    // Apply deprecation aliases
    validateOptions(opts, config.reembedinglessSettings, null);
    
    // Determine if a reload is necessary
    var needsReload = false;
    for (var name in opts) {
      if (!config.reembedinglessSettings[name]) {
        needsReload = true;
        break;
      }
    }

    // Log debug information
    if (canLog()) {
      window.console.info("reload required: ", needsReload);
      window.console.info("options: ", opts);
    }
    
    if (needsReload) {
      if (element && has(element, "reload")) {
        var vars = prepareFlashVars(false);
        if (canLog()) {
          window.console.info("flashvars: ", vars);
        }
        if ((has(opts, "dataUrl") || has(opts, "dataXml")) && 
            (options.dataXml == null || options.dataUrl == null)) {
          element.loadDataFromXML(null);
        }
        element.reload(vars);
      } else {
        embed(false);
      }
    } else {
      if (has(opts, "dataUrl") || has(opts, "dataXml")) {
        if (options.dataUrl) {
          element.loadDataFromURL(options.dataUrl);
        } else if (options.dataXml) {
          element.loadDataFromXML(options.dataXml);
        } else {
          element.loadDataFromXML(null);
        }
      }

      for (var option in setOptionMethods) {
        if (has(opts, option)) {
          setOptionMethods[option].call(thisVisualization, opts);
        }
      }
    }
    
    if (canLog()) {
      window.console.groupEnd();
    }
  };

  var getOptionMethods = {
      "group": groupInfo,
      "document": documentInfo,
      "dataXml": modelAsXML
  };
  
  /**
   * Returns internal option values.
   */
  function get() {
    if (arguments.length == 0) {
      return options;
    } else {
      var name = arguments[0];
      
      if (name == null) {
        // Undocumented in public API, useful for UI code, no cloning at the moment.
        return config.defaults; 
      }
      
      var opts = { };
      opts[name] = true;
      validateOptions(opts, getOptionMethods, function() {
        window.console.group(config.visualizationName + ": getting options");
      });
      
      // If option is invalid, it will be removed
      if (has(opts, name)) {
        if (getOptionMethods[name]) {
          return getOptionMethods[name].call(element, arguments[1]);
        } else if (typeof config.defaults[arguments[0]] !== 'undefined') {
          return options[arguments[0]];
        }
      }
    }
  };
  
  /**
   * Changes group selection.
   */
  function select(opts) {
    var groups = null;
    var selection = opts.selection;
    // Delete selection from options so that it doesn't mislead the caller:
    // options.selection contains the last request's selection change and not
    // the current/aggregated selection.
    delete options.selection; 
    if (selection !== null) {
      var g = isObject(selection) ? selection.groups : selection;
      groups = isArray(g) ? g : [ g ];
    }
    
    if (groups !== null) {
      for (var group in groups) {
        element.selectGroupById(groups[group], 
            def(selection, "selected", true));
      }
    } else {
      element.clearSelection();
    }
  };
  
  /**
   * Checks the provided options object for deprecated option names,
   * corrects old names to the new ones if necessary and emits deprecation warnings.
   * Removes unknown options and emits unknown option warnings.
   */
  function validateOptions(opts, extraOpts, openGroupCallback) {
    if (config.aliases) {
      var groupOpen = false;
      var allAliases = config.aliases;
      for (var removedInVersion in allAliases) {
        var aliases = allAliases[removedInVersion];
        for (var oldSetting in aliases) {
          var newSetting = aliases[oldSetting];
          if (has(opts, oldSetting) && !has(opts, newSetting)) {
            opts[newSetting] = opts[oldSetting];
            if (canLog()) {
              if (!groupOpen) {
                if (openGroupCallback) {
                  openGroupCallback.apply(this);
                }
                window.console.group(config.visualizationName + ": deprecated option names used");
                groupOpen = true;
              }
              window.console.warn("Use \"" + newSetting + "\" instead of \"" + oldSetting + "\". The old option name will stop working in version " + removedInVersion + ".");
            }
            delete opts[oldSetting];
          }
        }
      }
      if (canLog()) {
        if (groupOpen) {
          window.console.groupEnd();
        }
        if (openGroupCallback) {
          window.console.groupEnd();
        }
      }
    }
    
    for (var o in opts) {
      if (!has(config.defaults, o) && !has(extraOpts, o)) {
        if (canLog()) {
          window.console.warn("Ignoring unknown option: ", o);
        }
        delete opts[o];
      }
    }
  }
  
  /**
   * A simpler version of jQuery.extend, loosely based on jQuery original implementation.
   */
  function extend(var_args) {
    var target = arguments[0];
    var length = arguments.length;

    for (var i = 1; i < length; i++) {
      var values = arguments[i];
      if (values != null) {
        for (var name in values) {
          var value = values[name];
          target[name] = value;
        }
      }
    }
    return target;
  }

  function def(o, prop, def) {
    return has(o, prop) ? o[prop] : def;
  }
  
  function has(o, prop) {
    return typeof o[prop] != "undefined";
  }
  
  //
  // Option transformers between the JS an Flash API conventions
  //
  var jsToFlashGroupColorModel = {
      "gradient": "hsv",
      "palette": "palette",
      "custom": "custom"
  };
  function toFlashGroupColorModel(val) {
    if (typeof val == "string" && jsToFlashGroupColorModel[val]) {
      return jsToFlashGroupColorModel[val];
    } else {
      return "hsv";
    }
  }
  
  function toHexString(val) {
    if (isArray(val)) {
      var converted = [];
      for (var v in val) {
        converted.push(toHexString(val[v]));
      }
      return converted;
    }
    if (typeof val == "string") {
      if (/^0x.*/.test(val)) {
        return val;
      }
      if (/^#.*/.test(val)) {
        return "0x" + val.substring(1);
      }
      return "0x" + val;
    } 
    else {
      return val;
    }
  }
  
  function negate(val) {
    if (typeof val == "boolean") {
      return !val;
    } else {
      return val;
    }
  }

  function colorArrayToString(val) {
    if (isArray(val)) {
      return toHexString(val).join(", ");
    } else {
      return val;
    }
  }
  
  /**
   * Transforms a comma-separated string into an array of strings.
   * If there is no comma on input, the result will be on-element array
   * containing the input string.
   */
  function toStringArray(string) {
    if (string == null || typeof string !== "string") {
      return string;
    }
    return string.split(/,/);
  }
  
  /**
   * Wraps the provided function in such a way that this points to the visualization instance
   * in the returned function.
   */
  function toCallbackOnThisVisualization(val, prop) {
    return asGlobalCallback(function () {
      return val.apply(thisVisualization, arguments);
    }, prop);
  }

  /**
   * Creates a transformer that runs the results of a callback through
   * the specified transformer.
   */
  function toCallbackWithTransformedResult(transformer) {
    return function(callback, property) {
      return asGlobalCallback(function() {
        var result = callback.apply(thisVisualization, arguments);
        return transformer.call(thisVisualization, result, property);
      }, property);
    };
  }

  /**
   * Converts a private function to a global one by creating a reference to it
   * in the window, scoped by the id of this visualization instance. Returns the 
   * name of the global function.
   * 
   * The reason to make callbacks global is to be able to pass them by name to 
   * Flash using params string. In practice, Flash seems to accept function code as well,
   * but then the param string may get long.
   */
  function asGlobalCallback(f, name) {
    var globalName = "__carrotsearchvisualization__" + options.id.replace(/[^a-zA-Z0-9]/, "") + "__" + name;
    window[globalName] = f;
    return globalName;
  }
  
  function isArray(a) {
    return Object.prototype.toString.call(a) == "[object Array]";
  }

  function isObject(a) {
    return Object.prototype.toString.call(a) == "[object Object]";
  }
  
  function canLog() {
    return options.logging && typeof window.console != "undefined" && has(window.console, "group") && has(window.console, "groupEnd");
  }
  
  //
  // Flash method proxy functions
  //
  function groupInfo(group) { 
    var result = element.groupInfo(group);
    result[1] = toStringArray(result[1]); // convert comma-delimited ids to an array
    return result; 
  }
  function documentInfo(document) { return element.documentInfo(document); }
  function modelAsXML() { return element.modelAsXML(); }
  function documentsPanel() { return element.documentsPanel(options.documentPanelPosition); }
  function print() { return element.print(); }
  
  function saveAsImage(format) { 
    if (!options.echoServiceUrl && canLog()) {
      window.console.warn("To use saveAsImage() method, set the echoServiceUrl option first.");
      return;
    }
    return element.saveAsImage(format); 
  }
};
