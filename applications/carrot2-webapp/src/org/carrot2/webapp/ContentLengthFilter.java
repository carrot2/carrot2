/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Buffer the output from filters below and set accurate <code>Content-Length</code>
 * header. This header is required by flash, among others, to display progress
 * information.
 */
public class ContentLengthFilter implements Filter
{
    private final static class BufferingOutputStream extends ServletOutputStream
    {
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        @Override
        public void write(int b) throws IOException
        {
            baos.write(b);
        }
        
        @Override
        public void write(byte [] b) throws IOException
        {
            baos.write(b);
        }

        @Override
        public void write(byte [] b, int off, int len) throws IOException
        {
            baos.write(b, off, len);
        }
    }

    private final static class BufferingHttpServletResponse extends
        HttpServletResponseWrapper
    {
        private enum StreamType
        {
            OUTPUT_STREAM,
            WRITER
        }

        private final HttpServletResponse httpResponse;

        private StreamType acquired;
        private PrintWriter writer;
        private ServletOutputStream outputStream;
        private boolean buffering;

        public BufferingHttpServletResponse(HttpServletResponse response)
        {
            super(response);
            httpResponse = response;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException
        {
            if (acquired == StreamType.WRITER)
                throw new IllegalStateException("Character stream already acquired.");

            if (outputStream != null)
                return outputStream;

            if (hasContentLength())
            {
                outputStream = super.getOutputStream();
            }
            else
            {
                outputStream = new BufferingOutputStream();
                buffering = true;
            }

            acquired = StreamType.OUTPUT_STREAM;
            return outputStream;
        }

        @Override
        public PrintWriter getWriter() throws IOException
        {
            if (acquired == StreamType.OUTPUT_STREAM)
                throw new IllegalStateException("Binary stream already acquired.");

            if (writer != null)
                return writer;

            if (hasContentLength())
            {
                writer = super.getWriter();
            }
            else
            {
                writer = new PrintWriter(new OutputStreamWriter(
                    getOutputStream(), getCharacterEncoding()), false);
            }
            
            acquired = StreamType.WRITER;
            return writer;
        }

        /**
         * Returns <code>true</code> if the user set <code>Content-Length</code>
         * explicitly.
         */
        private boolean hasContentLength()
        {
            return super.containsHeader("Content-Length");
        }

        /**
         * Push out the buffered data.
         */
        public void pushBuffer() throws IOException
        {
            if (!buffering)
                throw new IllegalStateException("Not buffering.");

            BufferingOutputStream bufferedStream = 
                (BufferingOutputStream) outputStream;

            byte [] buffer = bufferedStream.baos.toByteArray();
            httpResponse.setContentLength(buffer.length);
            httpResponse.getOutputStream().write(buffer);
        }
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
        throws IOException, ServletException
    {
        final HttpServletResponse response = (HttpServletResponse) resp;
        final BufferingHttpServletResponse wrapped = 
            new BufferingHttpServletResponse(response);
        
        chain.doFilter(req, wrapped);
        
        if (wrapped.buffering)
        {
            wrapped.pushBuffer();
        }
    }

    public void destroy()
    {
        // Empty
    }

    public void init(FilterConfig config) throws ServletException
    {
        // Empty
    }
}
