<?php 

$ENGINE_SCRIPT      = substr(strrchr( $_SERVER["SCRIPT_NAME"], "/"), 1);

$BASE_REAL_DIR      = substr( $_SERVER["SCRIPT_FILENAME"], 0, -1-strlen($ENGINE_SCRIPT));
$PAGES_REAL_DIR     = $BASE_REAL_DIR . "/site";
$XSLT_REAL_DIR      = $BASE_REAL_DIR . "/xslt";

$BASE_URL           = substr( $_SERVER["SCRIPT_NAME"], 0, -1-strlen($ENGINE_SCRIPT));

$serverName = $_SERVER["SERVER_NAME"];
	if ($serverName == "" || $serverName == null)
		$serverName = $_SERVER["SERVER_ADDR"];

if ($serverName != null) {
    if ($_SERVER["SERVER_PORT"] != null) {
        $BASE_URL = "http://" . $serverName . ":" . $_SERVER["SERVER_PORT"] . $BASE_URL;
    }
    else {
        $BASE_URL = "http://" . $serverName . $BASE_URL;
    }
}

$REAL_PAGES_URL     = $BASE_URL . "/site";

// defaults for processors 

$XSLTPROC_PATH      = "xsltproc";
$SABCMD_PATH        = "sabcmd";


// CVS Info: $Id$
?>