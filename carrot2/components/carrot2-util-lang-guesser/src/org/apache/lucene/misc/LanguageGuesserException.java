package org.apache.lucene.misc;

/*
 * Copyright 2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *  
 */

/**
 * 
 * Thrown to indicate that an exception has occured during one of the calls to
 * the LanguageGuesser methods.
 * 
 * @author Jean-Francois Halleux
 */
public class LanguageGuesserException extends RuntimeException {
	
	/**
	 * Constructs a LanguageGuesserException will null
	 * as error message
	 *
	 */
	public LanguageGuesserException() {
		super();
	}

	/**
	 * Constructs a LanguageGuesserException with msg
	 * as error message
	 * 
	 * @param msg The error message
	 */
	public LanguageGuesserException(String msg) {
		super(msg);
	}

	/**
	 * Constructs a LanguageGuesserException with cause
	 * as cause and the cause error message as message * as error message
	 * 
	 * @param cause The cause
	 */
	public LanguageGuesserException(Throwable cause) {
		super(cause);
	}
	
}
