Carrot2 Open Directory Project Core Interface
---------------------------------------------

This package provides an interface for (limited for the time being) indexing and 
searching of the Open Directory Project database. The ODP Carrot2 local input 
component serves as a source of document snippets drawn from the ODP directory.
See JavaDoc for the com.stachoodev.carrot.input.odp.local.ODPLocalInputComponent
for more details.

The following ODP indices are available

http://www.man.poznan.pl/~stachoo/carrot/odp-computers.zip  
	- index of all categories under Top/Computers
	
http://www.man.poznan.pl/~stachoo/carrot/odp-cshpark.zip  
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
