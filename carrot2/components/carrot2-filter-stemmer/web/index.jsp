<%@page contentType="text/html; charset=UTF-8" %>
<HTML>
<BODY>

<h1>Stemming and language detection filter for remote architecture components</h1>

<h2>Purpose</h2>

<p>
The stemming filter performs a light pre-processesing of the input documents
(snippets and titles) in Carrot<sup>2</sup> search results clustering format. 

<p>
The pre-processing
includes language detection, stemming or lemmatization and tagging stop words.

<h2>Configuration</h2>

This filter accepts one parameter: <code>languages</code>.
The parameter is a comma-separated ISO codes of the languages that should be recognized
by the component (if available).

</BODY>
</HTML>