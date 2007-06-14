/**
 * A class for managing changes to the visual rendering of search tabs.
 */
function SearchTabView(tabModel) {
  this.tabModel = tabModel;
};


SearchTabView.prototype.applyTabInsert = function(tab) {
  var rowElement = document.getElementById("main-tabs");

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
  var diff = (tds.length - 1) / 2 - this.tabModel.tabs.length;
  if (diff > 0) {
    // Remove td
    for (var i = 0; i < (tds.length - 1) / 2; i++) {
      var tabEl = tds[i * 2 + 1].getElementsByTagName("div")[0];
      if (this.tabModel.getTabIndex(tabEl.id) < 0) {
        rowElement.removeChild(tds[i * 2]);
        rowElement.removeChild(tds[i * 2]);
        break;
      }
    }
  }

  // Move tabs' content around
  for (var i = 0; i < this.tabModel.tabs.length; i++) {
    var tab = this.tabModel.tabs[i].getElement();
    tab.parentNode.removeChild(tab);
    var td = Dom.elementAt(rowElement, i * 2 + 1);
    td.appendChild(tab);
  }

  // Highlight the active tab
  for (var i = 0; i < this.tabModel.tabs.length; i++) {
    var tab = this.tabModel.tabs[i];
    var tabElement = tab.getElement();
    var first = this.tabModel.isTabFirst(tab.id);
    var last = this.tabModel.isTabLast(tab.id);
    var prevActive = i != 0 ? this.tabModel.tabs[i - 1] == this.tabModel.activeTab : this.tabModel.tabs[i] == this.tabModel.activeTab;
    
    if (tab == this.tabModel.activeTab) {
      tabElement.parentNode.className = "tab-active-body";
      if (first) {
        Dom.previousSiblingElement(tabElement.parentNode).className = "tab-active-lead-in";
      }
      else {
        Dom.previousSiblingElement(tabElement.parentNode).className = "tab-passive-active-link";
      }

      if (last) {
        Dom.nextSiblingElement(tabElement.parentNode).className = "tab-active-lead-out";
      }
      else {
        Dom.nextSiblingElement(tabElement.parentNode).className = "tab-active-passive-link";
      }
    }
    else {
      tabElement.parentNode.className = "tab-passive-body";
      if (first) {
        Dom.previousSiblingElement(tabElement.parentNode).className = "tab-passive-lead-in";
      }
      else {
        if (prevActive) {
          Dom.previousSiblingElement(tabElement.parentNode).className = "tab-active-passive-link";
        }
        else {
          Dom.previousSiblingElement(tabElement.parentNode).className = "tab-passive-passive-link";
        }
      }

      if (last) {
        Dom.nextSiblingElement(tabElement.parentNode).className = "tab-passive-lead-out";
      }
      else {
        Dom.nextSiblingElement(tabElement.parentNode).className = "tab-passive-passive-link";
      }
    }
  }
}



