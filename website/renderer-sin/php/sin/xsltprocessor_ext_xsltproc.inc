<?php

// CVS Info: $Id$

require_once("./sin/functions.inc");

/*
 * XSLT Processor class for XSLTPROC (invoked as an external shell process!).
 */
class XSLTProcessor
{
	var $params;

	function initialize()
	{
		$this->params = array();
	}
	
	function addParam($name, $value)
	{
		if (debugMode()) print "<p><b>addParam:</b> " . " Name: " . $name . " Value: " . $value;
		$this->params[$name] = $value;
	}

    function setBase($base)
    {
    }
	
	function addFileUrlParam($name,$value)
	{
	    $value = "file:///" . str_replace("\\","/",$value);
	    if (debugMode()) print("<p><b>addFileUrlParam:</b> " . " Name: " . $name . " Value: " . $value);
	    $this->params[$name] = $value;
	}

	function processFiles($xslFile, $xmlFile)
	{
		$processor = $GLOBALS["XSLTPROC_PATH"];
		$cmd 	   = $processor . " ";
		
		foreach($this->params as $key => $value)
		{
			$cmd = $cmd . " -stringparam " . $key . " \"$value\"";
		}
		
		$cmd = $cmd . ' "' . $xslFile . '" "' . $xmlFile . '"';

		if (debugMode())
		{
			print "<p><b>XSLTproc</b><ul><li>stylesheet: " . $xslFile . "<li> xml: " . $xmlFile . "</ul>";
			print "<p><b>XSLTproc command:</b> " . $cmd;
			print "<hr><h3>xsltproc's output below</h3><hr>";
			ob_start();
			passthru( $cmd );
			$outp = ob_get_contents();
			ob_end_clean();
			
			print "<TEXTAREA cols=120 rows=25>";
			print htmlspecialchars( $outp );
			print "</textarea>";
		}
		else
		{
			if ($processor == '')
			{
				print "<html><body>External xsltproc processor class error: variable XSLTPROC_PATH not specified</body></html>";
			}
			else
				passthru( $cmd );
		}
	}

	function destroy()
	{
	}
}
?>
