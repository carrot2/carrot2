

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.controller.carrot2.guard;


import com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.*;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.*;
import org.apache.log4j.*;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Limits the number of queries to a given component per hour.
 */
public class HourlyLimitGuard
    implements QueryGuard, org.put.util.component.PostConfigureCheck
{
    private static final Logger log = Logger.getLogger(HourlyLimitGuard.class);
    private static String CONTEXT_COUNTER_NAME_PREFIX = "__" + HourlyLimitGuard.class.getName()
        + "__COUNTER__";
    private String counterName;
    private String componentId;
    private String componentPrefix;
    private int hourlyLimit;
    private long resetPeriod = 1000 * 60 * 60;

    public void setCounterResetPeriod(int resetPeriod)
    {
        if (resetPeriod <= 0)
        {
            throw new IllegalArgumentException("Reset period cannot be less or equal to zero.");
        }

        this.resetPeriod = resetPeriod * 1000;
    }


    /**
     * Sets the ID prefix of a family of components to count
     */
    public void setComponentPrefix(String prefix)
    {
        if (this.componentId != null)
        {
            throw new IllegalArgumentException("Set either prefix or component id.");
        }

        if (this.componentPrefix != null)
        {
            throw new IllegalArgumentException("Set prefix only once");
        }

        this.componentPrefix = prefix;
        this.counterName = CONTEXT_COUNTER_NAME_PREFIX + ":prefix:" + componentId;
    }


    /**
     * Sets the ID of a component to count
     */
    public void setComponentId(String componentId)
    {
        if (this.componentId != null)
        {
            throw new IllegalArgumentException("Set componentId only once");
        }

        if (this.componentPrefix != null)
        {
            throw new IllegalArgumentException("Set either prefix or component id");
        }

        this.componentId = componentId;
        this.counterName = CONTEXT_COUNTER_NAME_PREFIX + ":id:" + componentId;
    }


    /**
     * Set hourly limit of queries to this component
     */
    public void setHourlyLimit(int hitsPerHour)
    {
        this.hourlyLimit = hitsPerHour;
    }


    /**
     * Return null to allow the query to be executed for the given component or any other string to
     * indicate an erraneous situation. The string will be looked up in the locales file and
     * displayed to the user.
     */
    public String allowInputComponent(
        Query q, ComponentDescriptor component, HttpSession session, HttpServletRequest request,
        ServletContext context
    )
    {
        return allow(component, session, request, context);
    }


    /**
     * Return null to allow the data stream to be passed to the given component or any other string
     * to indicate an erraneous situation. The string will be looked up in the locales file and
     * displayed to the user.
     */
    public String allowFilterComponent(
        ComponentDescriptor component, HttpSession session, HttpServletRequest request,
        ServletContext context
    )
    {
        return allow(component, session, request, context);
    }


    /**
     * Common for input/filter components.
     */
    private final String allow(
        ComponentDescriptor component, HttpSession session, HttpServletRequest request,
        ServletContext context
    )
    {
        if (
            ((componentPrefix != null) && component.getId().startsWith(componentPrefix))
                || ((componentId != null) && component.getId().equals(this.componentId))
        )
        {
            Object counter = context.getAttribute(counterName);

            if (counter == null)
            {
                counter = new GuardInfo(resetPeriod);
                context.setAttribute(counterName, counter);
            }

            synchronized (counter)
            {
                GuardInfo info = (GuardInfo) counter;
                info.increaseHitsLastHour();

                if (info.getHitsLastHour() > this.hourlyLimit)
                {
                    return "hits-limit-exceeded";
                }

                log.debug("Hits: " + info.getHitsLastHour());
            }
        }

        return null;
    }

    /**
     * Hits counter class
     */
    private static final class GuardInfo
    {
        private int hitsLastHour = 0;
        private long nextReset = nextResetTime();
        private long resetPeriod;

        public GuardInfo(long resetPeriodMillis)
        {
            this.resetPeriod = resetPeriodMillis;
        }

        public int getHitsLastHour()
        {
            counterCheck();

            return hitsLastHour;
        }


        public void increaseHitsLastHour()
        {
            counterCheck();
            hitsLastHour++;
        }


        private void counterCheck()
        {
            if (this.nextReset < System.currentTimeMillis())
            {
                this.nextReset = nextResetTime();
                this.hitsLastHour = 0;
            }
        }


        private long nextResetTime()
        {
            return System.currentTimeMillis() + resetPeriod;
        }
    }

    /**
     * Make sure everything has been configured as needed.
     */
    public String assertConfigured()
    {
        if (hourlyLimit == 0)
        {
            return "hourlyLimit property is required";
        }

        if ((componentId == null) && (componentPrefix == null))
        {
            return "componentId or componentPrefix property is required";
        }

        return null;
    }
}
