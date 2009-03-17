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
  
  /**
   * Returns true of client is using a modern browser. A modern browser is anything
   * other than IE6-.
   */
  jQuery.modern = function(string) {
    return !jQuery.browser.msie || parseInt(jQuery.browser.version) > 6; 
  }
  
  /**
   * Converts all selected checkboxes into ones submitted as a hidden field.
   */
  jQuery.fn.submitCheckboxAsHidden = function() {
    return this.filter(":checkbox").each(function() {
      var name = this.name;
      var $hidden = $("<input name='" + name + "' type='hidden' value='' />");
      $(this).removeAttr("name").after($hidden).change(updateHidden).click(updateHidden).change();
      
      function updateHidden() {
        $hidden.val(this.checked ? 'true' : 'false');
      }
    }).end();
  }
})(jQuery);
