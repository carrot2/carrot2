
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.grokker;

import java.util.ArrayList;
import java.util.List;

import com.dawidweiss.carrot.core.local.clustering.RawCluster;
import com.dawidweiss.carrot.core.local.clustering.RawClusterBase;
import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentBase;
import com.groxis.support.plugins.facade.CategorizerFacade;

/**
 * Tests the clusterizer SPI.
 *  
 * @author Dawid Weiss
 * @version $Revision$
 */
public class CarrotSPITest extends junit.framework.TestCase {

	public CarrotSPITest(String s) {
		super(s);
	}

    public void testPathConversion() {
        CarrotSPI s = new CarrotSPI();
        s.initialize();
        
        final String [] documents = new String [] { "a", "b", "c", "d" };
        
        RawDocumentBase [] rd = new RawDocumentBase[ documents.length ];
        for (int i=0;i<documents.length;i++) {
            final int j = i;
            rd[i] = new RawDocumentBase() {
                {
                    this.setProperty(RawDocument.PROPERTY_TITLE, documents[j]);
                    this.setProperty(RawDocument.PROPERTY_URL, "url://" + documents[j]);
                }
                public Object getId() {
                    return new Integer(j);
                }
            };
        }

        RawDocumentBase a = rd[0];
        RawDocumentBase b = rd[1];
        RawDocumentBase c = rd[2];
        RawDocumentBase d = rd[3];

        List clusters = new ArrayList();
        RawClusterBase first = new RawClusterBase();
        RawClusterBase second = new RawClusterBase();
        RawClusterBase third = new RawClusterBase();
        
        first.addLabel("first");
        second.addLabel("second");
        third.addLabel("third");
        second.addSubcluster(third);
        
        first.addDocument(a);
        first.addDocument(b);
        second.addDocument(a);
        second.addDocument(c);
        third.addDocument(a);
        third.addDocument(d);

        clusters.add(first);
        clusters.add(second);
        
        String result [][] = s.convertToPaths(clusters, documents);
        String paths [] = result[0];
        String categories [] = result[1];

        assertEquals("first" 
                    + CategorizerFacade.PATH_SEPARATOR
                    + "second"
                    + CategorizerFacade.PATH_SEPARATOR
                    + "second" + CategorizerFacade.SEPARATOR + "third", paths[0]);
        assertEquals("first", paths[1]);
        assertEquals("second", paths[2]);
        assertEquals("second" + CategorizerFacade.SEPARATOR + "third", paths[3]);

        assertEquals("first", categories[0]);
        assertEquals("second", categories[1]);
        assertEquals("second" + CategorizerFacade.SEPARATOR + "third", categories[2]);
    }
    
    public void testPathConversionWithScoreSorting() {
        CarrotSPI s = new CarrotSPI();
        s.initialize();
        
        final String [] documents = new String [] { "a", "b", "c", "d" };
        
        RawDocumentBase [] rd = new RawDocumentBase[ documents.length ];
        for (int i=0;i<documents.length;i++) {
            final int j = i;
            rd[i] = new RawDocumentBase() {
                {
                    this.setProperty(RawDocument.PROPERTY_TITLE, documents[j]);
                    this.setProperty(RawDocument.PROPERTY_URL, "url://" + documents[j]);
                }
                public Object getId() {
                    return new Integer(j);
                }
            };
        }

        RawDocumentBase a = rd[0];
        RawDocumentBase b = rd[1];
        RawDocumentBase c = rd[2];
        RawDocumentBase d = rd[3];

        List clusters = new ArrayList();
        RawClusterBase c1 = new RawClusterBase();
        RawClusterBase c2 = new RawClusterBase();
        RawClusterBase c3 = new RawClusterBase();
        RawClusterBase c4 = new RawClusterBase();
        RawClusterBase c5 = new RawClusterBase();
        
        c1.addLabel("c1");
        c2.addLabel("c2");
        c3.addLabel("c3");
        c4.addLabel("c4");
        c5.addLabel("c5");

        c1.addDocument(a);
        c1.addDocument(b);
        c2.addDocument(a);
        c2.addDocument(c);
        c3.addDocument(a);
        c3.addDocument(d);
        c4.addDocument(d);
        c5.addDocument(c);

        clusters.add(c1);
        clusters.add(c2);
        c1.addSubcluster(c3);
        c2.addSubcluster(c4);
        c2.addSubcluster(c5);

        c1.setDoubleProperty(RawCluster.PROPERTY_SCORE, 1);
        c2.setDoubleProperty(RawCluster.PROPERTY_SCORE, 0.9);
        c3.setDoubleProperty(RawCluster.PROPERTY_SCORE, 0.6);
        c4.setDoubleProperty(RawCluster.PROPERTY_SCORE, 0.7);
        c5.setDoubleProperty(RawCluster.PROPERTY_SCORE, 0.4);

        s.setUseScoreComparator();
        String result [][] = s.convertToPaths(clusters, documents);
        String paths [] = result[0];
        String categories [] = result[1];

        assertEquals("c1", categories[0]);
        assertEquals("c2", categories[1]);
        assertEquals("c2" + CategorizerFacade.SEPARATOR + "c4", categories[2]);
        assertEquals("c1" + CategorizerFacade.SEPARATOR + "c3", categories[3]);
        assertEquals("c2" + CategorizerFacade.SEPARATOR + "c5", categories[4]);
    }

    public void testClusterizeMethod() {
        CarrotSPI s = new CarrotSPI();
        s.initialize();
        
        List input = populate();
        
        String[] dyn_paragraphs = (String[]) input.toArray( new String[ input.size() ] );
        String[] dyn_query_words = { "programmer" }; 

        String[][] cats = s.categorizeInOrder(dyn_paragraphs, dyn_query_words);

        assertNotNull(cats);
        assertEquals(dyn_paragraphs.length, cats[0].length);
        assertTrue(cats[1].length > 1);

        // [dw] Enable this to see categories assigned to test documents
        // for (int i=0; i<cats[0].length; i++) {
        //     System.out.println(dyn_paragraphs[i] + "\n\t:: " + cats[0][i]);
        // }
    }
    
    private List populate() {
        ArrayList input = new ArrayList();
        input.add("***INDEPENDENT CONSULTANTS ONLY! NO THIRD PARTIES/NO SUB CONTRACTORS*** Job Requirement: Skill Set Required ArcFm/ArcGI 8.3, VB 6, AML, Oracle 9.I, Crystal Rpts a plus, IBM AIX Shell Scripting ");
        input.add("IT Anaylst/b ");
        input.add("Technisource is seeking a junior level resource skilled in using report writes (Reporting Services, Crystal, etc..)./b ");
        input.add("Looking for a solid C++ bprogrammer/b. Need someone that is more senior bprogrammer/b with at least 3-5 years experience. Would like to have someone with experience in tcp/ip comms, concepts of design patterns, multi-threading and financial messaging. Background in the financial industry");
        input.add("Technisource is seeking a junior level resource skilled in using report writes (Reporting Services, Crystal, etc..).Requirements: Report development .NET Crystal Reports SQLContract duration: 7 months...");
        input.add("Technisource is seeking a talented Informatica Developer. Support clients conversion of systems using INFORMATICA. Data mappingRequirements: ETL, InformaticaContract duration: 5 months ");
        input.add("Ideal candidate will be proficient in VBA, Access 97/x/XP application development. Have strong knowledge of SQL Server xs stored procedures, DTS packages and triggers. ");
        input.add("bProgrammer/b Analyst homejob display Wednesday, February 09 _*_ See other jobs with similar skills Scripts (Unix) Visual Basic (Windows Development) ASP (E-Commerce / Internet) Any of the above / All of the above bProgrammer/b Analyst bProgrammer/b Analyst is needed for an immediate contract opportunity");
        input.add("Currently seeking a Java developer with at least 2-3 years experience. Need to be proficient in J2EE and SQL Server. Responsibilities will consist of new development, product expansion and conversions...");
        input.add("Purpose: TEKsystems is currently searching for an experienced Python bProgrammer/b to work at one of their client sites in Philadelphia, Pa. Successful candidate will work to enhance an internal DNS management application written in python. The bprogrammer/b should have detailed, protocol-level knowledge");
        input.add("Position Summary: Develop software for real-time interactive simulation and training applications in a project team environment. Participate in relevant aspects of application design with senior softw...");
        input.add("Cold Fusion Developer - 3 years experience building apps in ColdFusion MX, Fusebox framework experience helpful. 4-6 years software development experience. 3 years experience building applications in ...");
        input.add("Our client located in Mercer County, NJ is seeking one bProgrammer/b Analyst for a permanent position that includes base salary, bonus and benefits. The successful candidate will design, develop and implement new software applications to provide future growth and stability for our client&#039");
        input.add(" under the direction of the data warehouse lead bprogrammer/b and the lead bprogrammer/b/web master. The parameterized web");
        input.add("Purpose: TEKsystems has partnered with a company in the Dayton area to identify an COBOL AS400&amp;nbsp;bProgrammer/b. The ideal candidate will be doing package configuration and customization. Requirements are: Experience with package configurations Experience with RPG,&amp;nbsp;AS400, and TMO COBOL");
        input.add("Jobid: 070-91_*_ 3 Company: Robert Half Technology Title: Access bProgrammer/b City: Greatter Hartford State: CT ApplyUrl: x_*_ Description: Consulting 4+ Years of Access programming experience. With more than 100 locations in North America, Europe, and Australia, Robert Half Technology");
        input.add("Computer programming position working with clinical trials data. Provides programming support for some or all of the following activities: creation and maintenance of programs used for data entry, dat...");
        input.add("Classification: Consulting Compensation: 30 Aid in developing various applications with .Net framework. With more than 100 locations in North America, Europe, and Australia, Robert Half Technology is ...");
        input.add("Jobid: 380-01_*_ Company: Robert Half Technology Title: bProgrammer/b/Analyst City: JOHNSTON State: RI ApplyUrl: x_*_ Description: Consulting Qualified candidates will have a Bachelor &#039;s degree from a four-year college or university, 2+ year &#039;s related experience and a solid foundation");
        input.add("bProgrammer/b Analyst position in Alpharetta- Degree in Computer Science or related technical degree. At least 7 years bprogrammer/b analyst experience required. Programming experience in Assembler and... applications. Job Title: Computer bProgrammer/b Primary Skills: Assembler; CICS Job Industry: Services Vacancies");
        input.add("Manpower Professional Seeking Perl bProgrammer/b!! This is for a six month contract with possibility of extension for a maximum of 18 months. Looking for a bprogrammer/b with primary experience in Perl 5.6... resolve first and second line support issues. Job Title: Perl bProgrammer/b Primary Skills: PERL; UNIX");
        input.add("Manpower Professional Seeking Perl bProgrammer/b!! This is for a six month contract with possibility of extension for a maximum of 18 months. Looking for a bprogrammer/b with primary experience in Perl 5.6... resolve first and second line support issues. Job Title: Perl bProgrammer/b Primary Skills: PERL; UNIX");
        input.add("Candidate will ensure reliability and availability of enterprise systems to meet business needs. Candidate must have experience in installing, configuring, maintaining, and supporting Portal technolog...");
        input.add("This position is with a client of Technisource. This agency is charged to provide production support services to a range of systems that are critical to the success of the agency. The position perform...");
        input.add("CNC bProgrammer/b Job Summary: Ensures that the companies CNC is programmed and all the tooling maintained for the company. Responsibilities: -Creates accurate G-code programs for the CNC -Communicates... Job Title: CNC bProgrammer/b Primary Skills: Must be available 1st 2nd or 3rd shift Job Industry");
        input.add("CNC bProgrammer/b Job Summary: Ensures that the companies CNC is programmed and all the tooling maintained for the company. Responsibilities: -Creates accurate G-code programs for the CNC -Communicates... Job Title: CNC bProgrammer/b Primary Skills: Must be available 1st 2nd or 3rd shift Job Industry");
        input.add("Jobid: 393-01_*_ Company: Robert Half Technology Title: bProgrammer/b/Analyst City: NASHVILLE State: TN ApplyUrl: x_*_ Description: Full Time D.O.E. One of our best Nashville, TN-based clients is searching for a candidate to fulfill the role of bProgrammer/b/Analyst. In this role, you&#039;ll");
        input.add("JOIN THE CDI TEAM has an Immediate Need for a .NET bProgrammer/b of this position will include but are not limited to: &amp;lt;br&amp;gt; &amp;lt;br&amp;gt;The successful senior bprogrammer/b analyst will be experienced in developing data centric applications based on Microsoft Visual Studio. Ideal");
        input.add("!!!!!Local Kansas City company is looking for a Java bProgrammer/b!!!!!Work with associates to complete the design, development, testing, and implementation of the initial release of the Weapons... bProgrammer/b Primary Skills: J2EE; Java; UML; Apache Jakarta Job Industry: Architectural/Engineering");
        input.add("Consultants will support corporate customer information systems. Key responsibilities include: using mainframe technology, development methods, database technology, and development tools (Cobol, CICS,...");
        input.add("Our client has an immediate need for a Principle Analyst for their project in MI state. The ideal resource must have an overall experience of at least 7 years in IT with at least the listed experience...");
        input.add("Seeking full time individuals with experience in PLC/DCS/Scada Programming, Particular any of the following Allen Bradley Control Logix, Devicenet GE Fanuc Westinghouse Ovation/WDPF ABB Bailey (infi-9...");
        input.add("Purpose: TEKsystems is currently searching for an experienced Java bProgrammer/b to work at one of their client sites in Allentown, Pa. Successful candidate will work within a team environment to design, develop, and support insurance related systems, they will assist in the analysis and design");
        input.add(" requirements and why youre the exceptional bprogrammer/b were looking for to: . You may also visit our website");
        input.add("Description: Consultis, a premier Minority Owned Technical Recruiting Services Company with the single focus of pairing the right candidate and skill sets with the right employment opportunity is currently searching for 3 PICK bProgrammer/b / Analyst for Direct Permanent Hire with our well known");
        input.add("Computech is a rapidly growing information technology consulting firm working with government agencies, large corporations, and major associations on mission-critical applications, systems, services a...");
        input.add("Candidate will lead or participate in the design, development, and maintenance of Java and C++ application program interfaces for access to XML documents from both batch and on-line systems residing o...");
        input.add("Client/Server bProgrammer/b Analyst homejob display Wednesday, February 09 _*_ See other jobs with similar skills SQL (Database Systems) Any of the above / All of the above Client/Server bProgrammer/b Analyst Hot contract for company seeking Client/Server bProgrammer/b Analyst! Responsible for full life");
        input.add(", then MyGamer.com is absolutely for you. PHP/MYSQL bProgrammer/b MyGamer.com is looking for a PHP/MYSQL bProgrammer/b - The PHP/MYSQL bProgrammer/b is responsible for creating and modifying new and existing code");
        input.add("Our client is in need of several Java programmers with J2EE application server experience in Minneapolis. The programmers will work with the business analyst team to build, test and deploy functionali...");
        input.add("Our direct client is seeking several ATG DEVELOPER&#039;s / bPROGRAMMER/b ANALYST&#039;s NEED 3 ATG Developers/ bProgrammer/b Analysts for a 5-Month CONTRACT TO HIRE Position Responsibilities: ~ Assist development team in developing ATG Java modules for new Assortment Planning Tool. ~ Be accountable");
        input.add("5+ Years Experience using Progress V.x through V9 (GUI + CHUI) APPServer knowledge & experience Progress Database ERP knowledge preferred UNIX and Windows email : _*_ Sunil Bhandari VedaSoft Inc. 201-...");
        input.add("Commerce Server bProgrammer/b homejob display Wednesday, February 09 _*_ See other jobs with similar... / Developer (E-Commerce / Internet) Any of the above / All of the above Commerce Server bProgrammer/b This industry leader has a need for a Commerce Server bProgrammer/b (preferably Commerce Server x");
        return input;
    }

}
