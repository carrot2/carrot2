(function($) {
  /**
   * A utility method for delegating event handling.
   */
  jQuery.delegate = function(rules) {
    return function(e) {
      var target = $(e.target);
      for (var selector in rules) {
        if (target.is(selector)) {
          return rules[selector].apply(this, jQuery.makeArray(arguments));
        }
      }
    }
  };

  /** 
   * Pushes all elements from fromArray to toArray.
   */
  jQuery.pushAll = function(toArray, fromArray) {
    $.each(fromArray, function(i, val) {
      toArray.push(val);
    });
  };

  /**
   * Sorts an integer array and removes duplicates.
   */
  jQuery.sortUnique = function(array) {
    if (array.length == 0) {
      return array;
    }

    array.sort(function(a, b) { return a - b; } );

    var prev = array[0];
    var i = 1;
    while (i < array.length) {
      if (array[i] == prev) {
        array.splice(i, 1);
      }
      else {
        prev = array[i];
        i++;
      }
    }

    return array;
  }

  /**
   * Copies to the selected elements the requested CSS properties from the
   * first element matching the provided selector.
   */
  jQuery.fn.cssFrom = function(selector, properties) {
    var $cssSource = $(selector);
    return this.each(function () {
      var $this = $(this);
      $.each(properties, function(i, val) {
        $this.css(val, $cssSource.css(val));
      });
    });
  }

  /**
   * Sets width and height of the selected elements to the respective inner dimensions
   * of their immediate parents.
   */
  jQuery.fn.sizeToParent = function() {
    return this.each(function() {
      $this = $(this);

      $this.width($this.parent().innerWidth());
      $this.height($this.parent().innerHeight());
    });
  }
  
  /**
   * Unescapes the '&amp;' entities in a string to '&' characters.
   */
  jQuery.unescape = function(string) {
    return string.replace(/\&amp;/g, "&");
  }
})(jQuery);
