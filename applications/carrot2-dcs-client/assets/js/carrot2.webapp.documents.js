(function($) {
  $.pluginhelper.make("documents", function(el, options) {
    var $listing = $(options.components.listing);
    var $summary = $(options.components.summary);

    // Compiled templates
    var documentTemplate = _.template('<div id="<%- id %>">' +
      '<h2><a href="<%- url %>"><%- title %></a></h2>' +
      '<p><%- snippet %></p>' +
      '<a href="<%- url %>"><%- url %></a>' +
      '<span><%= sources.join(\'</span><span>\') %></span>' +
    '</div>');

    // Export public methods
    this.populate = populate;
    return undefined;

    //
    // Private methods
    //
    function populate(data) {
      $listing.html(_.reduce(data.documents, function (html, doc) {
        html += documentTemplate(doc);
        return html;
      }, ""));
    }
  });
})(jQuery);
