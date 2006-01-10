<?php

//
// Tests system capabilities for use with SIN.
//

header("Content-type: text/html;");

echo("<html><body><pre>");
echo("Testing system capabilities for use with SIN: \n\n");

$safe_mode = ini_get("safe_mode");
echo("PHP in safe mode (expecting NO): " . ($safe_mode == 1 ? "yes" : "no") . "\n");

$short_open_tag = ini_get("short_open_tag");
echo("Short open tag (expecting NO): " . ($short_open_tag == 1 ? "yes" : "no") . "\n");

echo "\n";

ob_start();
passthru("sabcmd", $str);
$outp = ob_get_contents();
ob_end_clean();
echo("SABCMD processor available: " . ($outp != "" ? "yes" : "no") . "\n");

ob_start();
passthru("xsltproc", $str);
$outp = ob_get_contents();
ob_end_clean();
echo("XSLTPROC processor available: " . ($outp != "" ? "yes" : "no") . "\n");

echo("</pre>");

phpinfo();
?>