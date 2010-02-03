
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.yahoo;

import org.carrot2.util.attribute.constraint.IValueHintMapping;

/**
 * Yahoo country codes.
 * 
 * http://developer.yahoo.com/search/countries.html
 */
public enum YahooCountryCodes implements IValueHintMapping
{
    ARGENTINA("Argentina", "ar"),
    AUSTRALIA("Australia", "au"),
    AUSTRIA("Austria", "at"),
    BELGIUM("Belgium", "be"),
    BRAZIL("Brazil", "br"),
    CANADA("Canada", "ca"),
    CHINA("China", "cn"),
    CHECH_SLOVAK("Chech Rep. and Slovakia", "cz"),
    DENMARK("Denmark", "dk"),
    FINLAND("Finland", "fi"),
    FRANCE("France", "fr"),
    GERMANY("Germany", "de"),
    ITALY("Italy", "it"),
    JAPAN("Japan", "jp"),
    KOREA("Korea", "kr"),
    NETHERLANDS("Netherlands", "nl"),
    NORWAY("Norway", "no"),
    POLAND("Poland", "pl"),
    RUSSIA("Russian Federation", "ru"),
    SPAIN("Spain", "es"),
    SWEDEN("Sweden", "se"),
    SWITZERLAND("Switzerland", "ch"),
    TAIWAN("Taiwan", "tw"),
    UK("United Kingdom and Ireland", "uk"),
    USA("United States", "us");

    public final String friendlyName;
    public final String paramCode;

    private YahooCountryCodes(String friendlyName, String paramCode)
    {
        this.friendlyName = friendlyName;
        this.paramCode = paramCode;
    }

    public String getAttributeValue()
    {
        return paramCode;
    }

    public String getUserFriendlyName()
    {
        return friendlyName;
    }
    
    @Override
    public String toString()
    {
        return getUserFriendlyName();
    }
}
