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
import org.carrot2.util.tokenizer.languages.chinese.Chinese;

/**
 * A Lingo component fixed to the Chinese language.
 * 
 * @author Stanislaw Osinski
 */
public class ChineseLingoLocalFilterComponent extends LingoLocalFilterComponent
{
    public ChineseLingoLocalFilterComponent()
    {
        super(new Language []
        {
            new Chinese()
        }, new Chinese(), new HashMap());
    }
}