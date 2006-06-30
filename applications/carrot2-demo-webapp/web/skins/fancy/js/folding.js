function show(elementId)
{
  var element = document.getElementById(elementId);
  if (!element)
  {
    return;
  }

  if (element.style.display != "none")
  {
    element.style.display = "none";
  }
  else
  {
    element.style.display = "";
  }
}

function fold(elementId)
{
  show(elementId);
  hl(elementId);
};

function hl(elementId)
{
  clearHighlights();
  var textTd = document.getElementById('t' + elementId);
  if (textTd)
  {
    textTd.className = 'text hl';
  }
}

function foldRange(prefix, morePrefix, start, end)
{
  for(var i = start; i <= end; i++)
  {
    show(prefix + i);
  }
  show(prefix + morePrefix + (start-1));
  show(prefix + morePrefix + (end));
};

function clearHighlights()
{
  var nodes = document.getElementsByTagName('td');
  for (var i = 0; i < nodes.length; i++)
  {
    if (nodes[i].className && nodes[i].className.indexOf('text hl') >= 0)
    {
      nodes[i].className = 'text';
    }
  }
}

function contains(array, value)
{
  for (var i = 0; i < array.length; i++)
  {
    if (array[i]+"" == value+"")
    {
      return true;
    }
  }

  return false;
}

function sel(refids)
{
  var documentElements = parent.documents.document.getElementsByTagName('table');
  for (var i = 0; i < documentElements.length; i++)
  {
    if (documentElements[i].className == 'd')
    {
      if (contains(refids, documentElements[i].id))
      {
        documentElements[i].style.display = "block";
      }
      else
      {
        documentElements[i].style.display = "none";
      }
    }
  }
}

function showAll()
{
  var documentElements = parent.documents.document.getElementsByTagName('table');
  for (var i = 0; i < documentElements.length; i++)
  {
    if (documentElements[i].className == 'd')
    {
      documentElements[i].style.display = "block";
    }
  }
}