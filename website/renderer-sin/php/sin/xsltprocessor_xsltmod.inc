<?php

require_once("./sin/functions.inc");

/*
 * XSLT Processor class utilizing built-in Sablotron (mod_xslt).
 */
class XSLTProcessor
{
	var $params;
    var $arguments;
    var $processor;

	function initialize()
	{
		$this->params = array();
        $this->arguments = array();
        $this->processor = xslt_create();
	}
	
	function addParam($name, $value)
	{
		if (debugMode()) print "<p><b>addParam:</b> " . " Name: " . $name . " Value: " . $value;
		$this->params[$name] = $value;
	}
	
	function addFileUrlParam($name,$value)
	{
	    $value = "file://" . str_replace("\\","/",$value);
	    if (debugMode()) print("<p><b>addFileUrlParam:</b> " . " Name: " . $name . " Value: " . $value);
	    $this->params[$name] = $value;
	}
    
    function setBase($base)
    {
        if (debugMode()) print("<p><b>XSL Base:</b> " . $base);
        xslt_set_base($this->processor, $base);
    }

    function process($xsl, $xml)
    {
        $this->arguments['/_xml'] = $xml;
        $this->arguments['/_xsl'] = $xsl;

        xslt_set_error_handler($this->processor, array(&$this, "xslt_trap_error") );
        $result = xslt_process($this->processor, 'arg:/_xml', 'arg:/_xsl', NULL, $this->arguments, $this->params);
        return $result;
    }
    
    function xslt_trap_error($parser, $errorno, $level, $fields) {
      $M = "Error Number $errorno, Level $level, Fields;\n";
      if(is_array($fields)) {
        while(list($key, $value) = each($fields)) {
          $M .= " $key => $value\n";
        }
      } else {
        $M .= "$fields";
      }
      echo $M;
    }

	function processFiles($xslFile, $xmlFile)
	{
        print $this->process( $this->readall($xslFile), $this->readall($xmlFile) );
	}

	function destroy()
	{
        xslt_free($this->processor);
	}
    
    function readall($url)
    {
        $handle = fopen( $url, "rb" );
        $contents = "";
        do {
            $data = fread($handle, 8192);
            if (strlen($data) == 0) {
                break;
            }
            $contents .= $data;
        } while(true);
        fclose ($handle);
    
        return $contents;
    }    
}
?>
