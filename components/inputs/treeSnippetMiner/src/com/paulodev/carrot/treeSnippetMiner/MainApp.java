package com.paulodev.carrot.treeSnippetMiner;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Poznań University of Technology</p>
 * @author Paweł Kowalik
 * @version 1.0
 */

import java.io.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import org.put.util.xml.JDOMHelper;
import com.paulodev.carrot.treeSnippetMiner.frequentTreeMiner.*;
import com.paulodev.carrot.treeSnippetMiner.treeAnalyser.*;
import com.paulodev.carrot.util.html.parser.*;

public class MainApp
{
    public MainApp(String descriptor, double minSupport, String query,
        double level, String resPath)
        throws org.jdom.JDOMException, java.net.MalformedURLException,
        java.lang.Exception, java.io.IOException,
        gnu.regexp.REException
    {
        File desc = new File(descriptor);
        if (!desc.canRead())
        {
            System.err.println("Cannot open descriptor file !" +
                               desc.getAbsolutePath());
            return;
        }
        SAXBuilder builder = new SAXBuilder();
        Document config;
        builder.setValidation(false);
        config = builder.build(desc);
        pageGrabber grabber = new pageGrabber(config.getRootElement(), resPath);
        HTMLTree page = grabber.getPage(query);
        FreqSubtreeMiner miner = new FreqSubtreeMiner(page,
            grabber.getSnippetsOnPage(), minSupport, resPath);
        Element structure = miner.mineFrequentSubtree();
        Element result = null;
        if (structure != null) {
            treeAnalyser a = new treeAnalyser(structure, page, resPath, level);
            result = a.process();
        }
        if (result != null) {
            Element res = JDOMHelper.getElement("/service/response", config.getRootElement());
            if (result.getParent() != null)
                result.getParent().getChildren().remove(result);
            Element extr = new Element("extractor");
            res.getChildren().add(extr);
            extr.getChildren().add(result);
            XMLOutputter ou = new XMLOutputter("  ", true);
            try
            {
                FileOutputStream fi = new FileOutputStream(resPath +
                    "/output.xml");
                fi.write(ou.outputString(config).getBytes());
                fi.close();
            }
            catch (IOException ex)
            {
                System.err.println("Error creating output file");
            }
        }
        else
            System.err.println("Error: no result");
    }

    public static void main(String[] args)
        throws org.jdom.JDOMException, java.net.MalformedURLException,
        java.lang.Exception, java.io.IOException,
        gnu.regexp.REException
    {
        if (args.length < 1)
        {
            System.err.println("Usage: MainApp <plik_deskryptora>");
        }
        else
        {
            new MainApp(args[0], args.length > 2 ? Double.parseDouble(args[2]) : 0.6,
                args.length > 1 ? args[1] : "java",
                args.length > 3 ? Double.parseDouble(args[3]) : 0.35,
                args.length > 4 ? args[4] : "./");
        }
    }

}