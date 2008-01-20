
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.demo;

import java.io.*;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * A base class for process settings that are saved in properties file in user's home
 * directory.
 * 
 * @author Stanislaw Osinski
 */
public abstract class PersistentProcessSettingsBase extends ProcessSettingsBase
{
    private final static Logger logger = Logger
        .getLogger(PersistentProcessSettingsBase.class);

    private final static String CONFIG_DIR_NAME = ".carrot2";
    private final static File CONFIG_DIR = new File(System.getProperty("user.home"),
        CONFIG_DIR_NAME);

    protected abstract String getPropertiesFileNamePart();

    protected abstract void initFromProperties(Properties config);
    
    protected abstract Properties asProperties();

    protected boolean loadConfigFile()
    {
        if (getConfigFile().exists())
        {
            Properties config = new Properties();
            try
            {
                config.load(new FileInputStream(getConfigFile()));
            }
            catch (FileNotFoundException e)
            {
                // ignored
                return false;
            }
            catch (IOException e)
            {
                logger.warn("Problems loading Lucene config", e);
                return false;
            }

            initFromProperties(config);
            return true;
        }
        else
        {
            return false;
        }
    }

    protected void saveConfigFile()
    {
        Properties config = asProperties();
        CONFIG_DIR.mkdirs();

        try
        {
            config
                .store(
                    new FileOutputStream(getConfigFile()),
                    "Carrot2 Tuning Browser Lucene Input config file last saved by the application. Freel free to edit by hand.");
        }
        catch (FileNotFoundException e)
        {
            logger.warn("Problems saving Lucene config", e);
        }
        catch (IOException e)
        {
            logger.warn("Problems saving Lucene config", e);
        }
    }

    private File getConfigFile()
    {
        return new File(CONFIG_DIR, "carrot2-browser." + getPropertiesFileNamePart()
            + ".properties");
    }
}
