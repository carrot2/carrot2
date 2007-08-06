package org.carrot2.util.tokenizer.parser.jflex;
%%

%class JFlexWordBasedParserImpl
%unicode
%integer
%function getNextToken
%pack
%char

%{
public static final int TERM           = 1;
public static final int NUMERIC        = 2;
public static final int SENTENCEMARKER = 3;
public static final int PUNCTUATION    = 4;
public static final int EMAIL          = 5;
public static final int ACRONYM        = 6;
public static final int FULL_URL       = 7;
public static final int BARE_URL       = 8;
public static final int FILE           = 9;
public static final int HYPHTERM       = 10;

public final int yychar()
{
    return yychar;
}
%}

DOMAIN     = "mil" | "info" | "gov" | "edu" | "biz" | "com" | "org" | "net" | 
             "arpa" | {LETTER}{2}
             
WHITESPACE = \r\n | [ \r\n\t\f]
LETTER     = [\u0041-\u005a\u0061-\u007a\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u00ff\u0100-\u1fff\u3040-\u318f\u3300-\u337f\u3400-\u3d2d\u4e00-\u9fff\uf900-\ufaff]
DIGIT      = [\u0030-\u0039\u0660-\u0669\u06f0-\u06f9\u0966-\u096f\u09e6-\u09ef\u0a66-\u0a6f\u0ae6-\u0aef\u0b66-\u0b6f\u0be7-\u0bef\u0c66-\u0c6f\u0ce6-\u0cef\u0d66-\u0d6f\u0e50-\u0e59\u0ed0-\u0ed9\u1040-\u1049]
TERM       = ({LETTER}|{DIGIT})* {LETTER} ({LETTER}|{DIGIT}|("'"({LETTER})))*
SYMBOL     = ({LETTER}|{DIGIT})({LETTER}|{DIGIT}|"_"|"-")*
BARE_URL   = {SYMBOL}("."{SYMBOL})*"."{DOMAIN}
/* URL_PATH   = ("/" {SYMBOL})+ ("." {SYMBOL})? ("/")? ("?" {SYMBOL} ((";" | ":" | "@" | "&" | "=") {SYMBOL})*)? */
URL_PATH   = (";" | ":" | "@" | "&" | "=" | "?" | "/" | "_" | "%" | "." | {LETTER} | {DIGIT})+

%%

{DIGIT}+  ((":" | "-" | "/" | "," | ".") {DIGIT}+)*          { return NUMERIC; }

({LETTER} "." ({LETTER} ".")+) | ({TERM} ("&" {TERM})+)      { return ACRONYM; }

{LETTER} "." 												 { return TERM; }

{TERM} ( "-" {TERM})+                                        { return HYPHTERM; }

{TERM} "'" {LETTER}{1,2}                                     { return TERM; }

{TERM} "s'"                                                  { return TERM; }

{TERM}                                                       { return TERM; }

("mailto:")?{SYMBOL}("."{SYMBOL})*"@"{SYMBOL}("."{SYMBOL})*  { return EMAIL; }

{BARE_URL}                                                   { return BARE_URL;}
	
{SYMBOL}("."{SYMBOL})*                                       { return FILE;}
	
(("http" | "https" | "ftp") "://")? {BARE_URL} {URL_PATH}?   { return FULL_URL; }
	
("." | "?" | "!" | ";" )+	                                 { return SENTENCEMARKER; }

"," | "'" | ":" | "-"                                        { return PUNCTUATION; }

/** Ignore HTML entities */
"&" [a-zA-Z0-9#]+ ";"                                         { ; }

/** Ignore the rest */
. |	{WHITESPACE}		                                     { ; }
