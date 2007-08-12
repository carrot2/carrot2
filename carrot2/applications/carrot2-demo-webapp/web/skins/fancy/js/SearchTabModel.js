/**
 * A class for managing the logical model of search tabs.
 */
function SearchTabModel() {
  this.tabs = new Array();
  this.allTabs = new Array();
  this.activeTab = null;
};

SearchTabModel.prototype.swap = function(dragId, dropId) {
  var dragIndex = this.getTabIndex(dragId);
  var tab = this.tabs.splice(dragIndex, 1)[0];
  var dropIndex = this.getTabIndex(dropId);
  this.tabs.splice(dropIndex, 0, tab);
};

SearchTabModel.prototype.getTab = function(id) {
  var index = this.getTabIndex(id);
  if (index >= 0) {
    return this.tabs[index];
  }
  else {
    return null;
  }
}

SearchTabModel.prototype.getTabIndex = function(id) {
  for (var i = 0; i < this.tabs.length; i++) {
    if (this.tabs[i].id == id) {
      return i;
    }
  }
  return -1;
}

SearchTabModel.prototype.isTabLast = function(id) {
  return this.getTabIndex(id) == this.tabs.length - 1;
}

SearchTabModel.prototype.isTabFirst = function(id) {
  return this.getTabIndex(id) == 0;
}

SearchTabModel.prototype.activateTab = function(tab) {
  this.activeTab = tab;
};

SearchTabModel.prototype.activateTabById = function(tabId) {
  this.activeTab = this.getTab(tabId);
};

SearchTabModel.prototype.insertTab = function(tab) {
  if (tabModel.getTabIndex(tab.id) < 0) {
    tabModel.tabs[tabModel.tabs.length] = tabModel.tabs[tabModel.tabs.length - 1];
    tabModel.tabs[tabModel.tabs.length - 2] = tab;
    return true;
  }
  else {
    return false;
  }
};

SearchTabModel.prototype.removeTab = function(tab) {
  var index = tabModel.getTabIndex(tab.id);
  if (index >= 0) {
    tabModel.tabs.splice(index, 1);
    return true;
  }
  else {
    return false;
  }
};


