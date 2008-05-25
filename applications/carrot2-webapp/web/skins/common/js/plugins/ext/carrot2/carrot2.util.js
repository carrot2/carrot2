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
   * Unescapes the '&amp;' entities in a string to '&' characters.
   */
  jQuery.unescape = function(string) {
    return string.replace(/\&amp;/g, "&");
  }
})(jQuery);
