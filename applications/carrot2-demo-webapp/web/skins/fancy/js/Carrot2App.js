var tabView = new SearchTabView(tabModel);

var stc = new SearchTabController(tabModel, tabView,
                                  beforeSearchTabDeactivate,
                                  afterSearchTabActivate,
                                  afterSearchTabRemoveAdd,
                                  afterSearchTabRemoveAdd,
                                  afterSearchTabSwap);

var COOKIE_DD_TIP = "dd-tip";
var COOKIE_TAB_ORDER = "tab-order";
var COOKIE_ACTIVE_TAB = "active-tab";

function c2AppInit()
{
  // Drag&drop init
  stc.init();

  // Various links
  YAHOO.util.Event.addListener("hide-advanced", "click", hideAdvancedClickListener);
  YAHOO.util.Event.addListener("show-advanced", "click", showAdvancedClickListener);

  // Show drag & drop tip if necessary
  if (Cookies.readCookie(COOKIE_DD_TIP)) {
    Dom.hide("dd-tip");
  }

  // Update search field based on the query (browsers normally don't
  // change text field values when pressing "Back", which is good for
  // long forms, but not for our case.
  YAHOO.util.Dom.get('search-field').value = query;
  YAHOO.util.Dom.get('search-field').focus();
  YAHOO.util.Dom.get('search-field').select();
}

/** Showing/hinding advanced option */
function showAdvancedClickListener()
{
  Dom.show('adv-opts');
  Dom.hide('process-instr');
  Dom.hide('adv-switch');
  Dom.show('sim-switch');
  YAHOO.util.Dom.get('search-field').focus();
  YAHOO.util.Dom.get('search-field').select();
  YAHOO.util.Dom.get('opts').value = 's';

  return false;
}

function hideAdvancedClickListener()
{
  Dom.hide('adv-opts');
  Dom.show('process-instr');
  Dom.show('adv-switch');
  Dom.hide('sim-switch');
  YAHOO.util.Dom.get('search-field').focus();
  YAHOO.util.Dom.get('search-field').select();
  YAHOO.util.Dom.get('opts').value = 'h';

  return false;
}

/** Drag&drop hooks */
function beforeSearchTabDeactivate(tab)
{
  Dom.hide(tab.id + "-desc");

  if ("-more" == tab.id) {
    Dom.show("search-area");
    Dom.hide("more-row");
    Dom.show("res-row");
  }
}

function afterSearchTabActivate(tab)
{
  Dom.show(tab.id + "-desc");

  if ("-more" == tab.id) {
    Dom.hide("search-area");
    Dom.show("more-row");
    Dom.hide("res-row");
  }
  else
  {
    Cookies.createCookieForever(COOKIE_ACTIVE_TAB, tab.id);
    YAHOO.util.Dom.get("search-field").focus();
    YAHOO.util.Dom.get("search-field").select();
  }

  YAHOO.util.Dom.get('tabElem').value = tab.id;
}

function afterSearchTabRemoveAdd(tab)
{
  var checkBoxes = document.getElementById("-more-desc").getElementsByTagName("input");
  if (stc.tabModel.tabs.length == 2) {
    // Disable active check box
    for (var i = 0; i < checkBoxes.length; i++) {
      if (checkBoxes[i].checked) {
        checkBoxes[i].disabled = true;
      }
    }
  } else {
    // Enable disabled check box
    for (var i = 0; i < checkBoxes.length; i++) {
      checkBoxes[i].disabled = false;
    }
  }
  storeTabOrder();
}

function afterSearchTabSwap(dragId, dropId)
{
  Cookies.createCookieForever(COOKIE_DD_TIP, "y");

  // Hide drag&drop tip
  Dom.hide("dd-tip");
  storeTabOrder();
}

function storeTabOrder()
{
  // Remember selected tabs and their order
  var selectedTabs = tabModel.tabs[0].id;
  for (var i = 1; i < tabModel.tabs.length - 1; i++)
  {
    selectedTabs += ":" + tabModel.tabs[i].id;
  }
  Cookies.createCookieForever(COOKIE_TAB_ORDER, selectedTabs);
}

function afterClustersLoaded()
{
  Dom.hide("clusters-progress");
}

function afterDocsLoaded()
{
  Dom.hide("docs-progress");
}
