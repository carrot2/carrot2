<?php
// by Edd Dumbill (C) 1999-2002
// <edd@usefulinc.com>
// Id: xmlrpcs.inc,v 1.40 2005/11/21 10:56:20 ggiunta Exp

// Copyright (c) 1999,2000,2002 Edd Dumbill.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
//
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//
//    * Redistributions in binary form must reproduce the above
//      copyright notice, this list of conditions and the following
//      disclaimer in the documentation and/or other materials provided
//      with the distribution.
//
//    * Neither the name of the "XML-RPC for PHP" nor the names of its
//      contributors may be used to endorse or promote products derived
//      from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
// FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
// REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
// INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
// STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
// OF THE POSSIBILITY OF SUCH DAMAGE.

	// XML RPC Server class
	// requires: xmlrpc.inc

	// listMethods: either a string, or nothing
	$_xmlrpcs_listMethods_sig=array(array($GLOBALS['xmlrpcArray'], $GLOBALS['xmlrpcString']), array($GLOBALS['xmlrpcArray']));
	$_xmlrpcs_listMethods_doc='This method lists all the methods that the XML-RPC server knows how to dispatch';
	function _xmlrpcs_listMethods($server, $m)
	{
		$outAr=array();
		foreach($server->dmap as $key => $val)
		{
			$outAr[]=&new xmlrpcval($key, 'string');
		}
		if($server->allow_system_funcs)
		{
			foreach($GLOBALS['_xmlrpcs_dmap'] as $key => $val)
			{
				$outAr[]=&new xmlrpcval($key, 'string');
			}
		}
		$v=&new xmlrpcval($outAr, 'array');
		return new xmlrpcresp($v);
	}

	$_xmlrpcs_methodSignature_sig=array(array($GLOBALS['xmlrpcArray'], $GLOBALS['xmlrpcString']));
	$_xmlrpcs_methodSignature_doc='Returns an array of known signatures (an array of arrays) for the method name passed. If no signatures are known, returns a none-array (test for type != array to detect missing signature)';
	function _xmlrpcs_methodSignature($server, $m)
	{
		$methName=$m->getParam(0);
		$methName=$methName->scalarval();
		if(ereg("^system\.", $methName))
		{
			$dmap=$GLOBALS['_xmlrpcs_dmap']; $sysCall=1;
		}
		else
		{
			$dmap=$server->dmap; $sysCall=0;
		}
		//	print "<!-- ${methName} -->\n";
		if(isset($dmap[$methName]))
		{
			if(isset($dmap[$methName]['signature']))
			{
				$sigs=array();
				foreach($dmap[$methName]['signature'] as $inSig)
				{
					$cursig=array();
					foreach($inSig as $sig)
					{
						$cursig[]=&new xmlrpcval($sig, 'string');
					}
					$sigs[]=&new xmlrpcval($cursig, 'array');
				}
				$r=&new xmlrpcresp(new xmlrpcval($sigs, 'array'));
			}
			else
			{
				// NB: according to the official docs, shouldn't we be returning an empty
				// array here???
				$r=&new xmlrpcresp(new xmlrpcval('undef', 'string'));
			}
		}
		else
		{
			$r=&new xmlrpcresp(0,$GLOBALS['xmlrpcerr']['introspect_unknown'], $GLOBALS['xmlrpcstr']['introspect_unknown']);
		}
		return $r;
	}

	$_xmlrpcs_methodHelp_sig=array(array($GLOBALS['xmlrpcString'], $GLOBALS['xmlrpcString']));
	$_xmlrpcs_methodHelp_doc='Returns help text if defined for the method passed, otherwise returns an empty string';
	function _xmlrpcs_methodHelp($server, $m)
	{
		$methName=$m->getParam(0);
		$methName=$methName->scalarval();
		if(ereg("^system\.", $methName))
		{
			$dmap=$GLOBALS['_xmlrpcs_dmap']; $sysCall=1;
		}
		else
		{
			$dmap=$server->dmap; $sysCall=0;
		}
		// print "<!-- ${methName} -->\n";
		if(isset($dmap[$methName]))
		{
			if(isset($dmap[$methName]['docstring']))
			{
				$r=&new xmlrpcresp(new xmlrpcval($dmap[$methName]['docstring']), 'string');
			}
			else
			{
				$r=&new xmlrpcresp(new xmlrpcval('', 'string'));
			}
		}
		else
		{
			$r=&new xmlrpcresp(0, $GLOBALS['xmlrpcerr']['introspect_unknown'], $GLOBALS['xmlrpcstr']['introspect_unknown']);
		}
		return $r;
	}

	$_xmlrpcs_multicall_sig = array(array($GLOBALS['xmlrpcArray'], $GLOBALS['xmlrpcArray']));
	$_xmlrpcs_multicall_doc = 'Boxcar multiple RPC calls in one request. See http://www.xmlrpc.com/discuss/msgReader$1208 for details';

	function _xmlrpcs_multicall_error($err)
	{
		if(is_string($err))
		{
			$str  = $GLOBALS['xmlrpcstr']["multicall_${err}"];
			$code = $GLOBALS['xmlrpcerr']["multicall_${err}"];
		}
		else
		{
			$code = $err->faultCode();
			$str = $err->faultString();
		}
		$struct = array();
		$struct['faultCode'] =& new xmlrpcval($code, 'int');
		$struct['faultString'] =& new xmlrpcval($str, 'string');
		return new xmlrpcval($struct, 'struct');
	}

	function _xmlrpcs_multicall_do_call($server, $call)
	{
		if($call->kindOf() != 'struct')
		{
			return _xmlrpcs_multicall_error('notstruct');
		}
		$methName = @$call->structmem('methodName');
		if(!$methName)
		{
			return _xmlrpcs_multicall_error('nomethod');
		}
		if($methName->kindOf() != 'scalar' || $methName->scalartyp() != 'string')
		{
			return _xmlrpcs_multicall_error('notstring');
		}
		if($methName->scalarval() == 'system.multicall')
		{
			return _xmlrpcs_multicall_error('recursion');
		}

		$params = @$call->structmem('params');
		if(!$params)
		{
			return _xmlrpcs_multicall_error('noparams');
		}
		if($params->kindOf() != 'array')
		{
			return _xmlrpcs_multicall_error('notarray');
		}
		$numParams = $params->arraysize();

		$msg =& new xmlrpcmsg($methName->scalarval());
		for($i = 0; $i < $numParams; $i++)
		{
			if(!$msg->addParam($params->arraymem($i)))
			{
				$i++;
				return _xmlrpcs_multicall_error(new xmlrpcresp(0,
					$GLOBALS['xmlrpcerr']['incorrect_params'],
					$GLOBALS['xmlrpcstr']['incorrect_params'] . ": probable xml error in param " . $i));
			}
		}

		$result = $server->execute($msg);

		if($result->faultCode() != 0)
		{
			return _xmlrpcs_multicall_error($result);    // Method returned fault.
		}

		return new xmlrpcval(array($result->value()), 'array');
	}

	function _xmlrpcs_multicall($server, $m)
	{
		$calls = $m->getParam(0);
		$numCalls = $calls->arraysize();
		$result = array();

		for($i = 0; $i < $numCalls; $i++)
		{
			$call = $calls->arraymem($i);
			$result[$i] = _xmlrpcs_multicall_do_call($server, $call);
		}

		return new xmlrpcresp(new xmlrpcval($result, 'array'));
	}

	$GLOBALS['_xmlrpcs_dmap']=array(
		'system.listMethods' => array(
			'function' => '_xmlrpcs_listMethods',
			'signature' => $_xmlrpcs_listMethods_sig,
			'docstring' => $_xmlrpcs_listMethods_doc),
		'system.methodHelp' => array(
			'function' => '_xmlrpcs_methodHelp',
			'signature' => $_xmlrpcs_methodHelp_sig,
			'docstring' => $_xmlrpcs_methodHelp_doc),
		'system.methodSignature' => array(
			'function' => '_xmlrpcs_methodSignature',
			'signature' => $_xmlrpcs_methodSignature_sig,
			'docstring' => $_xmlrpcs_methodSignature_doc),
		'system.multicall' => array(
			'function' => '_xmlrpcs_multicall',
			'signature' => $_xmlrpcs_multicall_sig,
			'docstring' => $_xmlrpcs_multicall_doc
		)
	);

	$GLOBALS['_xmlrpcs_occurred_errors'] = '';
	$GLOBALS['_xmlrpcs_prev_ehandler'] = '';
	/**
	* Error handler used to track errors that occur during server-side execution of PHP code.
	* This allows to report back to the client whether an internal error has occurred or not
	* using an xmlrpc response object, instead of letting the client deal with the html junk
	* that a PHP execution error on the server generally entails.
	*
	* NB: in fact a user defined error handler can only handle WARNING, NOTICE and USER_* errors.
	*
	*/
	function _xmlrpcs_errorHandler($errcode, $errstring, $filename=null, $lineno=null, $context=null)
	{
		//if($errcode != E_NOTICE && $errcode != E_WARNING && $errcode != E_USER_NOTICE && $errcode != E_USER_WARNING)
		if($errcode != 2048) // do not use E_STRICT by name, since on PHP 4 it will not be defined
		{
			$GLOBALS['_xmlrpcs_occurred_errors'] = $GLOBALS['_xmlrpcs_occurred_errors'] . $errstring . "\n";
		}
		// Try to avoid as much as possible disruption to the previous error handling
		// mechanism in place
		if($GLOBALS['_xmlrpcs_prev_ehandler'] == '')
		{
			// The previous error handler was the default: all we should do is log error
			// to teh default error log (if level high enough)
			if(ini_get('log_errors') && (intval(ini_get('error_reporting')) & $errcode))
			{
				error_log($errstring);
			}
		}
		else
		{
			// Pass control on to previous error handler, trying to avoid loops...
			if($GLOBALS['_xmlrpcs_prev_ehandler'] != '_xmlrpcs_errorHandler')
			{
				// NB: this code will NOT work on php < 4.0.2: only 2 params were used for error handlers
				if(is_array($GLOBALS['_xmlrpcs_prev_ehandler']))
				{
					$GLOBALS['_xmlrpcs_prev_ehandler'][0]->$GLOBALS['_xmlrpcs_prev_ehandler'][1]($errcode, $errstring, $filename, $lineno, $context);
				}
				else
				{
					$GLOBALS['_xmlrpcs_prev_ehandler']($errcode, $errstring, $filename, $lineno, $context);
				}
			}
		}
	}

	$GLOBALS['_xmlrpc_debuginfo']='';
	function xmlrpc_debugmsg($m)
	{
		$GLOBALS['_xmlrpc_debuginfo']=$GLOBALS['_xmlrpc_debuginfo'] . $m . "\n";
	}

	class xmlrpc_server
	{
		var $dmap=array();
		/// controls wether the server is going to echo debugging messages back to the client as comments in response body. valid values: 0,1,2,3
		var $debug = 1;
		/**
		* When set to true, it will enable HTTP compression of the response, in case
		* the client has declared its support for compression in the request.
		*/
		var $compress_response = false;
		/**
		* List of http compression methods accepted by the server for requests.
		* NB: PHP supports deflate, gzip compressions out of the box if compiled w. zlib
		*/
		var $accepted_compression = array();
		/// shall we serve calls to system.* methods?
		var $allow_system_funcs = true;
		/// list of charset encodings natively accepted for requests
		var $accepted_charset_encodings = array();
		var $xml_header = "<?xml version=\"1.0\" ?>\n";

		function xmlrpc_server($dispMap='', $serviceNow=1)
		{
			// if ZLIB is enabled, let the server by default accept compressed requests,
			// and compress responses sent to clients that support them
			if(function_exists('gzinflate'))
			{
				$this->accepted_compression = array('gzip', 'deflate');
				$this->compress_response = true;
			}

			// by default the xml parser can support these 3 charset encodings
			$this->accepted_charset_encodings = array('UTF-8', 'ISO-8859-1', 'US-ASCII');

			// dispMap is a dispatch array of methods
			// mapped to function names and signatures
			// if a method
			// doesn't appear in the map then an unknown
			// method error is generated
			/* milosch - changed to make passing dispMap optional.
			 * instead, you can use the class add_to_map() function
			 * to add functions manually (borrowed from SOAPX4)
			 */
			if($dispMap)
			{
				$this->dmap = $dispMap;
				if($serviceNow)
				{
					$this->service();
				}
			}
		}

		/**
		* @param integer $in debug lvl: determines info added to xmlrpc responses (as xml comments)
		* 0 = no debug info,
		* 1 = msgs set from user with debugmsg(),
		* 2 = add complete xmlrpc request (headers and body),
		* 3 = (to be implemented) add also all processing warnings happened during method processing
		* (NB: this involves setting a custom error handler, and might interfere
		* with the standard processing of the php function exposed as method. In
		* particular, triggering an USER_ERROR level error will not halt script
		* execution anymore, but just end up logged in the xmlrpc response)
		*/
		function setDebug($in)
		{
			$this->debug=$in;
		}

		function serializeDebug()
		{
			if($GLOBALS['_xmlrpc_debuginfo']!='')
			{
				// Tough encoding problem: which charset should we assume for debug info?
				// It might contain a copy of raw data received from client, ie with unknown encoding,
				// intermixed with php generated data and user generated data...
				return "<!-- DEBUG INFO:\n\n" . xmlrpc_encode_entitites($GLOBALS['_xmlrpc_debuginfo']) . "\n-->\n";
				// NB: a better solution MIGHT be to use CDATA, but we need to insert it
				// into return payload AFTER the beginning tag
				//return "<![CDATA[ DEBUG INFO:\n\n" . str_replace(']]>', ']_]_>', $GLOBALS['_xmlrpc_debuginfo']) . "\n]]>\n";
			}
			else
			{
				return '';
			}
		}

		function service()
		{
			// Play nice to PHP 4.0.x: superglobals were not yet invented...
			if(!isset($_SERVER))
			{
				$_SERVER = $GLOBALS['HTTP_SERVER_VARS'];
			}

			// Echo back what we received, before parsing it
			// NB: we should make sure we have no substring -- in the received data, and
			// that it does not end with a '-' char, or it would break
			// xml comment encapsulating it
			if($this->debug > 1)
			{
				xmlrpc_debugmsg("+++GOT+++");
				if(function_exists('getallheaders'))
				{
					foreach(getallheaders() as $name => $val)
					{
						xmlrpc_debugmsg("$name: $val\r");
					}
				}
				// do NOT log back to client the compressed request we received,
				// until we find a way to correctly encode it in charset-agnostic xml
				if(isset($_SERVER['HTTP_CONTENT_ENCODING']))
				{
					xmlrpc_debugmsg("\r\n+++END+++");
				}
				else
				{
					xmlrpc_debugmsg("\r\n" . $GLOBALS['HTTP_RAW_POST_DATA'] . "\n+++END+++");
				}
			}

			if(isset($_SERVER['HTTP_CONTENT_ENCODING']))
			{
				$r=$this->parseRequest($GLOBALS['HTTP_RAW_POST_DATA'], $_SERVER['HTTP_CONTENT_ENCODING']);
			}
			else
			{
				$r=$this->parseRequest($GLOBALS['HTTP_RAW_POST_DATA']);
			}

			if($this->debug > 2 && $GLOBALS['_xmlrpcs_occurred_errors'])
			{
				xmlrpc_debugmsg("+++PROCESSING ERRORS AND WARNINGS+++\n" .
					$GLOBALS['_xmlrpcs_occurred_errors'] . "+++END+++");
			}

			//$payload='<?xml version="1.0" encoding="' . $GLOBALS['xmlrpc_defencoding'] . '"?' . '>' . "\n"
			$payload=$this->xml_header;
			if($this->debug > 0)
			{

				//$payload = $payload . "<methodResponse>\n" . $this->serializeDebug();
				//$payload = $payload . substr($r->serialize(), 17);
				$payload = $payload . $this->serializeDebug();
			}
			//else
			//{
			$payload = $payload . $r->serialize();
			//}


			// if we get a warning/error that has output some text before here, then we cannot
			// add a new header. We cannot say we are sending xml, either...
			if(!headers_sent())
			{
				header('Content-Type: '.$r->content_type);

				// http compression of output
				if($this->compress_response && function_exists('gzencode') && isset($_SERVER['HTTP_ACCEPT_ENCODING']))
				{
					if(strstr($_SERVER['HTTP_ACCEPT_ENCODING'], 'gzip'))
					{
						//(strstr($HTTP_SERVER_VARS['HTTP_ACCEPT_ENCODING'], 'gzip')) {
						$payload = gzencode($payload);
						header("Content-Encoding: gzip");
						header("Vary: Accept-Encoding");
					}
					elseif (strstr($_SERVER['HTTP_ACCEPT_ENCODING'], 'deflate'))
					{
						//(strstr($HTTP_SERVER_VARS['HTTP_ACCEPT_ENCODING'], 'deflate')) {
						$payload = gzdeflate($payload);
						header("Content-Encoding: deflate");
						header("Vary: Accept-Encoding");
					}
				}

				header('Content-Length: ' . (int)strlen($payload));
			}
			else
			{
				//print "Internal server error: headers sent before PHP response"
			}

			print $payload;
		}

		/**
		* add a method to the dispatch map
		*/
		function add_to_map($methodname,$function,$sig,$doc='')
		{
			$this->dmap[$methodname] = array(
				'function'  => $function,
				'signature' => $sig,
				'docstring' => $doc
			);
		}

		/**
		* @access private
		*/
		function verifySignature($in, $sig)
		{
			// check each possible signature in turn
			foreach($sig as $cursig)
			{
				if(sizeof($cursig)==$in->getNumParams()+1)
				{
					$itsOK=1;
					for($n=0; $n<$in->getNumParams(); $n++)
					{
						$p=$in->getParam($n);
						// print "<!-- $p -->\n";
						if($p->kindOf() == 'scalar')
						{
							$pt=$p->scalartyp();
						}
						else
						{
							$pt=$p->kindOf();
						}
						// $n+1 as first type of sig is return type
						if($pt != $cursig[$n+1] && $cursig[$n+1] != $GLOBALS['xmlrpcValue'])
						{
							$itsOK=0;
							$pno=$n+1;
							$wanted=$cursig[$n+1];
							$got=$pt;
							break;
						}
					}
					if($itsOK)
					{
						return array(1,'');
					}
				}
			}
			if(isset($wanted))
			{
				return array(0, "Wanted ${wanted}, got ${got} at param ${pno})");
			}
			else
			{
				return array(0, "No method signature matches number of parameters");
			}
		}

		/**
		* @access private
		*/
		function parseRequest($data, $content_encoding='')
		{
			// 2005/05/07 commented and moved into caller function code
			//if($data=='')
			//{
			//	$data=$GLOBALS['HTTP_RAW_POST_DATA'];
			//}

			// check if request body has been compressed and decompress it
			if($content_encoding != '' && strlen($data))
			{
				if($content_encoding == 'deflate' || $content_encoding == 'gzip')
				{
					// if decoding works, use it. else assume data wasn't gzencoded
					if(function_exists('gzinflate') && in_array($content_encoding, $this->accepted_compression))
					{
						if($content_encoding == 'deflate' && $degzdata = @gzinflate($data))
						{
							$data = $degzdata;
							if($this->debug > 1)
							{
								xmlrpc_debugmsg("+++INFLATED REQUEST+++[".strlen($data)." chars]+++\n" . $data . "\n+++END+++");
							}
						}
						elseif($content_encoding == 'gzip' && $degzdata = @gzinflate(substr($data, 10)))
						{
							$data = $degzdata;
							if($this->debug > 1)
								xmlrpc_debugmsg("+++INFLATED REQUEST+++[".strlen($data)." chars]+++\n" . $data . "\n+++END+++");
						}
						else
						{
							$r =& new xmlrpcresp(0, $GLOBALS['xmlrpcerr']['server_decompress_fail'], $GLOBALS['xmlrpcstr']['server_decompress_fail']);
							return $r;
						}
					}
					else
					{
						//error_log('The server sent deflated data. Your php install must have the Zlib extension compiled in to support this.');
						$r =& new xmlrpcresp(0, $GLOBALS['xmlrpcerr']['server_cannot_decompress'], $GLOBALS['xmlrpcstr']['server_cannot_decompress']);
						return $r;
					}
				}
			}

			// 'guestimate' request encoding
			/// @todo check if mbstring is enabled and automagic input conversion is on: it might mingle with this check???
			$req_encoding = guess_encoding(isset($_SERVER['HTTP_CONTENT_TYPE']) ? $_SERVER['HTTP_CONTENT_TYPE'] : '',
				$data);

			// G. Giunta 2005/02/13: we do NOT expect to receive html entities
			// so we do not try to convert them into xml character entities
			//$data = xmlrpc_html_entity_xlate($data);

			$GLOBALS['_xh']=array();
			$GLOBALS['_xh']['isf']=0;
			$GLOBALS['_xh']['isf_reason']='';
			$GLOBALS['_xh']['params']=array();
			$GLOBALS['_xh']['stack']=array();
			$GLOBALS['_xh']['valuestack'] = array();
			$GLOBALS['_xh']['method']='';

			// decompose incoming XML into request structure
			if (!in_array($req_encoding, array('UTF-8', 'ISO-8859-1', 'US-ASCII')))
			// the following code might be better for mb_string enabled installs, but
			// makes the lib about 200% slower...
			//if (!is_valid_charset($req_encoding, array('UTF-8', 'ISO-8859-1', 'US-ASCII')))
			{
				error_log('XML-RPC: xmlrpc_server::parseRequest: invalid charset encoding of received request: '.$req_encoding);
				$req_encoding = $GLOBALS['xmlrpc_defencoding'];
			}
			$parser = xml_parser_create($req_encoding);
			xml_parser_set_option($parser, XML_OPTION_CASE_FOLDING, true);
			// G. Giunta 2005/02/13: PHP internally uses ISO-8859-1, so we have to tell
			// the xml parser to give us back data in the expected charset
			xml_parser_set_option($parser, XML_OPTION_TARGET_ENCODING, $GLOBALS['xmlrpc_internalencoding']);

			xml_set_element_handler($parser, 'xmlrpc_se', 'xmlrpc_ee');
			xml_set_character_data_handler($parser, 'xmlrpc_cd');
			xml_set_default_handler($parser, 'xmlrpc_dh');
			if(!xml_parse($parser, $data, 1))
			{
				// return XML error as a faultCode
				$r=&new xmlrpcresp(0,
				$GLOBALS['xmlrpcerrxml']+xml_get_error_code($parser),
				sprintf('XML error: %s at line %d',
					xml_error_string(xml_get_error_code($parser)),
					xml_get_current_line_number($parser)));
				xml_parser_free($parser);
			}
			elseif ($GLOBALS['_xh']['isf'])
			{
				xml_parser_free($parser);
				$r=&new xmlrpcresp(0,
					$GLOBALS['xmlrpcerr']['invalid_request'],
					$GLOBALS['xmlrpcstr']['invalid_request'] . ' ' . $GLOBALS['_xh']['isf_reason']);
			}
			else
			{
				xml_parser_free($parser);
				$m=&new xmlrpcmsg($GLOBALS['_xh']['method']);
				// now add parameters in
				$plist='';
				for($i=0; $i<sizeof($GLOBALS['_xh']['params']); $i++)
				{
					//print "<!-- " . $GLOBALS['_xh']['params'][$i]. "-->\n";
					$plist.="$i - " .  $GLOBALS['_xh']['params'][$i]. ";\n";
					$m->addParam($GLOBALS['_xh']['params'][$i]);
				}
				$r = $this->execute($m);
			}
			return $r;
		}

		function execute($m)
		{
			// now to deal with the method
			$methName = $m->method();
			$sysCall = $this->allow_system_funcs && ereg("^system\.", $methName);
			$dmap = $sysCall ? $GLOBALS['_xmlrpcs_dmap'] : $this->dmap;

			if(!isset($dmap[$methName]['function']))
			{
				// No such method
				return new xmlrpcresp(0,
					$GLOBALS['xmlrpcerr']['unknown_method'],
					$GLOBALS['xmlrpcstr']['unknown_method']);
			}

			// Check signature.
			if(isset($dmap[$methName]['signature']))
			{
				$sig = $dmap[$methName]['signature'];
				list($ok, $errstr) = $this->verifySignature($m, $sig);
				if(!$ok)
				{
					// Didn't match.
					return new xmlrpcresp(
						0,
						$GLOBALS['xmlrpcerr']['incorrect_params'],
						$GLOBALS['xmlrpcstr']['incorrect_params'] . ": ${errstr}"
					);
				}
			}

			$func = $dmap[$methName]['function'];
			// check that function defined in dispatch method does in fact exist
			if((is_array($func) && !method_exists($func[0], $func[1])) && !function_exists($func))
			{
				return new xmlrpcresp(
					0,
					$GLOBALS['xmlrpcerr']['server_error'],
					$GLOBALS['xmlrpcstr']['server_error'] . ": no function matches method"
				);
			}

			// If debug level is 3, we should catch all errors generated during
			// processing of user function, and log them into response
			if($this->debug > 2)
			{
				$GLOBALS['_xmlrpcs_prev_ehandler'] = set_error_handler('_xmlrpcs_errorHandler');
			}
			if($sysCall)
			{
				$r = call_user_func($func, $this, $m);
			}
			else
			{
				$r = call_user_func($func, $m);
			}
			if($this->debug > 2)
			{
				// note: restore the error handler we found before calling the
				// user func, even if it has been changed inside the func itself
				if($GLOBALS['_xmlrpcs_prev_ehandler'])
				{
					set_error_handler($GLOBALS['_xmlrpcs_prev_ehandler']);
				}
				else
				{
					restore_error_handler();
				}
			}
			return $r;
		}

		/**
		* A debugging routine: just echoes back the input packet as a string value
		* DEPRECATED!
		*/
		function echoInput()
		{
			$r=&new xmlrpcresp(new xmlrpcval( "'Aha said I: '" . $GLOBALS['HTTP_RAW_POST_DATA'], 'string'));
			print $r->serialize();
		}
	}
?>
