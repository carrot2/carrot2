<?php

	// Strips the last component of a filename before the first slash.
	function dirOnly( $page )
	{
		$i = strlen( $page )-1;
		while ( $i>=0 && $page[$i]!='/') $i--;
		
		if ($i<=0)
			return '';
		
		return substr( $page, 0, $i );
	}

	// Check if the page does obey to the security rules.
	function isPageSecure( $page, $processor )
	{
		if (eregi( $page, "\.\." ))
		{
			// $processor->addParam("errorMessage", "Illegal path attempt");
			return FALSE;
		}
		else
		if ($page[0] != "/" || ( endsWith($page, ".xml")==FALSE ))
		{
			// $processor->addParam("errorMessage", "Input file has no .xml extension");
			return FALSE;
		}
		else
		if (count($_GET)>5)
		{
			// $processor->addParam("errorMessage", "Too many GET parameters");
			$_GET = array();
			return FALSE;
		}
		else
			return TRUE;
	}

	// check whether we're in the debug mode	
	function debugMode()
	{
		return $_GET["debugmode"]=="yes" || $_GET["debugmode"]=="true";
	}
	
	
	// checks if one string ends with another (sufix)
	function endsWith( $string, $ending )
	{
		return strcmp( substr($string, -strlen($ending)), $ending )==0;
	}

	// checks if one string starts with another (prefix)
	function startsWith( $string, $beginning )
	{
		return $beginning == substr($string, 0, strlen($beginning));
	}
	
	// chops trailing slash
	function chopTrailingSlash( $str )
	{
		if (endsWith($str,"/"))
		{
		   return substr( $str, 0, strlen($str) - 1);
		}
		else
		   return $str;
	}
	

// CVS Info: $Id$

?>