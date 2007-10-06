/**
 * A class for managing search tabs.
 */ 
function SearchTabController(tabModel, tabView) {
  this.tabModel = tabModel;
  this.tabView = tabView;
};

SearchTabController.prototype.init = function() {
  // Create DD tabs
  for (var i in this.tabModel.tabs) {
    new DDTab(this.tabModel.tabs[i].id, "tabs", null, this);
  }

  // Register listeners
  for (var i = 0; i < this.tabModel.tabs.length; i++) {
    YAHOO.util.Event.addListener(this.tabModel.tabs[i].getElement(), 
                                 "click", this.tabClickListener, 
                                 { controller: this, tab: this.tabModel.tabs[i] },
                                 true);
  }

  for (var i = 0; i < this.tabModel.allTabs.length; i++) {
    YAHOO.util.Event.addListener(this.tabModel.allTabs[i].id + "-cb", 
                                 "change", this.tabChangeListener, 
                                 { controller: this, tab: this.tabModel.allTabs[i] },
                                 true);
  }
};

SearchTabController.prototype.tabClickListener = function (e, tab) {
  this.controller.tabModel.activateTab(this.tab);
  this.controller.tabView.applyModelChanges();
}

SearchTabController.prototype.tabChangeListener = function(e, tab) {
  var checkBox = YAHOO.util.Event.getTarget(e);
  if (checkBox.checked) {
    if (this.controller.tabModel.insertTab(this.tab)) {
      var tabEl = this.controller.tabView.applyTabInsert(this.tab);
      this.controller.tabView.applyModelChanges();
      YAHOO.util.Event.addListener(tabEl, 
                                   "click", this.tabClickListener, 
                                   { controller: this.controller, tab: this.tab },
                                   true);
	  new DDTab(this.tab.id, "tabs", null, this.controller);
    }
  }
  else {
    if (this.controller.tabModel.removeTab(this.tab)) {
      this.controller.tabView.applyModelChanges();
    }
  }
}

SearchTabController.prototype.swap = function(dragId, dropId) {
  this.tabModel.swap(dragId, dropId);
  this.tabView.applyModelChanges();
}
