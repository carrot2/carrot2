/*
 * Copyright 2004-2006 Sean Owen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.planetj.servlet.filter.compression;


import java.util.Enumeration;
import java.util.Iterator;

/**
 * <p>An {@link java.util.Enumeration} which enumerates the contents of an {@link Iterator}.</p>
 *
 * @author Sean Owen
 * @since 1.6
 */
final class IteratorEnumeration implements Enumeration {

	private final Iterator iterator;

	IteratorEnumeration(final Iterator iterator) {
		if (iterator == null) {
			throw new IllegalArgumentException();
		}
		this.iterator = iterator;
	}

	public boolean hasMoreElements() {
		return iterator.hasNext();
	}

	public Object nextElement() {
		return iterator.next();
	}

	public String toString() {
		return "IteratorEnumeration";
	}

}
