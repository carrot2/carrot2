<?php

// CVS Info: $Id$

require_once("./sin/functions.inc");

/*
 * XSLT Processor class for Sablotron (invoked as an external shell process!).
 * 
 * A global variable SABCMD_PATH must point to Sablotron executable.
 *
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
	    $value = "file://" . str_replace("\\","/",$value);
	    if (debugMode()) print("<p><b>addFileUrlParam:</b> " . " Name: " . $name . " Value: " . $value);
	    $this->params[$name] = $value;
	}

	function processFiles($xslFile, $xmlFile)
	{
	    $xslFile   = "file://" . $xslFile;
	    $xmlFile   = "file://" . $xmlFile;
	    
		$sablotron = $GLOBALS["SABCMD_PATH"];
		$cmd 	   = $sablotron . " " . '"' . $xslFile . '" "' . $xmlFile . '"';
		
		foreach($this->params as $key => $value)
		{
			$cmd = $cmd . " ";
			 if (PHP_OS == "WINNT") 
				$cmd = $cmd . '$';
			 else
				$cmd = $cmd . '\\$';
			 
			$cmd = $cmd . $key . "=\"$value\"";
		}

		if (debugMode())
		{
			print "<p><b>Sablotron</b><ul><li>stylesheet: " . $xslFile . "<li> xml: " . $xmlFile . "</ul>";
			print "<p><b>Sablotron command:</b> " . $cmd;
			print "<hr><h3>Sablotron's output below</h3><hr>";
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
			if ($sablotron == '')
			{
				print "<html><body>External Sablotron processor class error: Sablotron's executable variable SABCMD_PATH not specified</body></html>";
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
