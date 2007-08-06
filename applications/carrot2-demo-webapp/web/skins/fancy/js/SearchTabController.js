/**
 * A class for managing search tabs.
 */ 
function SearchTabController(tabModel, tabView, 
                             beforeTabDeactivate, afterTabActivate, 
                             afterTabRemove, afterTabAdd,
                             afterTabSwap) {
  this.tabModel = tabModel;
  this.tabView = tabView;
  this.beforeTabDeactivate = beforeTabDeactivate;
  this.afterTabActivate = afterTabActivate;
  this.afterTabRemove = afterTabRemove;
  this.afterTabAdd = afterTabAdd;
  this.afterTabSwap = afterTabSwap;
};

SearchTabController.prototype.init = function() {
  // Create DD tabs
  for (var i in this.tabModel.tabs) {
    new DDTab(this.tabModel.tabs[i].id, "tabs", null, this, this.tabModel.tabs[i].moreTab);
  }

  // Register listeners
  Dom.applyToElements(document.getElementById("main-tabs"),
  function(element) { 
    return element.tagName.toLowerCase() == 'td' &&
           element.className && element.className.indexOf("body") > 0;
  },
  function(element, controller) {
    YAHOO.util.Event.addListener(element, 
                                 "click", controller.tabClickListener, 
                                 { controller: controller},
                                 true);
  }, this);
  
  for (var i = 0; i < this.tabModel.allTabs.length; i++) {
    YAHOO.util.Event.addListener(this.tabModel.allTabs[i].id + "-cb", 
                                 "click", this.tabChangeListener, 
                                 { controller: this, tab: this.tabModel.allTabs[i] },
                                 true);
  }

  for (var i = 0; i < this.tabModel.allTabs.length; i++) {
    YAHOO.util.Event.addListener(this.tabModel.allTabs[i].id + "-h-link", 
                                 "click", this.tabChangeListener, 
                                 { controller: this, tab: this.tabModel.allTabs[i] },
                                 true);
  }
};

SearchTabController.prototype.tabClickListener = function (e, tab) {
  var el = YAHOO.util.Event.getTarget(e);
  var td = Dom.findParentElement(el, function(element) {
    return element.tagName.toLowerCase() == 'td';
  });
  var tabId = Dom.elementAt(td, 0).id;
  
  if (this.controller.beforeTabDeactivate) {
    this.controller.beforeTabDeactivate(this.controller.tabModel.activeTab);
  }
  this.controller.tabModel.activateTabById(tabId);
  this.controller.tabView.applyModelChanges();
  if (this.controller.afterTabActivate) {
    this.controller.afterTabActivate(this.controller.tabModel.activeTab);
  }
}

SearchTabController.prototype.tabChangeListener = function(e, tab) {
  var el = YAHOO.util.Event.getTarget(e);
  var checkBox;
  if (el.tagName.toLowerCase() == "input") {
    checkBox = el;
  }
  else {
    checkBox = document.getElementById(this.tab.id + "-cb");
    checkBox.checked = !checkBox.checked;
  }
  
  if (checkBox.checked) {
    if (this.controller.tabModel.insertTab(this.tab)) {
      var td = this.controller.tabView.applyTabInsert(this.tab);
      this.controller.tabView.applyModelChanges();
      YAHOO.util.Event.addListener(td, 
                                   "click", this.controller.tabClickListener, 
                                   { controller: this.controller },
                                   true);
      new DDTab(this.tab.id, "tabs", null, this.controller);
      if (this.controller.afterTabAdd) {
        this.controller.afterTabAdd(this.tab);
      }
    }
  }
  else {
    if (this.controller.tabModel.removeTab(this.tab)) {
      this.controller.tabView.applyModelChanges();
      if (this.controller.afterTabRemove) {
        this.controller.afterTabRemove(this.tab);
      }
    }
  }
}

SearchTabController.prototype.swap = function(dragId, dropId) {
  this.tabModel.swap(dragId, dropId);
  this.tabView.applyModelChanges();
  if (this.afterTabSwap) {
    this.afterTabSwap(dragId, dropId);
  }
}
