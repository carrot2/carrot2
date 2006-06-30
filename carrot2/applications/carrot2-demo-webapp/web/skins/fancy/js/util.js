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

function resize()
{
  var height = document.body.offsetHeight;
  var width = document.body.offsetWidth;
  var mainHeight = height - 180;
  var mainWidth = width - 20;
  if (mainHeight <= 0 || mainWidth <= 0)
  {
    return;
  }
  
  var main = document.getElementById('results-main-content');
  main.style.height = mainHeight + "px";
  main.style.width = mainWidth + "px";
};

function switchTab(tabElemName, processId)
{
  var main = document.getElementById(tabElemName);
  hide("tab-" + main.value);
  main.value = processId;
  show("tab-" + processId);
  focus('search-field');
};

function showAdvanced()
{
  show('adv-opts');
  hide('process-instr');
  hide('adv-switch');
  show('sim-switch');
  document.getElementById('opts').value = 's';
};

function hideAdvanced()
{
  hide('adv-opts');
  show('process-instr');
  show('adv-switch');
  hide('sim-switch');
  document.getElementById('opts').value = 'h';
};

function show(elementId)
{
  setElementVisible(elementId, true);
}

function hide(elementId)
{
  setElementVisible(elementId, false);
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
