function SearchTab(id, moreTab) {
  this.id = id;
  this.moreTab = moreTab;
};

SearchTab.prototype.getElement = function() {
  return document.getElementById(this.id);
};

