
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2014, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.examples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.carrot2.core.Document;

/**
 * Some example search results for use off-line.
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
}
