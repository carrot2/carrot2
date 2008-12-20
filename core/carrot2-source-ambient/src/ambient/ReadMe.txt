
==========================================================
   AMBIENT  Dataset
  
   http://credo.fub.it/ambient/
  
   http://credo.fub.it/ambient/dataset.tar.gz   (for downloading the data)
==========================================================



SUMMARY
==========================================================
AMBIENT (AMBIgous ENTries) is a dataset designed for evaluating subtopic information retrieval.

It consists of 44 topics, each with a set of subtopics and a list of 100 ranked documents.

The topics were selected from the list of ambiguous Wikipedia entries; i.e., those with "disambiguation" in the title 
(see http://en.wikipedia.org/wiki/Wikipedia:Links_to_%28disambiguation%29_pages)

The 100 documents associated with each topic were collected from a Web search engine as of January 2008, and they were subsequently annotated with subtopic relevance judgments.

The AMBIENT dataset consists of four files where each row is terminated by Linefeed (ASCII 10) and fields are separated by Tab (ASCII 9). The four files are described below.




==================== topics.txt ========================

It contains topic ID and description

ID	description
1	Aida
2	B-52
3	Beagle
.........
==========================================================



==================== subTopics.txt ========================

It contains subtopic ID (formed by topic ID and subtopic number) and description; 

ID	description
1.1	Aida, female given name
1.2	Aida can be a Japanese name
1.3	A Persian name

.........
==========================================================



==================== results.txt ========================

It contains result ID (formed by topic ID and search engine rank of result), url,  title, and snippet

ID	url	title	snippet
1.1	http://www.aida-international.org/	AIDA International	International Assoication for Development of Apnea dedicated for breath-hold diving or apnea which manages and oversees the recognition of records, organizes competitions, and sets standards for freedive education.
1.2	http://disney.go.com/theatre/aida	Disney on Broadway - 'Elton John and Tim Rice's Aida'- Official Homepage	The official site of 'Elton John and Tim Rice's Aida.' Buy tickets online, meet the cast, hear the Tony Award-winning music, get performance and tour information, ...
1.3	http://en.wikipedia.org/wiki/Aida	Aida - Wikipedia, the free encyclopedia	For other references see Aida (disambiguation) Aida is an opera in four ... Aida, the daughter of the Ethiopian King Amonasro, lives at Memphis as a slave. ...
.........
==========================================================



==================== STRel.txt ========================
It contains subtopic ID (formed by topic ID and subtopic number) and result ID (formed by topic ID and search engine rank of result)

subTopicID	resultID
1.4	1.3
1.4	1.4
1.4	1.10
.........
==========================================================



USAGE LICENSE
==========================================================

Copyright(c) 2008 Fondazione Ugo Bordoni 
All rights reserved. 
Authors: Claudio Carpineto and Giovanni Romano 

The copyright holder and authors can not guarantee the correctness of the data, its suitability for any particular purpose, or the validity of results based on the use of the data set, and they assume no responsibility for the content, legality, reliability, and accuracy of the data.
The data set may be used for any research purposes.
Please acknowledge the use of the data set in publications resulting from the use of the data set:
Carpineto C., Romano G. (2008). Ambient dataset, http://credo.fub.it/ambient/





DOWNLOAD
==========================================================
http://credo.fub.it/ambient/dataset.tar.gz



MORE INFO
==========================================================
If you have any further questions or comments, please contact Claudio Carpineto
<carpinet@fub.it>


