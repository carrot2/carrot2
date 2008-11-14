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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Sean Owen
 */
final class JavaUtilLoggingImpl implements CompressingFilterLogger {

	private final Logger logger;

	JavaUtilLoggingImpl(final String loggerName) {
		logger = Logger.getLogger(loggerName);
	}

	public void log(final String message) {
		logger.info(message);
	}

	public void log(final String message, final Throwable t) {
		logger.log(Level.INFO, message, t);
	}

	public void logDebug(final String message) {
		logger.fine(message);
	}

	public void logDebug(final String message, final Throwable t) {
		logger.log(Level.FINE, message, t);
	}

	public String toString() {
		return "JavaUtilLoggingImpl[" + String.valueOf(logger) + ']';
	}

}
