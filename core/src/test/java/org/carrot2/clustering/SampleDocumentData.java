/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.clustering;

import java.util.*;

/**
 * A set of sample documents returned for the query <i>data mining</i>. This set is hard-coded so
 * that no other external components are needed to run tests (i.e., circular dependency between the
 * XML input component and the core).
 */
public final class SampleDocumentData {

  public static final List<Document> DOCUMENTS_DATA_MINING;

  static {
    final String[][] data = {
      {
        "http://en.wikipedia.org/wiki/Data_mining",
        "Data mining - Wikipedia, the free " + "encyclopedia",
        "Article about knowledge-discovery in databases (KDD), the practice of automatically "
            + "searching large stores of data for patterns."
      },
      {
        "http://www.ccsu.edu/datamining/resources.html",
        "CCSU - Data Mining",
        "A collection of Data Mining links "
            + "edited by the Central Connecticut State University ... Graduate Certificate Program. Data Mining "
            + "Resources. Resources. Groups ..."
      },
      {
        "http://www.kdnuggets.com/",
        "KDnuggets: Data Mining, Web Mining, and Knowledge Discovery",
        "Newsletter on "
            + "the data mining and knowledge industries, offering information on data mining, knowledge discovery, text"
            + " mining, and web mining software, courses, jobs, publications, and meetings."
      },
      {
        "http://en.wikipedia.org/wiki/Data-mining",
        "Data mining - Wikipedia, the free encyclopedia",
        "Data mining "
            + "is considered a subfield within the Computer Science field of knowledge discovery. ... claim to perform "
            + "\"data mining\" by automating the creation ..."
      },
      {
        "http://www.anderson.ucla.edu/faculty/jason.frand/teacher/technologies/palace/datamining.htm",
        "Data Mining:" + " What is Data Mining?",
        "Outlines what knowledge discovery, the process of analyzing data from different"
            + " perspectives and summarizing it into useful information, can do and how it works."
      },
      {
        "http://www.the-data-mine.com/",
        "Data Mining - Home Page (Misc)",
        "Provides information about data mining "
            + "also known as knowledge discovery in databases (KDD) or simply knowledge discovery. List software, "
            + "events, organizations, and people working in data mining."
      },
      {
        "http://www.spss.com/data_mining/",
        "Data Mining Software, Data Mining Applications and Data Mining " + "Solutions",
        "... complete data mining customer ... Data mining applications, on the other hand, embed .."
            + ". it, our daily lives are influenced by data mining applications. ..."
      },
      {
        "http://datamining.typepad.com/data_mining/",
        "Data Mining: Text Mining, Visualization and Social Media",
        "Commentary on text mining, data mining, social media and data visualization. ... Opinion Mining Startups"
            + " ... in sentiment mining, deriving tuples of ..."
      },
      {
        "http://www.statsoft.com/textbook/stdatmin.html",
        "Data Mining Techniques",
        "Outlines the crucial concepts "
            + "in data mining, defines the data warehousing process, and offers examples of computational and graphical"
            + " exploratory data analysis techniques."
      },
      {
        "http://answers.yahoo.com/question/index?qid=1006040419333",
        "<b>answers.yahoo.com</b>/question/index?qid" + "=1006040419333",
        "Generally, data mining (sometimes called data or knowledge discovery) is the ... "
            + "Midwest grocery chain used the data mining capacity of Oracle software to ..."
      },
      {
        "http://www.ccsu.edu/datamining/master.html",
        "CCSU - Data Mining",
        "Details on how to apply to the Master "
            + "of Science in data mining may be found here. ... All data mining majors are classified for business "
            + "purposes as part-time ..."
      },
      {
        "http://databases.about.com/od/datamining/a/datamining.htm",
        "Data Mining: An Introduction",
        "About.com "
            + "article on how businesses are discovering new trends and patterns of behavior that previously went "
            + "unnoticed through data mining, automated statistical analysis techniques."
      },
      {
        "http://www.thearling.com/",
        "Data Mining and Analytic Technologies (Kurt Thearling)",
        "Kurt Thearling's "
            + "site dedicated to sharing information about data mining, the automated extraction of hidden predictive "
            + "information from databases, and other analytic technologies."
      },
      {
        "http://www.sas.com/technologies/analytics/datamining/index.html",
        "Data Mining Software and Text Mining | " + "SAS",
        "Data mining is the process of selecting, exploring and modeling large amounts of ... The "
            + "knowledge gleaned from data and text mining can be used to fuel ..."
      },
      {
        "http://databases.about.com/od/datamining/Data_Mining_and_Data_Warehousing.htm",
        "Data Mining and Data " + "Warehousing",
        "From data mining tutorials to data warehousing techniques, you'll find it all! ... "
            + "Administration Design Development Data Mining Database Training Careers Reviews ..."
      },
      {
        "http://www.oracle.com/technology/products/bi/odm/index.html",
        "Oracle Data Mining",
        "Oracle Data Mining "
            + "Product Center ... Using data mining functionality embedded in Oracle Database 10g, you can find ... "
            + "Mining High-Dimensional Data for ..."
      },
      {
        "http://www.ncdm.uic.edu/",
        "National Center for Data Mining - Welcome",
        "Conducts research in: scaling "
            + "algorithms, applications and systems to massive data sets, developing algorithms, applications, and "
            + "systems for mining distributed data, and establishing standard languages, protocols, and services for "
            + "data mining and predictive modeling."
      },
      {
        "http://research.microsoft.com/dmx/DataMining/default.aspx",
        "Data Mining Project",
        "A long term Knowledge "
            + "Discovery and Data Mining project which has the current ... Read more about how data mining is "
            + "integrated into SQL server. Contact Us ..."
      },
      {
        "http://www.dmg.org/",
        "Data Mining Group - DMG",
        "... high performance networking, internet computing, data"
            + " mining and related areas. ... Peter Stengard, Oracle Data Mining Technologies. prudsys AG, Chemnitz, .."
            + "."
      },
      {
        "http://datamining.typepad.com/data_mining/2006/05/the_truth_about.html",
        "Data Mining: Text Mining, " + "Visualization and Social Media: The Truth About Blogs",
        "Commentary on text mining, data mining, social "
            + "media and data visualization. ... Data Mining points to the latest papers from the 3rd International "
            + "Workshop on ..."
      },
      {
        "http://searchsqlserver.techtarget.com/sDefinition/0,,sid87_gci211901,00.html",
        "What is data mining? - a "
            + "definition from Whatis.com - see also: data miner, data analysis",
        "Data mining is the analysis of data "
            + "for relationships that have not previously been discovered. ... Data mining techniques are used in a "
            + "many research areas, ..."
      },
      {
        "http://www.thearling.com/text/dmwhite/dmwhite.htm",
        "An Introduction to Data Mining",
        "Data mining, the "
            + "extraction of hidden predictive information from large ... prospective analyses offered by data mining "
            + "move beyond the analyses of ..."
      },
      {
        "http://www.oracle.com/solutions/business_intelligence/data-mining.html",
        "Oracle Data Mining",
        "Using data "
            + "mining functionality embedded in ... Oracle Data Mining JDeveloper and SQL Developer ... Oracle "
            + "Magazine: Using the Oracle Data Mining API ..."
      },
      {
        "http://www.amazon.com/tag/data%20mining",
        "Amazon.com: data mining",
        "A community about data mining. Tag "
            + "and discover new products. ... Data Mining (Paperback) Data Mining: Practical Machine Learning Tools and"
            + " Techniques, Second Edition ..."
      },
      {
        "http://ocw.mit.edu/OcwWeb/Sloan-School-of-Management/15-062Data-MiningSpring2003/CourseHome/index.htm",
        "MIT OpenCourseWare | Sloan School of Management | 15.062 Data Mining, Spring 2003 | Home",
        "... class of"
            + " methods known as data mining that assists managers in recognizing ... Data mining is a rapidly growing "
            + "field that is concerned with developing ..."
      },
      {
        "http://www.sas.com/offices/europe/sweden/2746.html",
        "Om Data Mining och Text Mining. Ta fram s\u00E4kra "
            + "beslutsunderlag med Data Miningverktyg fr\u00E5n SAS Institute.",
        "SAS Insitutes business intelligence "
            + "ger v\u00E4rdefull kunskap till hela din ... Till\u00E4mpningen av data mining str\u00E4cker sig "
            + "\u00F6ver m\u00E5nga branscher och omr\u00E5den. ..."
      },
      {
        "http://www.dmoz.org/Computers/Software/Databases/Data_Mining/",
        "Open Directory - Computers: Software: " + "Databases: Data Mining",
        "Data Mining and Knowledge Discovery - A peer-reviewed journal publishing ... "
            + "In assessing the potential of data mining based marketing campaigns one needs to ..."
      },
      {
        "http://www.investorhome.com/mining.htm",
        "Investor Home - Data Mining",
        "Data Mining or Data Snooping is "
            + "the practice of searching for relationships and ... up by making a case study in data mining out of the "
            + "Motley Fool's Foolish Four. ..."
      },
      {
        "http://www.amazon.com/Data-Mining-Concepts-Techniques-Management/dp/1558604898",
        "Amazon.com: Data Mining: "
            + "Concepts and Techniques (The Morgan Kaufmann Series in Data Management Systems): Jiawei Han...",
        "Amazon"
            + ".com: Data Mining: Concepts and Techniques (The Morgan Kaufmann Series in Data Management Systems): "
            + "Jiawei Han,Micheline Kamber: Books"
      },
      {
        "http://www.monografias.com/trabajos/datamining/datamining.shtml",
        "Data Mining - Monografias.com",
        "Data "
            + "Mining, la extracci\u00F3n de informaci\u00F3n oculta y predecible de grandes bases ... Las herramientas"
            + " de Data Mining predicen futuras tendencias y comportamientos, ..."
      },
      {
        "http://www.megaputer.com/data_mining.php",
        "Data Mining Technology - Megaputer",
        "Data Mining Technology "
            + "from Megaputer ... Typical tasks addressed by data mining include: ... Yet, data mining requires far "
            + "more than just machine learning. ..."
      },
      {"http://datamining.itsc.uah.edu/", "itsc data mining solutions center", ""},
      {
        "http://www.dmreview.com/specialreports/20050503/1026882-1.html",
        "Hard Hats for Data Miners: Myths and " + "Pitfalls of Data Mining",
        "This article debunks several myths about data mining and presents a plan of "
            + "action to avoid some of the pitfalls. ... a typical data mining conference or ..."
      },
      {
        "http://research.microsoft.com/dmx/",
        "Data Management, Exploration and Mining- Home",
        "The Data Management "
            + "Exploration and Mining Group (DMX) ... Our research effort in data mining focuses on ensuring that "
            + "traditional ..."
      },
      {
        "http://www.biomedcentral.com/info/about/datamining",
        "BioMed Central | about us | Data mining research",
        "."
            + ".. a collection of links to publications on the subject of biomedical text mining. Data mining Open "
            + "Access research - an article in the 8 September 2003 edition of ..."
      },
      {
        "http://www.datapult.com/Data_Mining.htm",
        "Data Mining",
        "Data Mining Services provide customers with tools"
            + " to quickly sift through the ... into Datapult Central for use with Data Mining tools and other Datapult"
            + " products. ..."
      },
      {
        "http://www.siam.org/meetings/sdm02/",
        "SIAM International Conference on Data Mining",
        "SIAM International "
            + "Conference on Data Mining, co-Sponsored by AHPCRC and ... Clustering High Dimensional Data and its "
            + "Applications. Mining Scientific Datasets ..."
      },
      {
        "http://dir.yahoo.com/Computers_and_Internet/Software/Databases/Data_Mining/",
        "Data Mining in the Yahoo! " + "Directory",
        "Learn about data mining and knowledge discovery, the process of finding patterns ... Cross "
            + "Industry Standard Process for Data Mining (CRISP-DM) ..."
      },
      {
        "http://www.llnl.gov/str/Kamath.html",
        "Data Mining",
        "... Sapphire-a semiautomated, flexible data-mining "
            + "software infrastructure. ... Data mining is not a new field. ... scale, scientific data-mining efforts "
            + "such ..."
      },
      {
        "http://www.sqlserverdatamining.com/",
        "SQL Server Data Mining > Home",
        "SQL Server Data Mining Portal ... "
            + "information about our exciting data mining features. ... CTP of Microsoft SQL Server 2008 Data Mining "
            + "Add-Ins for Office 2007 ..."
      },
      {
        "http://www.dbmsmag.com/9807m01.html",
        "DBMS - DBMS Data Mining Solutions Supplement",
        "As recently as two "
            + "years ago, data mining was a new concept for many people. Data mining products were new and marred by "
            + "unpolished interfaces. ..."
      },
      {
        "http://www.oclc.org/research/projects/mining",
        "Data mining [OCLC - Projects]",
        "Describes the goals, " + "methodology, and timing of the Data mining project."
      },
      {
        "http://www.the-data-mine.com/bin/view/Misc/IntroductionToDataMining",
        "Data Mining - Introduction To Data " + "Mining (Misc)",
        "Some example application areas are listed under Applications Of Data Mining ... Crows "
            + "Introduction - \"Introduction to Data Mining and Knowledge Discovery\"- http: ..."
      },
      {
        "http://www.pentaho.com/products/data_mining/",
        "Pentaho Commercial Open Source Business Intelligence: Data " + "Mining",
        "... (BI) to the next level by adding data mining and workflow to the mix. ... Pentaho Data "
            + "Mining is differentiated by its open, standards-compliant nature, ..."
      },
      {
        "http://www.unf.edu/~selfayou/html/data_mining.html",
        "Data Mining",
        "This course approaches data mining "
            + "topics from an Artificial Intelligence ... The course will also cover Applications and Trends in Data "
            + "Mining. Textbook: ..."
      },
      {
        "http://www.statsoft.com/products/dataminer.htm",
        "Data Mining Software & Predictive Modeling Solutions",
        "data mining software & predictive modeling sold online by statsoft.com. ... of automated and "
            + "ready-to-deploy data mining solutions for a wide variety of ..."
      },
      {
        "http://gosset.wharton.upenn.edu/wiki/index.php/Main_Page",
        "Main Page - Knowledge Discovery",
        "The Penn "
            + "Data Mining Group develops principled means of modeling and ... knowledge of specific application areas "
            + "to develop new approaches to data mining. ..."
      },
      {
        "http://www.twocrows.com/glossary.htm",
        "Two Crows: Data mining glossary",
        "Data mining terms concisely "
            + "defined. ... Accuracy is an important factor in assessing the success of data mining. ... data mining .."
            + "."
      },
      {
        "http://www.cdc.gov/niosh/mining/data/",
        "NIOSH Mining: MSHA Data File Downloads | CDC/NIOSH",
        "MSHA "
            + "accident, injury, employment, and production data files in SPSS and dBase formats ... Data files on "
            + "mining accidents, injuries, fatalities, employment, ..."
      },
      {
        "http://www.cartdatamining.com/",
        "Salford Data mining 2006",
        "Objective | Previous Conferences | Call for " + "Abstracts | LATEST INFO ..."
      },
      {
        "http://www.inductis.com/",
        "Data Mining | Focused Data Mining For Discovery To Assist Management",
        "Inductis offers high-level data mining services to assist management decisions ... The Data Mining "
            + "Shootout ...more>> ISOTech 2006 - The Insurance Technology ..."
      },
      {
        "http://www.datamininglab.com/",
        "Elder Research: Predictive Analytics & Data Mining Consulting",
        "Provides "
            + "consulting and short courses in data mining and pattern discovery patterns in data."
      },
      {
        "http://www.microsoft.com/sql/technologies/dm/default.mspx",
        "Microsoft SQL Server: Data Mining",
        "Microsoft"
            + " SQL Server Data Mining helps you explore your business data and discover patterns to reveal the hidden "
            + "trends about your products, customer, market, and ..."
      },
      {
        "http://www.dataminingcasestudies.com/",
        "Data Mining Case Studies",
        "Recognizing outstanding practical "
            + "contributions in the field of data mining. ... case studies are one of the most discussed topics at data"
            + " mining conferences. ..."
      },
      {
        "http://www.webopedia.com/TERM/D/data_mining.html",
        "What is data mining? - A Word Definition From the " + "Webopedia Computer Dictionary",
        "This page describes the term data mining and lists other pages on the "
            + "Web where you can find additional information. ... Data Mining and Analytic Technologies ..."
      },
      {
        "http://www.cs.waikato.ac.nz/~ml/weka/book.html",
        "Data Mining: Practical Machine Learning Tools and " + "Techniques",
        "Book. Data Mining: Practical Machine Learning Tools and Techniques (Second Edition) ... "
            + "Explains how data mining algorithms work. ..."
      },
      {
        "http://www.datamining.com/",
        "Predictive Modeling and Predictive Analytics Solutions | Enterprise Miner "
            + "Software from Insightful Software",
        "Insightful Enterprise Miner - Enterprise data mining for predictive"
            + " modeling and predictive analytics."
      },
      {
        "http://www.sra.com/services/index.asp?id=153",
        "SRA International - Data Mining Solutions",
        "... and "
            + "business who ask these questions are finding solutions through data mining. ... Data mining is the "
            + "process of discovering previously unknown relationships in ..."
      },
      {
        "http://en.wiktionary.org/wiki/data_mining",
        "data mining - Wiktionary",
        "Data mining. Wikipedia. data "
            + "mining. a technique for searching large-scale databases for patterns; used mainly to ... Czech: data "
            + "mining n., dolov\u00E1n\u00ED dat n. ..."
      },
      {"http://www.datamining.org/", "data mining institute", ""},
      {
        "http://videolectures.net/Top/Computer_Science/Data_Mining/",
        "Videolectures category: Data Mining",
        "Next "
            + "Generation Data Mining Tools: Power laws and self-similarity for graphs, ... Parallel session 4 - "
            + "Hands-on section Data mining with R. Luis Torgo. 1 comment ..."
      },
      {
        "http://www2008.org/CFP/RP-data_mining.html",
        "WWW2008 CFP - WWW 2008 Call For Papers: Refereed Papers - " + "Data Mining",
        "WWW2008 - The 17th International World Wide Web Conference - Beijing, China (21 - 25 "
            + "April 2008) Hosted by Beihang Universit ... data mining, machine ..."
      },
      {
        "http://answers.yahoo.com/question/index?qid=20070227091350AAVDlI1",
        "what is data mining?",
        "... the "
            + "purchases of customers, a data mining system could identify those customers ... A simple example of data"
            + " mining, often called Market Basket Analysis, ..."
      },
      {
        "http://clubs.yahoo.com/clubs/datamining",
        "datamining2 : Data Mining Club - 1600+ members!!",
        "datamining2:" + " Data Mining Club - 1600+ members!"
      },
      {
        "http://www.siam.org/meetings/sdm01/",
        "First SIAM International Conference on Data Mining",
        "The field of "
            + "data mining draws upon extensive work in areas such as statistics, ... recent results in data mining, "
            + "including applications, algorithms, software, ..."
      },
      {
        "http://www.statserv.com/datamining.html",
        "St@tServ - About Data Mining",
        "St@tServ Data Mining page ... "
            + "Data mining in molecular biology, by Alvis Brazma. Graham Williams page. Knowledge Discovery and Data "
            + "Mining Resources, ..."
      },
      {
        "http://www.springer.com/computer/database+management+&+information+retrieval/journal/10618",
        "Data Mining "
            + "and Knowledge Discovery - Data Mining and Knowledge Discovery Journals, Books & Online Media | Springer",
        "Technical journal focused on the theory, techniques, and practice for extracting information from "
            + "large databases."
      },
      {
        "http://msdn2.microsoft.com/en-us/library/ms174949.aspx",
        "Data Mining Concepts",
        "Data mining is frequently"
            + " described as &quot;the process of extracting ... Creating a data mining model is a dynamic and "
            + "iterative process. ..."
      },
      {
        "http://www.cs.wisc.edu/dmi/",
        "DMI:Data Mining Institute",
        "Data Mining Institute at UW-Madison ... The "
            + "Data Mining Institute (DMI) was ... Corporation with the support of the Data Mining Group of Microsoft "
            + "Research. ..."
      },
      {
        "http://www.dataminingconsultant.com/",
        "DataMiningConsultant.com",
        "... Website for Data Mining Methods and"
            + " ... data mining at Central Connecticut State University, he ... also provides data mining consulting "
            + "and statistical ..."
      },
      {
        "http://www.dmreview.com/channels/data_mining.html",
        "Data Mining",
        "... business intelligence, data "
            + "warehousing, data mining, CRM, analytics, ... M2007 Data Mining Conference Hitting 10th Year and Going "
            + "Strong ..."
      },
      {
        "http://www.unc.edu/~xluan/258/datamining.html",
        "Data Mining",
        "What is the current state of data mining? "
            + "The immediate future ... Data Mining is the process of extracting knowledge hidden from large volumes of"
            + " ..."
      },
      {
        "http://www.data-miners.com/",
        "Data Miners Inc. We wrote the book on data mining!",
        "Data mining "
            + "consultancy; services include predictive modeling, consulting, and seminars."
      },
      {
        "http://www.versiontracker.com/dyn/moreinfo/macosx/27607",
        "Data Mining 2.2.2 software download - Mac OS X -" + " VersionTracker",
        "Find Data Mining downloads, reviews, and updates for Mac OS X including commercial "
            + "software, shareware and freeware on VersionTracker.com."
      },
      {
        "http://www.webtechniques.com/archives/2000/01/greening/",
        "New Architect: Features",
        "Article by Dan "
            + "Greening on data mining techniques applied to analyzing and making decisions from web data. ... and "
            + "business analysts use data-mining techniques. ..."
      },
      {
        "http://www.networkdictionary.com/software/DataMining.php",
        "Data Mining | NetworkDictionary",
        "Data Mining "
            + "is the automated extraction of hidden predictive information from databases. ... The data mining tools "
            + "can make this leap. ..."
      },
      {
        "http://www.youtube.com/watch?v=wqpMyQMi0to",
        "YouTube - What is Data Mining? - February 19, 2008",
        "Association Labratory President and CEO Dean West discusses Data Mining and how it can be applied to "
            + "associations. ... Data Mining Association Forum Dean West ..."
      },
      {
        "http://www.cs.sfu.ca/~han/DM_Book.html",
        "Book page",
        "Chapter 4. Data Mining Primitives, Languages, and "
            + "System Architectures ... Chapter 9. Mining Complex Types of Data ... to Microsoft's OLE DB for Data "
            + "Mining ..."
      },
      {
        "http://www.twocrows.com/",
        "Two Crows data mining home page",
        "Dedicated to the development, marketing, "
            + "sales and support of tools for knowledge discovery to make data mining accessible and easy to use."
      },
      {
        "http://www.autonlab.org/tutorials",
        "Statistical Data Mining Tutorials",
        "Includes a set of tutorials on "
            + "many aspects of statistical data mining, including the foundations of probability, the foundations of "
            + "statistical data analysis, and most of the classic machine learning and data mining algorithms."
      },
      {
        "http://ecommerce.ncsu.edu/technology/topic_Datamining.html",
        "E-commerce Technology: Data Mining",
        "\"Web "
            + "usage mining: discovery and applications of web usage patterns from web data\" ... Patterns and Trends "
            + "by Applying OLAP and Data Mining Technology on Web Logs. ..."
      },
      {
        "http://www.teradata.com/t/page/106002/index.html",
        "Teradata Data Mining Warehouse Solution",
        "... a "
            + "high-powered analytic warehouse that streamlines the data mining process. ... while building the "
            + "analytic model using your favorite data mining tool. ..."
      },
      {
        "http://datamining.japati.net/",
        "Indo Datamining",
        "Apa yang bisa dan tidak bisa dilakukan data mining ? .."
            + ". Iko Pramudiono \"&raquo ... Apa itu data mining ? Iko Pramudiono \"&raquo. artikel lainnya \" tutorial"
            + " ..."
      },
      {
        "http://www.affymetrix.com/products/software/specific/dmt.affx",
        "Affymetrix - Data Mining Tool (DMT) " + "(Unsupported - Archived Product)",
        "Affymetrix is dedicated to developing state-of-the-art technology "
            + "for acquiring, analyzing, and managing complex genetic ... The Data Mining Tool (DMT) ..."
      },
      {
        "http://www.pcc.qub.ac.uk/tec/courses/datamining/stu_notes/dm_book_1.html",
        "Data Mining Student Notes, QUB",
        "2 - Data Mining Functions. 2.1 - Classification. 2.2 - Associations ... 5 - Data Mining Examples. 5.1 "
            + "- Bass Brewers. 5.2 - Northern Bank. 5.3 - TSB Group PLC ..."
      },
      {
        "http://www.spss.com/text_mining_for_clementine/",
        "Text Mining for Clementine | Improve the accuracy of " + "data mining",
        "Text Mining for Clementine from SPSS enables you to use text data to improve the accuracy"
            + " of predictive models. ... and about data mining in general. ..."
      },
      {
        "http://www.open-mag.com/features/Vol_16/datamining/datamining.htm",
        "Data Mining",
        "Without data mining, a "
            + "merchant isn't even close to leveraging what customers want and will buy. ... Data mining is to be found"
            + " in applications like bio ..."
      },
      {
        "http://wordpress.com/tag/data-mining/",
        "Data Mining \u2014 Blogs, Pictures, and more on WordPress",
        "Going"
            + " Beyond the Numbers: Context-Sensitive Data Mining ... Data mining examples ... many websites employing "
            + "data mining technology to provide recommendation ..."
      },
      {
        "http://www.dmbenchmarking.com/",
        "Benchmarking- Data Mining Benchmarking Association",
        "Association of "
            + "companies and organizations working to identify \"best in class\" data mining processes through "
            + "benchmarking studies."
      },
      {
        "http://www.dataentryindia.com/data_processing/data_mining.php",
        "Data Mining, Data Mining Process, Data "
            + "Mining Techniques, Outsourcing Mining Data Services",
        "... Walmart, Fundraising Data Mining, Data Mining"
            + " Activities, Web-based Data Mining, ... in many industries makes us the best choice for your data mining"
            + " needs. ..."
      },
      {
        "http://www.target.com/Data-Mining-Applications-International-Information/dp/1853127299",
        "Data Mining V: " + "Data Mining, Text Mining... [Hardcover] | Target.com",
        "Shop for Data Mining V: Data Mining, Text Mining"
            + " and Their Business Applications : Fifth International Conference on Data Mining (Management Information"
            + " System) at"
      },
      {
        "http://www.cs.ubc.ca/~rng/research/datamining/data_mining.htm",
        "Data Mining",
        "... varying degrees of "
            + "success, the data mining tools developed thus far, by and ... (a) we should recognize that data mining "
            + "is a multi-step process, and that (b) ..."
      },
      {
        "http://jcp.org/en/jsr/detail?id=73",
        "The Java Community Process(SM) Program - JSRs: Java Specification "
            + "Requests - detail JSR# 73",
        "Currently, there is no widely agreed upon, standard API for data mining. By"
            + " using JDMAPI, implementers of data mining applications can expose a single, ..."
      },
      {
        "http://www.microsoft.com/spain/sql/technologies/dm/default.mspx",
        "Microsoft SQL Server2005: Data Mining",
        "Data Mining es la tecnolog\u00EDa BI que le ayudar\u00E1 a construir modelos anal\u00EDticos complejos e"
            + " integrar esos modelos con sus operaciones comerciales."
      },
      {
        "http://www.bos.frb.org/economic/nerr/rr2000/q3/mining.htm",
        "Regional Review: Mining Data",
        "Although data "
            + "mining by itself is not going to get the Celtics to the playoffs, ... then, firms that specialize in "
            + "data-mining software have been developing a ..."
      },
      {
        "http://www.scianta.com/technology/datamining.htm",
        "Data Mining",
        "... are excellent candidates for data "
            + "mining, fault prediction, problem diagnosis, ... Data Mining uses this theory to support Link and "
            + "Affinity Group analysis \u2013 an ..."
      },
      {
        "http://www.gusconstan.com/DataMining/index.htm",
        "Discovery and Mining",
        "Verification-Driven Data Mining. "
            + "Advantages of Symbolic Classifiers. Manual vs. Automatic ... Currently, data mining solutions have been "
            + "developed by large software ..."
      },
      {
        "http://www.dataminingconsultant.com/DKD.htm",
        "DataMiningConsultant.com",
        "Companion Website for Data "
            + "Mining Methods and Models ... \"This is an excellent introductory book on data mining. ... An "
            + "Introduction to Data Mining at Amazon.com ..."
      },
      {
        "http://www.pfaw.org/pfaw/general/default.aspx?oid=9717",
        "People For the American Way - Data Mining",
        "data"
            + " mining, civil liberties, civil rights, terrorism, september 11th, anti-terrorism, ashcroft, government "
            + "intrusion, privacy, email, patriot, american"
      },
      {
        "http://dm1.cs.uiuc.edu/",
        "Data Mining Research Group",
        "... conducting research in various areas in data "
            + "mining and other related fields. ... on Data Mining (SDM'08), (full paper), Atlanta, GA, April 2007. .."
            + "."
      }
    };

    final ArrayList<Document> documents = new ArrayList<>();
    for (String[] row : data) {
      FieldMapDocument doc = new FieldMapDocument();
      doc.addField("url", row[0]);
      doc.addField("title", row[1]);
      doc.addField("snippet", row[2]);
      documents.add(doc);
    }

    DOCUMENTS_DATA_MINING = Collections.unmodifiableList(documents);
  }

  public static final List<Document> DOCUMENTS_DAWID;

  static {
    final String[][] data =
        new String[][] {
          {
            "http://www.dawid.tv/",
            "dawid.tv",
            "Watch free videos on dawid.tv. Now "
                + "Playing: DAWID DRIF ... About. Dawid. Bielawa - Poland. Friends: 1. Last Login: ... View All Members of "
                + "dawid.tv. Tag Cloud ..."
          },
          {
            "http://www.dawid.co.za/",
            "DAWID",
            "Welkom by: Dawid Bredenkamp se webtuiste. Foto's. Skakels. Kontak ..."
          },
          {
            "http://www.dawid-nowak.org/",
            "Dawid Nowak",
            "Dawid Nowak Home Page ... Resume. Gallery. Thailand. Still in"
                + " Thailand. Into Laos. Through Laos To Cambodia. RSS feeds for lazy technically oriented people ..."
          },
          {
            "http://dawid.digitalart.org/",
            "dawid.digitalart.org - Profile of Dawid Michalczyk",
            "A gallery of "
                + "masterfully created works of digital art. ... Dawid Michalczyk \" Send Private Message \" Send an E-mail"
                + ". Art Gallery (13) Guestbook ..."
          },
          {
            "http://www.dawid.nu/index.php?ID=4",
            "dawid :: images / commercial work :: advertising & illustrations",
            "The official site of photographer Dawid, Bj\u00F6rn Dawidsson. Fotograf Dawid - Bj\u00F6rn Dawidsson ..."
                + " references: AB Vin & Sprit, Apple, Berliner, Bond, Ericsson, ..."
          },
          {
            "http://www.dawidphotography.com/",
            "Photographer London UK, Dawid de Greeff \u00A9 2007 , Digital "
                + "photographer - Portfolio",
            "South African born Dawid & Annemarie de Greeff are International digital ..."
                + " NAME. EMAIL. MESSAGE ..."
          },
          {
            "http://www.anniedawid.com/",
            ": : Annie Dawid : : Author and Photographer",
            "Annie Dawid is the author of "
                + "Resurrection City: A Novel of Jonestown (to be ... Annie Dawid lives and writes in the Sangre de Cristo "
                + "range of South-Central Colorado. ..."
          },
          {
            "http://en.wikipedia.org/wiki/Dawid_Janowski",
            "Dawid Janowski - Wikipedia, the free encyclopedia",
            "Dawid "
                + "Markelowicz Janowski (in English usually called David Janowski) (born 25 ... Dawid Janowski died on "
                + "January 15, 1927 of tuberculosis. ..."
          },
          {
            "http://www.dawid.nu/index.php?ID=2",
            "dawid :: images / art :: COMP",
            "The official site of photographer "
                + "Dawid, Bj\u00F6rn Dawidsson. Fotograf Dawid - Bj\u00F6rn Dawidsson ... dawid : images / art : COMP: "
                + "Series photographed during the mid 80's. ..."
          },
          {
            "http://en.wikipedia.org/wiki/Dawid",
            "Dawid - Wikipedia, the free encyclopedia",
            "Dawid. From Wikipedia, "
                + "the free encyclopedia. Jump to: navigation, search. Dawid may refer to the following people: David, the "
                + "biblical King David ..."
          },
          {
            "http://www.myspace.com/dawidszczesny",
            "MySpace.com - dawid szczesny - Wroclaw - www.myspace" + ".com/dawidszczesny",
            "MySpace music profile for dawid szczesny with tour dates, songs, videos, pictures,"
                + " blogs, band information, downloads and more"
          },
          {
            "http://www.art.eonworks.com/",
            "Computer wallpaper, stock illustration, Sci-Fi art, Fantasy art, Surreal "
                + "art, Space art, Abstract art - posters, ...",
            "Digital Art of Dawid Michalczyk. Unique posters, prints, "
                + "wallpapers and wall calendars. ... the official website of Dawid Michalczyk - a freelance illustrator .."
                + "."
          },
          {
            "http://www.surfski.info/content/view/384/147/",
            "Surf Ski . Info - Dawid Mocke King of the Harbour 2007",
            "Surf Ski information and news. Training tips from the experts, equipment, getting started guides, "
                + "surfski reviews, photos ...links and stories."
          },
          {
            "http://www.agentsbase.com/",
            "Agent's Base",
            "Dawid Kasperowicz. Get Firefox. Get Google Ads. Affiliates .."
                + ". By Dawid | February 28, 2008 - 12:05 pm - Posted in Technology ..."
          },
          {
            "http://www.target.com/Dawid-Dawidsson-Bjorn/dp/3882437243",
            "Dawid [Hardcover] | Target Official Site",
            "Shop for Dawid at Target. Choose from a wide range of Books. Expect More, Pay Less at Target.com"
          },
          {
            "http://www.dawid.tobiasz.org/",
            "Dawid",
            "Dawid. Fotografia stanowi w\u0142asno\u015B\u0107 autora. "
                + "Kopiowanie i rozpowszechnianie ... Copyright by Dawid Tobiasz [Fotografia stanowi "
                + "w\u0142asno\u015B\u0107 autora. ..."
          },
          {
            "http://juliedawid.co.uk/",
            "Julie Dawid :",
            "birthing support scotland. Poetical Fusion Folk. Words. Band. "
                + "Listen. Contact. Copyright \u00A9 2004 Julie Dawid. All Rights reserved. Powered by Accidental Media ..."
          },
          {
            "http://conference.dawid.uni.wroc.pl/index.php?lang=iso-8859-2",
            "konferencja - Welcome",
            "Joomla - the "
                + "dynamic portal engine and content management system ... The 1st Symposium of Pedagogy and Psychology PhD"
                + " Students. Monday, 13 February 2006 ..."
          },
          {
            "http://www.ibe.unesco.org/publications/ThinkersPdf/dawide.pdf",
            "Jan Wladyslaw Dawid",
            "All his life, Jan "
                + "Wladyslaw Dawid was closely associated with the teaching ... Dawid who believed that these experiments "
                + "were fundamental to the blossoming and ..."
          },
          {"http://www.dawid-posciel.pl/", "www.<b>dawid-posciel.pl</b>", ""},
          {
            "http://www.dawidrurkowski.com/",
            "Dawid Rurkowski - portfolio",
            "Dawid Rurkowski online webdesign portfolio"
                + " ... My name is Dawid, I am a web designer with a real passion to my work. ... \u00A9 Copyright 2007 "
                + "Dawid Rurkowski All ..."
          },
          {
            "http://conference.dawid.uni.wroc.pl/index.php?option=com_content&task=blogsection&id=20&Itemid=49%E2%8C%A9"
                + "=iso-8859-2",
            "konferencja - Warsztaty",
            "Joomla - the dynamic portal engine and content management "
                + "system ... Karolina Pietras is a psychologist, business trainer and PhD student at Faculty ..."
          },
          {
            "http://chess.about.com/library/persons/blp-jano.htm",
            "Famous Chess Players - Dawid Janowsky",
            "Beginners "
                + "Improve Your Game Play Chess Online Chess Downloads Computers and ... Dawid Janowsky. Unsuccessful "
                + "challenger for World Championship ..."
          },
          {
            "http://www.pbase.com/dawidwnuk",
            "Dawid Wnuk's Photo Galleries at pbase.com",
            "All images on this site "
                + "copyrighted by DAWID WNUK. Please contact me if you would like to purchase or licence a photograph. "
                + "Portraiture ..."
          },
          {
            "http://dawid-witos.nazwa.pl/chylu/en/index.php?link=news",
            "...Official Website of Michael Chylinski...",
            "Welcome to chylinski.info- the official web site of Polish National Team and ... We invite you to visite"
                + " our service and write your opinions on forum. A few ..."
          },
          {"http://photoexposed.com/", "photoeXposed.com", "Dawid Slaski-Sawicki Photography"},
          {
            "http://vids.myspace.com/index.cfm?fuseaction=vids.individual&VideoID=7370487",
            "MySpaceTV Videos: Edyp " + "trailer by dawid",
            "Edyp trailer by dawid Watch it on MySpace Videos. ... Posted by: dawid. Runtime: "
                + "0:52. Plays: 43. Comments: 0. Reinkarnacje - \"Czy to mi..."
          },
          {
            "http://www.linkedin.com/in/dawidmadon",
            "LinkedIn: Dawid Mado\u0144",
            "Dawid Mado\u0144's professional "
                + "profile on LinkedIn. ... Dawid Mado\u0144. ORACLE DBA at Apriso and Information Technology and Services "
                + "Consultant ..."
          },
          {
            "http://www.linkedin.com/pub/1/878/410",
            "LinkedIn: Dawid Tracz",
            "Dawid Tracz's professional profile on "
                + "LinkedIn. ... Dawid Tracz's Experience. Graphician, WebDesigner, InterfaceDesigner. DreamLab Onet.pl Sp."
                + " ..."
          },
          {
            "http://profiles.friendster.com/13547484",
            "Friendster - Dawid Martin",
            "Friendster: ; location: Poland, PL;"
                + " Kiedrowice, Warsaw (Poland),Jogja (Indonesia); Warsaw Gamelan Group, Bosso, Tepellere, Mandala, Suita "
                + "Etnik, Konco-Konco Blues ..."
          },
          {
            "http://www.genevievedawid.com/",
            "Genevieve Dawid mentor, lecturer and author",
            "Author of the Achiever's "
                + "Journey a real self help book for dyslexics, Genevieve Dawid offers a unique approach to mentoring and "
                + "personal development."
          },
          {
            "http://www.last.fm/music/dawid+szczesny",
            "dawid szczesny \u2013 Music at Last.fm",
            "People who like dawid "
                + "szczesny also like Masayasu Tzboguchi Trio, Ametsub, ... Dawid Szcz\u0119sny performed in Poland, "
                + "Germany (in 2005 invited by Kata Adamek and ..."
          },
          {
            "http://vids.myspace.com/index.cfm?fuseaction=vids.individual&videoid=2028359840",
            "MySpaceTV Videos: paka " + "2007-1 by dawid",
            "paka 2007-1 by dawid Watch it on MySpace Videos. ... Posted by: dawid. Runtime: 0:52."
                + " Plays: 43. Comments: 0. Reinkarnacje - \"Czy to mi..."
          },
          {
            "http://dawid.secondbrain.com/",
            "Dawid's profile page - Second Brain_ - All Your Content",
            "Dawid. People "
                + "first, strategy second ... Dawid's recent updates. February 07 2008. Wimbledon ... Posted by Dawid on "
                + "Second Brain February 05 2008. Post comment ..."
          },
          {
            "http://www.ushmm.org/wlc/article.php?lang=en&ModuleId=10007294",
            "Dawid Sierakowiak",
            "Dawid was an avid "
                + "reader and an excellent observer. Throughout Dawid's imprisonment in the Lodz ghetto he made sure to "
                + "write about ..."
          },
          {
            "http://www.ctbodyartist.com/",
            "CT Body Artist | Chrys Dawid (203) 255-1875",
            "CT Body Artist, Chrys Dawid "
                + "(203) 255-1875 Professional Body painting service. From Advertising Champaigns to Private parties, make "
                + "your statement & Marketing goals ..."
          },
          {
            "http://www.amazon.com/phrase/Dawid-Sierakowiak",
            "Amazon.com: \"Dawid Sierakowiak\": Key Phrase page",
            "Key"
                + " Phrase page for Dawid Sierakowiak: Books containing the phrase Dawid Sierakowiak ... Key Phrases: Dawid"
                + " Sierakowiak, United States, New York, Niutek ..."
          },
          {
            "http://www.planetizen.com/user/403/track",
            "Irvin Dawid | Planetizen",
            "Irvin Dawid. 0. 2 weeks 20 hours "
                + "ago. news ... Irvin Dawid. 1. 3 weeks 5 days ago. news. Traffic Crashes Cost Twice as Much as Congestion"
                + " ..."
          },
          {
            "http://www.ushmm.org/wlc/idcard.php?lang=en&ModuleId=10006389",
            "Dawid Szpiro",
            "Dawid was the older of two"
                + " sons born to Jewish parents in Warsaw. ... of Warsaw's Jewish district, where Dawid and his brother, "
                + "Shlomo, attended Jewish schools. ..."
          },
          {
            "http://groups.yahoo.com/group/dawid",
            "dawid : Katechetyczne Forum Dyskusyjne",
            "dawid \u00B7 Katechetyczne"
                + " Forum Dyskusyjne. Home. Messages ... Lista dyskusyjna strony internetowej DAWID. Most Recent Messages "
                + "(View All) (Group by Topic) ..."
          },
          {
            "http://www.blogger.com/profile/01359115939699161533",
            "Blogger: User Profile: Dawid",
            "Push-Button "
                + "Publishing. Dawid. Blogs. Blog Name. Team Members. Midwest Petanque Alliance BLOG ... MGal hdarpini "
                + "chilipepper diveborabora DanDan Mike A testerin ..."
          },
          {
            "http://www.blogger.com/profile/15768169977536938605",
            "Blogger: User Profile: David",
            "kilconriola Credo "
                + "Perp\u00E9tua Amanda Liturgeist Chris + AMDG + +Miguel Vinuesa+ Royal Girl ... roydosan chrysogonus "
                + "Brownthing Aristotle Boeciana Amanda Lactantius Juan ..."
          },
          {
            "http://www.babynamer.com/Dawid",
            "Dawid on BabyNamer",
            "For parents-to-be who want to confidently choose "
                + "potential names for their baby, ... Dawid. Meaning: Its source is a ... baby name page for boy name "
                + "Dawid. ..."
          },
          {
            "http://profile.myspace.com/index.cfm?fuseaction=user.viewprofile&friendid=38408574",
            "MySpace.com - Dawid -" + " 26 - Male - FR - www.myspace.com/trastaroots",
            "MySpace profile for Dawid with pictures, videos, "
                + "personal blog, interests, information about me and more ... yo dawid, ya un gars de ta r\u00E9gion "
                + "(koubiak) qui ..."
          },
          {
            "http://www.imdb.com/name/nm1058743/",
            "Dawid Kruiper",
            "Actor: Liebe. Macht. Blind.. Visit IMDb for Photos,"
                + " Filmography, Discussions, Bio, News, Awards, Agent, Fan Sites. ... on IMDb message board for Dawid "
                + "Kruiper ..."
          },
          {
            "http://citeseer.ist.psu.edu/context/55656/0",
            "Citations: Conditional independence in statistical theory - "
                + "Dawid (ResearchIndex)",
            "A. P. Dawid. Conditional independence in statistical theory (with discussion). "
                + "J. Roy. ... To capture Dawid s property for overlapping sets, Pearl introduces ..."
          },
          {
            "http://www.dawid.pl/gb/main.php",
            "Systemy ogrodzeniowe, ta\u015Bmy, sita, siatki - DAWID Cz\u0119stochowa",
            "Firma DAWID - Producent siatki ogrodzeniowej, bram, furtek, paneli D-1, D-2 itp. Cz\u0119stochowa. ..."
                + " DAWID Company has a long-standing tradition which has been ..."
          },
          {
            "http://www.imdb.com/name/nm2014139/",
            "Dawid Jakubowski",
            "Miscellaneous Crew: Once Upon a Knight. Visit "
                + "IMDb for Photos, Filmography, Discussions, Bio, News, Awards, Agent, Fan Sites."
          },
          {
            "http://www.lclark.edu/cgi-bin/shownews.cgi?1011726000.1",
            "Dawid publishes Lily in the Desert",
            "Lewis & "
                + "Clark College: Dawid publishes <i>Lily in the Desert</i> ... Annie Dawid is one of those all-too-rare "
                + "writers who fully inhabits each ..."
          },
          {
            "http://dir.nichd.nih.gov/lmg/lmgdevb.htm",
            "Igor Dawid Lab Home Page",
            "Dawid Lab. Welcome to Igor Dawid's "
                + "lab in the Laboratory of Molecular Genetics, ... National Institute of Child Health and Human "
                + "Development, National ..."
          },
          {
            "http://www.ucl.ac.uk/~ucak06d/",
            "Philip Dawid",
            "DEPARTMENT OF STATISTICAL SCIENCE. UNIVERSITY COLLEGE "
                + "LONDON. A. Philip Dawid ... Professor A. P. Dawid, Department of Statistical Science, University College"
                + " London, ..."
          },
          {
            "http://www.pbase.com/dawidwnuk/profile",
            "pbase Artist Dawid Wnuk",
            "View Galleries : Dawid Wnuk has 5 "
                + "galleries and 487 images online. ... My name is Dawid and I'm a photographer from Warsaw, Poland. ..."
          },
          {
            "http://dawidfrederik.deviantart.com/",
            "DawidFrederik on deviantART",
            "Art - community of artists and those"
                + " devoted to art. ... Dawid Frederik Strauss. Profile Gallery Faves Journal. Status: deviantART "
                + "Subscriber ..."
          },
          {
            "http://citeseer.ist.psu.edu/context/332153/0",
            "Citations: Statistical theory - Dawid (ResearchIndex)",
            "Dawid, P. (1984). Statistical theory. The prequential approach (with discussion) . Journal of the Royal "
                + "Statistical Society A, 147:178--292."
          },
          {
            "http://www.infinitee-designs.com/Dawid-Michalczyk.htm",
            "Dawid Michalczyk Artist of the Month Space Art",
            "Artist of the Month, Dawid Michalczyk Abstract 3D Space Art, Visions, computer graphics, 2D "
                + "illustration, sci-fi, fantasy, digital art"
          },
          {
            "http://www.myspace.com/dawidgatti",
            "MySpace.com - dawid - 26 - Male - www.myspace.com/dawidgatti",
            "MySpace profile for dawid with pictures, videos, personal blog, interests, information about me and more"
                + " ... to meet: dawid's Friend Space (Top 1) dawid has 1 ..."
          },
          {
            "http://ezinearticles.com/?expert=Genevieve_Dawid",
            "Genevieve Dawid - EzineArticles.com Expert Author",
            "Genevieve Dawid is a published author and highly successful ... Genevieve Dawid's Extended ... "
                + "[Business:Management] Genevieve Dawid explores the history of ..."
          },
          {
            "http://www.artnet.com/artist/698445/dawid-bjorn-dawidsson.html",
            "Dawid (Bjorn Dawidsson) on artnet",
            "Dawid (Bjorn Dawidsson) (Swedish, 1949) - Find works of art, auction results & sale prices of artist "
                + "Dawid (Bjorn Dawidsson) at galleries and auctions worldwide."
          },
          {
            "http://www.glennshafer.com/assets/downloads/other12.pdf",
            "Comments on \"Causal Inference without " + "Counterfactuals\" by A.P. Dawid",
            "Phil Dawid's elegant ... ted from discussions of causality with Phil "
                + "Dawid over many years. ... ground with those who tout counterfactual variables, Dawid ..."
          },
          {
            "http://www.primerica.com/dawidkmiotek",
            "Primerica Financial Services : Dawid Ireneusz Kmiotek",
            "Primerica"
                + " is in the business of ... Buy Term & Invest the Difference. The Theory of Decreasing ... About Dawid "
                + "Ireneusz Kmiotek. Office Directions ..."
          },
          {
            "http://www.youtube.com/watch?v=tEKmrUhCMFo",
            "YouTube - Dawid Janczyk POLAND u-19 - BELGIUM u-19 (4-1)",
            "Dawid Janczyk (Legia Warsaw) ... Dawid Janczy gral w sandecji nowy sacz i raz gralem z nim(ja gralem w "
                + "sokol ... Dawid Janczyk (Legia Warsaw) (less) Added: ..."
          },
          {
            "http://www.miniclip.com/games/david/en/",
            "David - Miniclip Games - Play Free Games",
            "Help David find the "
                + "Lost Sheep and avoid the rampaging wild animals ... Hotmail, AOL, Yahoo Mail & other online email "
                + "services. ..."
          },
          {
            "http://product.half.ebay.com/_W0QQprZ62221",
            "The Diary of Dawid Sierakowiak | Books at Half.com",
            "Buy The"
                + " Diary of Dawid Sierakowiak by Dawid Sierakowiak, Kamil Turowski (1998) at Half.com. Find new and used "
                + "books and save more than half off at Half.com."
          },
          {
            "http://www.primerica.com/PrimericaRep?rep=dawidkmiotek&pageName=about",
            "About Dawid Ireneusz Kmiotek",
            "Primerica is in the business of ... About Dawid Ireneusz Kmiotek. Office Directions ... Dawid Ireneusz "
                + "Kmiotek. DISTRICT LEADER. Mutual Funds ..."
          },
          {
            "http://www.dawid.tobiasz.org/Monachium%20-%20Dachau/index.html",
            "Dawid/Monachium - Dachau",
            "Dawid \" "
                + "Monachium - Dachau. Fotografia stanowi w\u0142asno\u015B\u0107 autora. Kopiowanie i ... Copyright by "
                + "Dawid Tobiasz [Fotografia stanowi w\u0142asno\u015B\u0107 autora. ..."
          },
          {"http://www.davidwilkerson.org/", "David Wilkerson | World Challenge", ""},
          {
            "http://www.statslab.cam.ac.uk/~apd/index.html",
            "Philip Dawid",
            "PHILIP DAWID. Professor of Statistics. "
                + "Contact Details. Professor A. P. Dawid, ... Valencia International Meetings on Bayesian Statistics. "
                + "Bayesians Worldwide ..."
          },
          {
            "http://ideas.repec.org/e/poc8.html",
            "Dawid Zochowski at IDEAS",
            "Dawid Zochowski: current contact "
                + "information and listing of economic research of this author provided by RePEc/IDEAS ... Pruski, Jerzy & "
                + "\u017Bochowski, Dawid, 2005. ..."
          },
          {
            "http://www.scrumalliance.org/profiles/15472-dawid-mielnik",
            "Scrum Alliance - Profile: Dawid Mielnik",
            "Dawid has five years of professional experience in telecommunications business. ... Dawid is a Warsaw "
                + "University of Technology graduate with a BSc in ..."
          },
          {
            "http://www.flickr.com/photos/dawidwalega/",
            "Flickr: Photos from 11September",
            "Flickr is almost certainly "
                + "the best online photo management and sharing ... Explore Page Last 7 Days Interesting Calendar A Year "
                + "Ago Today World Map Places ..."
          },
          {
            "http://www.youtube.com/watch?v=UOMk0M0hBNQ",
            "YouTube - Grembach Vigo Zgierz - Dawid Korona Rzesz\u00F3w " + "8-1",
            "Grembach Vigo Zgierz - Dawid Korona Rzesz\u00F3w 8-1 w Pucharze Polski ... Grembach Vigo Zgierz "
                + "Dawid Korona Rzesz\u00F3w futsal \u0142\u00F3d\u017A kolejarz clearex hurtap puchar polski ..."
          },
          {
            "http://www.amazon.com/Diary-Dawid-Sierakowiak-Notebooks-Ghetto/dp/0195122852",
            "Amazon.com: The Diary of "
                + "Dawid Sierakowiak: Five Notebooks from the Lodz Ghetto: Dawid Sierakowiak,Lawrence L. ...",
            "Amazon.com:"
                + " The Diary of Dawid Sierakowiak: Five Notebooks from the Lodz Ghetto: Dawid Sierakowiak,Lawrence L. "
                + "Langer,Alan Adelson,Kamil Turowski: Books"
          },
          {
            "http://shopping.yahoo.com/p:Kimberley%20Jim:1808599509",
            "Kimberley Jim - DVD at Yahoo! Shopping",
            "Yahoo! "
                + "Shopping is the best place to comparison shop for Kimberley Jim - DVD. Compare products, compare prices,"
                + " read reviews and merchant ratings."
          },
          {
            "http://www.ctfaceart.com/",
            "CT Face Art (203) 255-1875 - Chrys Dawid CTFaceArt@aol.com",
            "Award winning "
                + "Face Painting for children through adults. ... CT FACE ART is owned and operated by Chrys Dawid. CT FACE"
                + " ART is CT's finest face painting service. ..."
          },
          {
            "http://www.discogs.com/artist/Dawid+Szczesny",
            "Dawid Szczesny",
            "Submissions Drafts Collection Wantlist "
                + "Favorites Watchlist Friends ... Dawid Szczesny / artists (D) Real Name: Dawid Szcz\u0119sny. URLs: ..."
          },
          {
            "http://www.shop.com/+-p94105045-st.shtml",
            "York Ferry Annie Dawid - SHOP.COM",
            "Shop for York Ferry Annie "
                + "Dawid at Shop.com. $1.99 - york ferry annie dawid language:english, format:paperback, "
                + "fiction/non-fiction:fiction, publisher:cane hill pr,"
          },
          {
            "http://www.the-artists.org/artistsblog/posts/st_content_001.cfm?id=2600",
            "Dawid Michalczyk ...the-artists" + ".org",
            "Dawid Michalczyk; portfolio & art news...the-artists.org, modern and contemporary art ... Dawid "
                + "Michalczyk. Conflicting emotions. Suburbs 2100. After the ..."
          },
          {
            "http://www.dcorfield.pwp.blueyonder.co.uk/2006/06/dawid-on-probabilities.html",
            "Philosophy of Real " + "Mathematics: Dawid on probabilities",
            "... reading group ran through Phil Dawid's Probability, Causality"
                + " and the Empirical ... Dawid (pronounced 'David') holds a Bayesian position, made evident in his ..."
          },
          {
            "http://www.cs.put.poznan.pl/dweiss/xml/index.xml?lang=en",
            "Dawid Weiss - Main page",
            "Dawid Weiss, PhD. "
                + "Institute of Computing Science. Poznan University of Technology. ul. ... (Available as RSS) (c) Dawid "
                + "Weiss. All rights reserved unless stated ..."
          },
          {
            "http://www.dawid.eu/",
            "dawid.eu",
            "Hier entsteht dawid.eu ... dawid.eu. Hier entsteht in K\u00FCrze das "
                + "Projekt. dawid.eu. info@dawid.eu ..."
          },
          {
            "http://www.local.com/results.aspx?keyword=Dawid+Frank+B+Inc&location=06890",
            "Dawid Frank B Inc in " + "Southport, CT (Connecticut) @ Local.com",
            "Dawid Frank B Inc located in Southport, CT (Connecticut). "
                + "Find contact info, maps and directions for local contractors and home improvement services at Local.com."
          },
          {
            "http://www.anniedawid.com/shortfiction.htm",
            ": : Annie Dawid : : Short Fiction",
            "Annie Dawid is the "
                + "author of Resurrection City: A Novel of Jonestown (to be ... Copyright \u00A9 2007 Annie Dawid. Web Site"
                + " Design by Chameleon Web Design ..."
          },
          {
            "http://dawid.ca/",
            "www.dawid.ca",
            "I was in such a huge mistake. (Dawid Bober) ... 2006-02-26 Skating - "
                + "Agnieszka, Joanna, Michal, Dawid (Nathan Phillips Square \u2013 Toronto) ..."
          },
          {
            "http://www.planetizen.com/?q=about/correspondent/dawid",
            "Irvin Dawid | Planetizen",
            "Irvin Dawid is a "
                + "long-time Sierra Club activist, having worked in transportation, ... Irvin Dawid. Leo Vazquez. Mary "
                + "Reynolds. Michael Dudley. Mike Lydon ..."
          },
          {
            "http://www.sourcekibitzer.org/Bio.ext?sp=l6",
            "SourceKibitzer - Bio - Dawid Weiss",
            "Dawid Weiss - Bio. "
                + "Dawid Weiss. The founder of the Carrot2 project. Adjunct professor at the Laboratory of Intelligent "
                + "Decision Support Systems ..."
          },
          {
            "http://www.lulu.com/content/815029",
            "MD by Marcin and Dawid Witukiewicz (Music & Audio) in Electronic & " + "Dance",
            "MD by Marcin and Dawid Witukiewicz (Music & Audio) in Electronic & Dance : Music ... Music "
                + "inspierd by the photography of Marcin and Dawid. ..."
          },
          {
            "http://www.juliedawid.co.uk/index.php?page=Band",
            "Julie Dawid : Halfwise",
            "the songs of prize winning "
                + "folk singer and poet Julie Dawid. ... Also a lover and keeper of fish, professional storyteller Julie "
                + "Dawid ..."
          },
          {
            "http://www.jewishencyclopedia.com/view.jsp?artid=38&letter=M",
            "JewishEncyclopedia.com - MAGEN DAWID",
            "The"
                + " hexagram formed by the combination of two equilateral triangles; used as the ... The \"Magen Dawid,\" "
                + "therefore, probably did not originate withinRabbinism, the ..."
          },
          {
            "http://www.lulu.com/content/815298",
            "MD Photography by Marcin and Dawid Witukiewicz (Book) in Arts & " + "Photography",
            "... This is a book feturing some of Marcin and Dawid Witukiewicz photographic work. ... "
                + "by Marcin and Dawid Witukiewicz. Share This. Report this item. Preview ..."
          },
          {
            "http://finance.yahoo.com/q?s=dawid.x",
            "DAWID.X: Summary for DIA Sep 2008 134.0000 call - Yahoo! Finance",
            "Get detailed information on DIA Sep 2008 134.0000 call (DAWID.X) including quote performance, Real-Time "
                + "ECN, technical chart analysis, key stats, insider ..."
          },
          {
            "http://www.bikepics.com/members/dawid/",
            "BikePics - Dawid's Member Page on BikePics.Com",
            "Dawid's Member "
                + "Page. Member: dawid. Name: Dawid. From: ... You must be a BikePics Member and be logged in to message "
                + "members. Current: 1998 Suzuki GS 500 ..."
          },
          {
            "http://www.david-banner.com/main.html",
            "David Banner",
            "Universal Records \\ SRC \\ Artists \\ David " + "Banner ..."
          },
          {
            "http://www.dawid.com.pl/",
            "Kinga Dawid",
            "PORTRAITS by Kinga Dawid. Copying, dissemination, forwarding, "
                + "printing and/or ... All rights reserved. Copyright C 2006 Kinga Dawid ..."
          },
          {
            "http://www.bikepics.com/members/devdawid/",
            "BikePics - dawid's Member Page on BikePics.Com",
            "dawid's "
                + "Member Page. Member: devdawid. Name: dawid. From: Poland. Message: You must be a BikePics Member and be "
                + "logged in to message members. Current: 2002 ..."
          },
          {
            "http://dawid.bracka.pl/",
            "Portfolio",
            "google | portfolio | klan mortal. google | portfolio | klan mortal " + "..."
          },
          {
            "http://amiestreet.com/dawid",
            "Amie Street - DaWid's Music Store",
            "Amie Street empowers musicians to "
                + "release, and music fans to discover, new and ... music from DaWid. recommendations (3) more info. "
                + "SELECT: All, None, Free ..."
          },
          {
            "http://markoff.pl/",
            "Dawid Markoff Photography",
            "Nude, Fashion and Portrait photography"
          },
          {
            "http://www.archinect.com/schoolblog/blog.php?id=C0_372_39",
            "Archinect : Schoolblog : UC DAAP (Dawid)",
            "UC"
                + " DAAP (Dawid) (002) a couple of quotes and a mini thesis rant. Oct 02 2006, 6 comments ... UC DAAP "
                + "(Dawid) (001) it's the year of the thesis. Sep 06 2006, 4 ..."
          },
          {
            "http://groups.yahoo.com/group/dawid/rss",
            "dawid : RSS / XML",
            "dawid: Katechetyczne Forum Dyskusyjne ... "
                + "Sign In. dawid \u00B7 Katechetyczne Forum Dyskusyjne. Home. Messages. Members Only. Post. Files ..."
          },
          {
            "http://cssoff.com/2007/06/14/and-the-winner-is-dawid-lizak/",
            "CSS OFF",
            "And the Winner is Dawid Lizak. "
                + "View the winning entry. Dawid Lizak is from \u0141\u0119czna \u2013 a ... Dawid is currently expanding "
                + "his knowledge of JavaScript, usability, ..."
          },
        };

    final ArrayList<Document> documents = new ArrayList<>();
    for (String[] row : data) {
      FieldMapDocument doc = new FieldMapDocument();
      doc.addField("url", row[0]);
      doc.addField("title", row[1]);
      doc.addField("snippet", row[2]);
      documents.add(doc);
    }

    DOCUMENTS_DAWID = Collections.unmodifiableList(documents);
  }
}
