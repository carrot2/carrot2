
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
package com.dawidweiss.util.struts;


import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;
import org.apache.struts.util.MessageResources;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * This class loads localized messages from an XML file.
 */
public class XmlMessageResources
    extends MessageResources
{
    private static final Logger log = Logger.getLogger(XmlMessageResources.class);
    private final boolean autoupdateable = true;

    /**
     * A list of object arrays, which holds the reference to a file messages were read from and the
     * last time the file was modified (for the reasons of autoupdating.
     */
    private List files = new LinkedList();

    /** Strings cache */
    private HashMap messages = new HashMap();

    /**
     * MessageFormat objects cache. This is a WeakHashMap so that resources cannot be saturated by
     * random requests.
     */
    private WeakHashMap messageFormatCache = new WeakHashMap();

    /** Default messages language */
    private String defaultXmlLanguage;

    /** Key language extension - all keys in cache are postfixed with this string */
    static final String CACHE_KEY_EXTENSION = "__";

    /**
     * This is a set of missing keys (stored for debugging purposes). If you wish to disable
     * storing missing keys (debugging warning will be issues more than once), set
     * REPORT_MISSING_KEYS_ONCE to false.
     */
    private HashSet missingKeys = new HashSet();

    /** Set this to true if you want missing keys to be reported only once */
    private static final boolean REPORT_MISSING_KEYS_ONCE = true;

    /**
     * Creates an instance of the message resources. The xmlFileName should contain a
     * comma-separated list of XML resources to be read. XML files should follow certain
     * structure, refer to example files. The first specified default language (in the first
     * messages file) specifies the default language for all resources.
     */
    public XmlMessageResources(XmlMessageResourcesFactory factory, String xmlFileName)
    {
        super(factory, xmlFileName);

        StringTokenizer s = new StringTokenizer(xmlFileName, ",");

        while (s.hasMoreTokens())
        {
            String file = s.nextToken();
            loadXmlMessagesFile(file);
        }
    }

    /**
     * Returns a text message for the specified key, for the specified Locale. A null string result
     * will be returned by this method if no relevant message resource is found for this key or
     * Locale, if the <code>returnNull</code> property is set.
     *
     * @param locale The requested message Locale, or <code>null</code> for the system default
     *        Locale
     * @param key The message key to look up
     */
    public String getMessage(Locale locale, String key)
    {
        String message;

        message = internalGetMessage(locale, key);

        if (message != null)
        {
            return message;
        }

        // try Struts default locale
        message = internalGetMessage(super.defaultLocale, key);

        if (message != null)
        {
            return message;
        }

        // try user-config XML default locale
        message = internalGetMessage(null, key);

        if (message != null)
        {
            return message;
        }

        if (!missingKeys.contains(key))
        {
            log.warn(
                "Missing message key: " + key + " ["
                + com.dawidweiss.carrot.remote.controller.util.ExceptionHelper.getCurrentStackTrace() + "]"
            );

            if (REPORT_MISSING_KEYS_ONCE)
            {
                missingKeys.add(key);
            }
        }

        if (super.returnNull)
        {
            return null;
        }
        else
        {
            return "[!missing locale key: " + key + "!]";
        }
    }


    /**
     * This method is overriden, because it otherwise causes double-caching of messages.
     */
    public String getMessage(Locale locale, String key, Object [] args)
    {
        // Cache MessageFormat instances as they are accessed
        if (locale == null)
        {
            locale = super.defaultLocale;
        }

        MessageFormat format = null;
        String formatKey = messageKey(locale, key);

        synchronized (messageFormatCache)
        {
            format = (MessageFormat) messageFormatCache.get(formatKey);

            if (format == null)
            {
                String formatString = getMessage(locale, key);

                if (formatString == null)
                {
                    if (returnNull)
                    {
                        return (null);
                    }
                    else
                    {
                        return ("???" + formatKey + "???");
                    }
                }

                format = new MessageFormat(escape(formatString));
                messageFormatCache.put(formatKey, format);
            }
        }

        return (format.format(args));
    }

    /**
     * Returns a text message for the specified key, for the specified Locale. A null string result
     * will be returned by this method if no relevant message resource is found for this key or
     * Locale.
     *
     * @param locale The requested message Locale, or <code>null</code> for the system default
     *        Locale
     * @param key The message key to look up
     */
    private String internalGetMessage(Locale locale, String key)
    {
        String localeKey = localeKey(locale).toLowerCase();

        while (true)
        {
            if (defaultXmlLanguage.equals(localeKey) || (locale == null))
            {
                return (String) messages.get(key);
            }

            String message = (String) messages.get(key + CACHE_KEY_EXTENSION + localeKey);

            if (message != null)
            {
                return message;
            }

            int underscore = localeKey.lastIndexOf("_");

            if (underscore < 0)
            {
                break;
            }

            localeKey = localeKey.substring(0, underscore);
        }

        // String not found for this locale.
        return null;
    }


    /**
     * Loads an XML messages file and converts it into cached hashmap entries.
     */
    private synchronized void loadXmlMessagesFile(String fileName)
    {
        for (Iterator i = files.iterator(); i.hasNext();)
        {
            Object [] fileInfo = (Object []) i.next();

            if (fileName.equals(fileInfo[0]))
            {
                log.warn("Trying to add messages file twice: " + fileName);

                return;
            }
        }

        InputStream resource = this.getClass().getClassLoader().getResourceAsStream(fileName);

        if (resource == null)
        {
            log.fatal(
                "Cannot open Messages file: " + fileName + " (URL: "
                + this.getClass().getClassLoader().getResource(fileName) + ")"
            );

            return;
        }

        SAXReader reader = new SAXReader(false);

        try
        {
            Element root = reader.read(resource).getRootElement();

            if ("messages".equalsIgnoreCase(root.getName()) == false)
            {
                log.error("The root element of Messages file must be <messages default-lang=\"\">");

                return;
            }

            if (root.attribute("default-lang") == null)
            {
                log.error("The root element must contain attribute 'default-lang'.");

                return;
            }

            String thisFileDefaultLanguage = root.attributeValue("default-lang").trim().toLowerCase();

            if (defaultXmlLanguage == null)
            {
                defaultXmlLanguage = thisFileDefaultLanguage;
            }

            // parse strings and add them to cache.
            addKeysToCache(thisFileDefaultLanguage, "", root);
        }
        catch (Exception e)
        {
            log.error(
                "Cannot parse Messages file: "
                + this.getClass().getClassLoader().getResource(fileName), e
            );
        }
    }


    /**
     * Recursively add all messages to cache, postfixing it with language code. Default language is
     * not postfixed.
     */
    private final void addKeysToCache(String language, String currentKey, Element element)
    {
        if ("lang".equals(element.getNamespacePrefix()))
        {
            language = element.getName().toLowerCase();
        }

        List p = element.elements();
        if ((p != null) && (p.size() > 0))
        {
            // internal node.
            for (Iterator i = p.iterator(); i.hasNext();)
            {
                Element child = (Element) i.next();
                String subKey = currentKey;

                // check if it's language selector, if so, descend only, don't
                // extend the key.
                if (!"lang".equals(child.getNamespacePrefix()))
                {
                    if (currentKey.length() > 0)
                    {
                        subKey += ".";
                    }

                    subKey += child.getName();
                }

                addKeysToCache(language, subKey, child);
            }
        }
        else
        {
            // if different than the default language, extend the key.
            if (!language.equals(defaultXmlLanguage))
            {
                currentKey += (CACHE_KEY_EXTENSION + language);
            }

            messages.put(currentKey, element.getTextTrim());
            log.debug("Adding message [" + currentKey + "]");
        }
    }
}
