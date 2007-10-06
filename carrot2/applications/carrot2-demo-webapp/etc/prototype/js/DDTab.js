/**
 * @class a YAHOO.util.DDProxy implementation that handles search tab
 * dragging and dropping.
 *
 * @extends YAHOO.util.DDProxy
 * @constructor
 * @param {String} id the id of the linked element
 * @param {String} sGroup the group of related DragDrop items
 */
DDTab = function(id, sGroup, config, tabController) {
  this.swapInit(id, sGroup, config);
  this.tabController = tabController;
};

YAHOO.lang.extend(DDTab, YAHOO.util.DDProxy);

DDTab.prototype.swapInit = function(id, sGroup, config) {
  if (!id) { return; }

  this.init(id, sGroup, config);
  this.initFrame();

  var el = this.getDragEl()
  YAHOO.util.Dom.setStyle(el, "borderWidth", "1px");
  YAHOO.util.Dom.setStyle(el, "borderStyle", "dotted");
  YAHOO.util.Dom.setStyle(el, "opacity", 0.75);

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
