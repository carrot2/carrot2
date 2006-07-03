function initPage()
{
    focus("search-field");
}

function focus(elementId)
{ 
  var searchField = document.getElementById(elementId);
  if (searchField != null) {
      searchField.focus();
      searchField.select();
  }
};

function switchTab(tabElemName, processId)
{
  var main = document.getElementById(tabElemName);
  hideByIdFragment("-tab", "table");
  hideByIdFragment("-desc", "div");
  main.value = processId;
  show(processId + "-tab");
  show(processId + "-desc");
  focus('search-field');
};

function showAdvanced()
{
  show('adv-opts');
  hide('process-instr');
  hide('adv-switch');
  show('sim-switch');
  document.getElementById('opts').value = 's';
  focus('search-field');
};

function hideAdvanced()
{
  hide('adv-opts');
  show('process-instr');
  show('adv-switch');
  hide('sim-switch');
  document.getElementById('opts').value = 'h';
  focus('search-field');
};

function show(elementId)
{
  setElementVisible(elementId, true);
}

function hide(elementId)
{
  setElementVisible(elementId, false);
}

function hideByIdFragment(idFragment, tagName)
{
  var elems = document.getElementsByTagName(tagName);
  for (var i = 0; i < elems.length; i++)
  {
    if (elems[i].id.indexOf(idFragment) >= 0)
    {
      hide(elems[i].id);
    }
  }
}

function setElementVisible(elementId, visible)
{
  var element = document.getElementById(elementId);
  if (element)
  {
    if (visible)
    {
      element.style.display = '';
    }
    else
    {
      element.style.display = 'none';
    }
  }
};

function insertNewProcess(processId)
{
  for(var i = 0; i < otherProcesses.length; i++)
  {
    for(var j = 0; j < 3; j++)
    {
      setElementVisible(otherProcesses[i] + "-" + j, processId == otherProcesses[i]);
    }

    setElementVisible(otherProcesses[i] + "-d", processId != otherProcesses[i]);
  }

  processes[1] = processId;
  switchProcess(1);
  focus('search-field');
};
