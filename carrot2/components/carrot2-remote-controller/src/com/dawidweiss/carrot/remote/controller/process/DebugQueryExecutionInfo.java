package com.dawidweiss.carrot.remote.controller.process;

import java.io.StringWriter;

import org.apache.log4j.*;

import com.dawidweiss.carrot.util.common.PlainTextHelper;


/**
 * Holds various data about the debug execution of a query.
 */
public class DebugQueryExecutionInfo
{
    private final String PATTERN = "%d %-5p [%t] %c{2} -- %m%n";
    private Appender appender;
    private StringWriter logCapture = new StringWriter();
    private Logger logger;
    private int logStreamBytes = 255;


    public DebugQueryExecutionInfo(Logger captureLogger)
    {
        this.logger = captureLogger;
    }
    
    public synchronized void startCapturingLog4j()
    {
        if (this.appender != null)
            logger.removeAppender(appender);
        
        PatternLayout layout = new ColorizedPatternLayout(PATTERN);
        appender = new WriterAppender( layout, logCapture);
        logger.addAppender(appender);
    }
    
    public synchronized void finish()
    {
        if (appender != null)
        {
            logger.removeAppender(appender);
        }
    }

    public synchronized void startComponent(String componentId)
    {
        logger.warn("Starting component: " + componentId);
    }

	/**
	 * @param string
	 * @param bytes
	 */
	public synchronized void addStreamInfo(String string, byte[] bytes)
    {
        logger.debug(string);
        logger.debug("First " + logStreamBytes + " bytes: \n"
            + PlainTextHelper.hexDump( bytes, 0, logStreamBytes));
        logger.debug("Last " + logStreamBytes + " bytes: \n"
            + PlainTextHelper.hexDump( bytes, -logStreamBytes, logStreamBytes));
	}
    
	/**
	 * @return
	 */
	public String getDebugLogAsHTML()
    {
        return "<pre>" + logCapture.getBuffer().toString() + "</pre>";
	}
}
