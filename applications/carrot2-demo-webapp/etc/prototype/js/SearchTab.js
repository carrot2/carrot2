function SearchTab(id, active) {
  this.id = id;
};

SearchTab.prototype.getElement = function() {
  return document.getElementById(this.id);
};

