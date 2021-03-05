export const globExclusionsHelpHtml = `
<p>
 If a phrase matches any pattern listed, the phrase will not be used as a cluster label.
 Put one pattern per line. Separate words with spaces, <code>*</code> matches zero or more words.
</p>

<h4>Pattern syntax and matching rules</h4>

<ul>
  <li>Each entry must consist of one or more space-separated tokens.</li>
  <li>A token can be a sequence of arbitrary characters, such as words, numbers, identifiers.</li>
  <li>Matching is case-insensitive by default.</li>
  <li>The <code>*</code> token matches zero or more words.</li>
  <li>
    Using the <code>*</code> wildcard character in combination with other characters, for
    example 1<code>programm*</code>, is not supported.
  </li>
  <li>
    Token put in double quotes, for example <code>"Rating***"</code> is taken literally: matching
    is case-sensitive, <code>*</code> characters are allowed and taken literally.
  </li>
  <li>
    To include double quotes as part of the token, escape them with the <code>\\</code> character,
    for example: <code>\\"information\\"</code>.
  </li>
</ul>

<h4>Example patterns</h4>

<dl>
  <dt><code>more information</code> (exact match)</dt>
  <dd>
    Matches:
    <ul>
      <li><code>more information</code></li>
      <li><code>More information</code></li>
      <li><code>MORE INFORMATION</code></li>
    </ul>

    Does not match:

    <ul>
      <li><code title="'informations' does not match pattern token 'information'.">more informations</code><br/></li>
      <li><code title="Pattern does not contain wildards, only 2-word strings can match.">more information about</code><br/></li>
      <li><code title="Pattern does not contain wildards, only 2-word strings can match.">some more information</code></li>
    </ul>
  </dd>

  <dt><code>more information *</code> (leading match)</dt>
  <dd>
    Matches:
    <ul>
      <li><code>more information</code></li>
      <li><code>More information about</code></li>
      <li><code>More information about a</code></li>
    </ul>

    Does not match:

    <ul>
      <li title="'informations' does not match pattern token 'information'."><code>informations</code></li>
      <li title="'informations' does not match pattern token 'information'."><code>more informations about</code></li>
      <li title="'informations' does not match pattern token 'information'."><code>some more informations</code></li>
    </ul>
  </dd>


  <dt><code>* more information *</code> (containing match)</dt>
  <dd>
    Matches:
    <ul>
      <li><code>information</code></li>
      <li><code>more information</code></li>
      <li><code>information about</code></li>
      <li><code>a lot more information on</code></li>
    </ul>

    Does not match:
    <ul>
      <li title="'informations' does not match pattern token 'information'."><code>informations</code></li>
      <li title="'informations' does not match pattern token 'information'."><code>more informations about</code></li>
      <li title="'informations' does not match pattern token 'information'."><code>some more informations</code></li>
    </ul>
  </dd>

  <dt><code>"Information" *</code> (literal match)</dt>
  <dd>
    Matches:
    <ul>
      <li><code>Information</code></li>
      <li><code>Information about</code></li>
      <li><code>Information ABOUT</code></li>
    </ul>

    Does not match:
    <ul>
      <li title="&quot;Information&quot; token is case-sensitive, it does not match 'information'."><code>information</code></li>
      <li title="&quot;Information&quot; token is case-sensitive, it does not match 'information'."><code>information about</code></li>
      <li title="'Informations' does not match pattern token &quot;Information&quot;."><code>Informations about</code></li>
    </ul>
  </dd>

  <dt><code>"Programm*"</code> (literal match)</dt>
  <dd>
    Matches:
    <ul>
      <li><code>Programm*</code></li>
    </ul>

    Does not match:
    <ul>
      <li title="&quot;Programm*&quot; token is taken literally, it matches only 'Programm*'."><code>Programmer</code></li>
      <li title="&quot;Programm*&quot; token is taken literally, it matches only 'Programm*'."><code>Programming</code></li>
    </ul>
  </dd>

  <dt><code>\\"information\\"</code> (escaping quote characters)</dt>
  <dd>
    Matches:
    <ul>
      <li><code>"information"</code></li>
    </ul>

    Does not match:
    <ul>
      <li title="Escaped quotes are taken literally, so match is case-insensitive"><code>"INFOrmation"</code></li>
      <li title="Escaped quotes not found in the string being matched."><code>information</code></li>
      <li title="Escaped quotes not found in the string being matched."><code>"information</code></li>
    </ul>
  </dd>

  <dt><code>programm*</code></dt>
  <dd>
    Illegal pattern, combinations of the <code>*</code> wildcard and other characters are not supported.
  </dd>

  <dt><code>"information</code></dt>
  <dd>
    Illegal pattern, unbalanced double quotes.
  </dd>

  <dt><code>*</code></dt>
  <dd>
    Illegal pattern, there must be at least one non-wildcard token.
  </dd>
</dl>`;
