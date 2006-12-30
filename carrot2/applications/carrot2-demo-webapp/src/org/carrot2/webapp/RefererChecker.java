package org.carrot2.webapp;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * Verifies if a given request's referer matches any pattern from 
 * a list of regular expressions. 
 * 
 * @author Dawid Weiss
 */
public final class RefererChecker {
    private final static Logger logger = Logger.getLogger(RefererChecker.class);

    public final static String HTTP_HEADER_REFERER = "Referer";
    
    /**
     * Precompiled referer patterns.
     */
    private final Pattern[] patterns;

    /**
     * @param acceptReferers A list of comma-separated regular expressions
     * with patterns of accepted referers.
     * @param regexpFlags Flags for compiling regular expressions.
     */
    public RefererChecker(String acceptReferers, int regexpFlags) {
        if (acceptReferers == null || acceptReferers.trim().length() == 0) {
            this.patterns = new Pattern[0];
        } else {
            final String [] acceptPatterns = acceptReferers.split("\\,");
            this.patterns = new Pattern[acceptPatterns.length];
            for (int i = 0; i < acceptPatterns.length; i++) {
                this.patterns[i] = Pattern.compile(acceptPatterns[i], regexpFlags);
            }
        }
    }

    public boolean check(HttpServletRequest request) {
        final String referer = request.getHeader(HTTP_HEADER_REFERER);

        if (logger.isInfoEnabled()) {
            logger.info("Request: " + request.getRequestURL() + " referer: " + referer);
        }

        // accept empty referers.
        if (referer == null || referer.trim().length() == 0) {
            return true;
        }

        for (int i = 0; i < patterns.length; i++) {
            if (false == patterns[i].matcher(referer).matches()) {
                return false;
            }
        }

        return true;
    }
}

