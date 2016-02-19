
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.test;

import java.util.*;

import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;

import org.carrot2.shaded.guava.common.collect.ImmutableList;
import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * A set of sample documents returned for the query <i>data mining</i>. This set is
 * hard-coded so that no other external components are needed to run tests (i.e., circular
 * dependency between the XML input component and the core).
 */
public final class SampleDocumentData
{
    public final static List<Document> DOCUMENTS_DATA_MINING;
    static
    {
        final String [][] data = new String [] []
        {
            {
                "http://en.wikipedia.org/wiki/Data_mining",
                "Data mining - Wikipedia, the free encyclopedia",
                "Article about knowledge-discovery in databases (KDD), the practice of automatically searching large stores of data for patterns."
            },

            {
                "http://www.ccsu.edu/datamining/resources.html",
                "CCSU - Data Mining",
                "A collection of Data Mining links edited by the Central Connecticut State University ... Graduate Certificate Program. Data Mining Resources. Resources. Groups ..."
            },

            {
                "http://www.kdnuggets.com/",
                "KDnuggets: Data Mining, Web Mining, and Knowledge Discovery",
                "Newsletter on the data mining and knowledge industries, offering information on data mining, knowledge discovery, text mining, and web mining software, courses, jobs, publications, and meetings."
            },

            {
                "http://en.wikipedia.org/wiki/Data-mining",
                "Data mining - Wikipedia, the free encyclopedia",
                "Data mining is considered a subfield within the Computer Science field of knowledge discovery. ... claim to perform \"data mining\" by automating the creation ..."
            },

            {
                "http://www.anderson.ucla.edu/faculty/jason.frand/teacher/technologies/palace/datamining.htm",
                "Data Mining: What is Data Mining?",
                "Outlines what knowledge discovery, the process of analyzing data from different perspectives and summarizing it into useful information, can do and how it works."
            },

            {
                "http://www.the-data-mine.com/",
                "Data Mining - Home Page (Misc)",
                "Provides information about data mining also known as knowledge discovery in databases (KDD) or simply knowledge discovery. List software, events, organizations, and people working in data mining."
            },

            {
                "http://www.spss.com/data_mining/",
                "Data Mining Software, Data Mining Applications and Data Mining Solutions",
                "... complete data mining customer ... Data mining applications, on the other hand, embed ... it, our daily lives are influenced by data mining applications. ..."
            },

            {
                "http://datamining.typepad.com/data_mining/",
                "Data Mining: Text Mining, Visualization and Social Media",
                "Commentary on text mining, data mining, social media and data visualization. ... Opinion Mining Startups ... in sentiment mining, deriving tuples of ..."
            },

            {
                "http://www.statsoft.com/textbook/stdatmin.html",
                "Data Mining Techniques",
                "Outlines the crucial concepts in data mining, defines the data warehousing process, and offers examples of computational and graphical exploratory data analysis techniques."
            },

            {
                "http://answers.yahoo.com/question/index?qid=1006040419333",
                "<b>answers.yahoo.com</b>/question/index?qid=1006040419333",
                "Generally, data mining (sometimes called data or knowledge discovery) is the ... Midwest grocery chain used the data mining capacity of Oracle software to ..."
            },

            {
                "http://www.ccsu.edu/datamining/master.html",
                "CCSU - Data Mining",
                "Details on how to apply to the Master of Science in data mining may be found here. ... All data mining majors are classified for business purposes as part-time ..."
            },

            {
                "http://databases.about.com/od/datamining/a/datamining.htm",
                "Data Mining: An Introduction",
                "About.com article on how businesses are discovering new trends and patterns of behavior that previously went unnoticed through data mining, automated statistical analysis techniques."
            },

            {
                "http://www.thearling.com/",
                "Data Mining and Analytic Technologies (Kurt Thearling)",
                "Kurt Thearling's site dedicated to sharing information about data mining, the automated extraction of hidden predictive information from databases, and other analytic technologies."
            },

            {
                "http://www.sas.com/technologies/analytics/datamining/index.html",
                "Data Mining Software and Text Mining | SAS",
                "Data mining is the process of selecting, exploring and modeling large amounts of ... The knowledge gleaned from data and text mining can be used to fuel ..."
            },

            {
                "http://databases.about.com/od/datamining/Data_Mining_and_Data_Warehousing.htm",
                "Data Mining and Data Warehousing",
                "From data mining tutorials to data warehousing techniques, you'll find it all! ... Administration Design Development Data Mining Database Training Careers Reviews ..."
            },

            {
                "http://www.oracle.com/technology/products/bi/odm/index.html",
                "Oracle Data Mining",
                "Oracle Data Mining Product Center ... Using data mining functionality embedded in Oracle Database 10g, you can find ... Mining High-Dimensional Data for ..."
            },

            {
                "http://www.ncdm.uic.edu/",
                "National Center for Data Mining - Welcome",
                "Conducts research in: scaling algorithms, applications and systems to massive data sets, developing algorithms, applications, and systems for mining distributed data, and establishing standard languages, protocols, and services for data mining and predictive modeling."
            },

            {
                "http://research.microsoft.com/dmx/DataMining/default.aspx",
                "Data Mining Project",
                "A long term Knowledge Discovery and Data Mining project which has the current ... Read more about how data mining is integrated into SQL server. Contact Us ..."
            },

            {
                "http://www.dmg.org/",
                "Data Mining Group - DMG",
                "... high performance networking, internet computing, data mining and related areas. ... Peter Stengard, Oracle Data Mining Technologies. prudsys AG, Chemnitz, ..."
            },

            {
                "http://datamining.typepad.com/data_mining/2006/05/the_truth_about.html",
                "Data Mining: Text Mining, Visualization and Social Media: The Truth About Blogs",
                "Commentary on text mining, data mining, social media and data visualization. ... Data Mining points to the latest papers from the 3rd International Workshop on ..."
            },

            {
                "http://searchsqlserver.techtarget.com/sDefinition/0,,sid87_gci211901,00.html",
                "What is data mining? - a definition from Whatis.com - see also: data miner, data analysis",
                "Data mining is the analysis of data for relationships that have not previously been discovered. ... Data mining techniques are used in a many research areas, ..."
            },

            {
                "http://www.thearling.com/text/dmwhite/dmwhite.htm",
                "An Introduction to Data Mining",
                "Data mining, the extraction of hidden predictive information from large ... prospective analyses offered by data mining move beyond the analyses of ..."
            },

            {
                "http://www.oracle.com/solutions/business_intelligence/data-mining.html",
                "Oracle Data Mining",
                "Using data mining functionality embedded in ... Oracle Data Mining JDeveloper and SQL Developer ... Oracle Magazine: Using the Oracle Data Mining API ..."
            },

            {
                "http://www.amazon.com/tag/data%20mining",
                "Amazon.com: data mining",
                "A community about data mining. Tag and discover new products. ... Data Mining (Paperback) Data Mining: Practical Machine Learning Tools and Techniques, Second Edition ..."
            },

            {
                "http://ocw.mit.edu/OcwWeb/Sloan-School-of-Management/15-062Data-MiningSpring2003/CourseHome/index.htm",
                "MIT OpenCourseWare | Sloan School of Management | 15.062 Data Mining, Spring 2003 | Home",
                "... class of methods known as data mining that assists managers in recognizing ... Data mining is a rapidly growing field that is concerned with developing ..."
            },

            {
                "http://www.sas.com/offices/europe/sweden/2746.html",
                "Om Data Mining och Text Mining. Ta fram s\u00E4kra beslutsunderlag med Data Miningverktyg fr\u00E5n SAS Institute.",
                "SAS Insitutes business intelligence ger v\u00E4rdefull kunskap till hela din ... Till\u00E4mpningen av data mining str\u00E4cker sig \u00F6ver m\u00E5nga branscher och omr\u00E5den. ..."
            },

            {
                "http://www.dmoz.org/Computers/Software/Databases/Data_Mining/",
                "Open Directory - Computers: Software: Databases: Data Mining",
                "Data Mining and Knowledge Discovery - A peer-reviewed journal publishing ... In assessing the potential of data mining based marketing campaigns one needs to ..."
            },

            {
                "http://www.investorhome.com/mining.htm",
                "Investor Home - Data Mining",
                "Data Mining or Data Snooping is the practice of searching for relationships and ... up by making a case study in data mining out of the Motley Fool's Foolish Four. ..."
            },

            {
                "http://www.amazon.com/Data-Mining-Concepts-Techniques-Management/dp/1558604898",
                "Amazon.com: Data Mining: Concepts and Techniques (The Morgan Kaufmann Series in Data Management Systems): Jiawei Han...",
                "Amazon.com: Data Mining: Concepts and Techniques (The Morgan Kaufmann Series in Data Management Systems): Jiawei Han,Micheline Kamber: Books"
            },

            {
                "http://www.monografias.com/trabajos/datamining/datamining.shtml",
                "Data Mining - Monografias.com",
                "Data Mining, la extracci\u00F3n de informaci\u00F3n oculta y predecible de grandes bases ... Las herramientas de Data Mining predicen futuras tendencias y comportamientos, ..."
            },

            {
                "http://www.megaputer.com/data_mining.php",
                "Data Mining Technology - Megaputer",
                "Data Mining Technology from Megaputer ... Typical tasks addressed by data mining include: ... Yet, data mining requires far more than just machine learning. ..."
            },

            {
                "http://datamining.itsc.uah.edu/", "itsc data mining solutions center",
                ""
            },

            {
                "http://www.dmreview.com/specialreports/20050503/1026882-1.html",
                "Hard Hats for Data Miners: Myths and Pitfalls of Data Mining",
                "This article debunks several myths about data mining and presents a plan of action to avoid some of the pitfalls. ... a typical data mining conference or ..."
            },

            {
                "http://research.microsoft.com/dmx/",
                "Data Management, Exploration and Mining- Home",
                "The Data Management Exploration and Mining Group (DMX) ... Our research effort in data mining focuses on ensuring that traditional ..."
            },

            {
                "http://www.biomedcentral.com/info/about/datamining",
                "BioMed Central | about us | Data mining research",
                "... a collection of links to publications on the subject of biomedical text mining. Data mining Open Access research - an article in the 8 September 2003 edition of ..."
            },

            {
                "http://www.datapult.com/Data_Mining.htm",
                "Data Mining",
                "Data Mining Services provide customers with tools to quickly sift through the ... into Datapult Central for use with Data Mining tools and other Datapult products. ..."
            },

            {
                "http://www.siam.org/meetings/sdm02/",
                "SIAM International Conference on Data Mining",
                "SIAM International Conference on Data Mining, co-Sponsored by AHPCRC and ... Clustering High Dimensional Data and its Applications. Mining Scientific Datasets ..."
            },

            {
                "http://dir.yahoo.com/Computers_and_Internet/Software/Databases/Data_Mining/",
                "Data Mining in the Yahoo! Directory",
                "Learn about data mining and knowledge discovery, the process of finding patterns ... Cross Industry Standard Process for Data Mining (CRISP-DM) ..."
            },

            {
                "http://www.llnl.gov/str/Kamath.html",
                "Data Mining",
                "... Sapphire-a semiautomated, flexible data-mining software infrastructure. ... Data mining is not a new field. ... scale, scientific data-mining efforts such ..."
            },

            {
                "http://www.sqlserverdatamining.com/",
                "SQL Server Data Mining > Home",
                "SQL Server Data Mining Portal ... information about our exciting data mining features. ... CTP of Microsoft SQL Server 2008 Data Mining Add-Ins for Office 2007 ..."
            },

            {
                "http://www.dbmsmag.com/9807m01.html",
                "DBMS - DBMS Data Mining Solutions Supplement",
                "As recently as two years ago, data mining was a new concept for many people. Data mining products were new and marred by unpolished interfaces. ..."
            },

            {
                "http://www.oclc.org/research/projects/mining",
                "Data mining [OCLC - Projects]",
                "Describes the goals, methodology, and timing of the Data mining project."
            },

            {
                "http://www.the-data-mine.com/bin/view/Misc/IntroductionToDataMining",
                "Data Mining - Introduction To Data Mining (Misc)",
                "Some example application areas are listed under Applications Of Data Mining ... Crows Introduction - \"Introduction to Data Mining and Knowledge Discovery\"- http: ..."
            },

            {
                "http://www.pentaho.com/products/data_mining/",
                "Pentaho Commercial Open Source Business Intelligence: Data Mining",
                "... (BI) to the next level by adding data mining and workflow to the mix. ... Pentaho Data Mining is differentiated by its open, standards-compliant nature, ..."
            },

            {
                "http://www.unf.edu/~selfayou/html/data_mining.html",
                "Data Mining",
                "This course approaches data mining topics from an Artificial Intelligence ... The course will also cover Applications and Trends in Data Mining. Textbook: ..."
            },

            {
                "http://www.statsoft.com/products/dataminer.htm",
                "Data Mining Software & Predictive Modeling Solutions",
                "data mining software & predictive modeling sold online by statsoft.com. ... of automated and ready-to-deploy data mining solutions for a wide variety of ..."
            },

            {
                "http://gosset.wharton.upenn.edu/wiki/index.php/Main_Page",
                "Main Page - Knowledge Discovery",
                "The Penn Data Mining Group develops principled means of modeling and ... knowledge of specific application areas to develop new approaches to data mining. ..."
            },

            {
                "http://www.twocrows.com/glossary.htm",
                "Two Crows: Data mining glossary",
                "Data mining terms concisely defined. ... Accuracy is an important factor in assessing the success of data mining. ... data mining ..."
            },

            {
                "http://www.cdc.gov/niosh/mining/data/",
                "NIOSH Mining: MSHA Data File Downloads | CDC/NIOSH",
                "MSHA accident, injury, employment, and production data files in SPSS and dBase formats ... Data files on mining accidents, injuries, fatalities, employment, ..."
            },

            {
                "http://www.cartdatamining.com/", "Salford Data mining 2006",
                "Objective | Previous Conferences | Call for Abstracts | LATEST INFO ..."
            },

            {
                "http://www.inductis.com/",
                "Data Mining | Focused Data Mining For Discovery To Assist Management",
                "Inductis offers high-level data mining services to assist management decisions ... The Data Mining Shootout ...more>> ISOTech 2006 - The Insurance Technology ..."
            },

            {
                "http://www.datamininglab.com/",
                "Elder Research: Predictive Analytics & Data Mining Consulting",
                "Provides consulting and short courses in data mining and pattern discovery patterns in data."
            },

            {
                "http://www.microsoft.com/sql/technologies/dm/default.mspx",
                "Microsoft SQL Server: Data Mining",
                "Microsoft SQL Server Data Mining helps you explore your business data and discover patterns to reveal the hidden trends about your products, customer, market, and ..."
            },

            {
                "http://www.dataminingcasestudies.com/",
                "Data Mining Case Studies",
                "Recognizing outstanding practical contributions in the field of data mining. ... case studies are one of the most discussed topics at data mining conferences. ..."
            },

            {
                "http://www.webopedia.com/TERM/D/data_mining.html",
                "What is data mining? - A Word Definition From the Webopedia Computer Dictionary",
                "This page describes the term data mining and lists other pages on the Web where you can find additional information. ... Data Mining and Analytic Technologies ..."
            },

            {
                "http://www.cs.waikato.ac.nz/~ml/weka/book.html",
                "Data Mining: Practical Machine Learning Tools and Techniques",
                "Book. Data Mining: Practical Machine Learning Tools and Techniques (Second Edition) ... Explains how data mining algorithms work. ..."
            },

            {
                "http://www.datamining.com/",
                "Predictive Modeling and Predictive Analytics Solutions | Enterprise Miner Software from Insightful Software",
                "Insightful Enterprise Miner - Enterprise data mining for predictive modeling and predictive analytics."
            },

            {
                "http://www.sra.com/services/index.asp?id=153",
                "SRA International - Data Mining Solutions",
                "... and business who ask these questions are finding solutions through data mining. ... Data mining is the process of discovering previously unknown relationships in ..."
            },

            {
                "http://en.wiktionary.org/wiki/data_mining",
                "data mining - Wiktionary",
                "Data mining. Wikipedia. data mining. a technique for searching large-scale databases for patterns; used mainly to ... Czech: data mining n., dolov\u00E1n\u00ED dat n. ..."
            },

            {
                "http://www.datamining.org/", "data mining institute", ""
            },

            {
                "http://videolectures.net/Top/Computer_Science/Data_Mining/",
                "Videolectures category: Data Mining",
                "Next Generation Data Mining Tools: Power laws and self-similarity for graphs, ... Parallel session 4 - Hands-on section Data mining with R. Luis Torgo. 1 comment ..."
            },

            {
                "http://www2008.org/CFP/RP-data_mining.html",
                "WWW2008 CFP - WWW 2008 Call For Papers: Refereed Papers - Data Mining",
                "WWW2008 - The 17th International World Wide Web Conference - Beijing, China (21 - 25 April 2008) Hosted by Beihang Universit ... data mining, machine ..."
            },

            {
                "http://answers.yahoo.com/question/index?qid=20070227091350AAVDlI1",
                "what is data mining?",
                "... the purchases of customers, a data mining system could identify those customers ... A simple example of data mining, often called Market Basket Analysis, ..."
            },

            {
                "http://clubs.yahoo.com/clubs/datamining",
                "datamining2 : Data Mining Club - 1600+ members!!",
                "datamining2: Data Mining Club - 1600+ members!"
            },

            {
                "http://www.siam.org/meetings/sdm01/",
                "First SIAM International Conference on Data Mining",
                "The field of data mining draws upon extensive work in areas such as statistics, ... recent results in data mining, including applications, algorithms, software, ..."
            },

            {
                "http://www.statserv.com/datamining.html",
                "St@tServ - About Data Mining",
                "St@tServ Data Mining page ... Data mining in molecular biology, by Alvis Brazma. Graham Williams page. Knowledge Discovery and Data Mining Resources, ..."
            },

            {
                "http://www.springer.com/computer/database+management+&+information+retrieval/journal/10618",
                "Data Mining and Knowledge Discovery - Data Mining and Knowledge Discovery Journals, Books & Online Media | Springer",
                "Technical journal focused on the theory, techniques, and practice for extracting information from large databases."
            },

            {
                "http://msdn2.microsoft.com/en-us/library/ms174949.aspx",
                "Data Mining Concepts",
                "Data mining is frequently described as &quot;the process of extracting ... Creating a data mining model is a dynamic and iterative process. ..."
            },

            {
                "http://www.cs.wisc.edu/dmi/",
                "DMI:Data Mining Institute",
                "Data Mining Institute at UW-Madison ... The Data Mining Institute (DMI) was ... Corporation with the support of the Data Mining Group of Microsoft Research. ..."
            },

            {
                "http://www.dataminingconsultant.com/",
                "DataMiningConsultant.com",
                "... Website for Data Mining Methods and ... data mining at Central Connecticut State University, he ... also provides data mining consulting and statistical ..."
            },

            {
                "http://www.dmreview.com/channels/data_mining.html",
                "Data Mining",
                "... business intelligence, data warehousing, data mining, CRM, analytics, ... M2007 Data Mining Conference Hitting 10th Year and Going Strong ..."
            },

            {
                "http://www.unc.edu/~xluan/258/datamining.html",
                "Data Mining",
                "What is the current state of data mining? The immediate future ... Data Mining is the process of extracting knowledge hidden from large volumes of ..."
            },

            {
                "http://www.data-miners.com/",
                "Data Miners Inc. We wrote the book on data mining!",
                "Data mining consultancy; services include predictive modeling, consulting, and seminars."
            },

            {
                "http://www.versiontracker.com/dyn/moreinfo/macosx/27607",
                "Data Mining 2.2.2 software download - Mac OS X - VersionTracker",
                "Find Data Mining downloads, reviews, and updates for Mac OS X including commercial software, shareware and freeware on VersionTracker.com."
            },

            {
                "http://www.webtechniques.com/archives/2000/01/greening/",
                "New Architect: Features",
                "Article by Dan Greening on data mining techniques applied to analyzing and making decisions from web data. ... and business analysts use data-mining techniques. ..."
            },

            {
                "http://www.networkdictionary.com/software/DataMining.php",
                "Data Mining | NetworkDictionary",
                "Data Mining is the automated extraction of hidden predictive information from databases. ... The data mining tools can make this leap. ..."
            },

            {
                "http://www.youtube.com/watch?v=wqpMyQMi0to",
                "YouTube - What is Data Mining? - February 19, 2008",
                "Association Labratory President and CEO Dean West discusses Data Mining and how it can be applied to associations. ... Data Mining Association Forum Dean West ..."
            },

            {
                "http://www.cs.sfu.ca/~han/DM_Book.html",
                "Book page",
                "Chapter 4. Data Mining Primitives, Languages, and System Architectures ... Chapter 9. Mining Complex Types of Data ... to Microsoft's OLE DB for Data Mining ..."
            },

            {
                "http://www.twocrows.com/",
                "Two Crows data mining home page",
                "Dedicated to the development, marketing, sales and support of tools for knowledge discovery to make data mining accessible and easy to use."
            },

            {
                "http://www.autonlab.org/tutorials",
                "Statistical Data Mining Tutorials",
                "Includes a set of tutorials on many aspects of statistical data mining, including the foundations of probability, the foundations of statistical data analysis, and most of the classic machine learning and data mining algorithms."
            },

            {
                "http://ecommerce.ncsu.edu/technology/topic_Datamining.html",
                "E-commerce Technology: Data Mining",
                "\"Web usage mining: discovery and applications of web usage patterns from web data\" ... Patterns and Trends by Applying OLAP and Data Mining Technology on Web Logs. ..."
            },

            {
                "http://www.teradata.com/t/page/106002/index.html",
                "Teradata Data Mining Warehouse Solution",
                "... a high-powered analytic warehouse that streamlines the data mining process. ... while building the analytic model using your favorite data mining tool. ..."
            },

            {
                "http://datamining.japati.net/",
                "Indo Datamining",
                "Apa yang bisa dan tidak bisa dilakukan data mining ? ... Iko Pramudiono \"&raquo ... Apa itu data mining ? Iko Pramudiono \"&raquo. artikel lainnya \" tutorial ..."
            },

            {
                "http://www.affymetrix.com/products/software/specific/dmt.affx",
                "Affymetrix - Data Mining Tool (DMT) (Unsupported - Archived Product)",
                "Affymetrix is dedicated to developing state-of-the-art technology for acquiring, analyzing, and managing complex genetic ... The Data Mining Tool (DMT) ..."
            },

            {
                "http://www.pcc.qub.ac.uk/tec/courses/datamining/stu_notes/dm_book_1.html",
                "Data Mining Student Notes, QUB",
                "2 - Data Mining Functions. 2.1 - Classification. 2.2 - Associations ... 5 - Data Mining Examples. 5.1 - Bass Brewers. 5.2 - Northern Bank. 5.3 - TSB Group PLC ..."
            },

            {
                "http://www.spss.com/text_mining_for_clementine/",
                "Text Mining for Clementine | Improve the accuracy of data mining",
                "Text Mining for Clementine from SPSS enables you to use text data to improve the accuracy of predictive models. ... and about data mining in general. ..."
            },

            {
                "http://www.open-mag.com/features/Vol_16/datamining/datamining.htm",
                "Data Mining",
                "Without data mining, a merchant isn't even close to leveraging what customers want and will buy. ... Data mining is to be found in applications like bio ..."
            },

            {
                "http://wordpress.com/tag/data-mining/",
                "Data Mining \u2014 Blogs, Pictures, and more on WordPress",
                "Going Beyond the Numbers: Context-Sensitive Data Mining ... Data mining examples ... many websites employing data mining technology to provide recommendation ..."
            },

            {
                "http://www.dmbenchmarking.com/",
                "Benchmarking- Data Mining Benchmarking Association",
                "Association of companies and organizations working to identify \"best in class\" data mining processes through benchmarking studies."
            },

            {
                "http://www.dataentryindia.com/data_processing/data_mining.php",
                "Data Mining, Data Mining Process, Data Mining Techniques, Outsourcing Mining Data Services",
                "... Walmart, Fundraising Data Mining, Data Mining Activities, Web-based Data Mining, ... in many industries makes us the best choice for your data mining needs. ..."
            },

            {
                "http://www.target.com/Data-Mining-Applications-International-Information/dp/1853127299",
                "Data Mining V: Data Mining, Text Mining... [Hardcover] | Target.com",
                "Shop for Data Mining V: Data Mining, Text Mining and Their Business Applications : Fifth International Conference on Data Mining (Management Information System) at"
            },

            {
                "http://www.cs.ubc.ca/~rng/research/datamining/data_mining.htm",
                "Data Mining",
                "... varying degrees of success, the data mining tools developed thus far, by and ... (a) we should recognize that data mining is a multi-step process, and that (b) ..."
            },

            {
                "http://jcp.org/en/jsr/detail?id=73",
                "The Java Community Process(SM) Program - JSRs: Java Specification Requests - detail JSR# 73",
                "Currently, there is no widely agreed upon, standard API for data mining. By using JDMAPI, implementers of data mining applications can expose a single, ..."
            },

            {
                "http://www.microsoft.com/spain/sql/technologies/dm/default.mspx",
                "Microsoft SQL Server2005: Data Mining",
                "Data Mining es la tecnolog\u00EDa BI que le ayudar\u00E1 a construir modelos anal\u00EDticos complejos e integrar esos modelos con sus operaciones comerciales."
            },

            {
                "http://www.bos.frb.org/economic/nerr/rr2000/q3/mining.htm",
                "Regional Review: Mining Data",
                "Although data mining by itself is not going to get the Celtics to the playoffs, ... then, firms that specialize in data-mining software have been developing a ..."
            },

            {
                "http://www.scianta.com/technology/datamining.htm",
                "Data Mining",
                "... are excellent candidates for data mining, fault prediction, problem diagnosis, ... Data Mining uses this theory to support Link and Affinity Group analysis \u2013 an ..."
            },

            {
                "http://www.gusconstan.com/DataMining/index.htm",
                "Discovery and Mining",
                "Verification-Driven Data Mining. Advantages of Symbolic Classifiers. Manual vs. Automatic ... Currently, data mining solutions have been developed by large software ..."
            },

            {
                "http://www.dataminingconsultant.com/DKD.htm",
                "DataMiningConsultant.com",
                "Companion Website for Data Mining Methods and Models ... \"This is an excellent introductory book on data mining. ... An Introduction to Data Mining at Amazon.com ..."
            },

            {
                "http://www.pfaw.org/pfaw/general/default.aspx?oid=9717",
                "People For the American Way - Data Mining",
                "data mining, civil liberties, civil rights, terrorism, september 11th, anti-terrorism, ashcroft, government intrusion, privacy, email, patriot, american"
            },

            {
                "http://dm1.cs.uiuc.edu/",
                "Data Mining Research Group",
                "... conducting research in various areas in data mining and other related fields. ... on Data Mining (SDM'08), (full paper), Atlanta, GA, April 2007. ..."
            }
        };

        final ArrayList<Document> documents = new ArrayList<Document>();
        for (String [] row : data)
        {
            documents.add(new Document(row[1], row[2], row[0]));
        }

        DOCUMENTS_DATA_MINING = Collections.unmodifiableList(documents);
    }

    public final static List<Document> DOCUMENTS_DAWID;
    static
    {
        final String [][] data = new String [] []
        {
            {
                "http://www.dawid.tv/",
                "dawid.tv",
                "Watch free videos on dawid.tv. Now Playing: DAWID DRIF ... About. Dawid. Bielawa - Poland. Friends: 1. Last Login: ... View All Members of dawid.tv. Tag Cloud ..."
            },

            {
                "http://www.dawid.co.za/", "DAWID",
                "Welkom by: Dawid Bredenkamp se webtuiste. Foto's. Skakels. Kontak ..."
            },

            {
                "http://www.dawid-nowak.org/",
                "Dawid Nowak",
                "Dawid Nowak Home Page ... Resume. Gallery. Thailand. Still in Thailand. Into Laos. Through Laos To Cambodia. RSS feeds for lazy technically oriented people ..."
            },

            {
                "http://dawid.digitalart.org/",
                "dawid.digitalart.org - Profile of Dawid Michalczyk",
                "A gallery of masterfully created works of digital art. ... Dawid Michalczyk \" Send Private Message \" Send an E-mail. Art Gallery (13) Guestbook ..."
            },

            {
                "http://www.dawid.nu/index.php?ID=4",
                "dawid :: images / commercial work :: advertising & illustrations",
                "The official site of photographer Dawid, Bj\u00F6rn Dawidsson. Fotograf Dawid - Bj\u00F6rn Dawidsson ... references: AB Vin & Sprit, Apple, Berliner, Bond, Ericsson, ..."
            },

            {
                "http://www.dawidphotography.com/",
                "Photographer London UK, Dawid de Greeff \u00A9 2007 , Digital photographer - Portfolio",
                "South African born Dawid & Annemarie de Greeff are International digital ... NAME. EMAIL. MESSAGE ..."
            },

            {
                "http://www.anniedawid.com/",
                ": : Annie Dawid : : Author and Photographer",
                "Annie Dawid is the author of Resurrection City: A Novel of Jonestown (to be ... Annie Dawid lives and writes in the Sangre de Cristo range of South-Central Colorado. ..."
            },

            {
                "http://en.wikipedia.org/wiki/Dawid_Janowski",
                "Dawid Janowski - Wikipedia, the free encyclopedia",
                "Dawid Markelowicz Janowski (in English usually called David Janowski) (born 25 ... Dawid Janowski died on January 15, 1927 of tuberculosis. ..."
            },

            {
                "http://www.dawid.nu/index.php?ID=2",
                "dawid :: images / art :: COMP",
                "The official site of photographer Dawid, Bj\u00F6rn Dawidsson. Fotograf Dawid - Bj\u00F6rn Dawidsson ... dawid : images / art : COMP: Series photographed during the mid 80's. ..."
            },

            {
                "http://en.wikipedia.org/wiki/Dawid",
                "Dawid - Wikipedia, the free encyclopedia",
                "Dawid. From Wikipedia, the free encyclopedia. Jump to: navigation, search. Dawid may refer to the following people: David, the biblical King David ..."
            },

            {
                "http://www.myspace.com/dawidszczesny",
                "MySpace.com - dawid szczesny - Wroclaw - www.myspace.com/dawidszczesny",
                "MySpace music profile for dawid szczesny with tour dates, songs, videos, pictures, blogs, band information, downloads and more"
            },

            {
                "http://www.art.eonworks.com/",
                "Computer wallpaper, stock illustration, Sci-Fi art, Fantasy art, Surreal art, Space art, Abstract art - posters, ...",
                "Digital Art of Dawid Michalczyk. Unique posters, prints, wallpapers and wall calendars. ... the official website of Dawid Michalczyk - a freelance illustrator ..."
            },

            {
                "http://www.surfski.info/content/view/384/147/",
                "Surf Ski . Info - Dawid Mocke King of the Harbour 2007",
                "Surf Ski information and news. Training tips from the experts, equipment, getting started guides, surfski reviews, photos ...links and stories."
            },

            {
                "http://www.agentsbase.com/",
                "Agent's Base",
                "Dawid Kasperowicz. Get Firefox. Get Google Ads. Affiliates ... By Dawid | February 28, 2008 - 12:05 pm - Posted in Technology ..."
            },

            {
                "http://www.target.com/Dawid-Dawidsson-Bjorn/dp/3882437243",
                "Dawid [Hardcover] | Target Official Site",
                "Shop for Dawid at Target. Choose from a wide range of Books. Expect More, Pay Less at Target.com"
            },

            {
                "http://www.dawid.tobiasz.org/",
                "Dawid",
                "Dawid. Fotografia stanowi w\u0142asno\u015B\u0107 autora. Kopiowanie i rozpowszechnianie ... Copyright by Dawid Tobiasz [Fotografia stanowi w\u0142asno\u015B\u0107 autora. ..."
            },

            {
                "http://juliedawid.co.uk/",
                "Julie Dawid :",
                "birthing support scotland. Poetical Fusion Folk. Words. Band. Listen. Contact. Copyright \u00A9 2004 Julie Dawid. All Rights reserved. Powered by Accidental Media ..."
            },

            {
                "http://conference.dawid.uni.wroc.pl/index.php?lang=iso-8859-2",
                "konferencja - Welcome",
                "Joomla - the dynamic portal engine and content management system ... The 1st Symposium of Pedagogy and Psychology PhD Students. Monday, 13 February 2006 ..."
            },

            {
                "http://www.ibe.unesco.org/publications/ThinkersPdf/dawide.pdf",
                "Jan Wladyslaw Dawid",
                "All his life, Jan Wladyslaw Dawid was closely associated with the teaching ... Dawid who believed that these experiments were fundamental to the blossoming and ..."
            },

            {
                "http://www.dawid-posciel.pl/", "www.<b>dawid-posciel.pl</b>", ""
            },

            {
                "http://www.dawidrurkowski.com/",
                "Dawid Rurkowski - portfolio",
                "Dawid Rurkowski online webdesign portfolio ... My name is Dawid, I am a web designer with a real passion to my work. ... \u00A9 Copyright 2007 Dawid Rurkowski All ..."
            },

            {
                "http://conference.dawid.uni.wroc.pl/index.php?option=com_content&task=blogsection&id=20&Itemid=49%E2%8C%A9=iso-8859-2",
                "konferencja - Warsztaty",
                "Joomla - the dynamic portal engine and content management system ... Karolina Pietras is a psychologist, business trainer and PhD student at Faculty ..."
            },

            {
                "http://chess.about.com/library/persons/blp-jano.htm",
                "Famous Chess Players - Dawid Janowsky",
                "Beginners Improve Your Game Play Chess Online Chess Downloads Computers and ... Dawid Janowsky. Unsuccessful challenger for World Championship ..."
            },

            {
                "http://www.pbase.com/dawidwnuk",
                "Dawid Wnuk's Photo Galleries at pbase.com",
                "All images on this site copyrighted by DAWID WNUK. Please contact me if you would like to purchase or licence a photograph. Portraiture ..."
            },

            {
                "http://dawid-witos.nazwa.pl/chylu/en/index.php?link=news",
                "...Official Website of Michael Chylinski...",
                "Welcome to chylinski.info- the official web site of Polish National Team and ... We invite you to visite our service and write your opinions on forum. A few ..."
            },

            {
                "http://photoexposed.com/", "photoeXposed.com",
                "Dawid Slaski-Sawicki Photography"
            },

            {
                "http://vids.myspace.com/index.cfm?fuseaction=vids.individual&VideoID=7370487",
                "MySpaceTV Videos: Edyp trailer by dawid",
                "Edyp trailer by dawid Watch it on MySpace Videos. ... Posted by: dawid. Runtime: 0:52. Plays: 43. Comments: 0. Reinkarnacje - \"Czy to mi..."
            },

            {
                "http://www.linkedin.com/in/dawidmadon",
                "LinkedIn: Dawid Mado\u0144",
                "Dawid Mado\u0144's professional profile on LinkedIn. ... Dawid Mado\u0144. ORACLE DBA at Apriso and Information Technology and Services Consultant ..."
            },

            {
                "http://www.linkedin.com/pub/1/878/410",
                "LinkedIn: Dawid Tracz",
                "Dawid Tracz's professional profile on LinkedIn. ... Dawid Tracz's Experience. Graphician, WebDesigner, InterfaceDesigner. DreamLab Onet.pl Sp. ..."
            },

            {
                "http://profiles.friendster.com/13547484",
                "Friendster - Dawid Martin",
                "Friendster: ; location: Poland, PL; Kiedrowice, Warsaw (Poland),Jogja (Indonesia); Warsaw Gamelan Group, Bosso, Tepellere, Mandala, Suita Etnik, Konco-Konco Blues ..."
            },

            {
                "http://www.genevievedawid.com/",
                "Genevieve Dawid mentor, lecturer and author",
                "Author of the Achiever's Journey a real self help book for dyslexics, Genevieve Dawid offers a unique approach to mentoring and personal development."
            },

            {
                "http://www.last.fm/music/dawid+szczesny",
                "dawid szczesny \u2013 Music at Last.fm",
                "People who like dawid szczesny also like Masayasu Tzboguchi Trio, Ametsub, ... Dawid Szcz\u0119sny performed in Poland, Germany (in 2005 invited by Kata Adamek and ..."
            },

            {
                "http://vids.myspace.com/index.cfm?fuseaction=vids.individual&videoid=2028359840",
                "MySpaceTV Videos: paka 2007-1 by dawid",
                "paka 2007-1 by dawid Watch it on MySpace Videos. ... Posted by: dawid. Runtime: 0:52. Plays: 43. Comments: 0. Reinkarnacje - \"Czy to mi..."
            },

            {
                "http://dawid.secondbrain.com/",
                "Dawid's profile page - Second Brain_ - All Your Content",
                "Dawid. People first, strategy second ... Dawid's recent updates. February 07 2008. Wimbledon ... Posted by Dawid on Second Brain February 05 2008. Post comment ..."
            },

            {
                "http://www.ushmm.org/wlc/article.php?lang=en&ModuleId=10007294",
                "Dawid Sierakowiak",
                "Dawid was an avid reader and an excellent observer. Throughout Dawid's imprisonment in the Lodz ghetto he made sure to write about ..."
            },

            {
                "http://www.ctbodyartist.com/",
                "CT Body Artist | Chrys Dawid (203) 255-1875",
                "CT Body Artist, Chrys Dawid (203) 255-1875 Professional Body painting service. From Advertising Champaigns to Private parties, make your statement & Marketing goals ..."
            },

            {
                "http://www.amazon.com/phrase/Dawid-Sierakowiak",
                "Amazon.com: \"Dawid Sierakowiak\": Key Phrase page",
                "Key Phrase page for Dawid Sierakowiak: Books containing the phrase Dawid Sierakowiak ... Key Phrases: Dawid Sierakowiak, United States, New York, Niutek ..."
            },

            {
                "http://www.planetizen.com/user/403/track",
                "Irvin Dawid | Planetizen",
                "Irvin Dawid. 0. 2 weeks 20 hours ago. news ... Irvin Dawid. 1. 3 weeks 5 days ago. news. Traffic Crashes Cost Twice as Much as Congestion ..."
            },

            {
                "http://www.ushmm.org/wlc/idcard.php?lang=en&ModuleId=10006389",
                "Dawid Szpiro",
                "Dawid was the older of two sons born to Jewish parents in Warsaw. ... of Warsaw's Jewish district, where Dawid and his brother, Shlomo, attended Jewish schools. ..."
            },

            {
                "http://groups.yahoo.com/group/dawid",
                "dawid : Katechetyczne Forum Dyskusyjne",
                "dawid \u00B7 Katechetyczne Forum Dyskusyjne. Home. Messages ... Lista dyskusyjna strony internetowej DAWID. Most Recent Messages (View All) (Group by Topic) ..."
            },

            {
                "http://www.blogger.com/profile/01359115939699161533",
                "Blogger: User Profile: Dawid",
                "Push-Button Publishing. Dawid. Blogs. Blog Name. Team Members. Midwest Petanque Alliance BLOG ... MGal hdarpini chilipepper diveborabora DanDan Mike A testerin ..."
            },

            {
                "http://www.blogger.com/profile/15768169977536938605",
                "Blogger: User Profile: David",
                "kilconriola Credo Perp\u00E9tua Amanda Liturgeist Chris + AMDG + +Miguel Vinuesa+ Royal Girl ... roydosan chrysogonus Brownthing Aristotle Boeciana Amanda Lactantius Juan ..."
            },

            {
                "http://www.babynamer.com/Dawid",
                "Dawid on BabyNamer",
                "For parents-to-be who want to confidently choose potential names for their baby, ... Dawid. Meaning: Its source is a ... baby name page for boy name Dawid. ..."
            },

            {
                "http://profile.myspace.com/index.cfm?fuseaction=user.viewprofile&friendid=38408574",
                "MySpace.com - Dawid - 26 - Male - FR - www.myspace.com/trastaroots",
                "MySpace profile for Dawid with pictures, videos, personal blog, interests, information about me and more ... yo dawid, ya un gars de ta r\u00E9gion (koubiak) qui ..."
            },

            {
                "http://www.imdb.com/name/nm1058743/",
                "Dawid Kruiper",
                "Actor: Liebe. Macht. Blind.. Visit IMDb for Photos, Filmography, Discussions, Bio, News, Awards, Agent, Fan Sites. ... on IMDb message board for Dawid Kruiper ..."
            },

            {
                "http://citeseer.ist.psu.edu/context/55656/0",
                "Citations: Conditional independence in statistical theory - Dawid (ResearchIndex)",
                "A. P. Dawid. Conditional independence in statistical theory (with discussion). J. Roy. ... To capture Dawid s property for overlapping sets, Pearl introduces ..."
            },

            {
                "http://www.dawid.pl/gb/main.php",
                "Systemy ogrodzeniowe, ta\u015Bmy, sita, siatki - DAWID Cz\u0119stochowa",
                "Firma DAWID - Producent siatki ogrodzeniowej, bram, furtek, paneli D-1, D-2 itp. Cz\u0119stochowa. ... DAWID Company has a long-standing tradition which has been ..."
            },

            {
                "http://www.imdb.com/name/nm2014139/",
                "Dawid Jakubowski",
                "Miscellaneous Crew: Once Upon a Knight. Visit IMDb for Photos, Filmography, Discussions, Bio, News, Awards, Agent, Fan Sites."
            },

            {
                "http://www.lclark.edu/cgi-bin/shownews.cgi?1011726000.1",
                "Dawid publishes Lily in the Desert",
                "Lewis & Clark College: Dawid publishes <i>Lily in the Desert</i> ... Annie Dawid is one of those all-too-rare writers who fully inhabits each ..."
            },

            {
                "http://dir.nichd.nih.gov/lmg/lmgdevb.htm",
                "Igor Dawid Lab Home Page",
                "Dawid Lab. Welcome to Igor Dawid's lab in the Laboratory of Molecular Genetics, ... National Institute of Child Health and Human Development, National ..."
            },

            {
                "http://www.ucl.ac.uk/~ucak06d/",
                "Philip Dawid",
                "DEPARTMENT OF STATISTICAL SCIENCE. UNIVERSITY COLLEGE LONDON. A. Philip Dawid ... Professor A. P. Dawid, Department of Statistical Science, University College London, ..."
            },

            {
                "http://www.pbase.com/dawidwnuk/profile",
                "pbase Artist Dawid Wnuk",
                "View Galleries : Dawid Wnuk has 5 galleries and 487 images online. ... My name is Dawid and I'm a photographer from Warsaw, Poland. ..."
            },

            {
                "http://dawidfrederik.deviantart.com/",
                "DawidFrederik on deviantART",
                "Art - community of artists and those devoted to art. ... Dawid Frederik Strauss. Profile Gallery Faves Journal. Status: deviantART Subscriber ..."
            },

            {
                "http://citeseer.ist.psu.edu/context/332153/0",
                "Citations: Statistical theory - Dawid (ResearchIndex)",
                "Dawid, P. (1984). Statistical theory. The prequential approach (with discussion) . Journal of the Royal Statistical Society A, 147:178--292."
            },

            {
                "http://www.infinitee-designs.com/Dawid-Michalczyk.htm",
                "Dawid Michalczyk Artist of the Month Space Art",
                "Artist of the Month, Dawid Michalczyk Abstract 3D Space Art, Visions, computer graphics, 2D illustration, sci-fi, fantasy, digital art"
            },

            {
                "http://www.myspace.com/dawidgatti",
                "MySpace.com - dawid - 26 - Male - www.myspace.com/dawidgatti",
                "MySpace profile for dawid with pictures, videos, personal blog, interests, information about me and more ... to meet: dawid's Friend Space (Top 1) dawid has 1 ..."
            },

            {
                "http://ezinearticles.com/?expert=Genevieve_Dawid",
                "Genevieve Dawid - EzineArticles.com Expert Author",
                "Genevieve Dawid is a published author and highly successful ... Genevieve Dawid's Extended ... [Business:Management] Genevieve Dawid explores the history of ..."
            },

            {
                "http://www.artnet.com/artist/698445/dawid-bjorn-dawidsson.html",
                "Dawid (Bjorn Dawidsson) on artnet",
                "Dawid (Bjorn Dawidsson) (Swedish, 1949) - Find works of art, auction results & sale prices of artist Dawid (Bjorn Dawidsson) at galleries and auctions worldwide."
            },

            {
                "http://www.glennshafer.com/assets/downloads/other12.pdf",
                "Comments on \"Causal Inference without Counterfactuals\" by A.P. Dawid",
                "Phil Dawid's elegant ... ted from discussions of causality with Phil Dawid over many years. ... ground with those who tout counterfactual variables, Dawid ..."
            },

            {
                "http://www.primerica.com/dawidkmiotek",
                "Primerica Financial Services : Dawid Ireneusz Kmiotek",
                "Primerica is in the business of ... Buy Term & Invest the Difference. The Theory of Decreasing ... About Dawid Ireneusz Kmiotek. Office Directions ..."
            },

            {
                "http://www.youtube.com/watch?v=tEKmrUhCMFo",
                "YouTube - Dawid Janczyk POLAND u-19 - BELGIUM u-19 (4-1)",
                "Dawid Janczyk (Legia Warsaw) ... Dawid Janczy gral w sandecji nowy sacz i raz gralem z nim(ja gralem w sokol ... Dawid Janczyk (Legia Warsaw) (less) Added: ..."
            },

            {
                "http://www.miniclip.com/games/david/en/",
                "David - Miniclip Games - Play Free Games",
                "Help David find the Lost Sheep and avoid the rampaging wild animals ... Hotmail, AOL, Yahoo Mail & other online email services. ..."
            },

            {
                "http://product.half.ebay.com/_W0QQprZ62221",
                "The Diary of Dawid Sierakowiak | Books at Half.com",
                "Buy The Diary of Dawid Sierakowiak by Dawid Sierakowiak, Kamil Turowski (1998) at Half.com. Find new and used books and save more than half off at Half.com."
            },

            {
                "http://www.primerica.com/PrimericaRep?rep=dawidkmiotek&pageName=about",
                "About Dawid Ireneusz Kmiotek",
                "Primerica is in the business of ... About Dawid Ireneusz Kmiotek. Office Directions ... Dawid Ireneusz Kmiotek. DISTRICT LEADER. Mutual Funds ..."
            },

            {
                "http://www.dawid.tobiasz.org/Monachium%20-%20Dachau/index.html",
                "Dawid/Monachium - Dachau",
                "Dawid \" Monachium - Dachau. Fotografia stanowi w\u0142asno\u015B\u0107 autora. Kopiowanie i ... Copyright by Dawid Tobiasz [Fotografia stanowi w\u0142asno\u015B\u0107 autora. ..."
            },

            {
                "http://www.davidwilkerson.org/", "David Wilkerson | World Challenge", ""
            },

            {
                "http://www.statslab.cam.ac.uk/~apd/index.html",
                "Philip Dawid",
                "PHILIP DAWID. Professor of Statistics. Contact Details. Professor A. P. Dawid, ... Valencia International Meetings on Bayesian Statistics. Bayesians Worldwide ..."
            },

            {
                "http://ideas.repec.org/e/poc8.html",
                "Dawid Zochowski at IDEAS",
                "Dawid Zochowski: current contact information and listing of economic research of this author provided by RePEc/IDEAS ... Pruski, Jerzy & \u017Bochowski, Dawid, 2005. ..."
            },

            {
                "http://www.scrumalliance.org/profiles/15472-dawid-mielnik",
                "Scrum Alliance - Profile: Dawid Mielnik",
                "Dawid has five years of professional experience in telecommunications business. ... Dawid is a Warsaw University of Technology graduate with a BSc in ..."
            },

            {
                "http://www.flickr.com/photos/dawidwalega/",
                "Flickr: Photos from 11September",
                "Flickr is almost certainly the best online photo management and sharing ... Explore Page Last 7 Days Interesting Calendar A Year Ago Today World Map Places ..."
            },

            {
                "http://www.youtube.com/watch?v=UOMk0M0hBNQ",
                "YouTube - Grembach Vigo Zgierz - Dawid Korona Rzesz\u00F3w 8-1",
                "Grembach Vigo Zgierz - Dawid Korona Rzesz\u00F3w 8-1 w Pucharze Polski ... Grembach Vigo Zgierz Dawid Korona Rzesz\u00F3w futsal \u0142\u00F3d\u017A kolejarz clearex hurtap puchar polski ..."
            },

            {
                "http://www.amazon.com/Diary-Dawid-Sierakowiak-Notebooks-Ghetto/dp/0195122852",
                "Amazon.com: The Diary of Dawid Sierakowiak: Five Notebooks from the Lodz Ghetto: Dawid Sierakowiak,Lawrence L. ...",
                "Amazon.com: The Diary of Dawid Sierakowiak: Five Notebooks from the Lodz Ghetto: Dawid Sierakowiak,Lawrence L. Langer,Alan Adelson,Kamil Turowski: Books"
            },

            {
                "http://shopping.yahoo.com/p:Kimberley%20Jim:1808599509",
                "Kimberley Jim - DVD at Yahoo! Shopping",
                "Yahoo! Shopping is the best place to comparison shop for Kimberley Jim - DVD. Compare products, compare prices, read reviews and merchant ratings."
            },

            {
                "http://www.ctfaceart.com/",
                "CT Face Art (203) 255-1875 - Chrys Dawid CTFaceArt@aol.com",
                "Award winning Face Painting for children through adults. ... CT FACE ART is owned and operated by Chrys Dawid. CT FACE ART is CT's finest face painting service. ..."
            },

            {
                "http://www.discogs.com/artist/Dawid+Szczesny",
                "Dawid Szczesny",
                "Submissions Drafts Collection Wantlist Favorites Watchlist Friends ... Dawid Szczesny / artists (D) Real Name: Dawid Szcz\u0119sny. URLs: ..."
            },

            {
                "http://www.shop.com/+-p94105045-st.shtml",
                "York Ferry Annie Dawid - SHOP.COM",
                "Shop for York Ferry Annie Dawid at Shop.com. $1.99 - york ferry annie dawid language:english, format:paperback, fiction/non-fiction:fiction, publisher:cane hill pr,"
            },

            {
                "http://www.the-artists.org/artistsblog/posts/st_content_001.cfm?id=2600",
                "Dawid Michalczyk ...the-artists.org",
                "Dawid Michalczyk; portfolio & art news...the-artists.org, modern and contemporary art ... Dawid Michalczyk. Conflicting emotions. Suburbs 2100. After the ..."
            },

            {
                "http://www.dcorfield.pwp.blueyonder.co.uk/2006/06/dawid-on-probabilities.html",
                "Philosophy of Real Mathematics: Dawid on probabilities",
                "... reading group ran through Phil Dawid's Probability, Causality and the Empirical ... Dawid (pronounced 'David') holds a Bayesian position, made evident in his ..."
            },

            {
                "http://www.cs.put.poznan.pl/dweiss/xml/index.xml?lang=en",
                "Dawid Weiss - Main page",
                "Dawid Weiss, PhD. Institute of Computing Science. Poznan University of Technology. ul. ... (Available as RSS) (c) Dawid Weiss. All rights reserved unless stated ..."
            },

            {
                "http://www.dawid.eu/",
                "dawid.eu",
                "Hier entsteht dawid.eu ... dawid.eu. Hier entsteht in K\u00FCrze das Projekt. dawid.eu. info@dawid.eu ..."
            },

            {
                "http://www.local.com/results.aspx?keyword=Dawid+Frank+B+Inc&location=06890",
                "Dawid Frank B Inc in Southport, CT (Connecticut) @ Local.com",
                "Dawid Frank B Inc located in Southport, CT (Connecticut). Find contact info, maps and directions for local contractors and home improvement services at Local.com."
            },

            {
                "http://www.anniedawid.com/shortfiction.htm",
                ": : Annie Dawid : : Short Fiction",
                "Annie Dawid is the author of Resurrection City: A Novel of Jonestown (to be ... Copyright \u00A9 2007 Annie Dawid. Web Site Design by Chameleon Web Design ..."
            },

            {
                "http://dawid.ca/",
                "www.dawid.ca",
                "I was in such a huge mistake. (Dawid Bober) ... 2006-02-26 Skating - Agnieszka, Joanna, Michal, Dawid (Nathan Phillips Square \u2013 Toronto) ..."
            },

            {
                "http://www.planetizen.com/?q=about/correspondent/dawid",
                "Irvin Dawid | Planetizen",
                "Irvin Dawid is a long-time Sierra Club activist, having worked in transportation, ... Irvin Dawid. Leo Vazquez. Mary Reynolds. Michael Dudley. Mike Lydon ..."
            },

            {
                "http://www.sourcekibitzer.org/Bio.ext?sp=l6",
                "SourceKibitzer - Bio - Dawid Weiss",
                "Dawid Weiss - Bio. Dawid Weiss. The founder of the Carrot2 project. Adjunct professor at the Laboratory of Intelligent Decision Support Systems ..."
            },

            {
                "http://www.lulu.com/content/815029",
                "MD by Marcin and Dawid Witukiewicz (Music & Audio) in Electronic & Dance",
                "MD by Marcin and Dawid Witukiewicz (Music & Audio) in Electronic & Dance : Music ... Music inspierd by the photography of Marcin and Dawid. ..."
            },

            {
                "http://www.juliedawid.co.uk/index.php?page=Band",
                "Julie Dawid : Halfwise",
                "the songs of prize winning folk singer and poet Julie Dawid. ... Also a lover and keeper of fish, professional storyteller Julie Dawid ..."
            },

            {
                "http://www.jewishencyclopedia.com/view.jsp?artid=38&letter=M",
                "JewishEncyclopedia.com - MAGEN DAWID",
                "The hexagram formed by the combination of two equilateral triangles; used as the ... The \"Magen Dawid,\" therefore, probably did not originate withinRabbinism, the ..."
            },

            {
                "http://www.lulu.com/content/815298",
                "MD Photography by Marcin and Dawid Witukiewicz (Book) in Arts & Photography",
                "... This is a book feturing some of Marcin and Dawid Witukiewicz photographic work. ... by Marcin and Dawid Witukiewicz. Share This. Report this item. Preview ..."
            },

            {
                "http://finance.yahoo.com/q?s=dawid.x",
                "DAWID.X: Summary for DIA Sep 2008 134.0000 call - Yahoo! Finance",
                "Get detailed information on DIA Sep 2008 134.0000 call (DAWID.X) including quote performance, Real-Time ECN, technical chart analysis, key stats, insider ..."
            },

            {
                "http://www.bikepics.com/members/dawid/",
                "BikePics - Dawid's Member Page on BikePics.Com",
                "Dawid's Member Page. Member: dawid. Name: Dawid. From: ... You must be a BikePics Member and be logged in to message members. Current: 1998 Suzuki GS 500 ..."
            },

            {
                "http://www.david-banner.com/main.html", "David Banner",
                "Universal Records \\ SRC \\ Artists \\ David Banner ..."
            },

            {
                "http://www.dawid.com.pl/",
                "Kinga Dawid",
                "PORTRAITS by Kinga Dawid. Copying, dissemination, forwarding, printing and/or ... All rights reserved. Copyright C 2006 Kinga Dawid ..."
            },

            {
                "http://www.bikepics.com/members/devdawid/",
                "BikePics - dawid's Member Page on BikePics.Com",
                "dawid's Member Page. Member: devdawid. Name: dawid. From: Poland. Message: You must be a BikePics Member and be logged in to message members. Current: 2002 ..."
            },

            {
                "http://dawid.bracka.pl/", "Portfolio",
                "google | portfolio | klan mortal. google | portfolio | klan mortal ..."
            },

            {
                "http://amiestreet.com/dawid",
                "Amie Street - DaWid's Music Store",
                "Amie Street empowers musicians to release, and music fans to discover, new and ... music from DaWid. recommendations (3) more info. SELECT: All, None, Free ..."
            },

            {
                "http://markoff.pl/", "Dawid Markoff Photography",
                "Nude, Fashion and Portrait photography"
            },

            {
                "http://www.archinect.com/schoolblog/blog.php?id=C0_372_39",
                "Archinect : Schoolblog : UC DAAP (Dawid)",
                "UC DAAP (Dawid) (002) a couple of quotes and a mini thesis rant. Oct 02 2006, 6 comments ... UC DAAP (Dawid) (001) it's the year of the thesis. Sep 06 2006, 4 ..."
            },

            {
                "http://groups.yahoo.com/group/dawid/rss",
                "dawid : RSS / XML",
                "dawid: Katechetyczne Forum Dyskusyjne ... Sign In. dawid \u00B7 Katechetyczne Forum Dyskusyjne. Home. Messages. Members Only. Post. Files ..."
            },

            {
                "http://cssoff.com/2007/06/14/and-the-winner-is-dawid-lizak/",
                "CSS OFF",
                "And the Winner is Dawid Lizak. View the winning entry. Dawid Lizak is from \u0141\u0119czna \u2013 a ... Dawid is currently expanding his knowledge of JavaScript, usability, ..."
            },
        };

        final ArrayList<Document> documents = new ArrayList<Document>();
        for (String [] row : data)
        {
            documents.add(new Document(row[1], row[2], row[0]));
        }

        DOCUMENTS_DAWID = Collections.unmodifiableList(documents);
    }

    public final static List<Document> DOCUMENTS_SALSA_MULTILINGUAL;
    static
    {
        DOCUMENTS_SALSA_MULTILINGUAL = ImmutableList.of(

            new Document(
              "© Salsa Cycles 2009",
              "Bikes. Chili Con Crosso Dos Niner ... Salsa Cycles CroMoto Bicycle Stems. Click Here for Further Information ... ©2009 Salsa Cycles, all rights reserved ...",
              "http://www.salsacycles.com/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Salsa (dance) - Wikipedia, the free encyclopedia",
              "Salsa is a syncretic dance genre created by Spanish-speaking people from the Caribbean. Salsa dancing mixes African and European dance influences through the music and dance fusions ...",
              "http://en.wikipedia.org/wiki/Salsa_(dance)",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Salsa music - Wikipedia, the free encyclopedia",
              "Salsa music is a musical genre that was brought to international fame by Puerto Rican musicians. Popular across Latin America, salsa incorporates multiple styles and variations; the ...",
              "http://en.wikipedia.org/wiki/Salsa_music",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "SALSA - Safe and Local Supplier Approval",
              "SALSA is a new supplier approval scheme designed to help local and regional food and drink producers supply their products to national and regional buyers.",
              "http://www.salsafood.co.uk/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Latin Entertainment Events All O",
              "Dance Partner Tips Even MORE Words of Wisdom, From Edie, The Salsa FREAK .... Zurich, Switzerland - February 26-28, 2010 - Europe's Finest Salsa Congress ...",
              "http://www.salsaweb.com/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Salsa History of Salsa Dancing",
              "Origin of Salsa Dance. Salsa is a distillation of many Latin and Afro-Caribbean dances. Origin of Latin dances, ballroom and country western dances.",
              "http://www.centralhome.com/ballroomcountry/salsa.htm",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "salsa.ch - the swiss salsa portal for events, pictures, congresses ...",
              "Salsa-Band aus Zürich (Con Sabor). Biografie, RealAudio und MP3, Konzertdaten, Links.",
              "http://www.salsa.ch/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Salsa (sauce)",
              "Wikipedia: Well-known salsas include. Salsa roja,  red sauce : used as a condiment in Mexican and Southwestern cuisine, and usually made with cooked tomatoes, chili peppers, onion, garlic, and fresh cilantro. Salsa cruda ( raw sauce ), also known as…",
              "http://en.wikipedia.org/wiki/Salsa_(sauce)",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Salsa Dancing Worldwide: Salsa Dance Resources, Events, Dance Classes &amp; ...",
              "Salsa dance events and worldwide salsa dancing news.",
              "http://www.salsacrazy.com/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "www.salsa.com",
              "",
              "http://www.salsa.com/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "SalsaCrazy's Salsa Dance Videos and DVDs. Seven Free Salsa Dance Videos!",
              "Salsa Dance! ... Discover Seven Amazing Learn to Salsa Dance Videos, That Contain Secrets Most People Will Never Know About Salsa Dance... ...   SalsaCrazy  and  Salsa Crazy  are trademarks used by SalsaCrazy, Inc..",
              "http://www.salsadancedvd.com/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Just Salsa Magazine ~ Find: Salsa Music &amp; Dance, Salsa History ...",
              "Just Salsa - A Web Magazine Dedicated to Latin Music, Dance, and Culture, Find: Salsa Music, Salsa Dance, Salsa Clubs, Salsa History, Salsa Photos, ...",
              "http://www.justsalsa.com/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "SKY.fm Salsa - Free Hot Salsa Internet Radio",
              "SKY.fm Salsa is your number destination for hot sizzzling free Salsa Music broadcasting 24 hours a day. Tune into to SKY.fm Salsa to here the best Salsa on ...",
              "http://www.sky.fm/salsa/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Index of /~clay/cookbook/salsa",
              "Index of /~clay/cookbook/salsa. Icon Name Last modified Size Description. [DIR] Parent Directory - [ ] 3 21-Jun-2003 16:26 4.3K [IMG] ...",
              "http://www.panix.com/clay/cookbook/salsa.html",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "SalsaJapan!",
              "Bienvenido! Welcome! Japanese | English.",
              "http://www.salsa.org/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Learn to dance Salsa with Ballroomdancers.com!",
              "Hip Movement: In Salsa, the hip action is usually relaxed and subtle, especially for men. Weight is normally placed onto a slightly bent knee. ...",
              "http://www.ballroomdancers.com/Dances/dance_menu.asp?Dance=SAL",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "salsa.ch - the swiss salsa portal for events, pictures, congresses ...",
              "el portal de la salsa en suiza ... has accompanied salsa stars such as Cheo Feliciano, Adalberto Santiago, Tito ... with his Salsa con Soul Orchestra as well as ...",
              "http://www.salsa.ch/news_view.php?idnews=1005",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Salsa Na Nartach 2010",
              "Salsa Na Nartach to taneczno-narciarski wyjazd • zadzwoń 0 5000 SALSA ... Salsa Na Nartach 2010 ...",
              "http://www.salsananartach.pl/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Salsa",
              "Salsa may mean: Salsa (sauce), any of various sauces of Spanish, Italian or Latin American origin, from the Spanish or Italian word for ...",
              "http://en.wikipedia.org/wiki/Salsa",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Dietary omega-3 polyunsaturated fatty acids plus vitamin E restore …",
              "Sixty patients with generalized solid tumors were randomized to receive dietary supplementation with either fish oil (18 g of omega-3 polyunsaturated fatty acids, PUFA) or placebo daily until death. Each group included 15 well-nourished and 15…",
              "http://www3.interscience.wiley.com/journal/75000274/abstract",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Music Genre: Salsa - Music of Puerto Rico",
              "Today, the center of salsa has shifted from New York to Puerto Rico. ... Others critics say that salsa is a combination of fast Latin music that embraces ...",
              "http://www.musicofpuertorico.com/index.php/genre/salsa/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "GourmetSleuth - History of Salsa",
              "Recipes and Related Reading Tomatillo Salsa - Gourmetsleuth Recipe for Tomatillo Salsa with onions, garlic, lime, serrano chiles and cilantro. Molcajete Salsa with Roasted Chilies - Gourmetsleuth - Recipe for Molcajete Salsa with Roasted Chilies.…",
              "http://www.gourmetsleuth.com/historyofsalsa.htm",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Salsa (2000 film)",
              "Salsa or ¡Salsa! is a 1999 French -Spanish romance film . The film was directed by Joyce Buñuel , and stars Vincent Lecoeur , Christianne ...",
              "http://en.wikipedia.org/wiki/Salsa_(2000_film)",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "A backward Harnack inequality and Fatou theorem for nonnegative solutions of …",
              "It is not an uncommon happening in the development of elliptic and parabolic pde, that resolutionof a problem first appears in the elliptic case and shortly after there is an attempttotdapt the techniques to the corre- sponding parabolic problem. In…",
              "http://www.projecteuclid.org/DPubS/Repository/1.0/Disseminate?handle=euclid.ijm/1256064230&amp;view=body&amp;content-type=pdf_1",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Salsa Pa'ti",
              "FELIPE POLANCO - PUERTO RICAN SALSA MASTER ... Einführung in die Salsa Puertorriqueña und das Tanzen on CLAVE. Drehungstechnick on CLAVE. ...",
              "http://www.salsapati.ch/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Mexican Food To Go, Gift Box, Tex Mex Salsa, Recipes, Tortilla",
              "We maintain salsa is only as good as its tomatoes and chilies and they are always better fresh out of the garden, so let us supply you with salsa recipe tips such as using white onions for salsa or ...",
              "http://www.texmextogo.com/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Salsa Central - Home",
              "Latest Salsa Reviews (CD, DVD, Events, and more) The Berlin Salsa Congress 25-27th September 2009. The Berlin Salsa Congress 25-27th September 2009 This congress was organised by Franco Sparfeld and his team Pura Salsa.",
              "http://www.salsa-central.com/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Caloric functions in Lipschitz domains and the regularity of solutions to phase …",
              "Annals of Mathematics, 143 (1996), 413-434 Caloric functions in Lipschitz domains and the regularity of solutions to phase transition problems By I. Athanasopoulos/1) L. Caffarelli/2) S. Salsa^3)* Introduction By a A-caloric function (and if Л = 1,…",
              "http://www.jstor.org/stable/2118531",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Picason - Salsa cubana from Switzerland - Home",
              "Picason - Salsa cubana from Switzerland ...",
              "http://www.picason.ch/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Home Page",
              "An all natural gourmet fusion product of Alaskan Salmon and Salsa. ... Alaskan Salsa Salmon, LLC is the original fire breathing Salmon, with a sombrero ...",
              "http://www.salsasalmon.net/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Home | J Smooth Salsa",
              "... key in becoming a salsa dancer. Instruct By providing salsa lessons in fully developed and detailed formats, we want our attendees to salsa dance quickly without compromising ... Play some Salsa music?",
              "http://www.jsmoothsalsa.com/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "National Center for Home Food Preservation | How Do I? Can Fruits",
              "... Can Salsa. Canning Salsa.",
              "http://www.uga.edu/nchfp/how/can_salsa.html",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Salsa Talks!",
              "",
              "http://www.salsatalks.com/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Regularity of the free boundary in parabolic phase-transition problems",
              "In this paper we start the study of the regularity properties of the free boundary, for parabolic two-phase free boundary problems. May be the best known example of a parabolic two-phase free boundary problem is the Stefan problem, a simplified…",
              "http://www.springerlink.com/index/P413HH8470840382.pdf",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Salsa Reisen, Quito",
              "cuador Rundreisen mit Reisebuch Autor Volker Feser, Salsa Reisen offeriert Galapagos, Amazonas Urwald, Indianer Märkte im Hochland von Ecuador und Peru, ...",
              "http://www.salsareisen.com/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Welcome to Salsa Londons Number 1 Latin Live Music Venue",
              "Welcome to Salsa Londons Number 1 Latin Live Music Venue, Food Served At Bar Salsa! Organize Parties At Bar Salsa! Our Clubs! Live Music At Bar Salsa!",
              "http://www.barsalsa.info/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Salsa! Salsa Dance School in Zurich. Salsa Courses, Salsa Information ...",
              "Salsapassion Dance School. Information about Salsa, Clubs, events, tricks and tips. ... The Partner-dance in Salsa is spontaneous based in it's leading and following ...",
              "http://www.salsapassion.net/e_salsa.htm",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Rumbanana Salsa Group - Rockin' the Casino Salsa World!",
              "... Photos: Salsa Rueda Congress, Miami 2009 Photos: Salsa Extravaganza, Palm Springs 2009 Stay in ... Seriously, we love Cuban Salsa (Casino), Rueda de Casino and are totally addicted to Timba.",
              "http://www.rumbanana.org/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Fresh Salsa Recipes by John Raven",
              "Cool salsa slightly and in a blender pulse until coarsely chopped (use caution when blending hot liquids). Salsa may be made up to this point 2 days ahead and cooled, uncovered, before being chilled, covered. Bring salsa to room temperature or…",
              "http://www.texascooking.com/features/feb2001raven.htm",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Cerebral Autosomal Dominant Arteriopathy with Subcortical Infarcts and …",
              "Cerebral autosomal dominant arteriopathy with subcortical infarcts and leukoencephalopathy (CADASIL) is a recently identified autosomal dominant cerebral arteriopathy char- acterized by the recurrence of subcortical infarcts leading to dementia. A…",
              "http://www.pubmedcentral.nih.gov/articlerender.fcgi?artid=1914956",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "www.sky.fm",
              "[playlist] NumberOfEntries=1. File1=http://72.26.204.18:6136. Title1=S K Y . F M - Salsa - Best Salsa Collection In the World! Length1=-1. Version=2",
              "http://www.sky.fm/aacplus/salsa.pls",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "www.alexander-martinez.com // www.weltklasse-salsa.ch",
              "both websites are under construction -",
              "http://www.alexandermartinez.com/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Lion Salsa Congress",
              "",
              "http://www.lionsalsacongress.com/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "World Salsa News . com",
              "... Zealand Sydney Salsa Congress Inssbruck Salsa Congress Austria Hawaii Salsa Festival Milano Italy Hong Kong Salsa Festival Chicago Salsa Congress Salt Lake Salsa Congress USA Pattaya ...",
              "http://www.worldsalsanews.com/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Mango Salsa",
              "Maybe you've heard of this unique salsa or you've already tried it. Mango salsa is the perfect summer time recipe that can be used as an appetizer, snack or topping. An unusual twist on an old favorite. Tomatoes and cilantro form the base and little…",
              "http://mexicanfood.about.com/od/supersalsas/r/MangoSalsa.htm",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Salsa Celtica",
              "Scottish folk and salsa band. Includes profile, tour news, album information, and audio samples",
              "http://www.salsaceltica.com/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Latin American music",
              "Salsa: Salsa music. in rhythm, tempo, baseline, riffs and instrumentation, Salsa represents an amalgamation of musical styles, including rock ...",
              "http://en.wikipedia.org/wiki/Latin_American_music",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "The stochastic approach for link-structure analysis (SALSA) and the TKC effect",
              "Today, when searching for information on the World Wide Web, one usually performs a query through a term-based search engine. These engines return, as the query's result, a list of Web sites whose contents match the query. For broad topic queries,…",
              "http://linkinghub.elsevier.com/retrieve/pii/S1389128600000347",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Welcome to Salsa Londons Number 1 Latin…",
              "Welcome to Salsa Londons Number 1 Latin Live Music Venue, Food Served At Bar Salsa! Organize Parties At Bar Salsa! Our Clubs! Live Music At Bar Salsa! ... Check out Bar Salsa's ...",
              "http://www.barsalsa.eu/info/",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Salsa - All Recipes",
              "Looking for salsa recipes? Allrecipes has more than 140 trusted salsa recipes complete with ratings, reviews and preparation tips.",
              "http://allrecipes.com/recipes/appetizers-and-snacks/dips-and-spreads/salsa/main.aspx",
              LanguageCode.ENGLISH
            ),

            
            new Document(
              "Salsa (Tanz) – Wikipedia",
              "Salsa ist ein moderner Gesellschaftstanz aus den USA und Lateinamerika, der ... Wie die Salsa-Musik ist auch der dazugehörige Tanz eine Verbindung ...",
              "http://de.wikipedia.org/wiki/Salsa_(Tanz)",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa – Wikipedia",
              "Salsa (spanisch für „Sauce“) bezeichnet: eine Sauce, siehe Salsa (Gericht) eine lateinamerikanische Musikrichtung, siehe Salsa (Musik) einen lateinamerikanischen Tanz, siehe Salsa ...",
              "http://de.wikipedia.org/wiki/Salsa",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "salsa.ch - the swiss salsa portal for events, pictures, congresses ...",
              "Salsa-Band aus Zürich (Con Sabor). Biografie, RealAudio und MP3, Konzertdaten, Links.",
              "http://www.salsa.ch/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa will change your life - Salsafestival Switzerland, the Salsa ...",
              "13.01.2010: Der provisorische Workshop-Plan 2010 ist online... »mehr. 12.12.2009: Das Salsafestival Switzerland neu mit 5 Dance Floors... »mehr",
              "http://www.salsafestival.com/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa in Berlin / Deutschland: Salsa Events, MP3 Downloads, Salsa ...",
              "Salsa in Berlin / Deutschland: Salsa Clubs, MP3 Downloads, Tanzschulen, Salsa Events. Weiterhin: Anfänger Guide, MP3 Download, Monatliche CD-Besprechung und CD Tipps, Tanzpartner ...",
              "http://www.salsa-berlin.de/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa Tanzschule Kurs Tanzkurs Zürich Luzern Salsa 1-2-3",
              "Salsa Tanzschule Kurs Tanzkurs Zürich Luzern. Salsa Kurse, Kurse Zug, Salsa lernen, Salsa Privatlektionen, Salsa Workshops Privat-Tanzkurse für Anfänger bis ...",
              "http://www.salsa123.ch/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "SalsaDE - das Portal für Salsa in Deutschland :: Startseite",
              "viele Informationen zum Thema Salsa in Deutschland.",
              "http://www.salsa.de/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa in Austria - Salsa in Österreich",
              "Salsa Österreich - Salsa dancing in Austria, Germany and worldwide: adresses and thousands of pictures of the salsa clubs - Tanzpartner, Adressen und Photos der Salsatecas ...",
              "http://www.salsa.at/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa Tanzschule Zürich. Nur 5min von Zürich HB. Salsa mit Passion!",
              "Salsa für Anfänger klicke hier. Alle Salsakurse / Workhsop klicke hier. Weihnachtsparty ... Salsa , Merengue, Bachata Kurse, Cha Cha Cha und Lady Styling Wokshop für ...",
              "http://www.salsapassion.net/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa in Germany and Austria - Adressen und Bilder der Salsa ...",
              "Salsa dancing in Austria, Germany and worldwide: adresses and thousands of pictures of the salsa clubs - Adressen und Photos der Salsatecas ( = Salsa-Clubs , ...",
              "http://www.salsatecas.de/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsapictures | Startseite",
              "2008 Salsapictures.ch - All rights reserverd - Website by CTEK GmbH.",
              "http://www.salsapictures.ch/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "salsa.ch - the swiss salsa portal for events, pictures, congresses ...",
              "GRANDE NOCHE DE SALSA, Im grossen Saal des Paulusheims, Luzern. Discoteca ... I LOVE SALSA, Viscose Bar Lounge Event, Luzern, 6020 Emmenbrücke. Discoteca ...",
              "http://www.salsa.ch/events.php",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa Reisen, Quito",
              "cuador Rundreisen mit Reisebuch Autor Volker Feser, Salsa Reisen offeriert Galapagos, Amazonas Urwald, Indianer Märkte im Hochland von Ecuador und Peru, ...",
              "http://www.salsareisen.com/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa (Musik) – Wikipedia",
              "5.2 Timba, tumba, bongó: Die Rhythmus-Sektion in der Salsa. 5.3 Geschwindigkeit ... Der andere gewichtige musikalische Strang hin zur Salsa stammt aus dem Latin Jazz. ...",
              "http://de.wikipedia.org/wiki/Salsa_(Musik)",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa-Forum.DE - Party, Salsa Dancing in Germany, Salsa Berlin ...",
              "Das Salsa Deutschland Portal. Hier finden Sie aktuelle Party-News aus der Szene, den Salsa Guide Berlin, Frankfurt, München, Salsa Party Kalender, Bands, ...",
              "http://www.salsa-forum.de/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Tanzfiguren-Beschreibungen: Salsa",
              "Salsa heißt soviel wie  Soße  und ist eine Mixtur diverser oft unbenannter ... Aus dem Salsa haben sich die lateinamerikanischen Tänze Rumba und ChaCha ...",
              "http://www.1ngo.de/tanz/salsa.html",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa-Stuttgart.com / Salsa-Community.de - Salsa, Events ...",
              "Immer neue Salsa-Fotoserien und Reports über deutschlandweite Großevents. Außerdem alles zum Thema Salsa: die Musik, der Tanz, Videoclips.",
              "http://www.salsa-stuttgart.com/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Willkommen bei SalsaRica - Die Salsa Tanzschule in Zürich",
              "SalsaRica - Tanzschule für Salsa in Zürich. Salsa, Puerto, Bachata, Merengue, ChaChaCha, Son, Reggaeton..Wir bieten Kurse und Workshops von Anfänger bis auf ...",
              "http://www.salsarica.ch/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa in Hamburg mit der cubanischen Choreographin Requena Delgado",
              "Salsa, Son, Merengue, Mambo, Rumba, Cha Cha Ch, die ganze Vielfalt der caribischen Tnze mit Requena Delgado, Workshops und Tanzkurse Im oberen Bereich erhältst Du Infos zum Programm Requena Delgados, sowie zu ihrem fachlichen Background als…",
              "http://www.salsaplus.de/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Floridita- Salsa Club",
              "salsa szene national · burgenland · kärnten · niederösterreich · oberösterreich · salzburg · steiermark ... salsa szene international ...",
              "http://www.floridita.at/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "SAA und SALSA: Zwei Fragebögen zur subjektiven Arbeitsanalyse",
              "397 SAA und SALSA: Zwei Fragebögen zur subjektiven Arbeitsanalyse Ivars Udris und Martin Rimann Zusammenfassung Mit dem Fragebogen zur „Subjektiven Arbeitsanalyse (SAA) sowie dem Fra- gebogen „Salutogenetische Subjektive Arbeitsanalyse (SALSA)…",
              "http://books.google.com/books?hl=en&amp;lr=lang_de&amp;id=Hmv7yeA7SYkC&amp;oi=fnd&amp;pg=PA397&amp;dq=salsa&amp;ots=bvVGpaXumg&amp;sig=qIUSIb43TlbojM9Guo-mpqRy4QU",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Home - Salsa, Samba, Merengue, Bachata, Latin-Shows, Tanzshow, Tanzkurse",
              "Salsa, Samba, Merengue, Bachata, Salsa Rueda, Capoeira, Zumba, Yoga, Hula, Salsa Aerobic, Pilates und Bauchtanz Kurse! ... Salsa, Samba, Merengue, Bachata, Salsa Rueda, Capoeira, Zumba, Yoga, Pilates ...",
              "http://www.tumbao.at/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Zur Chemie der Halophyten",
              "... der Seev6gel herrtihrend) und mit einem Prozentsatz an wasserl6slichen Salzen yon 2 5 bis 3O/o . Ein Tell der auf den salzhaltigen BSden wachsenden Pflanzen trg.gt den Habitus yon Sukkulenten, so z. B. Salicornia herbacea L., Suaeda arili1a Dum.…",
              "http://www.springerlink.com/index/JU1QQ22670011T36.pdf",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa in Germany and Austria - Adressen und Bilder der Salsa-Locations ...",
              "Salsa dancing in Austria, Germany and worldwide: adresses and thousands of pictures of the salsa clubs - Adressen und Photos der Salsatecas ( = Salsa-Clubs, Salsa-Discos, Latino ...",
              "http://www.salsatecas.net/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa Magica, Salsa Tanzschule Luzern",
              "Salsa Magica, Salsa Tanzschule Luzern Kriens",
              "http://www.salsa-magica.ch/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa Picante",
              "Salsa Picante ist eine deutsche Salsa-Band.",
              "http://www.salsapicante-online.de/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "… einer Zweikomponenten-Hochauftriebskonfiguration mit Vergleich zu LDA …",
              "... Zum Einsatz kommen Eingleichungsmodelle (Spalart–Allmaras, SALSA) und Zweigleichungsmodelle ( LL k-ε, Wilcox k-ω, LLR k-ω ). ... SALSA Eingleichungsmodell Beim SALSA Modell handelt es sich um eine Erweiterung des Eingleichungsmodells nach…",
              "http://www.cfd.tu-berlin.de/~schatz/PUBLICATIONS/nitsche.pdf",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Home - Gozando Salsa, die Tanzschule in Zürich (Salsa, Tanzkurs, Zürich ...",
              "Gozando la salsa, geniesse die Salsa, lautet das Motto unserer Schule. ... Herzlich willkommen bei gozando salsa, der Tanzschule am Albisriederplatz in Zürich. ...",
              "http://www.gozando-salsa.ch/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa-Tickets.com - Salsa, Events, Tickets , Salsa Reisen, Salsa Tickets,... !",
              "Zum Login Freitag, 31.07.2009 german | english Warenkorb | Zur Kasse Salsacongress Stuttgart Latin-Festival 2009 - by latin.de ... Für Alle Salseros gibt es das Latin-Christmas-Festival",
              "http://www.salsa-tickets.com/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa (Gericht)",
              "thumb | 150px | Salsa verde und Salsa roja thumb | 150px | Pico de gallo Salsa ist das spanische Wort für Sauce . Begriff Salsa meist, …",
              "http://de.wikipedia.org/wiki/Salsa_(Gericht)",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "RANS-BASIERTE DIREKTE NUMERISCHE OPTIMIERUNG ADAPTIVER …",
              "... 4.025% TAB. 1: Gitterkonvergenzindize für das DA VA2 Profil bei Verwendung des SALSA Turbu- lenzmodells. Die ... on. Hierfür wurde das SALSA Turbulenzmodell und die SCB Asymmetrie des besten Entwurfs verwen- det. Liegt ...",
              "https://www.iag.uni-stuttgart.de/IAG/institut/abteilungen/luftfahrzeugaerodynamik/paper/DGLR-JT-2004-055.pdf",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa Son Band aus Basel Schweiz",
              "... Gruppe spielt kubanische Son-Nummern, Salsa romantico und Boleros, Cumbias, Cha-cha-cha usw. ... Band präsentiert ausgefeilte Salsa Nummern und präzise ...",
              "http://www.salsa-son.ch/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Home - Salsa, Samba Show, Unterricht, Workshop",
              "Mit viel Temperament, Rhytmus, Sonne im Herzen, und einem strahlenden Lächeln machen Lira Mosquera, ihr Ensemble und ihre Band auch ihre Veranstaltung zu einem unvergesslichen Fest südamerikanischer Lebensfreude. Seite über Salsa Tanzprojekte,…",
              "http://www.arte-de-salsa.de/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa Picante",
              "Salsa Picante ist eine deutsche Salsa-Band. Salsa Picante wurde 1981 in Düsseldorf von Thomas Kukulies, Georg Corman, Günther Rink und …",
              "http://de.wikipedia.org/wiki/Salsa_Picante",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Granularzelltumor: ein gutartiger Tumor der Brustdrüse, der ein Karzinom …",
              "Granularzelltumor: ein gutartiger Tumor der Brustdrüse, der ein Karzinom vortäuscht. R DOLIF, A SALSA, A SCAGNOL, E MÜLLER-HOLZNER, G MIKUZ RöFo. Fortschritte auf dem Gebiete der Röntgenstrahlen und der Nuklearmedizin 149:44, 438-439, Thieme, 1988. .…",
              "http://cat.inist.fr/?aModele=afficheN&amp;cpsidt=7279897",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Hauptseite",
              "Aktuell Kalender Kurse Clubs Discografie DJs Galerie Salsa in Deutschland. Willkommen bei SalsaDE, der Quelle für Salsa in Deutschland.",
              "http://www.salsa.de/hauptseite.html",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "SALSA - South Argentinean Lake Sediment Archives and Modelling",
              "Welcome to the homepage of. (South Argentinean Lake Sediment Archives and Modelling)",
              "http://www.salsa.uni-bremen.de/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa de Cuba, Ignacio Camblor - Salsa, Tanzkurse",
              "",
              "http://www.salsa-de-cuba.de/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Congas &amp; Percussion",
              "Congas et musique afro-cubaine",
              "http://www.congas.ch/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "World Salsa Championships",
              "World Salsa Championships (dt. „Salsa Weltmeisterschaft“) ist ein Salsa -Tanzturnier , das den Anspruch erhebt, die offizielle …",
              "http://de.wikipedia.org/wiki/World_Salsa_Championships",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Monets  le Dejeuner : Interieur und Weiblichkeit-ein konstruierter Raum …",
              "GRIN - Verlag für akademische Texte Der GRIN Verlag mit Sitz in München und Ravensburg hat sich seit der Gründung im Jahr 1998 auf die Veröffentlichung akademischer Texte spezia- lisiert. Die Verlagswebseite www.grin.com ist für Studenten,…",
              "http://books.google.com/books?hl=en&amp;lr=lang_de&amp;id=ZCF2vylJq24C&amp;oi=fnd&amp;pg=PT13&amp;dq=salsa&amp;ots=1bk0hJ8QcF&amp;sig=S89ZBA6t6Wn_thNh8QDPT6IMtSA",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa - Musik, Tanz, Videos, MP3",
              "Salsa ist Kult, Rhythmus und Lebensgefül aus Lateinamerika. Auf Deutschlands größtem Portal findet ihr die neusten Videos, Songs, MP3 und Schritte und Styles.",
              "http://www.salsa-latinoamericana.de/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsastudio Tanzschule",
              "Täglich Salsa Tanzkurse in Zürich Nähe Airport.",
              "http://www.salsastudio.ch/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa Cubana - Tanzschule Luzern",
              "Salsa Cubana Tanzschule, Tanzenlernen in Luzern, latino,Son cubano, Merengue, Lebensfreude, ... Neue Salsa Einsteigerkurse ab: Di 5.1.2010 19:00 Uhr. Mi 6.1. ...",
              "http://www.salsacubana.ch/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa Festival Switzerland",
              "",
              "http://www.salsa-switzerland.ch/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa tanzen Salsa Infos Salsa/Latin in München mit Salsa Munich. Latin/Salsa/Brasil/Afro-Veranstaltungen",
              "Stets aktuelle Salsa - München - Infos. ... Latin/Salsa-Clubs, Salsa-Discos, Salsatecas und Latin Bars. ... Salsa Photos oder Salsa Fotos.",
              "http://www.salsa-munich.de/",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Salsa Loca",
              "Salsa Loca ist eine dänische Gruppe aus Kopenhagen . Datei:Salsa Loca. jpg | Salsa Loca Live in Concert im Copenhagen Jazzhouse am 15. …",
              "http://de.wikipedia.org/wiki/Salsa_Loca",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "Die Funktionen eines Galeristen als Intermediär",
              "2.1. Ökonomische Analyse des Kunstmarktes................................................................................. 4 2.1.1. Marktorganisation und Marktformen des Kunstmarktes........................................................... .…",
              "http://www.grin.com/e-book/129932/die-funktionen-eines-galeristen-als-intermediaer",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "salsa.ch - the swiss salsa portal for events, pictures, congresses ...",
              "el portal de la salsa en suiza ... British Pianist and Producer, Alex Wilson, relocates to the Zürichsee. The creator of hits such as the salsa version of Ain't Nobody, the soul-salsa ...",
              "http://salsa.ch/home.php",
              LanguageCode.GERMAN
            ),

            
            new Document(
              "SALSA – Musica Online, Discografias, MP3, Videos, Canciones, Fans ...",
              "SALSA - Es el lugar donde Encontraras musica online de Salsa, como de ,Adolescents Orquesta,Andy Montañez,Antonio Cartagena,Camaguey,Caribeños,Celia Cruz,Costa Brava,Dan Den,Dani ...",
              "http://www.fulltono.com/salsa/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa (género musical) - Wikipedia, la enciclopedia libre",
              "Salsa es el nombre comúnmente utilizado para describir una mezcla de varios ritmos cubanos. La salsa no es un ritmo. Es un nombre comercial que se adoptó a ...",
              "http://es.wikipedia.org/wiki/Salsa_(g%C3%A9nero_musical)",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "salsa.ch - the swiss salsa portal for events, pictures, congresses ...",
              "Salsa-Band aus Zürich (Con Sabor). Biografie, RealAudio und MP3, Konzertdaten, Links.",
              "http://www.salsa.ch/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa, Noticias, Musica, Bachata, Merengue, Baile",
              "Salsa Noticias Musica Merengue Bachata Reggaton Pop Salsa News Dancing Latin Music Descarga Baile. Salsanoticias.com. C/ Ermita 7 · 46007 Valencia · +34 963 ...",
              "http://www.salsanoticias.com/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa dancing and music",
              "Salsa dancing, salsa music, La salsa y su historia, la música cubana, ficheros midi de salsa, radios de salsa y también vídeos, libros y Cds.",
              "http://www.esto.es/salsa/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa en Cuba - Música Salsa - Cursos de Salsa en Cuba",
              "Información del mundo del baile cubano, incluyendo detalles acerca del origen, historia, métodos de enseñanza, artistas y agencias.",
              "http://www.salsa-in-cuba.com/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa - Wikipedia, la enciclopedia libre",
              "El término salsa puede referirse a: la salsa en gastronomía, un aderezo líquido o pastoso utilizado en los alimentos; la música salsa, un género musical resultado de fusionar ...",
              "http://es.wikipedia.org/wiki/Salsa",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa | Musica Salsa Music | Baile Salsa Dance | Salsa en Argentina Chile Colombia Cuba España Mexico",
              "Salsa interviews, articles, salsa pictures and videos, biographies of salsa artists, salsa dictionary, information on salsa dance clubs, dance classes, salsa dance schools and academies, salsa dance ...",
              "http://www.americasalsa.com/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa (gastronomía) - Wikipedia, la enciclopedia libre",
              "En gastronomía se denomina salsa a una mezcla líquida de ingredientes (fríos o calientes) que tienen por objeto acompañar a un plato. ...",
              "http://es.wikipedia.org/wiki/Salsa_(gastronom%C3%ADa)",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "SalsaClasica.com / La Salsa de ayer, hoy, manana y siempre",
              "Recuerda la Salsa Clasica, discografia, liricas, fotos y mas. ... Join the Salsa Ranking. Bienvenidos a SalsaClasica.com La misión de esta página es recordar la Salsa Clásica ...",
              "http://salsaclasica.com/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Puerto Rico Salsa Congress - Aqui Empezò Todo",
              "14mo PUERTO RICO SALSA CONGRESS Julio 24 – 31, 2010 @ EL SAN JUAN HOTEL &amp; CASINO. Pronto las nuevas informaciones!!! Entra a la pagina 2009 ...",
              "http://www.puertoricosalsacongress.com/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Música Salsa Gratis con letras y video para escuchar y dedicar | Buenamusica.com",
              "Listas de musica gratis para escuchar en linea incluyendo generos como reggaeton, pop y rock en español, bachata, hip-hop, cristiana, salsa, merengue, vallenato, electronica ...",
              "http://www.buenamusica.com/salsa",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "La X Estéreo - 100% Pura Salsa - Salsa Radio",
              "100% Pura Salsa, Salsa con el Sabor de Cali Colombia, Salsa Radio, Emisora Colombiana, Emisora de Salsa, Salsa 24 horas.",
              "http://www.laxestereo.com/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "www.RosarioSalsa.com.ar - Salsa sin limites!!! Argentina, Mexico ...",
              "RosarioSalsa.com.ar - Salsa sin limites! - Tu musica en la web. Donde aprender a bailar salsa en Rosario y en Argentina. Pensada para vos.",
              "http://www.rosariosalsa.com.ar/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Simposium Internacional Salsa 2010",
              "Inicio · Artistas · Abakua · Programa · Talleres · Precios · Inscripciones · Hoteles · Localización · DVDs · Fotos · Videos · Televisión · Prensa ...",
              "http://www.simposalsa.com/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Clases de Salsa en Málaga 20 euros al mes (ó 50 euros trimestrales). ( salsamalaga)",
              " Manda güevos  intentar cobrar matrícula por aprender a bailar salsa. · Posibilidad de clases particulares de salsa en Español o en Inglés. English private salsa lessons in Malaga. Insisto en que las clases en grupo son mucho más divertidas.",
              "http://sites.google.com/site/clasesdesalsaenmalaga/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "SalsaSevilla",
              "Sevilla. at 23: 00 El Rincon de la Salsa El Rincon de la Salsa Time: 23: 00 Alcalá de Guadaira at 23: 00 Carpe Diem Copas Carpe Diem Copas Time: 23: 00 Fiesta salsera.",
              "http://www.salsasevilla.com/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsapaca",
              "SALSAPACA: SALSA, LATIN JAZZ &amp; SONIDOS AFROAMERICANOS EN FRANCIA Y EN CUALQUIER LUGAR DEL MUNDO. ... Spécial II Mercado Cultural del Caribe Colombia y Perú: Memorias de la Salsa La Eurosalsa llegó....",
              "http://www.salsapaca.com/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              ".: Los chicos en su salsa :.",
              "",
              "http://www.loschicosensusalsa.com/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Quasilinear elliptic equations with quadratic growth in the gradient",
              "Quasilinear elliptic equations with quadratic growth in the gradient. C MADERNA, C DOMENICO PAGANI, S SALSA Journal of differential equations 97:11, 54-70, Academic Press, 1992. Equation elliptique; Elliptic equation; Ecuación ...",
              "http://cat.inist.fr/?aModele=afficheN&amp;cpsidt=5304069",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa X",
              "",
              "http://www.salsax.net/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa, sabor y control!: sociología de la música  tropical ",
              "ÍNDICE PREFACIO 13 1. DEL CANTO, EL BAILE... Y EL TIEMPO 32 2. DE  EL PABLO PUEBLO  A  LA MAESTRA VIDA': Mito, historia y cotidianeidad en la expresión salsera 87 3. EL TAMBOR CAMUFLADO: La melodización de ritmos y la etnicidad cimarroneada ...",
              "http://books.google.com/books?hl=en&amp;lr=lang_es&amp;id=BPoZWWucsNgC&amp;oi=fnd&amp;pg=PA7&amp;dq=salsa&amp;ots=56ykFJRjGa&amp;sig=CMUvs3jQj1T5wKaKCoCaYUeVErM",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "YouTube - Salsa Music Video Mix",
              "here is a salsa video i did of most of the salsa movies i had available thanks to my gf Maria :) :) song was suggested by my friend claudia , ENJOY :) song ...",
              "http://www.youtube.com/watch?v=Mv1ZsSrE03o",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa Radio Stations on the internet! SalsaPower.com",
              "19 Oct 2009 ... Radio Stations on the Internet - The best source for Cuban Casino-style Salsa, dancing, salsa music, live internet radio, cd reviews, ...",
              "http://www.salsapower.com/cities/fl/radio.htm",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa123.ch: Salsa y Reggaeton para mujeres",
              "Salsa 1-2-3 Overview Actualidades Filosofia Metodo1-2-3 Team Links Derecho ... Zurich Clases de prueba gratis Salsa y Reggaeton para mujeresWeekend-Cursos ...",
              "http://salsa123.ch/?url=/zurich/Salsa_Reggaeton&amp;change_language=4",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa Inmobiliaria",
              "Salsa Inmobiliaria · Lang_es_1 · Lang_en_0 · EL GRUPO; |; BUSCADOR INMOBILIARIO; |; NOTICIAS; |; CONTACTO. Newsletter. Introduzca su e-mail para recibir ...",
              "http://www.salsa.es/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "O valor nutritivo da mandioquinha-salsa..",
              "... Signatura : CNPH (3337). Corporativo : EMBRAPA-CNPH. Autor : PEREIRA, AS. Título : O valor nutritivo da mandioquinha-salsa.. P.imprenta : In: ENCONTRO NACIONAL SOBRE MANDIOQUINHA-SALSA, 5., 1995, Venda Nova do Imigrante, ES. Palestra e trabalhos…",
              "http://orton.catie.ac.cr/cgi-bin/wxis.exe/?IsisScript=ACERVO.xis&amp;method=post&amp;formato=2&amp;cantidad=1&amp;expresion=mfn=062757",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "salsamovimiento te saluda!",
              "",
              "http://www.salsamovimiento.com/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa - En Blogalaxia",
              "SALSA BARBACOA En Video Blog Cocina el 2009.09.15 receta cocinero fiel salsa barbacoa costillas cerdo... Tags: receta , cocinero , fiel , salsa , ... Flickr. com Terminó Salsa al parque.",
              "http://www.blogalaxia.com/tags/salsa",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsorro.com. Salsa, noticias, eventos, tienda, viajes, opinion, salsa en galicia...",
              "Durante la celebración de Salsorro 2009, la dirección de Salsorro entregó la tercera edición de los galardones de Salsorro, para destacar a diferentes profesionales por su labor de divulgación de la salsa a nivel local, nacional e internacional.",
              "http://www.salsorro.com/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa de tomate",
              "La salsa de tomate es una salsa o pasta elaborada principalmente a partir de pulpa de tomates, a la que se le añade, dependiendo del tipo ...",
              "http://es.wikipedia.org/wiki/Salsa_de_tomate",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "'SALSA NO TIENE FRONTERA': ORQUESTA DE LA LUZ AND THE …",
              "This article discusses key dynamics in the globalization of popular music, more specifically the interplay between technology, social and commercial structure, and meaningful sound forms. It analyses Orquesta de la Luz, an all-Japanese salsa band,…",
              "http://www.ingentaconnect.com/content/routledg/rcus/1999/00000013/00000003/art00007",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "MUSICA DE SALSA – Escucha Musica SALSA en…",
              "TuEscuchas.COM - Es el lugar donde Encontraras MUSICA EN LINEA de Salsa, como de: Adalberto, Adolecentes, Andy Montanez, Antonio Cartagena, Camaguey, Caribenos, Celia Cruz, Costa ...",
              "http://www.tuescuchas.com/musica/salsa/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "SALSA PA GOZAR",
              "... lo intentara con la salsa, me hizo caso y le gusto mucho a partir de ahí ... la Salsa o los ... conocido  León dela Salsa  permanecía internado en la ...",
              "http://www.salsapagozar.es/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "www.misalsa.cl",
              "",
              "http://www.misalsa.cl/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsas",
              "Salsas. Seleccion de las recetas de salsas más conocidas en la cocina española e internacional. Recetas de salsas...",
              "http://www.euroresidentes.com/Recetas/salsas/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa picante",
              "Una salsa picante (en inglés muy conocido: hot sauce) es una salsa altamente especia da empleada frecuentemente como condimento , y en ...",
              "http://es.wikipedia.org/wiki/Salsa_picante",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "A cultura da mandioquinha-salsa no Brasil..",
              "... Autor : SANTOS, FF dos. Título : A cultura da mandioquinha-salsa no Brasil.. ... Descriptores: Mandioquinha-salsa%Batata-baroa%Producao%Comercializacao%Arracacia xanthorrhiza%Peruvian carrot%Arracacha%Production%Marketing%Brazil. ...",
              "http://orton.catie.ac.cr/cgi-bin/wxis.exe/?IsisScript=ACERVO.xis&amp;method=post&amp;formato=2&amp;cantidad=1&amp;expresion=mfn=018774",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Vídeos de Salsa - Adrian y Anita,…",
              "Vídeos de salsa: Swinguys, Frankie Martinez, Fabian y Esther, Tito y Tamara y muchos mas.",
              "http://www.al1yal2.com/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa123.ch: Team",
              "Salsa 1-2-3 Overview Actualidades Filosofia Metodo1-2-3 TeamLinks Derecho ... El trabaja también como un instructor en Salsa -los clubs y los partidos privados. ...",
              "http://salsa123.ch/?url=/Salsa_Team&amp;change_language=4",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa - En Peru Blogs",
              "Tags: morenas , noche de copas , salsa 30. ... PeruTags: salsa tony vega watussi all stars el mas que... ... Tags: salsa , confio en ti , la elite , elite Plátanos en Salsa de Tomate En ...",
              "http://www.perublogs.com/tags/salsa",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa (baile)",
              "Salsa es un moderno baile de salón de los Estados Unidos de América y de América ... La salsa es el baile creado por gente de habla hispana ...",
              "http://es.wikipedia.org/wiki/Salsa_(baile)",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Doencas da mandioquinha-salsa..",
              "... 1 / 1 Seleccione referencia / Select reference. Signatura : CNPH. Corporativo : EMBRAPA-CNPH, Brasilia, DF. Autor : LOPES, CA%HENZ, GP. Título : Doencas da mandioquinha-salsa.. P.imprenta : Informe Agropecuario, Belo Horizonte, v.19, n.190, p.…",
              "http://orton.catie.ac.cr/cgi-bin/wxis.exe/?IsisScript=ACERVO.xis&amp;method=post&amp;formato=2&amp;cantidad=1&amp;expresion=mfn=022564",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Generos: Salsa - Música de Puerto Rico",
              "Introducción a nuestra colección de biografías de compositores y artistas incluidos en este web site",
              "http://www.musicofpuertorico.com/index.php/generos/salsa/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "100x100 SALSA",
              "La salsa en Venezuela ha tenido de todo: orquestas, combos, sextetos, vocalistas destacados, compositores importantes. Cuando uno encuentra el caso de un ...",
              "http://www.100x100salsa.com/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "¡Salsa!",
              "Figuras y emisoras de salsa, recetas para cócteles y mucho más ... Salsa-Rueda ... Salsa en Suiza ...",
              "http://hamwaves.com/salsa/salsa.html",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "salsasur - Clases de Salsa en Málaga 20€/mes",
              "Precio de las clases de salsa: 20 € mensuales (o 50€ cada 3 meses). ... Aristófanes 4 donde imparto las clases de salsa. ... Así a ojo. · El curso de salsa, merengue y bachata dura un año.",
              "http://salsasur.googlepages.com/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Sabor Dominicano Musica latina merengue bachata y Reggaeton | Sabordominicano",
              "musica latina, merengue, bachata, salsa, reggaeton, balada, musica, dominicana, chat latino, videos musicales [929 lecturas] Salsa. Jackeline y Fernando Villalona se unen nuevamente Después del éxito alcanzado en 1998, con el tema Me muero por ti,…",
              "http://www.sabordominicano.com/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa Sabrosa Kraków - salsa Kraków, kursy, pokazy, animacje - salsa ...",
              "... Salsa Sabrosa Krakow ... Salsa Sabrosa Kraków - najstarsza szkoła salsy w Krakowie",
              "http://www.salsasabrosa.pl/",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Bechamel",
              "La salsa bechamel es una  salsa madre , base de muchas otras salsas. Características: Los ingredientes básicos de esta salsa casi siempre son ...",
              "http://es.wikipedia.org/wiki/Bechamel",
              LanguageCode.SPANISH
            ),

            
            new Document(
              "Salsa - Wikipédia",
              "La salsa (mot espagnol qui signifie « sauce ») désigne à la fois une danse, un genre musical, mais également une famille de genres musicaux (musique latino-américaine).",
              "http://fr.wikipedia.org/wiki/Salsa",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "SalsaFrance.com - le portail de la Salsa en France",
              "Magazine sur l'actualité de la Salsa en France, forum de discussion sur la Salsa, critiques de CDs et DVDs de Salsa, profils et biographies d'artistes Salsa, annuaire des cours de ...",
              "http://www.salsafrance.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "[Busca Salsa]",
              "Site consacré à la Salsa mais plus généralement aux musiques afro caribéennes. Articles, entrevues, traductions de chansons, chroniques de disques, ...",
              "http://www.buscasalsa.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Tryptico : VIDEOS de SALSA (portoricaine, cubaine, colombienne et ...",
              "VIDEOS de salsa cubaine, de salsa portoricaine (on1 et on2), de salsa colombiana, des shines, des choregraphies salsa ainsi que des videos de bachata et autres danses latines",
              "http://www.tryptico.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Video gratuite: 518 VIDEOS de SALSA cubaine, portoricaine ...",
              "VIDEOSALSA.COM : Videos de salsa de qualité professionelle gratuites à visionner et télécharger. Cours de salsa à télécharger, DVD Salsa, Salsa DVDs, Leçons de salsa ...",
              "http://www.videosalsa.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "SALSA - BORDEAUX SALSA - Infos sur soirées, concerts, stages ...",
              "Le site de la salsa sur Bordeaux. Un site très dynamique avec un forum, des photos à télécharger.",
              "http://www.bordeauxsalsa.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Salsa Montréal | LA RÉFÉRENCE SALSA et PLUS!",
              "L'Équipe Toca Danse invite les amoureux des danses latines, Salsa, Cha cha, Merengue et Bachata, aux cours d'essai GRATUIT qui se dérouleront du 9 au 15 ...",
              "http://www.salsamontreal.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Paris, Cours de salsa, stage de salsa à l'Ecole de danse latine ...",
              "Cours de salsa à Paris, stage de salsa, salsa cubaine, salsa portoricaine, cha cha, samba, danse africaine, merengue, bachata, tango, danse orientale.",
              "http://www.salsadanse.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Salsafolie_montreal_salsa_montreal",
              "Roberto &amp; Marie Josée de Chilital - cours de Salsa Intermédiaire Salsalicious Et bien entendu, prenez part chaque semaine à nos cours de salsa de niveau ...",
              "http://www.salsafolie.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "École de danse Salsa Etc",
              "Sauter l'introduction / Télécharger FLASH Player 9",
              "http://www.salsaetc.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Salsavirus.com Ecole de Salsa à Genève",
              "Ecole de Salsa à Genève, salsa cubaine, salsa portoricaine... Tous les vendredis : Les meilleures soirées salsa, west coast swing et rock avec ...",
              "http://www.salsavirus.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "La Salsa pour les Nuls - SalsaFrance.com",
              "Vous avez débuté la salsa il y a peu et vous avez déjà ruiné deux paires de chaussures de ville ? Vous avez testé quelques soirées et vous commencez à vous ...",
              "http://www.salsafrance.com/salsa/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Salsa, Cours de Salsa à Paris, Stages et soirées Salsa",
              "La Salsa, prenez des cours de Salsa à Paris et découvrez nos soirées Salsa. Tout pour danser la Salsa !",
              "http://www.salsapassionparis.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Salsa-Ouest.Com - Accueil",
              "Salsa-Ouest.Com, le site de la Salsa dans le Grand Ouest : agenda, cours, associations...",
              "http://www.salsaouest.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Danse salsa Marseille : Ecole, cours de…",
              "Marseille salsa - Cours à l'école de danse pour apprendre à danser la salsa, rock, tango argentin, be-bop, mambo, lindy hop, bebop et salon. Association Marseille Club Danse ...",
              "http://www.studio-2000.net/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Les danses à deux : Salsa portoricaine, Salsa cubaine, Rock'n'Roll , Tango Argentin, Danse de salon",
              "Planning ( calendrier ) des soirées et sorties danse a 2 sur Paris et toute la France : Salsa portoricaine, Salsa cubaine, Bachata, Rock'n'Roll, Tango Argentin, Danse de salon ...",
              "http://www.jesors.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Salsa Marseille",
              "cours de salsa à Marseille, stages et soirées salsa, école de danse pour apprendre la salsa cubaine et la salsa portoricaine à Marseille",
              "http://www.cours-salsa-marseille.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Annuaire annuaires salsa agenda evenements events agendas ...",
              "salsa portails salsas portail annuaire annuaires Portail informatif de salsa, soirées salsa, forum Ecole de danse salsa partenaires et couples écoles de ...",
              "http://www.salsapartners.net/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "SalsArcade, Cours de salsa à Genève",
              "Ecole de Salsa Genève, salsa cubaine, salsa portoricaine ... Horaires des cours. Nos tarifs. Contact. Vous etes le visiteur n° ...",
              "http://www.salsarcade.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Salsa Sunrise",
              "Salsa Sunrise : des cours et des soirées Salsa.",
              "http://www.salsasunrise.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "INRIA - Équipe SALSA",
              "L'objectif principal du projet de recherche SALSA est la résolution de systèmes polynomiaux, à coefficients rationnels ou dans des corps finis, en dimension ...",
              "http://www.inria.fr/recherche/equipes/salsa.fr.html",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Salsa Monaco",
              "Festival Salsa Monaco 2010 - Reservation en ligne.",
              "http://www.salsamonaco.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Étude des systèmes algébriques surdéterminés. Applications aux codes …",
              "... Magali TURREL BARDET Soutenance de th`ese de doctorat 8 D´ecembre 2004 http://www-calfor.lip6.fr/˜bardet Projets SALSA – CODES – INRIA Rocquencourt LIP6/CALFOR – Universit´e Paris 6 Magali Bardet – Soutenance de th`ese – 8 Décembre 2004 – p.1/46…",
              "http://www-calfor.lip6.fr/~bardet/soutenance.pdf.gz",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Salsa will change your life - Salsafestival Switzerland, the Salsa ...",
              "Comme d'habitude, nous démarrons notre Week-end de Salsa avec une superbe avant-fête en collaboration avec Salsamania et le Club X-TRA, la plus grande fête ...",
              "http://www.salsafestival.com/index.php?article_id=2&amp;clang=2",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Expresion salsa par Esteban",
              "Expresión De Salsa offre un large éventail de cours pour découvrir, comprendre et déguster la Salsa",
              "http://www.expresion-salsa.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Penas y salsa",
              "bouger sur le rythme endiablé de la salsa. L'association Peña Salbaïa. Peñas y Salsa, 9e edition le 13 juin 2009 ! La nouvelle édition de Peñas y Salsa se tiendra comme chaque année aux Arènes de Bayonne le 13 juin...",
              "http://www.penasysalsa.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Salsa (film, 2000)",
              "Salsa est un film franco -espagnol réalisé par Joyce Buñuel avec Vincent Lecœur dans le rôle-titre. Le film est sorti en France le 9 ...",
              "http://fr.wikipedia.org/wiki/Salsa_(film,_2000)",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Salsa: Puerto Rican and Latino Music",
              "Salsa: Puerto Rican and Latino Music. FM PADILLA Journal of popular culture 24:11, 87-104, Blackwell Publishing, 1990. Les débuts de la « salsa » musique portoricaine de New York à la fin des années 60, et son évolution depuis. ...",
              "http://cat.inist.fr/?aModele=afficheN&amp;cpsidt=6154734",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Lion salsa congress",
              "",
              "http://www.lionsalsacongress.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "école salsamas offre cours de salsa cubaine sur Genève.",
              "Salsamas est une école de salsa cubaine à Genève, on y apprend aussi la rueda de casino, le lady styling et salsa fit. ... connection ...",
              "http://www.salsamas.ch/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Sauce salsa",
              "La sauce salsa , qui tient son nom d'un pléonasme (salsa signifie déjà « sauce » en espagnol ), est une sauce courante utilisée dans la ...",
              "http://fr.wikipedia.org/wiki/Sauce_salsa",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Pulmonary tubercolosis in Italian children by age at presentation",
              "Aim. To evaluate the clinical characteristics, diagnostic methods and outcome of paediatric pulmonary tuberculosis(PTB) in relation to children's ages when observed. Methods. Children under 15, who had been admitted to the Children's Hospital with…",
              "http://cat.inist.fr/?aModele=afficheN&amp;cpsidt=15909824",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Salsa Valais",
              "Les news sur les événements salsa en valais, information sur les soirées, cours, stages, concerts, sorties,",
              "http://www.salsavalais.info/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "salsa",
              "Annuaire salsa 4075 Sites référencés dans notre annuaire salsa Indexation garantie à 100% dans l'annuaires salsa salsa .: Accueil .: Nouveauté .: Les plus visités .: Ajouter un site ...",
              "http://salsa.deboref.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Fania",
              "l'échec du catapultage de la salsa dans le marché américain avec Columbia Records et Atlantic, et l'arrivée des modes du merengue ...",
              "http://fr.wikipedia.org/wiki/Fania",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Entre imaginaires et réalités, la géographie mouvante des danses latines",
              "Christophe APPRILL, Elisabeth DORIER-APPRILL Autrement. Série mutations(1989) 207, 32-47, Autrement, 2001. Tango, salsa, samba, rumba... s' insèrent depuis le début du siècle dans une vogue mondiale de l'exotisme. ...",
              "http://cat.inist.fr/?aModele=afficheN&amp;cpsidt=13416399",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "SALSA Cours Salsa - Vidéos de salsa",
              "SALSA Cours de salsa, vidéos salsa, forum de discussion sur la Salsa, news, soirées... Vous saurez tout sur la Salsa",
              "http://dingadesalsa.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Fabio Salsa",
              "trouvez le salon Fabio Salsa près de chez vous. les produits ... découvrez les nouvelles tendances automne-hiver 2009 de Fabio Salsa. les collections ...",
              "http://www.fabiosalsa.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "salsa.ch - the swiss salsa portal for events, pictures, congresses ...",
              "el portal de la salsa en suiza ... Pour votre publicité sur salsa.ch, nous vous proposons les variantes suivantes: Banners (468x60 px) sur salsa.ch en général ...",
              "http://www.salsa.ch/contact.php?&amp;to_lang=F",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Cours danse Toulouse : Ecole de rock salsa tango valse salon",
              "... à danser le rock, le tango, la salsa, le paso, la valse ou le ... Les principales danses de salon sont le rock, la valse, le tango, la salsa et les danses latines (cha-cha, rumba, samba, mambo, etc.).",
              "http://www.cours-danse-toulouse.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Salsa-ragga et salsaton",
              "La fusion salsa -ragga s'est opérée dans les années 1990 et a apporté un souffle nouveau à la salsa . fusion de la salsa et du ...",
              "http://fr.wikipedia.org/wiki/Salsa-ragga_et_salsaton",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Indication of the growth of Cl. perfringens in aseptically packed …",
              "... Bacteria ; Clostridiales ; Clostridiaceae ; Espacio cabeza ; Clostridium perfringens ; Contaminación ; Indicador redox ; Control microbiológico ; Control calidad ; Empaque aséptico ; Acondicionamiento ; Producto de carne ; Salsa ; Espacio cabeza…",
              "http://cat.inist.fr/?aModele=afficheN&amp;cpsidt=19328677",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "salsa - Wiktionnaire",
              "Dernière modification de cette page le 4 novembre 2009 à 13:44. Les textes sont disponibles sous licence Creative Commons attribution partage à l’identique; d’autres termes ...",
              "http://fr.wiktionary.org/wiki/salsa",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Ecole K'Danse: cours et stages de salsa, disco-fox, hip-hop ...",
              "Ecole K'Danse, l'école de toutes les danses. Un choix inégalé en Suisse Romande de cours, de stages, de salles et de professeurs.",
              "http://www.kdanse.ch/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "La salsa",
              "salsa, salsa cubaine, ... Le mot  salsa , inventé aux Etats-Unis, devient populaire ... La salsa en tant que danse arrive et se répand en Europe à ...",
              "http://www.salsarcade.com/salsa.htm",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Salsa",
              "Salsa Suggerer un site Nouveaux sites Contact Référencement Annuaire SALSA Acheter et vendre Actualite Agence de voyage Agriculture Annonces immobilier Architecture ancienne et ...",
              "http://salsa.bonnerecherche.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Peñas y Salsa",
              "Peñas y Salsa est un festival de musique salsa qui se tient depuis 2001 aux arènes de Bayonne le samedi soir à la mi-juin, organisé par ...",
              "http://fr.wikipedia.org/wiki/Pe%C3%B1as_y_Salsa",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Beaucoup est Ailleurs. Expressions de degré et sous-spécification catégorielle",
              "... etre divisees en trois classes La premiere classe contient, par exemple, quelque et trois, qui fonctionnent uniquement dans le domaine nominal, comme le montrent les exemples de (l): (l) a Jean a lu trois/quelques tivres b* Jean a trois/quelque…",
              "http://books.google.com/books?hl=en&amp;lr=lang_fr&amp;id=6VeV13i-OogC&amp;oi=fnd&amp;pg=PA125&amp;dq=salsa&amp;ots=lazNymPWIy&amp;sig=12vcDDSAMCL21ArQu-iOy1O-GUQ",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Annuaire annuaires salsa agenda…",
              "salsa portails salsas portail annuaire annuaires Portail informatif de salsa, soirées salsa, forum Ecole de danse salsa partenaires et couples écoles de danses salsas ecoles ...",
              "http://salsapartners.net/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Cours de danse Rock'n roll Swing - salsa, rock'n roll et lindy hop ...",
              "Rock'n roll Swing : cours de danse lindy hop, salsa et rock'n roll sur Annemasse et Genève. Cours collectifs et cours particuliers, stages, spectacles, ...",
              "http://www.rocknrollswing.com/",
              LanguageCode.FRENCH
            ),

            
            new Document(
              "Salsa (danza) - Wikipedia",
              "La salsa è il ballo di coppia danzato sulle note dell'omonimo genere musicale, ed ha movimenti e regole codificate. Esistono varie scuole, stili e tecniche diverse; tuttavia le ...",
              "http://it.wikipedia.org/wiki/Salsa_(danza)",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsa.it! La Salsa in Italia. Locali, serate, musica latina, testi ...",
              "Il sito offre: un glossario dei termini, biografie di artisti, testi e Midi file di canzoni, recensioni e classifiche di dischi e una serie di editoriali e ...",
              "http://www.salsa.it/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "La Salsa in Italia! Locali, serate, eventi, musica latina e ballo ...",
              "Locali salsa, scuole di ballo, testi e traduzioni, classifica salsa cubana e salsa portoricana, bachata, forum salsa, cd e dvd musica latina e molto altro.",
              "http://www.ballilatini.it/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsa Social Club - HOME PAGE",
              "Salsa Social Club - Webzine mensile sul mondo della Salsa e della musica latina. News, musica, concerti, interviste, opinioni e molto altro!",
              "http://www.salsasocialclub.com/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "ANGELA CARLI...SALSA PLANET_Salsa Cubana a Roma, Scuola di ballo ...",
              "Angela Carli rivela tutto sulla Danza Salsa, Locali,corsi, e Concerti di musica salsa e Afro-Cubana a Roma e Lazio!",
              "http://www.salsaplanet.net/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Video di salsa baciata bachata merengue in dvd e vhs",
              "Video di salsa baciata bachata merengue anche portoricana, cha cha cha in VHS e DVD dove il maestro Marco Anzellini insegna a ballare.",
              "http://www.salsaschool.it/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "WORLD SALSA MEETING | HOME | Milano 22-23 &amp; 24 Gennaio 2010",
              "WORLD SALSA MEETING, Milano ... © 2009 World Salsa Meeting - Infoline: +39 0247999301- Prenotazioni hotel: +39 0282221 - info@worldsalsameeting.com - P.IVA 06344730962",
              "http://www.worldsalsameeting.com/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsa (musica) - Wikipedia",
              "Con il termine salsa vengono denominati vari ritmi, in gran parte caraibici, popolari in molte nazioni latinoamericane. Non è chiaro chi e perché abbia dato ...",
              "http://it.wikipedia.org/wiki/Salsa_(musica)",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "www.salsamania.it-Il portale italiano multimediale della musica salsa",
              "Il portale italiano multimediale sulla musica salsa. Qui troverai i video , i concerti , la musica salsa ,la timba cubana, il son cubano.",
              "http://www.salsamania.it/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsa - Wikipedia",
              "Salsa - preparazione alimentare; Salsa - genere musicale; Salsa - ballo di coppia; Salsa - fenomeno geotermale; Salsa, film del 1988 di Boaz Davidson; Salsa, film del 2000 di Joyce Buñuel",
              "http://it.wikipedia.org/wiki/Salsa",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "YouTube - Corso di Salsa ITA in coppia",
              "Corso di Salsa Cubana primi passi in coppia per principianti ballerini.",
              "http://www.youtube.com/watch?v=ELDxjdfQsAI",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Danze Caraibiche:nel cuore della salsa",
              "DANZE CARAIBICHE:nel cuore della salsa",
              "http://www.danzecaraibiche.com/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "General Software System Integrator: Consulenza Unix, Oracle, Monitoraggio sistemi e Security",
              "Consulenza su sistemi complessi Linux, Unix, Oracle, Veritas quali cluster - disaster recovery - monitoraggio dei sistemi e security (ids)",
              "http://salsa.gensoft.it/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Repubblica.it - Blog - Europe » Blog Archive » Diplomazia europea ...",
              "5 gen 2010 ... 9 commenti a “Diplomazia europea in salsa inglese”. mdffs scrive: 7 gennaio 2010 alle 21:28. Molti britannici ovviamente tra il serio e lo ...",
              "http://bonanni.blogautore.repubblica.it/2010/01/05/diplomazia-europea-in-salsa-inglese/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsamix Il portale Latino Americano Salsa Bachata Forum Musica ...",
              "Portale dedicato alla musica latina, vende diversi articoli delle principali marche, per il ballo e non. Acquisti previa registrazione.",
              "http://www.salsamix.it/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              ".:. La Salsa Vive . org .:. Salsa - Guaguanco - Latin Jazz - Mambo",
              "La Salsa Vive . org - Portale e forum dedicati alla salsa classica.",
              "http://www.lasalsavive.org/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsa.it! Classifiche",
              "Sei un DJ e vuoi pubblicare la tua classifica su Salsa.it? Clicca qui. Se sei già registrato Clicca qui. OFFICIAL DJ di SALSA.IT ...",
              "http://www.salsa.it/classifiche.aspx",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "SALSA WORLD FESTIVAL ROMA - SALSA CONGRESS",
              "Il festival di musica e ballo latinoamericano più importante d'Italia. Gli artisti più importanti del mondo per una tre giorni a Roma, ogni anno.",
              "http://www.salsaworldfestival.com/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Bologna Salsa Festival Stage e Show di salsa cubana, portoricana ...",
              "Bologna Salsa Festival un week-end di ballo, musica, show e piÃ¹ di 60 ore di stage a tutti i livelli con artisti di fama nazionale ed internazionale con ...",
              "http://www.bolognasalsafestival.com/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "salsa.ch - the swiss salsa portal for events, pictures, congresses ...",
              "el portal de la salsa en suiza ... Ballare la salsa - come a Cuba... a Locarno il congresso di salsa internazionale, in un modo completamente diverso ...",
              "http://www.salsa.ch/news_view.php?idnews=153",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsa.it! La Salsa in Italia. Locali,…",
              "Salsa.it! La musica latino americana ... 15/01/2010 (NEWS) HAITI, UN TERREMOTO DEVASTANTE Questo sito generalmente parla del mondo del sud America e in particolare del mondo ...",
              "http://www.salsa.it/index.aspx",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsa a Cuba - Corsi di salsa - Scuole di salsa -",
              "Salsa a Cuba - Corsi di salsa - Scuole di salsa . Scuole di salsa - Corsi di salsa - Cos'è la Salsa - La musica salsa - Il ballo salsa - Artisti di salsa ...",
              "http://www.salsa-in-cuba.com/ita/index.html",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsa will change your life - Salsafestival Switzerland, the Salsa ...",
              "Lunedì 1 marzo 2010 / Salsa Rica nel Maag Areal. Il Salsafestival Switzerland è troppo bello per finire il lunedì mattina. Salsa Rica è la serata del lunedì sera a ...",
              "http://www.salsafestival.com/index.php?article_id=2&amp;clang=3",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Forum BalliLatini.it: la Salsa in Italia!…",
              "Nuovi Messaggi dall'ultima visita. Vecchi Messaggi. ( 20 o più risposte.) Discussione Bloccata.",
              "http://www.ballilatini.it/forum.salsa/forum.asp?FORUM_ID=3",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Chi conosce un corso buono di salsa a Roma? - Yahoo! Answers",
              "7 gen 2010 ... Risposte alla domanda Chi conosce un corso buono di salsa a Roma? nella categoria Danza di Yahoo! Answers.",
              "http://it.answers.yahoo.com/question/index?qid=20100107153023AAY0wh2",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsa will change your life - Salsafestival Switzerland, the Salsa ...",
              "Il programma sconvolgente del Salsafestival é leggendario. ... Partner: RobertoCopyright by Salsa Kongress GmbH - Disclaimer - Website by CTEK GmbH ...",
              "http://www.salsafestival.com/?promo=chicho-dj&amp;clang=3",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Chips &amp; Salsa",
              "Ménard ha espresso la sua opinione in maniera problematica, ma senza dubbio una frontiera l’ha voluta passare, che non è quella della libertà, ma quella della disumanità. E non basta a giustificarlo l’orrore del terrorismo sanguinario, né il nobile…",
              "http://chipsandsalsa.wordpress.com/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsa (cucina)",
              "La salsa è una preparazione di cucina o di pasticceria con consistenza pastosa, cremosa o semiliquida. La salsa ha come scopo quello di ...",
              "http://it.wikipedia.org/wiki/Salsa_(cucina)",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Maremma Que Salsa!",
              "Maremma Que Salsa un weekend di ballo latino americano con i migliori artisti internazionali e le scuole di salsa d tutta Italia organizzato da ...",
              "http://www.maremmaquesalsa.com/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "TISalsa.ch - Il portale del mondo latino in Ticino",
              "Salsa. Merengue. Bachata. Salsa. Merengue. Bachata ... al mondo latino, in particolar modo ai suoi balli: salsa, merengue e bachata. ...",
              "http://www.tisalsa.ch/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsa di soia",
              "thumb | Tradizionale salsa di soia giapponese. La salsa di soia o shoyu (cinese tradizionale : 醬油 pinyin : jiàng yóu, giapponese : 醤油 rōmaji ...",
              "http://it.wikipedia.org/wiki/Salsa_di_soia",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "salsa ::: mondo salsa::: il nuovo portale salsero",
              "Il sito dedicato al ballo della salsa, bachata, con biografia artisti, eventi vari, le scuole di salsa che fanno corso di salsa e merengue, il tutto curato ...",
              "http://www.mondosalsa.com/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsa123.ch: Team",
              "Salsa 1-2-3 Übersicht Notizie Filosofia de la Scuola Metodo 1-2-3 TeamLinks ... Lavora anche come un istruttore a Salsa -le discoteche ed i partiti privati. ...",
              "http://salsa123.ch/?url=/Salsa_Team&amp;change_language=5",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Niente più temi in salsa Wikipedia un software debella il copia-incolla - Scuola&amp;Giovani - Repubblica.it",
              "Niente più temi in salsa Wikipedia un software debella il copia-incolla. Repubblica.it: il quotidiano online con tutte le notizie in tempo reale. News e ultime notizie. Niente più temi in salsa Wikipedia un software debella il copia-incolla -…",
              "http://www.repubblica.it/2008/05/sezioni/scuola_e_universita/servizi/copiaincolla-addio/copiaincolla-addio/copiaincolla-addio.html",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "spiaggia nudista Torre Salsa",
              "spiaggia nudista Torre Salsa Spiaggia nudista Torre Salsa. Spettacolare!!! Il nudismo è tollerato da più di 20 anni. Basta posteggiare nel parcheggio ...",
              "http://wikimapia.org/4532657/it/spiaggia-nudista-Torre-Salsa",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "DjMaximo - Baila Salsa",
              "ENTRA NEL SITO ...",
              "http://www.bailasalsa.ch/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Tabasco (salsa)",
              "Il Tabasco è una salsa a base di peperoncini piccanti macerati nel sale e lasciati ad invecchiare per tre anni in botti di quercia, ...",
              "http://it.wikipedia.org/wiki/Tabasco_(salsa)",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsa - Latina - Musica - www.real.com",
              "Salsa represents the ongoing evolution and assimilation of a variety of styles which have traveled from Cuba and Puerto Rico to New York, Miami, ...",
              "http://www.it.real.com/music/genre/Salsa/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsa Sandro - Politecnico di Milano - Dipartimento di Matematica",
              "Sandro Salsa Professore Ordinario. e-mail. Sede Milano Leonardo Dip. Matematica 7° Piano. Telefono: +39 02 2399 4553. Telefono segreteria: +39 02 2399 4500 ...",
              "http://web.mate.polimi.it/viste/pagina_personale/pagina_personale.php?id=80",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsa123.ch: Salsa_Cristiana",
              "Salsa 1-2-3 Übersicht Notizie Filosofia de la Scuola Metodo 1-2-3 Team Links ... Zurigo Salsa immagini Übersicht Impresions del corso di ballo Premuto ...",
              "http://salsa123.ch/?url=/Cristliche_Salsa&amp;change_language=5",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Sito Internet Scuola di Salsa  Sbandao  Chioggia",
              "sito internet  sbandao  sbandao@sbandao. net Iniziano i nuovi corsi a Chioggia e Vicenza... chiama per informazioni 3288507507 TEL 328 8 507 507 home corsi dove siamo insegnanti foto ...",
              "http://www.sbandao.net/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Arancini di riso dolce al maritozzo e salsa di cioccolato | Le ricette di Ginger &amp; Tomato...",
              "Servite le palline spolverate con poco zucchero a velo vanigliato e accompagnate dalla salsa al cioccolato. Se volete rendere questa ricetta un pochino più estiva potete sostituire la salsa E' stato scritto un commento su  Arancini di riso dolce al…",
              "http://www.gingerandtomato.com/ricette-dolci/arancini-riso-dolce-maritozzo-salsa-cioccolato/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsa (geologia)",
              "La salsa è un fenomeno geologico di vulcanismo secondario, consistente nella fuoriuscita del terreno di fango , acqua salata, misto a gas ...",
              "http://it.wikipedia.org/wiki/Salsa_(geologia)",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "DANZE…",
              "danze caraibiche-musica-video-abbigliamento-corsi di ballo-maestri-testi-forum-classifiche-evento-latino-salsa 2009",
              "http://www.danzecaraibiche.com/salsa/salsa.php",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsafestival Switzerland 2010 - Salsa will change your life ...",
              "Dopo otto anni di costante sviluppo il Salsafestival Switzerland é l'evento-Top di Salsa in Europa e uno dei congressi di salsa più grandi e prestigioso al ...",
              "http://www.salsafestival.com/index.php?article_id=1&amp;clang=3",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Storia Salsa",
              "La salsa incorpora vari stili e varianti; il termine può essere utilizzato per ... Il più diretto antenato della salsa è il son montuno di Cuba, che è una ...",
              "http://www.newstyledance.ch/html/storia_salsa.html",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "::: STEP EVOLUTION - Dance &amp; Fitness School :::",
              "Giovedì 17 settembre 2009 ... ASSOCIAZIONE SPORTIVA DILETTANTISTICA STEP EVOLUTION - DANCE &amp; FITNESS SCHOOL PRESSO CIRCOLO  LA TORRETTA  VIA MAZZINI, 2 - MOLINELLA (BO) - info@stepevolution. it",
              "http://www.isdancers.com/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Timballo di riso con salsa di lamponi | Le ricette di Ginger &amp; Tomato",
              "Quando i timballini saranno abbastanza freddi sformateli e serviteli con la salsa di lamponi. [photo courtesy of Alexipharmaka] Articoli correlati a  Timballo di riso con salsa di lamponi  E' stato scritto un commento su  Timballo di riso con salsa…",
              "http://www.gingerandtomato.com/ricette-dolci/timballo-riso-salsa-lamponi/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "SALSAROMACLUB...Salsa a Roma,corsi e…",
              "SALSAROMACLUB...Salsa a Roma,corsi e stage di salsa,bachata,merengue,rueda de casino,cha cha cha,son,organizzazione serate ed eventi..",
              "http://www.salsaromaclub.it/",
              LanguageCode.ITALIAN
            ),

            
            new Document(
              "Salsa Worcester",
              "La salsa Worcester (o salsa Worcestershire) è una salsa inglese, agrodolce e leggermente piccante, che prende il nome dall'omonima città ...",
              "http://it.wikipedia.org/wiki/Salsa_Worcester",
              LanguageCode.ITALIAN
            )
        );
    }
    
    public final static List<List<Document>> ALL;
    static
    {
        ALL = ImmutableList.of(
            DOCUMENTS_DATA_MINING, DOCUMENTS_DAWID, DOCUMENTS_SALSA_MULTILINGUAL);
        List<Document> flattened = Lists.newArrayList();
        for (List<Document> sub : ALL) {
            flattened.addAll(sub);
        }
        Document.assignDocumentIds(flattened);
    }
}
