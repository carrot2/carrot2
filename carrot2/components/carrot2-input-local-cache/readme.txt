
A local input component that can read and serve remote controller's cache files.

This is sometimes handy when you want to debug/ reuse some real queries with
local components.


QUERY FORMAT:

The query passed to the component may contain special prefixes, which alter
its behavior:

1) Direct file loading query: file:<file_path>

example: "file:c:\cache\mycachedfile.gz"

The path should be absolute.

2) Dump all cached queries in a cache store: dump:

example: "dump:"

This will return "fake" documents where for each document, its title
will correspond to a query available in the cached queries store. This is
a good way of checking which queries can be executed 'directly' by
the component.

3) Raw queries to a store: just type a query.

Example: "salsa"
Example: "salsa component:component:carrot2.input.snippet-reader.google"

Raw queries will be sought in the queries store used to initialize the component.
If a raw query is found, its result from any component is returned. If the cache
contains the result for this query from more than one component, a specific
result can be retrieved by appending a "component:" prefix. See above.



