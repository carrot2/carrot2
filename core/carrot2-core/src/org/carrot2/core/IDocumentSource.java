
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

package org.carrot2.core;

import java.util.List;

import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;

/**
 * A marker interface for processing components providing documents for further
 * processing. The general process of implementing {@link IDocumentSource}s is the
 * following following:
 * <ol>
 * <li>Create a class that implements {@link IDocumentSource} and annotate it with
 * {@link Bindable}. You may want to extend {@link ProcessingComponentBase} to get empty
 * implementations of the {@link IProcessingComponent} life cycle methods.</li>
 * <li>For each input parameter of your document source (e.g. query, number of results,
 * custom filtering etc.) declare a field and annotate it with {@link Attribute} and
 * {@link Input}. Also, add either {@link Init} or {@link Processing} annotation depending
 * on the intended scope of the parameter. See {@link IProcessingComponent} for
 * information when these fields will be populated with values passed by the caller.</li>
 * <li>For each output value produced by your document source declare a field and annotate
 * it with {@link Attribute}, {@link Output} and {@link Processing} annotations. For the
 * {@link Document}s fetched by your source declare a {@link List}&lt; {@link Document}
 * &gt; field whose {@link Attribute#key()} is {@link AttributeNames#DOCUMENTS}</li>
 * <li>Implement the {@link IProcessingComponent#process()} method to fetch the documents
 * (based on the values read from fields annotated with {@link Input} which will have
 * already been populated with values passed by the caller) and assign the results to the
 * fields annotated with {@link Output} (which Carrot<sup>2</sup> core will collect and
 * pass for further processing).</li>
 * </ol>
 */
public interface IDocumentSource extends IProcessingComponent
{
}
