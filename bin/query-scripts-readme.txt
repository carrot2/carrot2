
These scripts allow direct querying of remote input and filter
components.

INSTALLATION
============

You must build the carrot2-util-remote component first. Use
the following command (replace '/' with '\' for Windows-based
systems):

cd ../components/carrot2-util-remote
ant -Dcopy.dependencies=true build

RUNNING
=======

Use shell wrappers for windows:

QueryInput.bat [serviceURL] [requested results number] [query terms]

	Sends 'query terms' to the specified URL and requests the desired
	number of snippets. The result is printed to standard output.

QueryFilter.bat [-service url] [-param name value] {request file XML}
[] - required, {} - optional (if not present, stdin is read)

	Sends the request file (probably an XML), or the input stream,
	to the specified URL and prints the result to standard output.

NOTE: For unknown reasons, these scripts sometimes fail to print
the result to the console window. Use shell redirects to send the
output to a regular file.
