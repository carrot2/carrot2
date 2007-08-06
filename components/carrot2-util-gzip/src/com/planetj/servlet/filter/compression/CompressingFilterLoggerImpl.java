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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * A simple facade in front of logging services -- this class is used by other classes in this package to log
 * informational messages. It simply logs these message to the servlet log.
 *
 * @author Sean Owen
 */
final class CompressingFilterLoggerImpl implements CompressingFilterLogger {

	private static final String MESSAGE_PREFIX = " [CompressingFilter/" + CompressingFilter.VERSION + "] ";

	private final ServletContext servletContext;
	private final boolean debug;
	private final CompressingFilterLogger delegate;

	CompressingFilterLoggerImpl(final ServletContext ctx,
	                            final boolean debug,
	                            final String delegateLoggerName,
	                            final boolean isJavaUtilLogger) throws ServletException {
		Compat.assertion(ctx != null);
		servletContext = ctx;
		this.debug = debug;

		if (delegateLoggerName == null) {
			delegate = null;
		} else if (isJavaUtilLogger) {
			delegate = new JavaUtilLoggingImpl(delegateLoggerName);
		} else {
		    delegate = null;
        }
	}

	boolean isDebug() {
		return debug;
	}

	public void log(final String message) {
		servletContext.log(MESSAGE_PREFIX + message);
		if (delegate != null) {
			delegate.log(message);
		}
	}

	public void log(final String message, final Throwable t) {
		servletContext.log(MESSAGE_PREFIX + message, t);
		if (delegate != null) {
			delegate.log(message, t);
		}
	}

	public void logDebug(final String message) {
		if (debug) {
			log(message);
			if (delegate != null) {
				delegate.logDebug(message);
			}
		}
	}

	public void logDebug(final String message, final Throwable t) {
		if (debug) {
			log(message, t);
			if (delegate != null) {
				delegate.logDebug(message, t);
			}
		}
	}

	public String toString() {
		return "CompressingFilterLoggerImpl";
	}

}
