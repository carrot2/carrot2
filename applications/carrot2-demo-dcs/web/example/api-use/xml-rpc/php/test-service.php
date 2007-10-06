<?php

require_once("carrot2.inc");

//
// A test script to validate a remote Carrot2 XML RPC service
// and to check character encoding incompatibilities.
// 

//
// This page is encoded in this encoding.
//
$THISPAGE_ENCODING = "UTF-8";

//
// Force UTF-8 as the transport encoding.
//
$GLOBALS['xmlrpc_defencoding'] = 'UTF-8';
$GLOBALS['xmlrpc_internalencoding'] = 'UTF-8';

//
// Make an object to represent our server.
//
$server = new xmlrpc_client($SERVICE, $SERVER, $PORT);

//
// Start writing test response. This page is encoded in iso-8859-2
//
header("Content-type: text/html; charset=$THISPAGE_ENCODING");
?>
<html>
<head>
    <meta http-equiv="Content-type" content="text/html; charset=<?php echo $THISPAGE_ENCODING; ?>">
    <title>Carrot2 XML-RPC Test</title>
    <style type="text/css">
    body {
        margin: 10px;
        font-family: sans-serif;
        font-size: 11px;
        background-color: white;
        color: black;
    }
    p.head {
        font-weight: bold;
        margin-top: 15px;
    }
    p.value {
        margin-left: 15px;
        background-color: #e0e0e0;
        padding: 2px;
    }
    pre {
        margin-left: 15px;
        background-color: #f0f0f0;
        padding: 2px;
    }
    </style>
</head>
<body>

<?php

echo '<p class="head">Test URL:</p><p class="value">' . "$SERVER:$PORT ($SERVICE) </p>";

//
// Test: Check if the service works at all.
//

echo '<p class="head">Testing, simple ping:</p><p class="value">';

$message = new xmlrpcmsg('test-encoding.echoTrue', array());
$result = $server->send($message);
if (!$result) {
	echo "ERROR: No result (could not connect?).";
} elseif ($result->faultCode()) {
	echo "XML-RPC Fault #" . $result->faultCode() . ": " . $result->faultString();
} else {
	$value = $result->value();
	if ($value->scalarval() == true) {
        echo "PASSED";
    } else {
        echo "FAILED";
    }
}

echo "</p>";
flush();


//
// Test: Check server-to-client encoding.
//

echo '<p class="head">Testing, server-to-client encoding:</p><p class="value">';

$message = new xmlrpcmsg('test-encoding.echoUtfChars', array());
$result = $server->send($message);
if (!$result) {
	echo "ERROR: No result (could not connect?).";
} elseif ($result->faultCode()) {
	echo "XML-RPC Fault #" . $result->faultCode() . ": " . $result->faultString();
} else {
	$value = $result->value();
    $result = iconv("UTF-8", $THISPAGE_ENCODING, $value->scalarval());

	if (is_null($result)) {
        echo "FAILED (result is null)";
    } else {
        if ($result == "start:łóęąśżźćńŁÓĘŻĆŚ:end") {
            echo "PASSED";
        } else {
            echo "ERROR: String has been returned with a weird encoding: '"
                . $result . "' (should be: 'start:łóęąśżźćńŁÓĘŻĆŚ:end')";
        }
    }
}

echo "</p>";
flush();



//
// Test: two-way encoding test.
//

echo '<p class="head">Testing, round-trip encoding:</p><p class="value">';

$message = new xmlrpcmsg('test-encoding.echo',
            array(new xmlrpcval(iconv($THISPAGE_ENCODING, "UTF-8", "start:łóęąśżźćńŁÓĘŻĆŚ:end"), 'string')));
$result = $server->send($message);
if (!$result) {
	echo "ERROR: No result (could not connect?).";
} elseif ($result->faultCode()) {
	echo "XML-RPC Fault #" . $result->faultCode() . ": " . $result->faultString();
} else {
	$value = $result->value();
    $result = iconv("UTF-8", $THISPAGE_ENCODING, $value->scalarval());

	if (is_null($result)) {
        echo "FAILED (result is null)";
    } else {
        if ($result == "start:łóęąśżźćńŁÓĘŻĆŚ:end") {
            echo "PASSED";
        } else {
            echo "ERROR: String has been returned with a weird encoding: '"
                . $result . "' (should be: 'start:łóęąśżźćńŁÓĘŻĆŚ:end')";
        }
    }
}

echo "</p>";
flush();


//
// Test 1: Test Carrot2 service.
//
echo '<p class="head">Testing, Carrot2 service check:</p><p class="value">';

$carrot = new Carrot2($SERVER, $PORT, $SERVICE, $THISPAGE_ENCODING);

$results = array(
    "title", "http://www.nodomain.com/", "snippet",
    "start łóęąśżźćńŁÓĘŻĆŚ end", "http://www.nodomain.com/", "Sample snippet: łóęąśżźć",
    "start2 łóńłśżźćą", "http://www.nodomain.com/", "Second sample snippet: łóśńśąźćąś"
);

for ($i = 0; $i < count($results); $i += 3) {
  $title = $results[$i];
  $url = $results[$i + 1];
  $snippet = $results[$i + 2];
  $carrot->addDocument($url, strip_tags($title), strip_tags($snippet));
}

$clusters = $carrot->clusterQuery("test4");

if (isset($clusters)) {
    echo "PASSED (" . count($clusters) . " clusters)";
} else {
    echo "FAILED (no clusters object);";
}

echo "</p>";
flush();


//
// Test 2: Test Carrot2 service using some real data.
//
echo '<p class="head">Testing, Carrot2 service check (real data):</p><p class="value">';

$carrot = new Carrot2($SERVER, $PORT, $SERVICE, $THISPAGE_ENCODING, $SERVICE_NAME,
    /* Processing options. This can be used to select the clustering algorithm
       for example. See ProcessingOptionNames for key and possible values.
       
       Example:
       array("dcs.default.algorithm" => "stc-en"),
     */
    array(),
    /* Request parameters. This can be used to customize the clustering process. See
    particular components for keys/ values.
     */ 
    array()
);

$results = array(
"Data Mining - Wikipedia", "http://en.wikipedia.org/wiki/Data_mining", "Article about knowledge-discovery in databases (KDD), the practice of automatically searching large stores of data for patterns.",
"KD Nuggets", "http://www.kdnuggets.com/", "Newsletter on the data mining and knowledge industries, offering information on data mining, knowledge discovery, text mining, and web mining software, courses, jobs, publications, and meetings.",
"The Data Mine", "http://www.the-data-mine.com/", "Provides information about data mining also known as knowledge discovery in databases (KDD) or simply knowledge discovery. List software, events, organizations, and people working in data mining.",
"DMG", "http://www.dmg.org/", "The Laboratory for Advanced Computing develops technologies for high performance computing, high performance networking, internet computing, data mining and related areas. ... Data Mining Group. DMG. DMG Menu ... The Data Mining Group (DMG) is an independent, vendor led group which develops data mining standards, such as the ...",
"Two Crows: Data mining glossary", "http://www.twocrows.com/glossary.htm", "Data mining terms concisely defined. ... factor in assessing the success of data mining. When applied to data, accuracy refers to the rate of ... For example, a data mining software system may have an API which ...",
"Jeff Ullman's Data Mining Lecture Notes", "http://www-db.stanford.edu/~ullman/mining/mining.html", "Offers an introduction to various data mining applications and techniques: association-rule mining, low-support/high correlation, query flocks, searching the Web, web mining, and clustering.",
"Thearling.com", "http://www.thearling.com/", "Kurt Thearling's site dedicated to sharing information about data mining, the automated extraction of hidden predictive information from databases, and other analytic technologies.",
"Data Mining", "http://www.eco.utexas.edu/~norman/BUS.FOR/course.mat/Alex", "Introduction to knowledge discovery, the computer-assisted process of digging through and analyzing enormous sets of data and then extracting the meaning of the data.",
"CCSU - Data Mining", "http://www.ccsu.edu/datamining/resources.html", "Data Mining Resources. Resources. Groups. Data Sets. Papers on Data Mining. Commercial. Register at",
"Data Mining: Practical Machine Learning Tools and Techniques", "http://www.cs.waikato.ac.nz/~ml/weka/book.html", "Data Mining: Practical Machine Learning Tools and Techniques (Second Edition) Morgan Kaufmann. June 2005. 525 pages. Paper. ISBN 0-12-088407-0. Comments ... What's it all about? 1.1 Data mining and machine learning ...",
"Data Mining - Monografias.com", "http://www.monografias.com/trabajos/datamining/datamining.shtml", "En indice. En texto completo. En Internet. Data Mining ... Data Mining, la extracción de información oculta y predecible de grandes bases de ... de Información (Data Warehouse). Las herramientas de Data Mining predicen futuras tendencias y ...",
"Amazon.com: Data Mining: Books: Pieter Adriaans,Dolf Zantinge", "http://www.amazon.com/exec/obidos/tg/detail/-/0201403803?v=glance", "... Data Mining deals with discovering hidden knowlege, unexpected patterns and rules in large databases ... But setting up a data mining environment is not a trivial task ...",
"DMReview", "http://www.dmreview.com/", "An issues and solutions publication that focuses on data warehousing as well as client/server and object technology for the enterprise.",
"Data Mining @ CCSU", "http://www.ccsu.edu/datamining", "Offers degrees and certificates in data mining. Allows students to explore cutting-edge data mining techniques and applications: market basket analysis, decision trees, neural networks, machine learning, web mining, and data modeling.",
"What is Data Mining", "http://www.megaputer.com/dm/dm101.php3", "Megaputer offers data mining, text mining, and web data mining software tools for e-commerce, database marketing, and CRM; seminars, training and consulting on data mining. Customer ... in order to make informed business decisions. Data mining automates the process of finding relationships and patterns in ... In these situations data mining is your only real option ...",
"Electronic Statistics Textbook: Data Mining Techniques", "http://www.statsoft.com/textbook/stdatmin.html", "Outlines the crucial concepts in data mining, defines the data warehousing process, and offers examples of computational and graphical exploratory data analysis techniques.",
"data mining - a definition from Whatis.com - see also: data miner, data analysis", "http://searchcrm.techtarget.com/sDefinition/0,,sid11_gci211901,00.html", "... data mining. Home &amp;gt; CRM Definitions - Data mining ... about the future (This area of data mining is known as predictive analytics.) Data mining techniques are used in ...",
"St@tServ - About Data Mining", "http://www.statserv.com/datamining.html", "... What is Data Mining ? ' Data mining is the process of discovering meaningful new correlations, patterns ... Gartner Group). ' Data mining is the exploration and analysis, by automatic ...",
"DATA MINING 2005", "http://www.wessex.ac.uk/conferences/2005/data05", "... International Conference on Data Mining, Text Mining and their Business Applications ... Conference on Data Mining, Text Mining and their Business Applications (Data Mining ...",
"Investor Home - Data Mining", "http://www.investorhome.com/mining.htm", "... Data Mining. The rapid evolution of computer technology in the last few decades has provided ... and consequences of 'data mining.' Data mining involves searching through databases for ...",
"SAS | Data Mining and Text Mining", "http://www.sas.com/technologies/data_mining", "... status almost upon its introduction, our data mining technology continues to receive rave reviews ... text-based information with structured data for an enriched data mining process ...",
"Data Mining Student Notes, QUB", "http://www.pcc.qub.ac.uk/tec/courses/datamining/stu_notes/dm_book_1.html", "Data Mining. An Introduction. Student Notes. Ruth Dilly. Parallel Computer Centre. Queens University Belfast. Version 2.0. December1995 ... 1 - Data mining. 1.1 - What is data mining? 1.2 - Data mining background. 1.2.1 - Inductive learning ...",
"Data Mining", "http://datamining.typepad.com/data_mining", "... Data Mining. About. Galleries ... However, the distance between that data and some model of intention is currently pretty wide ...",
"Two Crows Corporation", "http://www.twocrows.com/", "Dedicated to the development, marketing, sales and support of tools for knowledge discovery to make data mining accessible and easy to use.",
"Statistical Data Mining Tutorials", "http://www.autonlab.org/tutorials", "Includes a set of tutorials on many aspects of statistical data mining, including the foundations of probability, the foundations of statistical data analysis, and most of the classic machine learning and data mining algorithms.",
"Data Mining: An Introduction", "http://databases.about.com/library/weekly/aa100700a.htm", "Data mining allows you to find the needles hidden in your haystacks of data. Learn how to use these advanced techniques to meet your business objectives. ... heard a good deal about data mining -- the database industry's latest buzzword ... of automated statistical analysis (or 'data mining') techniques, businesses are discovering new ...",
"Data Mining Project", "http://research.microsoft.com/dmx/datamining", "Search: All Research OnlineAll Microsoft.com. Data Mining: Efficient Data Exploration and Modeling. Overview. Goal ... will focus on exploiting data mining for advanced data summarization and also enable tighter ... database querying and data mining. Scalable Data Mining Algorithms: We are exploring ...",
"An Introduction to Data Mining", "http://www.thearling.com/text/dmwhite/dmwhite.htm", "... Data mining, the extraction of hidden predictive information from large databases, is a ... important information in their data warehouses. Data mining tools predict future trends ...",
"Untangling Text Data Mining", "http://www.sims.berkeley.edu/~hearst/papers/acl99/acl99-tdm.html", "... Untangling Text Data Mining. Marti A. Hearst. School of Information Management &amp;amp; Systems ... The possibilities for data mining from large text collections are virtually untapped ...",
"Data Mining Technologies", "http://www.data-mine.com/", "Provides Data Minining Software for Desktop and Real Time Needs ... Data Mining Technologies provides next generation decision support software and services for Data Mining and Business Intelligence applications ...",
"SQL Server Data Mining", "http://www.sqlserverdatamining.com/", "sql server | data mining. Welcome to SQLServerDataMining.com &amp;lt;we are hiring&amp;gt; ... This site has been designed by the SQL Server Data Mining team to provide the SQL Server community with access to and ...",
"Data Warehousing Information Center", "http://www.dwinfocenter.org/", "Provides information on tools and techniques to design, build, maintain, and retrieve information from a data warehouse.",
"ITworld.com - Data mining", "http://www.itworld.com/App/110/050805datamining", "... it into usable shape, however, requires sophisticated data mining tools. The same technology that police departments ... How does data mining work? Data mining is a subset of business ...",
"IBM Research | Almaden Research Center | Computer Science", "http://www.almaden.ibm.com/cs/quest", "... preserve the privacy and ownership of data while not impeding the flow of information ... Hippocratic Management of Data. Privacy Preserving Analytics (Mining &amp;amp; OLAP) Compliance Auditing ...",
"Data Mining and Discovery", "http://www.aaai.org/AITopics/html/mining.html", "AI Topics provides basic, understandable information and helpful resources concerning artificial intelligence, with an emphasis on material available online. ... Data Mining and Discovery. (a subtopic of Machine Learning ... Data mining is an AI powered tool that can discover useful information within a database that can then be used ...",
"Data Mining: An Overview", "http://www.fas.org/irp/crs/RL31798.pdf", "... assessing risk, and product. retailing, data mining involves the use of data analysis tools to discover ... homeland security, data mining is often viewed as a potential means to ...",
"Data Mining", "http://www.gr-fx.com/graf-fx.htm", "... databases with graphs and queries using a technique called Data Mining. It is also a quick way to ... learn how to use another data mining product. All you have to ...",
"Data Mining Benchmarking Association (DMBA)", "http://www.dmbenchmarking.com/", "Association of companies and organizations working to identify 'best in class' data mining processes through benchmarking studies.",
"Data Mining", "http://www.computerworld.com/databasetopics/businessintelligence/datamining", "Computerworld, the 'Voice of IT Management' is your leading information source for coverage databases, data mining, data center &amp;amp; data warehousing. ... Horizon Awards 2005Data Management: Mining for GemsData Management: Taming Data ChaosDevelopment: Hard ... ComplexityStorage: Cheap &amp;amp; Secure Data StoresStorage: Stretching Your Storage ...",
"National Center for Data Mining (NCDM) - University of Illinois at Chicago", "http://www.ncdm.uic.edu/", "Conducts research in: scaling algorithms, applications and systems to massive data sets, developing algorithms, applications, and systems for mining distributed data, and establishing standard languages, protocols, and services for data mining and predictive modeling.",
"URL's for Data Mining", "http://www.galaxy.gmu.edu/stats/syllabi/DMLIST.html", "URL's for Data Mining. The following URL's are some links to a variety of Data Mining webpages. They are not in any particular order. Actually, they are in the order I discovered (mined) them.",
"Data mining [OCLC - Projects]", "http://www.oclc.org/research/projects/mining/", "Describes the goals, methodology, and timing of the Data mining project. ... Data mining. DCMI Registry DSpace Harvesting Economics of Digital Preservation Electronic Theses and Dissertations ... this end, the OCLC Research Data-Mining Research Area will focus on ...",
"Rulequest Research", "http://www.rulequest.com/", "Provides software tools for data mining and knowledge discovery in databases.",
"Data Mining - Web Home (Misc)", "http://www.the-data-mine.com/bin/view/Misc/WebHome", "... with the Introduction To Data Mining. Popular pages include: Data Mining Books And ... OnLine Analytical Processing (OLAP) , Data Mining Journals, Data Mining Tutorials, Data Sources ...",
"Data Mining and Data Warehousing", "http://databases.about.com/od/datamining/", "The Net's best collection of data mining and data warehousing links from your About.com guide. From data mining tutorials to data warehousing techniques, you'll find it all! ... Benefits of Outsourcing Data Warehouse and Data Mining. Many organizations are seeking ...",
"MIT OpenCourseWare | Sloan School of Management | 15.062 Data Mining, Spring 2003 | Home", "http://ocw.mit.edu/OcwWeb/Sloan-School-of-Management/15-062Data-MiningSpring2003/CourseHome", "... marts specifically intended for management decision support. Data mining is a rapidly growing field that is ... The field of data mining has evolved from the disciplines of statistics ...",
"Data Management, Exploration and Mining- Home", "http://www.research.microsoft.com/dmx/", "The Data Management Exploration and Mining Group (DMX). ... break down with massive data sets. Therefore, we aim at exploiting data mining techniques, i.e ... Our research effort in data mining focuses on ensuring that traditional techniques ...",
"Data Mining: What is Data Mining?", "http://www.anderson.ucla.edu/faculty/jason.frand/teacher/technologies/palace/datamining.htm", "Outlines what knowledge discovery, the process of analyzing data from different perspectives and summarizing it into useful information, can do and how it works.",
"Data Mining Software", "http://www.megaputer.com/products/pa/index.php3", "Megaputer offers data mining, text mining, and web data mining software tools for e-commerce, database marketing, and CRM; seminars, training and consulting on data mining. Customer ... and versatile suite of advanced data mining tools. PolyAnalyst incorporates the latest ... discovery to analyze both structured and unstructured data. The PolyAnalyst platform offers ...",
"data mining - Webopedia.com", "http://itmanagement.webopedia.com/TERM/D/data_mining.html", "Search for more IT management terms . . . data mining. A class of database applications that look for hidden patterns in a group of data that can be used to predict future behavior. ... For example, data mining software can help retail companies find customers with common interests ... that presents data in new ways. True data mining software doesn't just change the ...",
"datamining2 : Data Mining Club - 1400+ members!!", "http://groups.yahoo.com/group/datamining2/", "... datamining2 ˇ Data Mining Club - 1400+ members ... A forum to discuss data mining and associated fields ...",
"Data Mining", "http://www.stayfreemagazine.org/archives/14/datamining.html", "... is arguably at the cutting edge of 'data mining': a new kind of information analysis that ... positively timid by comparison. Data mining uses artificial intelligence software to hunt ...",
"ONLamp.com -- Data Mining Email", "http://www.onlamp.com/pub/a/onlamp/2004/04/08/datamining_email.html", "Robert Bernier demonstrates how to store data from emails into a database, where you can use data-mining techniques to analyze it. ... What is data mining anyway? Data mining is a class of database applications that look for hidden patterns in a group of data ...",
"2005 SIAM International Conference on Data Mining", "http://www.siam.org/meetings/sdm05/", "2005 SIAM International Conference on Data Mining 2005 SIAM International Conference on Data Mining ...",
"Data Mining", "http://www.stat.rutgers.edu/~madigan/datamining", "... DATA MINING SPECIAL TOPICS CLASS ... will be using a draft version of Principles of Data Mining , by Hand, Mannila, and Smyth (MIT Press, forthcoming), as ...",
"Data Mining", "http://www.willyancey.com/data-mining.htm", "This page provides links about analyzing large files of business data. ... Data Mining. Will Yancey, PhD, CPA ... data analysis, data comparison, data extraction, data mining, data pattern recognition, data quality, data scrubbing ...",
"CRM Today - Data Mining &amp; Data Warehousing", "http://www.crm2day.com/data_mining/", "... an overview of data mining, describes several applications of data mining, details the unique features ...",
"Amazon.com: Data Mining: Practical Machine Learning Tools and Techniques with Java Implementations (The Morgan ... ", "http://www.amazon.com/exec/obidos/tg/detail/-/1558605525?v=glance", "... Topics covered: Data mining and machine learning basics, sample datasets and applications for data mining ... in the synthesis of data mining, data analysis, information theory and ...",
"Data Mining Software &amp; Predictive Modeling Solutions", "http://www.statsoft.com/products/dataminer.htm", "data mining software &amp;amp; predictive modeling sold online by statsoft.com. To learn more about data mining, data mining software &amp;amp; predictive modeling visit statsoft.com ... a selection of automated and ready-to-deploy data mining solutions for a wide variety of business applications ... fully Web-enabled data analysis and data mining system on the market ...",
"Data Mining, Clementine, Predictive Modeling, Predictive Analytics, Predictive Analysis", "http://www.spss.com/clementine", "Data Mining at SPSS. Specializing in clementines, predictive modeling, predictive analytics and predictive analysis ... Clementine, enterprise data mining software from SPSS, enables your organization to improve ... They've integrated data mining into their existing systems and processes, resulting in ...",
"What is data mining? - A Word Definition From the Webopedia Computer Dictionary", "http://www.webopedia.com/TERM/D/data_mining.html", "This page describes the term data mining and lists other pages on the Web where you can find additional information. ... For example, data mining software can help retail companies find customers with common interests ... that presents data in new ways. True data mining software doesn't just change the ...",
"Data Mining Resources", "http://www.cisl.ucar.edu/hps/GROUPS/dm/dm.html", "... and Zantige, D. Data Mining, Harlow, UK: Addison-Wesley, 1996. Berry, M.J.A. and Linoff, G., Data Mining Techniques for Marketing, Sales, and Customer Support, New York, NY: John ...",
"Predictive Data Mining", "http://www.data-miner.com/", "Data-miner software kit (DMSK) for efficient mining of big data.",
"Open Directory - Computers: Software: Databases: Data Mining", "http://dmoz.org/Computers/Software/Databases/Data_Mining/", "... About.com on Data Mining - About.com presents a collection of original feature articles, net ... room dedicated to data mining and data warehousing topics. The Data Mine - Launched ...",
"MicroStrategy - Advanced and Predictive Analysis", "http://www.microstrategy.com/Solutions/BIComponents/Mining.asp", "... to advanced statistical analysis and full data mining capabilities. Our BI technology was designed ... common functionality of statistical and data mining tools in a way that ...",
"Data Mining: Rumour Mull", "http://datamining.typepad.com/data_mining/2005/08/rumour_mull.html", "... Data Mining. About. Galleries ... for 2005-08-15 from Emergence Marketing. Data Mining: Rumour Mull Interesting analysis of the Technorati takeover rumour ...",
"ITSC Data Mining Solutions Center", "http://datamining.itsc.uah.edu/", "... The ITSC Data Mining Solutions Center is the focal point for data mining research, development and services at ...",
"Data Mining Software, Data Mining Applications and Data Mining Solutions", "http://www.spss.com/datamine/", "Data Mining at SPSS. Your source for data mining software, data mining tools, data mining applications and data mining solutions ... Most analysts separate data mining software into two groups: data mining tools and data mining applications. Data mining tools provide ...",
"Data Miners", "http://www.data-miners.com/", "Data mining consultancy; services include predictive modeling, consulting, and seminars.",
"Mining Scienti?c Data", "http://people.cs.vt.edu/~ramakris/papers/scimining.pdf", "... cant impetus to data mining in the scienti?c domain. Data mining is now recognized as a ...",
"Wired News: Why Data Mining Won't Stop Terror", "http://www.wired.com/news/columns/0,70357-0.html?tw=rss.index", "... Many believe data mining is the crystal ball that will enable us to uncover future terrorist plots ... the most wildly optimistic projections, data mining isn't tenable for that purpose ...",
"DMI:Data Mining Institute", "http://www.cs.wisc.edu/dmi", "Data Mining Institute at UW-Madison ... Data Mining Institute. Computer Sciences Department ... The Data Mining Institute (DMI) was started on June 1, 1999 at the Computer Sciences Department by a grant from the ...",
"Data Mining - MSDN Forums", "http://forums.microsoft.com/MSDN/ShowForum.aspx?ForumID=81&amp;SiteID=1", "Search Microsoft.com for: Data Mining. All questions and discussions related to data-mining in SQL Server ... Server -' SQL Server Integration Services -' Data Mining -' SQL Server Reporting Services -' SQL ...",
"NCBI Tools for Bioinformatics Research", "http://www.ncbi.nih.gov/Tools/", "... Tools for Data Mining. PubMed. Entrez. BLAST. OMIM. Books ... results of analyses that have been done on the sequence data. The amount and type of information presented depend ...",
"BYTE.com", "http://www.byte.com/art/9510/sec8/art1.htm", "Archives. Special. About Us. Newsletter. Free E-mail Newsletter from BYTE.com. Jump to... of uses for the relatively young practice of data mining. From analyzing customer purchases to analyzing ... health care to discovering galaxies, data mining has an enormous breadth of ...",
"Software for Data Mining and Knowledge Discovery", "http://www.kdnuggets.com/software", "This is a directory of general-purpose data mining software. To suggest an entry, email to . See also domain-specific data-mining solutions.",
"Data Mining Software | Guide to Data Mining Software", "http://www.data-mining-guide.net/", "What is Data Mining? Data Mining is the process of analyzing large data sets in order to find patterns that can help to isolate key variables to build predictive models for management decision making. ... In essence, data mining helps businesses to optimize their processes so that ...",
"Data Mining", "http://www.sas.com/govedu/edu/services/dmcertificate.pdf", "... Higher Education Consulting. Data Mining. Certificate Program ... A thorough knowledge of the statistical techniques used in data mining is essential ...",
"Integral Solutions Asia", "http://www.datamining.com.sg/", "Offers data mining solutions.",
"Open Directory - Computers: Software: Databases: Data Mining: Public Domain Software", "http://dmoz.org/Computers/Software/Databases/Data_Mining/Public_Domain_Software/", "... Miner - Client-server Java based data mining software for mining association rules ... one million data values). Visual Basic Data Mining .Net - Data Mining applications developed with ...",
"CRM Analytical Data Mining", "http://crm.ittoolbox.com/topics/t.asp?t=520&amp;p=520&amp;h1=520", "... Quality' Model (Line56)- Learning from the past; data mining and Service Quality provide roadmaps, but CRM ... trade-off analysis. Data Mining in Depth: Data Mining and Privacy (DM ...",
"Mining Relational and Nonrelational Data with IBM Intelligent Miner for Data", "http://www.redbooks.ibm.com/redbooks/pdfs/sg245278.pdf", "SG24-5278-00. International Technical Support Organization. http://www.redbooks.ibm.com. Mining Relational and Nonrelational Data with. IBM Intelligent Miner for Data. Using Oracle, SPSS, and SAS As Sample Data Sources",
"Data mining - Nature Biotechnology", "http://www.nature.com/cgi-taf/DynaPage.taf?file=/nbt/journal/v18/n10s/full/nbt1000_IT35.html", "... Data mining has been defined as 'the nontrivial extraction of implicit, previously unknown, and potentially ... the life sciences and healthcare, data mining is a huge industry, with ...",
"Data Mining", "http://www.anderson.ucla.edu/faculty_pages/jason.frand/teacher/technologies/palace/", "Data Mining. Technology Note prepared for Management 274A. Anderson Graduate School of Management at UCLA. Bill Palace. Spring 1996. Overview. This report is organized as follows: ... What is data mining? Describes what data mining can do and how it works ...",
"Data Mining Directory", "http://intertangent.com/023346/Emerging_Technologies/Data_Mining/", "... Intertangent : Emerging Technologies ' Data Mining. The Data Mining category lists cutting edge companies at the forefront of data mining and business intelligence development ...",
"SIAM International Conference on Data Mining", "http://www.siam.org/meetings/sdm02", "Co-Sponsored by AHPCRC and University of Illinois at Chicago ... The field of data mining draws upon extensive work in areas such as; statistics ... presentation of recent results in data mining, including; applications, algorithms, software, and ...",
"US plans massive data sweep | csmonitor.com", "http://www.csmonitor.com/2006/0209/p01s02-uspo.html", "A little-known data-collection system could troll news, blogs, even e-mails. Will it go too far? ... Russell Feingold want details on federal data-mining. AP/FILE ... A major part of ADVISE involves data-mining - or 'dataveillance,' as some call it ...",
"Data Mining White Papers, Webcasts and Product Information from Top IT Vendors", "http://www.bitpipe.com/data/rlist?t=itmgmt_10_40_96&amp;sort_by=status&amp;src=google", "Research the latest Data Mining technologies, tools and techniques. Read white papers, case studies, webcasts and product information from multiple vendors . ... Data Mining. ALSO CALLED: Datamining, Analytics, Data Dredging, Database Analytics, Datamine, and Data-mining. DEFINITION: Data mining is sorting through data ... DATA MINING DEFINITION ...",
"Dorian Pyle, data mining, modeling, analytical CRM, resources - Data Mining Links Page", "http://www.modelandmine.com/links_dmining.htm", "Model + Mine is Dorian Pyle's website, devoted to data mining, modeling and analytical CRM. Dorian wrote the industry-standard Data Preparation for Data Mining. ... - BN PowerPredictor: A data mining system for data modeling/classification/prediction ...",
"Book page", "http://www.cs.sfu.ca/~han/DM_Book.html", "... Jiawei Han and Micheline Kamber. Data Mining: Concepts and Techniques ... of Illinois at Urbana-Champaign: CS497JH 'Data Mining: Concepts and Techniques', Fall 2001, Course general ...",
"Solutions - Data Mining", "http://www.datamystic.com/solutions/data_mining.html", "Solutions - Data Mining. Subscribe. to our newsletter. Please allow active content. Use TextPipe Pro with WebPipe to data mine content from one or more web sites on a scheduled basis: ... Receive our white paper - Data Mining Web Sites. How to extract and harvest useful data from websites such as competitor ...",
"Volume Analytics: Duo-Mining: Combining Data and Text Mining", "http://www.dmreview.com/article_sub.cfm?articleId=1010449", "... As standalone capabilities, the pattern-finding technologies of data mining and text mining have been around for years ... of all, what are data mining and text mining? They are similar ...",
"Data Mining", "http://www.open-mag.com/features/Vol_16/datamining/datamining.htm", "DATA MINING: NAGGING THAT IT REALLY ADDS UP. Software marketers promoting the wonders of data mining often use the Tale of the Diapers to show what data mining can mean to any merchant.",
"Data Mining", "http://www.llnl.gov/str/Kamath.html", "MINING is an arduous, time-consuming business. Sometimes, tons of material must be excavated to uncover ounces of precious metals or gems. The computational equivalent of old-fashioned, down-in-the-dirt mining is data mining. ... is similar. In data mining, trillions of bytes of data must be ...",
"Data Mining Workshop Bookmarks", "http://www.abag.ca.gov/abag/overview/datacenter/popdemo/datamine.htm", "... Data Mining Workshop Bookmarks. Census Bureau. Current Population Survey Main Page. Data Extraction Service - Home Page ...",
"data mining - Webopedia.com", "http://ecrmguide.webopedia.com/TERM/d/data_mining.html", "... For example, data mining software can help retail companies find customers with common interests ... that presents data in new ways. True data mining software doesn't just change the ...",
"Weka 3 - Data Mining with Open Source Machine Learning Software in Java", "http://www.cs.waikato.ac.nz/ml/weka/", "... Weka 3: Data Mining Software in Java ... is a collection of machine learning algorithms for data mining tasks. The algorithms can either be applied directly to ...",
"Advanced Navigation - CIO Magazine May 15, 1998", "http://www.cio.com/archive/051598_mining_content.html", "Marketing secrets from the financial sector show how data mining charts a profitable course to customer management ... Data mining is the automated analysis of large data sets to find patterns and trends that might otherwise ... Bank in New York. Data mining has become an indispensable tool in ...",
"Elder Research", "http://www.datamininglab.com/", "Provides consulting and short courses in data mining and pattern discovery patterns in data.",
"DATA MINING 2004", "http://www.wessex.ac.uk/conferences/2004/datamining04", "... Fifth International Conference on Data Mining, Text Mining and their Business Applications ... 5th International Conference on Data Mining, Text Mining and their Business Applications ...",
"Data Mining - An Introduction Introduction, QUB", "http://www.pcc.qub.ac.uk/tec/courses/datamining/ohp/dm-OHP-final_2.html", "... Data mining is the analysis of data and the use of software techniques for finding patterns and ... in unexpected places as the data mining software extracts patterns not previously ...",
"DATA QUALITY MINING", "http://www.cs.cornell.edu/johannes/papers/dmkd2001-papers/p5_hipp.pdf", "... The goal of DQM is to employ data mining methods in order to ... opens new and promising application ?elds for data mining. methods outside the ?eld of pure data analysis ...",
"Data Mining Course", "http://www.cs.rpi.edu/~zaki/dmcourse", "... CSCI-4390/6390 Data Mining. Fall 2004 ... Sep 2  Exploratory Data Anaysis (EDA) Sep 6  NO CLASS (labor day ...",
"Statoo Consulting, Statistical Consulting + Data Analysis + Data Mining Services, Lausanne, Switzerland", "http://www.statoo.com/sections/Datamining", "Statoo Consulting is a vendor independent Swiss consulting firm specialized in statistical consulting and training, data analysis, data mining, analytical CRM and bioinformatics services. ... Statistical Consulting + Data Analysis + Data Mining Services. Lausanne, Switzerland. Methodological Training in Statistical Data Mining, September 4-6, 2006, Baden, Switzerland ...",
"Data Mining - eWEEK Web Buyer's Guide", "http://buyersguide.eweek.com/product/SearchResults.asp_Q_sitename_E_eweek_database_A_cboCategory_E_86", "... Data Mining. Research the latest in Data Mining technologies, tools and techniques ... Search within Data Mining. Browse by name: A B C D E F G H I J K ...",
"Discovering the Hidden Secrets in Your Data - the Data Mining Approach to Information", "http://informationr.net/ir/3-2/paper36.html", "Discovering the Hidden Secrets in Your Data - the Data Mining Approach to Information Discovering the Hidden Secrets in Your Data - the Data Mining Approach to Information Nowadays, digital information is relatively easy to capture and fairly ...",
"Insightful Corporation", "http://www.insightful.com/", "The developer of the technical calculation application Mathcad, as well as developer and provider of a variety of other software tools for users of PCs, Macintosh computers, and UNIX workstations.",
"Survey of Clustering Data Mining Techniques", "http://www.ee.ucr.edu/~barth/EE242/clustering_survey.pdf#search='data%20mining%20techniques'", "Survey designed to provide a comprehensive review of different clustering techniques in data mining.",
"The Java Community Process(SM) Program - JSRs: Java Specification Requests - detail JSR# 73", "http://jcp.org/en/jsr/detail?id=73", "... and maintain data and metadata supporting data mining models, data scoring, and data mining results serving J2EE ... agreed upon, standard API for data mining. By using JDMAPI ...",
"ACM SIGKDD: Welcome", "http://www.kdd.org/", "Data Mining",
"DSC 8330 : Data Mining", "http://nargund.com/gsu/mgs8040/", "... MGS 8040: Data Mining. Tentative Schedule for Spring 2006 ... Week 4: 2/6. Data Aggregation, Description. (Charts, Crosstabs, Dummies ...",
"The Serendip Home Page", "http://www.bell-labs.com/project/serendip", "Serendip - data mining system. ... The Serendip Data Mining Project. Minos Garofalakis. Rajeev Rastogi ... Welcome to the Serendip Data Mining Project home page ...",
"Data Mining", "http://www.peralta.cc.ca.us/indev/ncrp/ncrp3_5.pdf", "... Data Mining. Data Mining , the 'process of extracting valid ... served by exploring data mining software to extend their ...",
"Data Mining : DecisionCraft Analytics", "http://www.decisioncraft.com/dmdirect/datamining.htm", "DecisionMakers' Direct is a fortnightly ezine that brings cutting edge tools, technologies and ideas to the notice of top decision-makers in business. ... Extensive knowledge of data mining tools, advanced statistics and modeling expertise. A data mining vision that includes willingness to commit time ...",
"ACM SIGKDD", "http://www.acm.org/sigs/sigkdd", "Association for Computing Machinery special interest group and forum for advancement and adoption of the science of knowledge discovery and data mining. Also includes an online version of their newsletter, SIGKDD Explorations, that is published twice yearly.",
"TIME.com: Data Miners -- Dec. 23, 2002 -- Page 1", "http://www.time.com/time/globalbusiness/article/0,9171,1101021223-400017,00.html", "New software instantly connects key bits of data that once eluded teams of researchers ... The data-mining algorithms of ClearForest, based in New York City, are at work within both ... And these days, data-mining software, combined with technologies that connect disparate ...",
"Data Mining People &amp; Papers", "http://www.cs.umd.edu/users/nfa/dm_people_papers.html", "Links to homepages of people in data mining. Data mining papers (some of which are online available). ... DATA MINING PEOPLE &amp;amp; PAPERS. NOTE: This page has not been updated for years ... Subspace Clustering of High Dimensional Data for Data Mining Applications. SIGMOD Conference 1998: 94-105 ...",
"DM II - Data Mining II", "http://www.comp.nus.edu.sg/~dm2", "The DM-II system has two downloadable tools: CBA (v2.1) and IAS. CBA (v2.1) (Last Modify June, 25, 2001) is a data mining tool developed at School of Computing, National University of Singapore. ... Integrating Classification and Association Rule Mining' (KDD-98). Further improvements were made ...",
"New Architect: Features", "http://www.webtechniques.com/archives/2000/01/greening/", "... New Architect &amp;gt; Archives &amp;gt; 2000 &amp;gt; 01 &amp;gt; Features. Data Mining on the Web ... To solve this problem, marketers and business analysts use data-mining techniques ...",
"Features", "http://www.microsoft.com/sql/prodinfo/features/default.mspx", "Learn more about the hundreds of features added or updated in SQL Server 2005 ... advantage out of your enterprise data. Industry-leading support for enterprise data management, developer productivity, and ... from an enterprise-class data system. Learn more ...",
"' Biotech data mining | Emerging Technology Trends | ZDNet.com", "http://blogs.zdnet.com/emergingtech?p=120", "blogs. December 31, 2005. Biotech data mining. Posted by Roland Piquepaille @ 9:49 am. In the last ten years, biotech companies have been busy accumulating mountains of data. ... has started the BioGrid project. In Mining biotech's data mother lode, IST Results describes this ...",
"Data-Centric Automated Data Mining", "http://www.oracle.com/technology/products/bi/odm/pdf/automated_data_mining_paper_1205.pdf", "... Data-Centric Automated Data Mining. Marcos M. Campos. Oracle Data Mining ... This has limited the adoption of data mining at large. and in the database and business intelligence (BI ...",
"Data Mining", "http://www.pwcglobal.com/images/au/actuarial/DataMiningFeature.pdf", "Data Mining. PricewaterhouseCoopers. Confidential 2. Client name if required, typeset in 8pt, colour as rule. Data Mining. The label 'Data Mining' has become one of today's. fashionable buzz phrases. ... and are, at the same time, very fast. Data mining is defined by the new technologies used ... should not be. considered data mining. Today's data mining techniques. look in a ...",
"The Washington Monthly", "http://www.washingtonmonthly.com/archives/individual/2006_02/008183.php", "... February 9, 2006. DATA MINING....Suddenly, data mining is everywhere ... NSA's domestic spying program, which most likely involves data mining of some kind, but apparently good 'ol TIA ...",
"Data Mining - Case Study", "http://icarus.math.mcmaster.ca/peter/sora/case_studies_00/data_mining.html", "Data Mining - Case Study. Please check this page regularly for updates, corrections, and answers to frequently-asked questions! Introduction ... The statistical techniques of data mining are familiar. They include linear and logistic regression, multivariate ...",
"Mining Software Engineering Data", "http://www.dacs.dtic.mil/techs/datamining/datamining.pdf", "... Mining Software Engineering Data: A Survey ... database is called data mining. Formally, data mining has been ...",
"Deep Market Advanced Stock Market Analysis", "http://www.deepmarket.com/", "Think outside the Black Box. Advanced Stock Market Analysis. Stock market analysis using artificial intelligence, machine learning data mining techniques. Thanks a Ton for a Link! Concerned about climate change? ... Climate Change. Creativity. Data Mining. Distributed Computing. Links. Machine Learning ...",
"DATA MINING LABORATORY, UTCS", "http://www.cs.utexas.edu/users/dml", "Data Mining Laboratory. DATA MINING LAB. RELATED LINKS. UT LINKS. Others. Overview",
"Regional Review: Mining Data", "http://www.bos.frb.org/economic/nerr/rr2000/q3/mining.htm", "Mining Data. Quarter 3, 2000. by Miriam Wasserman. SCENE 1: It's late November 1999. The Celtics are struggling with their second lineup. ... They both include the use of data-mining computer technology to search for patterns in data ... player's potential is maximized. Although data mining by itself is not going to get ...",
"Data Mining and Homeland Security: An Overview", "http://www.fas.org/sgp/crs/intel/RL31798.pdf", "... assessing risk, and product. retailing, data mining involves the use of data analysis tools to discover ... homeland security, data mining can be a potential means to identify terrorist ...",
"DBMS - DBMS Data Mining Solutions Supplement", "http://www.dbmsmag.com/9807m01.html", "DBMS, Data Mining Solutions Supplement. As recently as two years ago, data mining was a new concept for many people. Data mining products were new and marred by unpolished interfaces.",
"Data Mining for Java", "http://www.oracle.com/technology/products/bi/odm/9idm4jv2.html", "... Oracle Data Mining for Java. Oracle Data Mining for Java (DM4J) works with Oracle9i Data Mining ...",
"Trends in Spatial Data Mining", "http://www.cs.umn.edu/research/shashi-group/paper_ps/dmchap.pdf", "Trends in Spatial Data. Mining. Shashi Shekhar. , Pusheng Zhang. , Yan Huang. , Ranga. Raju Vatsavai. Department of Computer Science and Engineering, University of Minnesota. 4-192, 200 Union ST SE, Minneapolis, MN 55455. Abstract ... Spatial data mining is the process of discovering interesting and previously un ... guish spatial data mining from classical data mining. Major accomplishments ...",
"Using DB2 Intelligent Miner for Data", "http://www.redbooks.ibm.com/pubs/pdfs/redbooks/sg246274.pdf", "ibm.com/redbooks. Mining Your Own. Business in Health Care. Using DB2 Intelligent Miner for Data. Corinne Baragoin. Christian M. Andersen. Stephan Bayerl. Graham Bent. Jieun Lee. Christoph Schommer. Exploring the health care business. issues",
"Explainer: Data mining", "http://www.computerworld.com/databasetopics/businessintelligence/story/0,10801,103726,00.html?source=x10", "Often used for predictive modeling, data mining is a subset of business intelligence that can help organizations better understand relationships among variables. ... it into usable shape, however, requires sophisticated data mining tools. The same technology that police departments ...",
"Data Mining Software in the Yahoo! Directory", "http://dir.yahoo.com/Business_and_Economy/Business_to_Business/Computers/Software/Databases/Data_Mining/", "Yahoo! reviewed these sites and found them related to Data Mining Software ... Manufactures multi-strategy data mining and text mining software solutions ... www.PredictiveDynamix.com. Predictive Data Mining Predictive Data Mining. Data-miner software kit (DMSK) for ...",
"Data Mining: The Xbox Files", "http://www.pcmag.com/article2/0,4149,1118791,00.asp", "Uncovering valuable patternsfrom homeland security to corporate marketing. ... Effective data mining is all about connecting the dots ... and their customers, the less they know what to do about it,' says Usama Fayyad, CEO of data-mining company digiMine ...",
"Togaware: Data Mining Catalogue", "http://www.togaware.com/datamining/catalogue.html", "freedom in anyone's language. Vrijheid Liberté Freiheit ????????? ??????? Bebas Libertad. Supporting. Hosting. About Us. Featured. Data Mining Catalogue ... Many Data Mining tools, vendors, and service providers are now available to service the Data Mining market. This Catalogue provides pointers to Data Mining tool vendors ...",
"IBM Software - DB2 Intelligent Miner - Family Overview", "http://www-306.ibm.com/software/data/iminer/", "The DB2 Intelligent Miner Family helps you identify and extract high-value business intelligence from your business data. ... IBM's data mining capabilities help you detect fraud, segment your customers, and simplify market ... analysis without moving your data into proprietary data mining platforms. Use SQL ...",
"INFT 979", "http://www.galaxy.gmu.edu/stats/syllabi/inft979.wegman.html", "... 'Data Mining' has become a buzz-word within the computer industry for extraction of knowledge or information ... The idea of data mining is to look for information which may ...",
"SQL Server Developer Center: Frequently Asked Questions: Data Mining", "http://msdn.microsoft.com/sql/sqlwarehouse/dmfaq.aspx", "... 3. What is data mining? Simply put, data mining is the process of exploring ... the historical data, referred to as a data mining model. After a data mining model is ...",
"Data mining software for Windows and Solaris | Insightful Miner", "http://www.insightful.com/products/iminer/", "Insightful's consulting group and S-PLUS software provide premier data mining and business intelligence solutions for finance, biopharm, manufacturing, e-business, government (GIS) and academia. ... Insightful Miner...has the best selection of ETL functions of any data mining tool on the market ... Miner is a powerful, scalable, data mining and analysis workbench that enables ...",
"Business Intelligence and Data Warehousing Insight Portal", "http://www.datawarehousingonline.com/", "A new era of e-business insight portal featuring Data Warehousing, Business Intelligence, Web Analytics, Analytical Applications and many more to come. ... Data Warehousing 101. Data Mining An Introduction ... Featured Insights. Metrics 2.0: Data-Driven Market Insights ...",
"E-commerce Technology: Data Mining", "http://ecommerce.ncsu.edu/technology/topic_Datamining.html", "Data Mining. Think of the amount of information that Amazon.com has to store to keep track of its 20 million customers---multiple addresses, credit cards, purchase history, ratings of books for its recommendation service, and more. ... from a data source is called data mining. The field has grown ...",
"TAP: Web Feature: Data Debase. by Max Blumenthal. December 19, 2003.", "http://www.prospect.org/webfeatures/2003/12/blumenthal-m-12-19.html", "... The powerful technology known as data mining -- and how, in the government's hands, it could ... Data-mining advocates within the law-enforcement and intelligence communities claim the ...",
"Data Mining", "http://www.unf.edu/~selfayou/html/data_mining.html", "... what we have collected. Data mining is the science of analyzing data to discover hidden ... This course approaches data mining topics from an Artificial Intelligence/Machine Learning ...",
"Using analytic services data mining", "http://dev.hyperion.com/resource_library/white_papers/Data_Mining_WP.pdf", "... focuses on using Naive Bayes, one of the Data. Mining algorithms (shipped in-the-box with Analytic ... and the Analytic Services. Data Mining Framework in particular, towards arriving at ...",
"Data mining with association rules using WebSphere Commerce Analyzer", "http://www.ibm.com/developerworks/websphere/library/techarticles/0411_poulin/0411_poulin.html", "... knowledge from large amounts of data. Data mining is a multidisciplinary field with many techniques ... of the techniques used in data mining, and particularly useful with e-commerce ...",
"Business Intelligence Network: BI &amp; Data Warehousing Resources", "http://www.b-eye-network.com/spotlights", "Business Intelligence Network delivers business intelligence, data warehousing and analytics resources provided by Claudia Imhoff, Bill Inmon and other experts. Additional topics include data quality, data integration, CRM, data marts, data ... Compliance Topic: Data Acquisition Topic: Data Management Topic ... more timely data combined with historical data to better ...",
"data mining", "http://www.cse.iitk.ac.in/users/pmitra/cs698v.html", "... CS698V: Data Mining. (Semester II, 2003-2004 ... Data Mining is an information extraction activity whose goal is to discover hidden facts contained in databases ...",
"GAO-04-548 Data Mining: Federal Efforts Cover a Wide Range of Uses", "http://www.gao.gov/new.items/d04548.pdf", "... Federal agencies are using data mining for a variety of purposes, ranging ... on their use of data mining shows that 52 agencies are using or are planning ...",
"NAG Data Mining Components", "http://www.nag.co.uk/numeric/DR/drdescription.asp", "Numerical Algorithms Group. More about NAG DMC. Need to know more about NAG Data Mining and Cleaning Components? Click on one of the following links or contact us to discuss your needs. NAG Data Mining Components ... performance and significantly reduce development time. Data mining plays an essential part in applications in a ...",
"Data Mining Tool (DMT) Version 3.0", "http://www.affymetrix.com/support/technical/datasheets/dmt_datasheet.pdf", "... Affymetrix. Data Mining Tool (DMT) Version 3.0 ... never been easier. Data Mining Tool. (DMT) software contains a variety of ...",
"580Syllabus", "http://www.cs.ccsu.edu/~markov/ccsu_courses/DataMining.html", "... Description: Data Mining studies algorithms and computational paradigms that allow computers to find ... The students will use recent Data Mining software. Prerequisites: CS 501 and ...",
"Data Mining 101: Finding Subversives with Amazon Wishlists | Applefritter", "http://www.applefritter.com/bannedbooks", "... 'Data mining' of all that information and communication is at the heart of the furor over the ... Combining a data mining operation with the Patriot Act's power to access ...",
"DATA MINING", "http://cseserv.engr.scu.edu/StudentWebPages/hchhay/hchhay_FinalPaper.htm", "... With the increased and widespread use of technologies, interest in data mining has increased rapidly. Companies are now utilized data mining techniques to exam their database looking ...",
"KDD 2001 Workshop on Temporal Data Mining", "http://www.acm.org/sigs/sigkdd/kdd2001/Workshops/TemporalMiningWorkshop.html", "... KDD 2001 Workshop on Temporal Data Mining. To be held in conjunction with the. 7th ACM SIGKDD International Conference on Knowledge Discovery and Data Mining (KDD-2001 ...",
"NIOSH Mining: MSHA Data File Downloads | CDC/NIOSH", "http://www.cdc.gov/niosh/mining/data/", "MSHA accident, injury, employment, and production data files in SPSS and dBase formats ... See also: Mining statistics. Data files on mining accidents, injuries, fatalities, employment, production ... 30cfr/50.0.htm). Original raw data files are released periodically to the ...",
"Teradata Data Mining Warehouse Solution", "http://www.teradata.com/t/go.aspx?id=106002", "... tasks against their data warehouse. The Teradata Data Mining Warehouse Solution provides a high ... warehouse that streamlines the data mining process. Data are centralized in the ...",
"Mining Data Streams Bibliography", "http://www.csse.monash.edu.au/~mgaber/WResources.htm", "... Mining Data Streams Bibliography. Maintained by: Mohamed Medhat Gaber ... on Knowledge Discovery and Data Mining (KDD'04), Seattle, WA, Aug ...",
"Microsoft's Plan To Bring Data Mining To Masses - Technology News by TechWeb", "http://www.techweb.com/wire/story/TWB19990525S0001", "May 25, 1999 (12:00 AM EDT) Microsoft's Plan To Bring Data Mining To Masses. Microsoft's Plan To Bring Data Mining To Masses. By Shawn Willett, Microsoft Corp. ... company formally announced its OLE DB for Data Mining API, which aims to create a standard ... information for data-mining applications and a standard 'model' for data-mining data ...",
"SSRN-Data Matching, Data Mining, and Due Process by Daniel Steinbock", "http://papers.ssrn.com/sol3/papers.cfm?abstract_id=763504", "... are using or planning to use data matching and data mining, in a total of 199 programs, some ... all anti-terrorist data matching and data mining decisions is the absence ...",
"Orange", "http://www.ailab.si/orange", "... Orange is a component-based data mining software. It includes a range of preprocessing ... Although many data mining suites now incorporate visual programming, Orange widgets are ...",
"Mining Marketing Data", "http://www.sti.nasa.gov/tto/spinoff2002/ct_8.html", "Mining Marketing Data. MarketMinerŽ software produces useful marketing information geared specifically toward the needs of marketing and sales professionals. ... Data mining is a process that uses various statistical and pattern recognition techniques to ... further by combining data mining techniques with data analysis and business intelligence ...",
"Glossary", "http://www.sdgcomputing.com/glossary.htm#DWManagement", "... because the Microsoft SQL Server 2000 version included data mining capabilities as well as the OLAP capabilities ... can be used for standard reports, for OLAP, and for data mining ...",
"Data Warehousing Review - Text Mining", "http://www.dwreview.com/Data_mining/text_mining.html", "This article gives an overview of Text Mining and also describes an application the author is developing for Text Mining ... and the overall complexity of utilizing data mining has been a hurdle to text mining that has been ... This can greatly help the data mining process and is termed text ...",
"Boston Data Mining - Home", "http://www.bostondatamining.com/", "Boston Data Mining is an independent consulting firm specializing in statistical consulting, data analysis, survey analysis, data mining, and analytical CRM.",
"data mining definition - Small Business Computing Online Dictionary of IT Terms: Powered by Webopedia", "http://sbc.webopedia.com/TERM/D/data_mining.html", "... For example, data mining software can help retail companies find customers with common interests ... that presents data in new ways. True data mining software doesn't just change the ...",
"GAO-05-866 Data Mining: Agencies Have Taken Key Steps to Protect Privacy in Selected Efforts, but Significant ... ", "http://www.gao.gov/new.items/d05866.pdf", "... The five data mining efforts we reviewed are used by federal agencies to ... individual privacy rights are being appropriately protected. Data mininga technique for ...",
"The Data Miner: SQL Server Data Mining News", "http://www.sqlserverdatamining.com/DMCommunity/Newsletter/default.aspx", "... sql server | data mining. Search: Go ... issue of 'The Data Miner' - the official newsletter of SQL Server Data Mining. This periodic newsletter keeps you up ...",
"Center for Data Insight (CDI) - Northern Arizona University", "http://www.insight.nau.edu/", "A cooperative effort between vendors of data mining and knowledge discovery products and Northern Arizona University, CDI conducts advanced research for business, government, and scientific needs in knowledge discovery in databases (KDD).",
"Mary D. Taffet's Home Page: WWW Sites for Students of Data Mining", "http://web.syr.edu/~mdtaffet/Data_Mining_sites.htm", "This webpage was put together in a very short time, so is far from exhaustive. ... was originally created for a workshop on Data Mining presented by my advisor Elizabeth Liddy in ... KDnuggets: Data Mining, Web Mining, Knowledge Discovery and CRM Guide -- this is ...",
"CS 456 Page", "http://www.cwu.edu/~borisk/456", "... CS 456 - DATA MINING. Instructor Dr. Boris Kovalerchuk ... Education in Data Mining and Knowledge Discovery. CoIL Challenge 2000 &amp;lt;see CS456 on alp ...",
"Privacy-preserving Data Mining References", "http://privacy.cs.cmu.edu/dataprivacy/papers/ppdm", "... Privacy-preserving Data Mining. Data mining techniques are used to find patterns in large databases of ... The notion of privacy-preserving data mining is to identify and disallow ...",
"Oracle Business Intelligence Data Mining", "http://oracle.ittoolbox.com/topics/t.asp?t=427&amp;p=427&amp;h1=427", "... Business Intelligence &amp;gt; Data Mining. Definition: Data Mining is a method of searching data with mathematical ... product evaluation process for Data Mining software. Oracle-BI-l - The ...",
"Data Mining Server", "http://dms.irb.hr/", "... Welcome to Data Mining Server. Data mining server (DMS) is an internet service for online data analysis based on ...",
"Data Mining Software - StarProbe Data Miner", "http://www.roselladb.com/starprobe.htm", "Rosella Data Mining &amp;amp; Database Analytics. StarProbe Visual Data Miner Suite. Enterprise-scale premium data mining and intelligent statistical analytic tools! ... (Add these tools to your currently existing data mining ranges! These tools can complement the limitations of your ...",
"PC AI - Data Warehouse and Data Mining", "http://www.pcai.com/web/ai_info/data_warehouse_mining.html", "... Data Mining. Overview: Data mining or knowledge discovery is becoming more important as more and more ... Data Warehouse and Data Mining Information on the Internet. Data Mining 4 U ...",
"Data Mining - CSE5230", "http://www.csse.monash.edu.au/courseware/cse5230/2003/assets/images/week04.pdf", "... CSE5230 - Data Mining, 2003. Lecture 4.1. Data Mining - CSE5230. Data Mining and Statistics ...",
"Data Mining and Data Warehousing", "http://www.arl.org/spec/SPEC274WebBook.pdf", "Data Mining and Data Warehousing. SPEC K. ITS. Supporting Effective Library Management for Over Thirty Years. Committed to assisting research and academic libraries in the continuous improvement of management",
"Data mining and data-driven modelling", "http://datamining.ihe.nl/", "The objectives of this Web site are: to introduce the main methods of data mining and data-driven modelling and their applications in the industry ... Welcome to the Data Mining &amp;amp; Data-Driven Modelling (DDM) website ... in the framework of the of the Delft Cluster project 'Data mining, knowledge discovery and data-driven modelling ...",
"XLMiner - Data Mining", "http://www.resample.com/xlminer", "XLMiner Version 3 is now available, with new time series analysis, plus the ability to save models for later review, plus the ability to score saved models to new data. ... Data mining in Excel: XLMiner is the only comprehensive data mining add-in for Excel, with neural nets ...",
"Affymetrix - Data Mining Tool (DMT)", "http://www.affymetrix.com/products/software/specific/dmt.affx", "... Data Mining Tool (DMT) The Data Mining Tool (DMT) software provides a variety of tools for filtering and ...",
"AusDM04 Australasian Data Mining Conference", "http://www.togaware.com/ausdm04", "The Australasian Data Mining Conference. 6 December 2004. Cairns Convention Centre, Cairns, Queensland, Australia. In conjunction with the 17th. Australian Joint Conference on Artificial Intelligence. (AI'2004) ... The Australasian Data Mining Conference is devoted to the art and science of intelligent data mining: the meaningful ...",
"FM: Data Mining Solutions and the Establishment of a Data Warehouse: Corporate Nirvana for the 21st Century?", "http://www.firstmonday.dk/issues/issue2_5/maxwell", "Addresses the need to broaden the meaning of data mining and data warehousing to encompass information mining and knowledge retrieval into complex adaptive systems with the business end user in mind. ... authors contend that if data mining and data warehousing are to become ...",
"Oracle Ž Data Mining", "http://wtcis.wtamu.edu/oracle/datamine.101/b10697/toc.htm", "Oracle Data Mining Administrator's Guide. 10g Release 1 (10.1) Part Number B10697-01. OracleŽ Data Mining. Administrator's Guide. 10g Release 1 (10.1) December 2003. Part No. B10697-01. 1 Introduction ... This document describes how to install the Oracle Data Mining (ODM) software and how to perform other administrative ...",
"Intelligence obtained by applying data mining to a database of French theses on the subject of Brazil", "http://informationr.net/ir/7-1/paper117.html", "... DocThéses, comprising the years 1969 -1999. The data mining technique was used to obtain intelligence and infer ... The technique of data mining is divided into stages which go from ...",
"Large-Scale Data Mining", "http://www.cs.rpi.edu/~zaki/LSDM", "Special Session on Large-Scale Data Mining. 7th International Conference on High Performance Computing ( HiPC2000) December 17-20, 2000 --- Bangalore, India. Session Chairs: Gautam Das. Microsoft Research. One Microsoft Way. Redmond WA 98052 ... The field of Data Mining (or Knowledge Discovery in Databases) attempts to ...",
"Oracle Data Mining Concepts", "http://www.stanford.edu/dept/itss/docs/oracle/10g/datamine.101/b10698.pdf", "OracleŽ Data Mining. Concepts. 10g. Release 1 (10.1) Part No. B10698-01. December 2003. Oracle Data Mining Concepts, 10g Release 1 (10.1) Part No. B10698-01. Copyright Š 2003 Oracle. All rights reserved.",
"Distributed Data Mining Bibliography", "http://www.cs.umbc.edu/~hillol/DDMBIB/ddmbib.pdf", "Distributed Data Mining Bibliography. Kun Liu, Hillol Kargupta, Kanishka Bhaduri and Jessica Ryan. Computer Science and Electrical Engineering Department. University of Maryland Baltimore County. Baltimore, Maryland 21250 ... data sources require a data mining technology designed for distributed applications ...",
"SpringerLink - Publication", "http://www.springerlink.com/(wqdvrz45hso1otug4rg3aj2c)/app/home/journal.asp?referrer=parent&amp;backto=linkingpublicationresults,1:100254,1", "Articles Publications Publishers. Publication. Data Mining and Knowledge Discovery. Publisher: Springer Netherlands ... Papers from the Eighth ACM SIGKDD International Conference on Knowledge Discovery and Data Mining Part II ...",
"Fractal Dimension for Data Mining", "http://www.cald.cs.cmu.edu/Education/masters/skkumar_kdd_project.pdf", "... Fractal Dimension for Data Mining. Krishna Kumaraswamy ... show how this can be used to aid in several data mining tasks. We are interested in answering ...",
"Data Mining", "http://www.knightsbridge.com/solutions/client/professional/requirements/mining.php", "... Data mining is a powerful data warehousing technology to assist users with the abundance ... that they have collected. Data mining uses sophisticated statistical analyses and modeling ...",
"Knowledge Management - Data Storage &amp; Mining - Warehouse, OLAP, glossary resources - Knowledge Management RC - CIO", "http://www.cio.com/research/data/data_mining.html", "CIO Data Storage &amp;amp; Mining Research Center is a compilation of articles, case studies, organizations, conferences, glossary of terms, and white papers related to data storage, mining/OLAP, and data warehousing.",
"PHENOMENAL DATA MINING: FROM DATA TO PHENOMENA", "http://www-formal.stanford.edu/jmc/data-mining/data-mining.html", "... PHENOMENAL DATA MINING: FROM DATA TO PHENOMENA ... income distribution, and sensitivity to price changes. A data mining program might be able to identify which baskets of ...",
"Azmy Thinkware Inc.", "http://www.azmy.com/", "Publishes database query and analysis tools.",
"Data Mining", "http://www.unc.edu/~xluan/258/datamining.html", "Data Minning. What is data mining? Data Mining is the process of extracting knowledge hidden from large volumes of raw data.The knowledge must be new, not obvious, and one must be able to use it. ... is termed data mining. Data mining finds these patterns and relationships using data analysis tools ...",
"Data Mining", "http://scianta.com/technology/datamining.htm", "... Decision Trees use a form of data mining to find the rules that classify a collection of data ... form of deep data mining to isolate patterns in data and then ...",
"The New York Times &amp;gt; National &amp;gt; Survey Finds U.S. Agencies Engaged in 'Data Mining'", "http://www.nytimes.com/2004/05/27/national/27privacy.html?ex=1401076800&amp;en=5671e4c741290d53&amp;ei=5007&amp;partner=USERLAND", "A survey of federal agencies has found more than 120 programs that collect and analyze large amounts of personal data on individuals to predict their behavior. ... These agencies reported 199 data mining projects, of which 68 were planned and 131 were in operation ... by the high number of data mining activities in the federal government involving ...",
"Data warehousing and data mining Course", "http://www.it.iitb.ac.in/~sunita/it642", "... IT642: Data warehousing and data mining course. Spring 2002 ... Ramasamy Uthurusamy, editors, Advances in Knowledge Discovery and Data Mining, chapter 12, pages 307-328. AAAI/MIT Press ..."
      
);

for ($i = 0; $i < count($results); $i += 3) {
  $title = $results[$i];
  $url = $results[$i + 1];
  $snippet = $results[$i + 2];
  $carrot->addDocument($url, strip_tags($title), strip_tags($snippet));
}

$clusters = $carrot->clusterQuery("data mining");

if (isset($clusters)) {
    echo "PASSED (" . count($clusters) . " clusters).";
    echo "</p>";
    
    echo '<pre>';
    for ($i = 0; $i < count($clusters); $i++) {
        echo $i . ": " . $clusters[$i]->label . " (" . count($clusters[$i]->documents) . ")\n";
    }
    echo '</pre><p>';
} else {
    echo "FAILED (no clusters object);";
}

echo "</p>";
flush();
?>

<p class="head">DONE.</p>

</body>
</html>
