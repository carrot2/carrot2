
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

package org.carrot2.text.linguistic;

import org.carrot2.core.LanguageCode;

/**
 * A factory for {@link ILanguageModel} implementations.
 */
public interface ILanguageModelFactory
{
    /**
     * @return Return a {@link ILanguageModel} associated with the given code or
     *         <code>null</code> if this language is not supported or its resources are
     *         not available.
     */
    public ILanguageModel getLanguageModel(LanguageCode language);

}
