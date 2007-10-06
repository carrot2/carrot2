/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.lingo.local;

import java.util.HashMap;

import org.carrot2.core.linguistic.Language;
import org.carrot2.util.tokenizer.languages.english.English;
import org.carrot2.util.tokenizer.languages.french.*;
import org.carrot2.util.tokenizer.languages.german.*;
import org.carrot2.util.tokenizer.languages.italian.*;
import org.carrot2.util.tokenizer.languages.polish.*;
import org.carrot2.util.tokenizer.languages.spanish.*;

/**
 * A Lingo component for multilingual content
 * 
 * @author Stanislaw Osinski
 */
public class MultilingualLingoLocalFilterComponent extends LingoLocalFilterComponent
{
    public MultilingualLingoLocalFilterComponent()
    {
        super(new Language []
        {
            new English(), new Polish(), new German(), new French(), new Spanish(),
            new Italian()
        }, new HashMap());
    }
}