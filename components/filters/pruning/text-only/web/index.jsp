<%@page contentType="text/html; charset=UTF-8" %>
<HTML>
<BODY>
<h1>Text-only filter</h1>
<p>
This is an example of a filter component for tokenizing snippets. Sentence-borders (.!?) are left intact,
all other tokens are copied to the output as they are parsed by the Tokenizer class.

<em>
This filter is merely for demonstration purposes. Tokenizer class is publicly available in the shared
library so it can be used directly (the side-effect of this filter is that you may lose certain words/
punctuation characters in the output).
</em>

</p>
</BODY>
</HTML>