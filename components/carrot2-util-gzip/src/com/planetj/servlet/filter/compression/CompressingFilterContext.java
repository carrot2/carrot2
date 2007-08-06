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

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Encapsulates the {@link CompressingFilter} environment, including configuration and runtime statistics. This object
 * may be conveniently passed around in the code to make this information available.
 *
 * @author Sean Owen
 * @author Peter Bryant
 */
final class CompressingFilterContext {

	private static final int DEFAULT_COMPRESSION_THRESHOLD = 1024;

	private final boolean debug;
	private final CompressingFilterLoggerImpl logger;
	private final int compressionThreshold;
	private final ServletContext servletContext;
	private final CompressingFilterStats stats;
	private final boolean includeContentTypes;
	private final Set contentTypes;
	// Thanks to Peter Bryant for suggesting this functionality:
	private final boolean includePathPatterns;
	private final Set pathPatterns;

	CompressingFilterContext(final FilterConfig filterConfig) throws ServletException {

        Compat.assertion(filterConfig != null);

		debug = readBooleanValue(filterConfig, "debug");

		final String javaUtilDelegateName = filterConfig.getInitParameter("javaUtilLogger");
		if (javaUtilDelegateName != null) {
			logger = new CompressingFilterLoggerImpl(filterConfig.getServletContext(),
			                                         debug,
			                                         javaUtilDelegateName,
			                                         true);
		} else {
			final String jakartaCommonsDelegateName =
				filterConfig.getInitParameter("jakartaCommonsLogger");
			if (jakartaCommonsDelegateName != null) {
				logger = new CompressingFilterLoggerImpl(filterConfig.getServletContext(),
				                                         debug,
				                                         jakartaCommonsDelegateName,
				                                         false);
			} else {
				logger = new CompressingFilterLoggerImpl(filterConfig.getServletContext(),
				                                         debug,
				                                         null,
				                                         false);
			}
		}

		logger.logDebug("Debug logging statements are enabled");

		compressionThreshold = readCompressionThresholdValue(filterConfig);
		if (logger.isDebug()) {
			logger.logDebug("Using compressing threshold: " + compressionThreshold);
		}

		servletContext = filterConfig.getServletContext();
        Compat.assertion(this.servletContext != null);

		if (readBooleanValue(filterConfig, "statsEnabled")) {
			stats = new CompressingFilterStats();
			ensureStatsInContext();
			logger.logDebug("Stats are enabled");
		} else {
			stats = null;
			logger.logDebug("Stats are disabled");
		}

		final String includeContentTypesString = filterConfig.getInitParameter("includeContentTypes");
		final String excludeContentTypesString = filterConfig.getInitParameter("excludeContentTypes");
		if (includeContentTypesString != null && excludeContentTypesString != null) {
			throw new IllegalArgumentException("Can't specify both includeContentTypes and excludeContentTypes");
		}

		if (includeContentTypesString == null) {
			includeContentTypes = false;
			contentTypes = parseContentTypes(excludeContentTypesString);
		} else {
			includeContentTypes = true;
			contentTypes = parseContentTypes(includeContentTypesString);
		}

		if (!contentTypes.isEmpty()) {
			logger.logDebug("Filter will " + (includeContentTypes ? "include" : "exclude") +
							" only these content types: " + contentTypes);
		}

		final String includePathPatternsString = filterConfig.getInitParameter("includePathPatterns");
		final String excludePathPatternsString = filterConfig.getInitParameter("excludePathPatterns");
		if (includePathPatternsString != null && excludePathPatternsString != null) {
			throw new IllegalArgumentException("Can't specify both includePathPatterns and excludePathPatterns");
		}

		if (includePathPatternsString == null) {
			includePathPatterns = false;
			pathPatterns = parsePathPatterns(excludePathPatternsString);
		} else {
			includePathPatterns = true;
			pathPatterns = parsePathPatterns(includePathPatternsString);
		}

		if (!pathPatterns.isEmpty()) {
			logger.logDebug("Filter will " + (includePathPatterns ? "include" : "exclude") +
			                " only these file patterns: " + pathPatterns);
		}
	}

	boolean isDebug() {
		return debug;
	}

	CompressingFilterLoggerImpl getLogger() {
        Compat.assertion(logger != null);
		return logger;
	}

	int getCompressionThreshold() {
		return compressionThreshold;
	}

	CompressingFilterStats getStats() {
		if (stats == null) {
			throw new IllegalStateException("Stats are not enabled");
		}
		ensureStatsInContext();
		return stats;
	}

	boolean isStatsEnabled() {
		return stats != null;
	}

	boolean isIncludeContentTypes() {
		return includeContentTypes;
	}

	Set getContentTypes() {
        Compat.assertion(contentTypes != null);
		return contentTypes;
	}

	boolean isIncludePathPatterns() {
		return includePathPatterns;
	}

	Set getPathPatterns() {
        Compat.assertion(pathPatterns != null);
		return pathPatterns;
	}

	public String toString() {
		return "CompressingFilterContext";
	}


	private void ensureStatsInContext() {
        Compat.assertion(servletContext != null);
		if (servletContext.getAttribute(CompressingFilterStats.STATS_KEY) == null) {
			servletContext.setAttribute(CompressingFilterStats.STATS_KEY, stats);
		}
	}

	private static boolean readBooleanValue(final FilterConfig filterConfig, final String parameter) {
		return Boolean.valueOf(filterConfig.getInitParameter(parameter)).booleanValue();
	}

	private static int readCompressionThresholdValue(final FilterConfig filterConfig) throws ServletException {
		final String compressionThresholdString = filterConfig.getInitParameter("compressionThreshold");
		final int value;
		if (compressionThresholdString != null) {
			try {
				value = Integer.parseInt(compressionThresholdString);
			} catch (NumberFormatException nfe) {
				throw new ServletException("Invalid compression threshold: " + compressionThresholdString, nfe);
			}
			if (value < 0) {
				throw new ServletException("Compression threshold cannot be negative");
			}
		} else {
			value = DEFAULT_COMPRESSION_THRESHOLD;
		}
		return value;
	}

	private static Set parseContentTypes(final String contentTypesString) {
		if (contentTypesString == null) {
			return Collections.EMPTY_SET;
		}
		final Set contentTypes = new HashSet(5);
        final String [] tokens = contentTypesString.split(",");
		for (int i = 0; i < tokens.length; i++) {
            final String contentType = tokens[i];
			if (contentType.length() > 0) {
				contentTypes.add(contentType);
			}
		}
		return Collections.unmodifiableSet(contentTypes);
	}

	private static Set parsePathPatterns(final String filePatternsString) {
		if (filePatternsString == null) {
			return Collections.EMPTY_SET;
		}
		final Set filePatterns = new HashSet(5);
        final String [] patterns = filePatternsString.split(",");
		for (int i = 0; i < patterns.length; i++) {
            final String filePattern = patterns[i];
			if (filePattern.length() > 0) {
				filePatterns.add(Pattern.compile(filePattern));
			}
		}
		return Collections.unmodifiableSet(filePatterns);
	}

}
