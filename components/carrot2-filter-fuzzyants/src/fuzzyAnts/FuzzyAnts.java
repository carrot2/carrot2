
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package fuzzyAnts;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Element;

import com.dawidweiss.carrot.filter.FilterRequestProcessor;


/**
 * Main Class
 * @author Steven Schockaert
 */
public class FuzzyAnts
    extends FilterRequestProcessor
    implements Constants
{
    private Element root;
    private HttpServletResponse response;
    private Map params;

    public void processFilterRequest(
        InputStream carrotData, HttpServletRequest request, HttpServletResponse response, Map params
    )
        throws Exception
    {
        try
        {
            this.response = response;
            this.params = params;

            root = parseXmlStream(carrotData, "UTF-8");

            java.util.List ch = root.elements("document");
            java.util.List children = new ArrayList(ch);
            java.util.List meta = new ArrayList(root.elements("l"));
            java.util.List query = new ArrayList(root.elements("query"));

            //determine parameter values
            getParameters();

            //obtain clusters
            DocumentClustering opl = new DocumentClustering(
                    0, children, meta, query, true, BINARY, params);
            List groups = opl.getGroups();

            for (ListIterator it = groups.listIterator(); it.hasNext();)
            {
                Element group = (Element) it.next();
                root.add(group);
            }

            //store result
            serializeXmlStream(root, response.getOutputStream(), "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void getParameters()
    {
        String p1 = "50";
        String p2 = "50";
        String p3 = "50";
        String p4 = "5";
        String p5 = "5";
        String p6 = "10";
        String p7 = "15";
        String p8 = "1000";
        LinkedList l = new LinkedList();
        l.add(p1);
        params.put("alfa", l);
        l = new LinkedList();
        l.add(p2);
        params.put("beta", l);
        l = new LinkedList();
        l.add(p3);
        params.put("gamma", l);
        l = new LinkedList();
        l.add(p4);
        params.put("n1", l);
        l = new LinkedList();
        l.add(p5);
        params.put("m1", l);
        l = new LinkedList();
        l.add(p6);
        params.put("n2", l);
        l = new LinkedList();
        l.add(p7);
        params.put("m2", l);
        l = new LinkedList();
        l.add(p8);
        params.put("numberOfIterations", l);
    }
}
