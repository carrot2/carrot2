/** On error, try to log the message to SWT. */
window.onerror = function(error) {
  var container = document.getElementById("viscontainer");
  if (container) {
    var hasSwtLog = (typeof swt_log === 'undefined');
    var msg = "Error: " + JSON.stringify(arguments, null, "  ") + ", container size: " +
      container.clientWidth + "x" + container.clientHeight + 
      (hasSwtLog ? " swt_log defined." : " swt_log undefined.");

      container.innerText = msg;
      if (hasSwtLog) {
        swt_log(msg);
      }
  }
  throw error;
};
