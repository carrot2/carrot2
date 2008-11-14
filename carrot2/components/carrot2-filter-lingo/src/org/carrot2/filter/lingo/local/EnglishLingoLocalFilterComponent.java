
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

package org.carrot2.filter.lingo.local;

import java.util.HashMap;

import org.carrot2.core.linguistic.Language;
import org.carrot2.util.tokenizer.languages.english.English;


/**
 * A Lingo component fixed to English language.
 * 
 * @author Dawid Weiss
 */
public class EnglishLingoLocalFilterComponent 
    extends LingoLocalFilterComponent
{
    public EnglishLingoLocalFilterComponent() {
        super(new Language [] { new English() }, new HashMap());
    }
}