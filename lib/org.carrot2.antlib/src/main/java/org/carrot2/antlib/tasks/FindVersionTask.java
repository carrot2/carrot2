
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

package org.carrot2.antlib.tasks;

import java.io.File;
import org.apache.tools.ant.*;


/**
 * Find Eclipse version number (?)
 */
public class FindVersionTask
{
    private File eclipseDir;

    private String pluginId;

    private String featureId;

    private String pluginForm = "jar";

    private String property;

    private Project project;

    public void setProject(Project project)
    {
        this.project = project;
    }

    public String getProperty()
    {
        return property;
    }

    public void setProperty(String property)
    {
        this.property = property;
    }

    public void setEclipseHome(String eclipseHome)
    {
        eclipseDir = new File(eclipseHome);
        if (!eclipseDir.exists())
        {
            throw new BuildException(eclipseHome + " directory does not exist!");
        }
        if (!eclipseDir.isDirectory())
        {
            throw new BuildException(eclipseHome + " is not a directory!");
        }
        File pluginsDir = new File(eclipseDir, "plugins");
        if (!pluginsDir.exists())
        {
            throw new BuildException(eclipseHome
                + " directory does not contain Eclipse installation!"
                + "Subdirectory 'plugins' not found");
        }
        File featuresDir = new File(eclipseDir, "features");
        if (!featuresDir.exists())
        {
            throw new BuildException(eclipseHome
                + " directory does not contain Eclipse installation!"
                + "Subdirectory 'features' not found");
        }
    }

    public String getPluginId()
    {
        return pluginId;
    }

    public void setPluginId(String pluginId)
    {
        this.pluginId = pluginId;
    }

    public String getFeatureId()
    {
        return featureId;
    }

    public void setFeatureId(String featureId)
    {
        this.featureId = featureId;
    }

    public String getPluginForm()
    {
        return pluginForm;
    }

    public void setPluginForm(String pluginForm)
    {
        this.pluginForm = pluginForm;
    }

    public void execute()
    {
        checkAttributesCorrectness();
        findVersion();
    }

    private void findVersion()
    {
        File baseDir = null;
        String includes = "";
        boolean dirExpected = true;
        if (pluginId != null && pluginId.length() > 0)
        {
            baseDir = new File(eclipseDir, "plugins");
            if ("jar".equals(pluginForm))
            {
                includes = pluginId + "_*.jar";
                dirExpected = false;
            }
            else
            {
                includes = pluginId + "_*";
            }
        }
        else
        {
            baseDir = new File(eclipseDir, "features");
            includes = featureId + "_*";
        }
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(baseDir);
        scanner.setIncludes(new String []
        {
            includes
        });
        scanner.scan();
        String [] result;
        if (dirExpected)
        {
            result = scanner.getIncludedDirectories();
        }
        else
        {
            result = scanner.getIncludedFiles();
        }
        if (result.length == 0)
        {
            throw new BuildException(includes + " not found!");
        }
        if (result.length == 1)
        {
            project.setProperty(property, new File(result[0]).getName());
        }
        else
        {
            // After update plugins can be duplicated - choose newest one than
            File newest = new File(result[0]);
            for (int i = 1; i < result.length; i++)
            {
                File file = new File(result[i]);
                if (file.lastModified() > newest.lastModified())
                {
                    newest = file;
                }
            }
            project.setProperty(property, new File(result[0]).getName());
        }
    }

    private void checkAttributesCorrectness()
    {
        if (property == null || property.length() == 0)
        {
            throw new BuildException("property attribute not set!");
        }

        if (!"jar".equals(pluginForm) && !"dir".equals(pluginForm))
        {
            throw new BuildException(
                "pluginForm attribute must have one of these values: 'dir' or 'jar'");
        }

        if ((pluginId == null || pluginId.length() == 0)
            && (featureId == null || featureId.length() == 0))
        {
            throw new BuildException(
                "PluginId nor featureId specified. One of them must be set!");
        }

        if ((pluginId != null && pluginId.length() > 0)
            && (featureId != null && featureId.length() > 0))
        {
            throw new BuildException("Both pluginId and featureId specified. "
                + "Only one of the should be set!");
        }
    }
}
