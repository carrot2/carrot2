/**
 * 
 */
package org.carrot2.examples;

import java.util.*;

import org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.core.controller.SimpleController;
import org.carrot2.core.parameter.AttributeNames;

/**
 *
 */
public class ClusteringWithDirectDocumentFeed
{
    // Documents to cluster
    private static final String [][] documentContent = new String [] [] {
        { "Data Mining - Wikipedia", "http://en.wikipedia.org/wiki/Data_mining" },
        { "KD Nuggets", "http://www.kdnuggets.com/" },
        { "The Data Mine", "http://www.the-data-mine.com/" },
        { "DMG", "http://www.dmg.org/" },
        { "Two Crows: Data mining glossary", "http://www.twocrows.com/glossary.htm" },
        { "Jeff Ullman's Data Mining Lecture Notes", "http://www-db.stanford.edu/~ullman/mining/mining.html" },
        { "Thearling.com", "http://www.thearling.com/" },
        { "Data Mining", "http://www.eco.utexas.edu/~norman/BUS.FOR/course.mat/Alex" },
        { "CCSU - Data Mining", "http://www.ccsu.edu/datamining/resources.html" },
        { "Data Mining: Practical Machine Learning Tools and Techniques", "http://www.cs.waikato.ac.nz/~ml/weka/book.html" },
        { "Data Mining - Monografias.com", "http://www.monografias.com/trabajos/datamining/datamining.shtml" },
        { "Amazon.com: Data Mining: Books: Pieter Adriaans,Dolf Zantinge", "http://www.amazon.com/exec/obidos/tg/detail/-/0201403803?v=glance" },
        { "DMReview", "http://www.dmreview.com/" },
        { "Data Mining @ CCSU", "http://www.ccsu.edu/datamining" },
        { "What is Data Mining", "http://www.megaputer.com/dm/dm101.php3" },
        { "Electronic Statistics Textbook: Data Mining Techniques", "http://www.statsoft.com/textbook/stdatmin.html" },
        { "data mining - a definition from Whatis.com - see also: data miner, data analysis", "http://searchcrm.techtarget.com/sDefinition/0,,sid11_gci211901,00.html" },
        { "St@tServ - About Data Mining", "http://www.statserv.com/datamining.html" },
        { "DATA MINING 2005", "http://www.wessex.ac.uk/conferences/2005/data05" },
        { "Investor Home - Data Mining", "http://www.investorhome.com/mining.htm" },
        { "SAS | Data Mining and Text Mining", "http://www.sas.com/technologies/data_mining" },
        { "Data Mining Student Notes, QUB", "http://www.pcc.qub.ac.uk/tec/courses/datamining/stu_notes/dm_book_1.html" },
        { "Data Mining", "http://datamining.typepad.com/data_mining" },
        { "Two Crows Corporation", "http://www.twocrows.com/" },
        { "Statistical Data Mining Tutorials", "http://www.autonlab.org/tutorials" },
        { "Data Mining: An Introduction", "http://databases.about.com/library/weekly/aa100700a.htm" },
        { "Data Mining Project", "http://research.microsoft.com/dmx/datamining" },
        { "An Introduction to Data Mining", "http://www.thearling.com/text/dmwhite/dmwhite.htm" },
        { "Untangling Text Data Mining", "http://www.sims.berkeley.edu/~hearst/papers/acl99/acl99-tdm.html" },
        { "Data Mining Technologies", "http://www.data-mine.com/" },
        { "SQL Server Data Mining", "http://www.sqlserverdatamining.com/" },
        { "Data Warehousing Information Center", "http://www.dwinfocenter.org/" },
        { "ITworld.com - Data mining", "http://www.itworld.com/App/110/050805datamining" },
        { "IBM Research | Almaden Research Center | Computer Science", "http://www.almaden.ibm.com/cs/quest" },
        { "Data Mining and Discovery", "http://www.aaai.org/AITopics/html/mining.html" },
        { "Data Mining: An Overview", "http://www.fas.org/irp/crs/RL31798.pdf" },
        { "Data Mining", "http://www.gr-fx.com/graf-fx.htm" },
        { "Data Mining Benchmarking Association (DMBA)", "http://www.dmbenchmarking.com/" },
        { "Data Mining", "http://www.computerworld.com/databasetopics/businessintelligence/datamining" },
        { "National Center for Data Mining (NCDM) - University of Illinois at Chicago", "http://www.ncdm.uic.edu/" },
    };
    
    @SuppressWarnings("unchecked")
    public static void main(String [] args)
    {
        List<Document> documents = new ArrayList<Document>();
        for (int i = 0; i < documentContent.length; i++)
        {
            documents.add(Document.create(i, documentContent[i][0], "",
                documentContent[i][1]));
        }

        SimpleController controller = new SimpleController();
        
        Map<String, Object> parameters = new HashMap<String, Object>();
        Map<String, Object> attributes = new HashMap<String, Object>();
        
        attributes.put(AttributeNames.DOCUMENTS, documents);
        
        final ProcessingResult result = 
            controller.process(parameters, attributes, ByUrlClusteringAlgorithm.class);

        ExampleUtils.displayResults(result);
    }
}
