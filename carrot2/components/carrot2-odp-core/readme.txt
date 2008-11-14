Carrot2 Open Directory Project Core Interface
---------------------------------------------

This package provides an interface for (limited for the time being) indexing and 
searching of the Open Directory Project database. The ODP Carrot2 local input 
component serves as a source of document snippets drawn from the ODP directory.
See JavaDoc for the ODPLocalInputComponent class for more details.

The following ODP indices are available

http://ophelia.cs.put.poznan.pl/~sosinski/carrot/odp-computers.zip	
	- index of all categories under Top/Computers
	
http://ophelia.cs.put.poznan.pl/~sosinski/carrot/odp-cshpark.zip
	- index of all the following categories
		Top/Arts/Literature
		Top/Arts/Movies
		Top/Computers
		Top/Science
		Top/Health
		Top/Recreation
		Top/World/Polska
		Top/Kids_and_Teens

To use the index simply unzip the file onto a folder on local drive, and point
the tools requiring an ODP index to that folder.

If you wish to build your own index, use the run script 
(ODPIndexer class) with two parameters:

ODPIndexer odp-xml-input-file index-location

Note: it is now possible to index the whole ODP contents (the big ~2G XML file)
on a reasonable PC with a reasonable amount of RAM (512MB+) within tens of 
minutes. However, in the present implementation, for the whole ODP database,
the primary topic index will take about 50MB of RAM during runtime, and the path
index will take another 50MB. With parts of the ODP the overhead is much smaller.
