<%@page contentType="text/html; charset=UTF-8" %>
<HTML>
<BODY>
<h1>Diagnostic filters</h1>
<p>
Some help in troubleshooting other filters.
</p>

<p>
Filters below log their data in container's <tt>log</tt> directory
(for Tomcat that would be <tt>/logs</tt> folder). Files are named using a
concatenation of current time and a static counter. The output directory
can be easily overriden using <tt>intercepted.streams.folder</tt> input
parameter for a given servlet. This parameter should point to a directory
where intercepted logs should be stored.
</p>

<p>
Installed filters include:
<dl>
    <dt>StreamInterceptor</dt>
    <dd>This filter read all of POSTed data and save it to a log file. This filter
    will return the input stream as it was (and thus will probably cause an exception
    in the subsequent filter, because it also returns POST headers.
    </dd>

    <dt>Carrot2ChunkInterceptor</dt>
    <dd>This filter, when put in between two other filters, will intercept any POSTed
        Carrot2 data and <em>copy it directly</em> to the output. It can be used
        in between other Carrot2-compliant filters and it should be transparent to them.
    </dd>

</dl>

</ul>
</BODY>
</HTML>