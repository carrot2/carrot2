<%@page contentType="text/html; charset=UTF-8" %>
<HTML>
<BODY>

<h1>Stemming filter</h1>

<h2>Purpose</h2>
The stemming filter performs a light pre-processesing of the input documents
(snippets and titles) in Carrot<sup>2</sup> search results clustering format. The pre-processing
includes stemming or lemmatization and tagging stop words. The stemmer and stop words set are fully
configurable using URL query string parameters.

<h2>Configuration</h2>

This filter accepts two parameters: <code>stemmer</code> and <code>stopwords</code>.

<dl>
    <dt><code>stemmer</code></dt>
    <dd>
        Stemmer parameter is responsible for choosing a term conflating algorithm. The value of
        this parameter must be a fully classified Java class name, implementing
        <code>com.dawidweiss.carrot.filter.stemming.DirectStemmer</code> interface. Currently,
        the following stemmers exist:
        <ul>
            <li><code>com.dawidweiss.carrot.filter.stemming.porter.PorterStemmer</code> - The most
                    popular stemmer for the English language. 
                    <a href="http://www.tartarus.org/~martin/PorterStemmer/index.html">Implementation
                    by Martin Porter himself</a>.
            </li>
            <li><code>com.dawidweiss.carrot.filter.stemming.lametyzator.Lametyzator</code> - A custom
                    lemmetization engine based on <a href="http://ispell-pl.sourceforge.net">the Polish i-spell dictionary</a>.
                    <a href="http://www.cs.put.poznan.pl/dweiss">More details on author's page</a>
                    (ehm... this happens to be me).
            </li>
        </ul>
    </dd>
    
    <dt><code>stopwords</code></dt>
    <dd>
        This argument is optional (if no value is given no words are marked as stop words). It should
        point to file with whitespace-separated list of terms, which should be marked in the text as belonging
        to a &quot;stop words&quot; set. These terms usually include prepositions or conjunctions
        and rarely carry any value (however, in some cases they do - &quot;To be or not to be&quot;...)
        <p>
        Currently, the file is sought in a folder relative to web application deployment directory:
        <tt>/stopwords/<i>stopwords_parameter</i></tt>. Available stop words sets include:
        <ul>
            <li><tt><a href="stopwords/stopwords-en.lst">stopwords-en.lst</a></tt> - a set of 323 common English terms</li>
            <li><tt><a href="stopwords/stopwords-pl.lst">stopwords-pl.lst</a></tt> - a set of 169 common Polish terms</li>
        </ul>
    </dd>
</dl>
 

</BODY>
</HTML>