==========================================================

-------------------- ODP239  Dataset --------------------
  
              http://credo.fub.it/odp239/

==========================================================



SUMMARY
==========================================================
ODP239 is a dataset designed for evaluating subtopic information retrieval.

It consists of 239 topics, each with a set of about 100 subtopics
and a set of about 100 documents associated with single subtopics.

The topics, subtopics, and their associated documents were selected
from the Open Directory Project (www.dmoz.org).

The ODP239 dataset consists of four files where each row is terminated
by Linefeed (ASCII 10) and fields are separated by Tab (ASCII 9).
The four files are described below.




==================== topics.txt ========================

It contains topic ID and description.

ID	description
1	Arts > Animation
2	Arts > Architecture
3	Arts > Bodyart
.........
==========================================================



==================== subTopics.txt ========================

It contains subtopic ID (formed by topic ID and subtopic number) and description. 

ID	description
1.1	Arts > Animation > Anime
1.2	Arts > Animation > Cartoons
1.3	Arts > Animation > Voice_Actors
.........
==========================================================



==================== docs.txt ========================

It contains doc ID (formed by topic ID and doc number), url,  title, and snippet.

ID	url	title	snippet
1.1	http://www.actingforanimators.com	Acting For Animators	Acting For Animators, by Ed Hooks, is the first book about acting theory that is written specifically for animators. Site contains information about classes, resources, and news.
1.2	http://web.mit.edu/anime/www/	MIT Anime Club	Viewing schedule, club information, art, library, and links. Cambridge, Massachusetts, USA at Massachusetts Institute of Technology.
1.3	http://www.nnanime.com/	No-Name Anime Society	Information about monthly showings and copies of  newsletters. Saratoga, California, USA.
.........
==========================================================



==================== STRel.txt ========================
It contains subtopic ID (formed by topic ID and subtopic number) 
and doc ID (formed by topic ID and doc number).

subTopicID	docID
1.2	1.25
1.2	1.44
1.2	1.55
.........
==========================================================




A FEW STATISTICS ABOUT THE COLLECTION
==========================================================
Number of topics: 239
Number of subtopics: 2285
Number of docs: 25580
Number of subtopics per topic: 9.5607 (min 6, max 10)
Number of docs per topic: 107.0293 (min 98, max 131)
Number of docs per subtopic: 11.1947 (min 4, max 94)
==========================================================




USAGE LICENSE
==========================================================

Copyright(c) 2009 Fondazione Ugo Bordoni 
All rights reserved. 
Authors: Claudio Carpineto and Giovanni Romano 

The copyright holder and authors can not guarantee the correctness of the data,
its suitability for any particular purpose, or the validity of results based
on the use of the data set, and they assume no responsibility for the content,
legality, reliability, and accuracy of the data.
The data set may be used for any research purposes.

Please acknowledge the use of the data set in publications
resulting from the use of the data set:
Carpineto C., Romano G. (2009). ODP239 dataset, http://credo.fub.it/odp239/





DOWNLOAD
==========================================================
http://credo.fub.it/odp239/odp239.tar.gz


MORE INFO
==========================================================
If you have any further questions or comments, please contact Claudio Carpineto
<carpinet@fub.it>

