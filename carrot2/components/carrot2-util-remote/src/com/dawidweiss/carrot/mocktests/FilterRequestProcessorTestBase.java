package com.dawidweiss.carrot.mocktests;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dawidweiss.carrot.filter.FilterRequestProcessorServlet;
import com.mockobjects.servlet.*;

/**
 * A base class for testing implementations of 
 * {@link com.dawidweiss.carrot.filter.FilterRequestProcessor} interface
 * using mock controller. 
 *  
 * @author Dawid Weiss
 */
public abstract class FilterRequestProcessorTestBase extends TestCase {
    private PatchedMockServletOutputStream output;
    private MockHttpServletResponse response;
    private MockHttpServletRequest request;
    private FilterRequestProcessorServlet servlet;

    public FilterRequestProcessorTestBase(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final PatchedMockServletOutputStream output = new PatchedMockServletOutputStream();  
        final MockHttpServletResponse response = new MockHttpServletResponse();
        response.setupOutputStream(output);

        final MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.setInitParameter("filterRequestProcessor", getFilterRequestProcessorClassName());

        final FilterRequestProcessorServlet servlet = new FilterRequestProcessorServlet();
        servlet.init(servletConfig);

        this.output = output;
        this.response = response;
        this.request = request;
        this.servlet = servlet;
    }

    /**
     * Override this method and return a class name implementing
     * {@link com.dawidweiss.carrot.filter.FilterRequestProcessor}
     * interface.
     */
    protected abstract String getFilterRequestProcessorClassName();

    /**
     * @return Returns a {@link FilterRequestProcessorServlet} initialized
     * to use the filter processor returned from {@link #getFilterRequestProcessorClassName()}.
     */
    protected final FilterRequestProcessorServlet getServletInstance() {
        return servlet;
    }

    protected final HttpServletRequest getRequest() {
        return request;
    }

    protected final HttpServletResponse getResponse() {
        return response;
    }

    /**
     * Utility method for parsing the servlet output stream into DOM4J's XML. 
     */
    protected final Element parseOutputXml() throws IOException {
        final SAXReader reader = new SAXReader(false);
        try {
            return reader.read(new ByteArrayInputStream(output.getBufferContents())).getRootElement();
        } catch (DocumentException e) {
            throw new IOException("Document parsing exception: " + e);
        }
    }
}
