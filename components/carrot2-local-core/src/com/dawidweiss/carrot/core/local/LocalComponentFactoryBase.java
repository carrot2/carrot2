
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.core.local;

/**
 * A base utility class for implementation of the
 * {@link LocalComponentFactory} interface.
 * 
 * <p>
 * The purpose of this class is mostly to prevent problems
 * caused by potential API changes in the future - this class
 * will make it possible to include empty implementation of
 * any future methods that are optional.
 * </p>  
 *
 * @author Dawid Weiss
 *
 * @see LocalComponentFactory
 */
public abstract class LocalComponentFactoryBase implements LocalComponentFactory {
    
	/**
     * Provides an empty implementation returning <code>null</code>.
	 * @see com.dawidweiss.carrot.core.local.LocalComponentFactory#getDescription()
	 */
	public String getDescription() {
		return null;
	}

    /**
     * Provides an empty implementation returning <code>null</code>.
	 * @see com.dawidweiss.carrot.core.local.LocalComponentFactory#getName()
	 */
	public String getName() {
		return null;
	}
}
