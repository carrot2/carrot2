/**
 * Code by Peter-Paul Koch obtained from
 * http://www.quirksmode.org/js/display.html.
 */

window.onload = function () {
	var x = document.getElementsByTagName('div');
	for (var i=0;i<x.length;i++)
	{
		if (x[i].className == 'label')
			x[i].onclick = clickNav;
	}
	closeNav();
	if (top.setNav)
		setNav(top.setNav,'currentPage');
}

function closeNav()
{
	var x = document.getElementsByTagName('div');
	for (var i=0;i<x.length;i++)
	{
		if (x[i].className == 'content')
			x[i].style.display = 'none';
	}
}

function clickNav(e)
{
	if (!e) var e = window.event;
	if (e.target) var tg = e.target;
	else if (e.srcElement) var tg = e.srcElement;
	while (tg.nodeName != 'DIV') // Safari GRRRRRRRRRR
		tg = tg.parentNode;
	var nextSib = tg.nextSibling;
	while (nextSib.nodeType != 1)
		nextSib = nextSib.nextSibling;
	var nextSibStatus = (nextSib.style.display == 'none') ? 'block' : 'none';
	nextSib.style.display = nextSibStatus;
}

function setNav(page,newID)
{
	var test = page.indexOf('#')+1;
	if (test)
		page = page.substring(0,test-1);
	var x = document.getElementsByTagName('a');
	var i;
	for (i=0;i<x.length;i++)
	{
		if (x[i].href == page)
		{
			x[i].id = newID;
			break;
		}
	}
	if (i < x.length && newID == 'currentPage')
	{
		var parDiv = x[i];
		while (parDiv.parentNode.tagName == 'DIV')
		{
			parDiv = parDiv.parentNode;
			parDiv.style.display = 'block';
		}
	}
}

