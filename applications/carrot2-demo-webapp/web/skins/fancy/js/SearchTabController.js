/**
 * A class for managing a simple set of tabs.
 */ 
function SimpleTabController(tabContainerId, tabModel, tabView, 
                             beforeTabDeactivate, afterTabActivate) {
  this.tabContainerId = tabContainerId;
  this.tabModel = tabModel;
  this.tabView = tabView;
  this.beforeTabDeactivate = beforeTabDeactivate;
  this.afterTabActivate = afterTabActivate;
};

SimpleTabController.prototype.init = function() {
  // Register listeners
  Dom.applyToElements(document.getElementById(this.tabContainerId),
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
};

SimpleTabController.prototype.tabClickListener = function (e, tab) {
  var el = YAHOO.util.Event.getTarget(e);
  var td = Dom.findParentElement(el, function(element) {
    return element.tagName.toLowerCase() == 'td';
  });
  var tabId = Dom.elementAt(td, 0).id;
  
  if (tabId == this.controller.tabModel.activeTab.id)
  {
    return;
  }
  
  if (this.controller.beforeTabDeactivate) {
    this.controller.beforeTabDeactivate(this.controller.tabModel.activeTab);
  }
  this.controller.tabModel.activateTabById(tabId);
  this.controller.tabView.applyModelChanges();
  if (this.controller.afterTabActivate) {
    this.controller.afterTabActivate(this.controller.tabModel.activeTab);
  }
}

/**
 * A class for managing search tabs, with tab adding/removing/reordering
 * support.
 */ 
function SearchTabController(tabModel, tabView, 
                             beforeTabDeactivate, afterTabActivate, 
                             afterTabRemove, afterTabAdd,
                             afterTabSwap) {
  this.simpleController = new SimpleTabController("main-tabs", tabModel,
                                                  tabView, beforeTabDeactivate, 
                                                  afterTabActivate);
  this.tabModel = tabModel;
  this.tabView = tabView;
  this.afterTabRemove = afterTabRemove;
  this.afterTabAdd = afterTabAdd;
  this.afterTabSwap = afterTabSwap;
};

SearchTabController.prototype.init = function() {
  this.simpleController.init();
  
  // Create DD tabs
  for (var i in this.tabModel.tabs) {
    new DDTab(this.tabModel.tabs[i].id, "tabs", null, this, this.tabModel.tabs[i].moreTab);
  }

  // Register combo box listeners
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
                                   "click", this.controller.simpleController.tabClickListener, 
                                   { controller: this.controller.simpleController },
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

/**
 * A class for managing visual changes resulting from activation/deactivation
 * of tabs.
 */
function SimpleTabView(tabContainerId, stylePrefix, tabModel) {
  this.tabModel = tabModel;
  this.tabContainerId = tabContainerId;
  this.stylePrefix = stylePrefix;
};

SimpleTabView.prototype.applyModelChanges = function() {
  var rowElement = document.getElementById(this.tabContainer);

  // Highlight the active tab
  for (var i = 0; i < this.tabModel.tabs.length; i++) {
    var tab = this.tabModel.tabs[i];
    var tabElement = tab.getElement();
    var first = this.tabModel.isTabFirst(tab.id);
    var last = this.tabModel.isTabLast(tab.id);
    var prevActive = i != 0 ? this.tabModel.tabs[i - 1] == this.tabModel.activeTab : this.tabModel.tabs[i] == this.tabModel.activeTab;

    if (tab == this.tabModel.activeTab) {
      tabElement.parentNode.className = this.stylePrefix + "tab-active-body";
      if (first) {
        Dom.previousSiblingElement(tabElement.parentNode).className = this.stylePrefix + "tab-active-lead-in";
      }
      else {
        Dom.previousSiblingElement(tabElement.parentNode).className = this.stylePrefix + "tab-passive-active-link";
      }

      if (last) {
        Dom.nextSiblingElement(tabElement.parentNode).className = this.stylePrefix + "tab-active-lead-out";
      }
      else {
        Dom.nextSiblingElement(tabElement.parentNode).className = this.stylePrefix + "tab-active-passive-link";
      }
    }
    else {
      tabElement.parentNode.className = this.stylePrefix + "tab-passive-body";
      if (first) {
        Dom.previousSiblingElement(tabElement.parentNode).className = this.stylePrefix + "tab-passive-lead-in";
      }
      else {
        if (prevActive) {
          Dom.previousSiblingElement(tabElement.parentNode).className = this.stylePrefix + "tab-active-passive-link";
        }
        else {
          Dom.previousSiblingElement(tabElement.parentNode).className = this.stylePrefix + "tab-passive-passive-link";
        }
      }

      if (last) {
        Dom.nextSiblingElement(tabElement.parentNode).className = this.stylePrefix + "tab-passive-lead-out";
      }
      else {
        Dom.nextSiblingElement(tabElement.parentNode).className = this.stylePrefix + "tab-passive-passive-link";
      }
    }
  }
}

/**
 * A class for managing changes to the visual rendering of search tabs,
 * including adding, removing and reordering of tabs.
 */
function SearchTabView(tabModel) {
  this.simpleView = new SimpleTabView("main-tabs", "", tabModel);
  this.tabModel = tabModel;
};


SearchTabView.prototype.applyTabInsert = function(tab) {
  var rowElement = document.getElementById(this.simpleView.tabContainerId);

  var tds = rowElement.getElementsByTagName("td");

  var bodyEl = tds[tds.length - 4].cloneNode(false);
  var linkEl = tds[tds.length - 3].cloneNode(true);

  var tabEl = document.getElementById(tab.id+"-h").cloneNode(true);
  tabEl.id = tab.id;

  bodyEl.appendChild(tabEl);
  rowElement.insertBefore(linkEl, tds[tds.length - 2]);
  rowElement.insertBefore(bodyEl, tds[tds.length - 3]);

  return bodyEl;
}

SearchTabView.prototype.applyModelChanges = function() {
  var rowElement = document.getElementById("main-tabs");

  // Make sure the number of td elements is correct
  var tds = rowElement.getElementsByTagName("td");

  // Add missing tabs
  for (var i = 0; i < tabModel.tabs.length; i++) {
    if (!document.getElementById(tabModel.tabs[i].id)) {
      var td = tabView.applyTabInsert(tabModel.tabs[i]);
      YAHOO.util.Event.addListener(td,
                                   "click", stc.simpleController.tabClickListener,
                                   { controller: stc },
                                   true);
      new DDTab(tabModel.tabs[i].id, "tabs", null, stc);
    }
  }

  // Remove tds
  for (var i = (tds.length - 1) / 2 - 2; i >= 0 ; i--) {
    var tabEl = tds[i * 2 + 1].getElementsByTagName("div")[0];
    if (this.tabModel.getTabIndex(tabEl.id) < 0) {
      rowElement.removeChild(tds[i * 2]);
      rowElement.removeChild(tds[i * 2]);
      Dom.hide(tabEl.id + "-desc");
    }
  }

  // Move tabs' content around
  for (var i = 0; i < this.tabModel.tabs.length; i++) {
    var tab = this.tabModel.tabs[i].getElement();
    tab.parentNode.removeChild(tab);
    var td = Dom.elementAt(rowElement, i * 2 + 1);
    td.appendChild(tab);
  }

  // Highlight selected tab
  this.simpleView.applyModelChanges();
}

/**
 * A class for managing the logical model of search tabs.
 */
function TabModel(tabIds, selectedTabId) 
{
  this.tabs = new Array();
  this.allTabs = new Array();
  this.activeTab = null;

  var split = tabIds.split(":");
  var i = 0;
  for (i = 0; i < split.length; i++) {
    if (split[i].length > 0) {
      this.tabs[i] = new Tab(split[i]);
      if (split[i] == selectedTabId) {
        this.activeTab = this.tabs[i];
      }
    }
  }
};

TabModel.prototype.swap = function(dragId, dropId) {
  var dragIndex = this.getTabIndex(dragId);
  var tab = this.tabs.splice(dragIndex, 1)[0];
  var dropIndex = this.getTabIndex(dropId);
  this.tabs.splice(dropIndex, 0, tab);
};

TabModel.prototype.getTab = function(id) {
  var index = this.getTabIndex(id);
  if (index >= 0) {
    return this.tabs[index];
  }
  else {
    return null;
  }
}

TabModel.prototype.getTabIndex = function(id) {
  for (var i = 0; i < this.tabs.length; i++) {
    if (this.tabs[i].id == id) {
      return i;
    }
  }
  return -1;
}

TabModel.prototype.isTabLast = function(id) {
  return this.getTabIndex(id) == this.tabs.length - 1;
}

TabModel.prototype.isTabFirst = function(id) {
  return this.getTabIndex(id) == 0;
}

TabModel.prototype.activateTab = function(tab) {
  this.activeTab = tab;
};

TabModel.prototype.activateTabById = function(tabId) {
  this.activeTab = this.getTab(tabId);
};

TabModel.prototype.insertTab = function(tab) {
  if (tabModel.getTabIndex(tab.id) < 0) {
    tabModel.tabs[tabModel.tabs.length] = tabModel.tabs[tabModel.tabs.length - 1];
    tabModel.tabs[tabModel.tabs.length - 2] = tab;
    return true;
  }
  else {
    return false;
  }
};

TabModel.prototype.removeTab = function(tab) {
  var index = tabModel.getTabIndex(tab.id);
  if (index >= 0) {
    tabModel.tabs.splice(index, 1);
    return true;
  }
  else {
    return false;
  }
};

/**
 * This class represents a single search tab.
 */
function Tab(id, moreTab) {
  this.id = id;
  this.moreTab = moreTab;
};

Tab.prototype.getElement = function() {
  return document.getElementById(this.id);
};

/**
 * @class a YAHOO.util.DDProxy implementation that handles search tab
 * dragging and dropping.
 *
 * @extends YAHOO.util.DDProxy
 * @constructor
 * @param {String} id the id of the linked element
 * @param {String} sGroup the group of related DragDrop items
 */
DDTab = function(id, sGroup, config, tabController, onlyTarget) {
  this.swapInit(id, sGroup, config, onlyTarget);
  this.tabController = tabController;
};

YAHOO.lang.extend(DDTab, YAHOO.util.DDProxy);

DDTab.prototype.swapInit = function(id, sGroup, config, onlyTarget) {
  if (!id) { return; }

  if (onlyTarget) {
    this.initTarget(id, sGroup, config);
  }
  else {
    this.init(id, sGroup, config);
    this.initFrame();
    var el = this.getDragEl();
    el.className = "drag-el";
    YAHOO.util.Dom.setStyle(el, "position", "absolute");
    YAHOO.util.Dom.setStyle(el, "borderWidth", "1px");
    YAHOO.util.Dom.setStyle(el, "borderStyle", "dotted");
    YAHOO.util.Dom.setStyle(el, "opacity", 0.75);
  }

  this.setPadding(28, 0, 13, 0);
  this.dragOverClass = "drag-over";
  this.els = [];
  this.remove = true;
};

DDTab.prototype.startDrag = function(x, y) {
  var Dom = YAHOO.util.Dom;

  var dragEl = this.getDragEl();
  var clickEl = this.getEl();

  dragEl.innerHTML = clickEl.innerHTML;
  dragEl.className = "drag";

  Dom.setStyle(clickEl, "opacity", 0.3);
};

DDTab.prototype.onDragDrop = function(e, id) {
  var dd = YAHOO.util.DDM.getDDById(id);
  this.swap(this.getEl(), dd.getEl());
  this.resetConstraints();
  dd.resetConstraints();
  this.remove = false;
};

DDTab.prototype.swap = function(el1, el2) {
  this.tabController.swap(el1.id, el2.id);
};

DDTab.prototype.onDragEnter = function(e, id) {
  // store a ref so we can restore the style later
  this.els[id] = true;

  // set the mouseover style
  var el = YAHOO.util.DDM.getElement(id);
  YAHOO.util.Dom.addClass(el, this.dragOverClass);
};

DDTab.prototype.onDragOut = function(e, id) {
  var el = YAHOO.util.DDM.getElement(id);
  YAHOO.util.Dom.removeClass(el, this.dragOverClass);
  this.remove = true;
};

DDTab.prototype.endDrag = function(e) {
  YAHOO.util.Dom.setStyle(this.getEl(), "opacity", 1);
  this.resetStyles();
  this.remove = true;
};

DDTab.prototype.resetStyles = function() {
  // restore all element styles
  for (var i in this.els) {
    var el = YAHOO.util.DDM.getElement(i);
    if (el) { 
      YAHOO.util.Dom.removeClass(el, this.dragOverClass);
    }
  }
};

DDTab.prototype.onDrag = function(e) { };

DDTab.prototype.onDragOver = function(e) { };

