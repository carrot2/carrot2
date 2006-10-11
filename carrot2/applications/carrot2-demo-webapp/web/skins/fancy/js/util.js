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
}

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
}

/* Disable/enable progress bar */
function setProgress(id, enabled) {
	if (enabled) {
		show(id);
	} else {
		hide(id);
	}
}

/** Parsing GET parameters */
function getParam(url, name)
{
  var regexS = "[\\?&]"+name+"=([^&#]*)";
  var regex = new RegExp( regexS );
  var tmpURL = url;
  var results = regex.exec( tmpURL );
  if( results == null )
    return "";
  else
    return Url.decode(results[1]);
}

/**
*
*  URL encode / decode
*  http://www.webtoolkit.info/
*
**/

var Url = {

    // public method for url encoding
    encode : function (string) {
        return escape(this._utf8_encode(string));
    },

    // public method for url decoding
    decode : function (string) {
        return this._utf8_decode(unescape(string));
    },

    // private method for UTF-8 encoding
    _utf8_encode : function (string) {
        string = string.replace(/\r\n/g,"\n");
        var utftext = "";

        for (var n = 0; n < string.length; n++) {

            var c = string.charCodeAt(n);

            if (c < 128) {
                utftext += String.fromCharCode(c);
            }
            else if((c > 127) && (c < 2048)) {
                utftext += String.fromCharCode((c >> 6) | 192);
                utftext += String.fromCharCode((c & 63) | 128);
            }
            else {
                utftext += String.fromCharCode((c >> 12) | 224);
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                utftext += String.fromCharCode((c & 63) | 128);
            }

        }

        return utftext;
    },

    // private method for UTF-8 decoding
    _utf8_decode : function (utftext) {
        var string = "";
        var i = 0;
        var c = c1 = c2 = 0;

        while ( i < utftext.length ) {

            c = utftext.charCodeAt(i);

            if (c < 128) {
                string += String.fromCharCode(c);
                i++;
            }
            else if((c > 191) && (c < 224)) {
                c2 = utftext.charCodeAt(i+1);
                string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                i += 2;
            }
            else {
                c2 = utftext.charCodeAt(i+1);
                c3 = utftext.charCodeAt(i+2);
                string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                i += 3;
            }

        }

        return string;
    }

}

