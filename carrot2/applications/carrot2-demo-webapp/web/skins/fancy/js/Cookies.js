/**
 * Adds a few useful Cookie handling methods taken from:
 * http://www.quirksmode.org/js/cookies.html
 */
Cookies = function () {
};

Cookies.createCookieForever = function(name, value) {
  Cookies.createCookie(name, value, 365 * 100);
} 

Cookies.createCookie = function(name, value, days) {
	if (days) {
		var date = new Date();
		date.setTime(date.getTime()+(days*24*60*60*1000));
		var expires = "; expires="+date.toGMTString();
	}
	else var expires = "";
	document.cookie = name+"="+value+expires+"; path=/";
}

Cookies.readCookie = function(name) {
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for(var i=0;i < ca.length;i++) {
		var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	}
	return null;
}

Cookies.eraseCookie = function(name) {
	Cookies.createCookie(name, "", -1);
}