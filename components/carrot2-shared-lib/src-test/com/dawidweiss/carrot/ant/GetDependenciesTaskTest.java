/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * All rights reserved.
 *
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */

package com.dawidweiss.carrot.ant;


import junit.framework.TestCase;
import org.apache.tools.ant.*;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;


/**
 * A test case for ANT dependency checker.
 */
public class GetDependenciesTaskTest
    extends TestCase
{
    public GetDependenciesTaskTest()
    {
        super();
    }


    public GetDependenciesTaskTest(String arg0)
    {
        super(arg0);
    }

    public void testDependencyChecker()
        throws ParserConfigurationException, SAXException, IOException
    {
        Project project = new Project();
        project.setBaseDir(new File("."));
        project.init();
        project.addBuildListener(
            new BuildListener()
            {
                public void buildStarted(BuildEvent arg0)
                {
                }


                public void buildFinished(BuildEvent arg0)
                {
                }


                public void targetStarted(BuildEvent arg0)
                {
                }


                public void targetFinished(BuildEvent arg0)
                {
                }


                public void taskStarted(BuildEvent arg0)
                {
                }


                public void taskFinished(BuildEvent arg0)
                {
                }


                public void messageLogged(BuildEvent arg0)
                {
                    System.out.println(arg0.getMessage());
                }
            }
        );

        GetDependenciesTask task = new GetDependenciesTask();
        task.setProject(project);
        task.setProperty("testproperty");
        task.setComponentDescriptor(
            new File("com/dawidweiss/carrot/ant/dependency.xml".replace('/', File.separatorChar))
        );

        FileSet fs = task.createDependencies();
        fs.setDir(new File("."));
        fs.setIncludes("**/*.dep.xml");
        task.execute();

        assertEquals(
            new File(
                "com/dawidweiss/carrot/ant/subdir/component-b.dep.xml".replace(
                    '/', File.separatorChar
                )
            ).getCanonicalPath(), new File(project.getProperty("testproperty")).getCanonicalPath()
        );
    }
    
    public void testPropertyNameWithPropertiesInside()
        throws ParserConfigurationException, SAXException, IOException
    {
        Project project = new Project();
        project.setBaseDir(new File("."));
        project.init();
        
        project.setProperty("propertyname", "value");

        GetDependenciesTask task = new GetDependenciesTask();
        task.setProject(project);
        task.setProperty("${propertyname}.dep");
        task.setComponentDescriptor(
            new File("com/dawidweiss/carrot/ant/dependency.xml".replace('/', File.separatorChar))
        );

        FileSet fs = task.createDependencies();
        fs.setDir(new File("."));
        fs.setIncludes("**/*.dep.xml");
        task.execute();
        
        assertNotNull(project.getProperty("value.dep"));

        assertEquals(
            new File(
                "com/dawidweiss/carrot/ant/subdir/component-b.dep.xml".replace(
                    '/', File.separatorChar
                )
            ).getCanonicalPath(), new File(project.getProperty("value.dep")).getCanonicalPath()
        );
    }
    
}
