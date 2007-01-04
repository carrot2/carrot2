
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.httpform;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

import org.carrot2.util.StreamUtils;
import org.carrot2.util.URLEncoding;




/**
 * Emulates a HTML FORM request, submitting parameters to some
 * URL using either POST or GET method.
 *
 * HTTP headers can be overriden/ custom headers can be added. Parameters
 * can be either constant, InputStreams or mapped through an external Map.
 *
 * @author Dawid Weiss
 */
public class HTTPFormSubmitter
{
    /** Specification of HTTP service to submit forms to. */
    private FormActionInfo service;

    private URLConnection connection;


    /**
     * Creates an instance of this class.
     * @param service A specification of HTTP service to submit forms to.
     */
    public HTTPFormSubmitter(FormActionInfo service)
    {
        this.service = service;
    }


    /**
     * Submits parameters passed and returns an InputStream to the results page returned by the service.
     *
     * @param   mappedParameters  Value map for mapped parameters.
     * @param   encoding          Encoding to be used when submitting parameters.
     * @param   request           Request parameters to be submitted.
     * @return  InputStream       A result page returned by the server in response to HTTP form submitted.
     */
    public InputStream submit(FormParameters request, Map mappedParameters, String encoding)
            throws IOException
    {
        InputStream is = null;

        // depending on the method, send either post or get and return the
        // InputStream from the connection.
        if ("post".equalsIgnoreCase(service.getMethod()))
        {
            is = doPOST(request, encoding, mappedParameters);
        }
        else if ("get".equalsIgnoreCase(service.getMethod()))
        {
            is = doGET(request, encoding, mappedParameters);
        }
        else
            throw new IllegalArgumentException("Unknown HTTP form submission method: " + service.getMethod());

        return is;
    }


    /**
     * Returns the connection after submitting the query.
     */
    public URLConnection getConnection()
    {
        return this.connection;
    }


    // ------------------------------------------------------- PROTECTED SECTION


    /**
     * Submits parameters using GET request method.
     *
     * InputStream/ Reader parameters are supported, but should be used with care. A query string length is approximately
     * 4k and if exceeded, will be rejected by HTTP servers.
     *
     * All InputStream data is converted to characters using default platform encoding, and then into bytes
     * again using the encoding passed in the input argument.
     *
     * @param request  Request parameters to be submitted.
     * @param encoding Encoding to be used for converting parameters to URL. UTF-8 is recommended.
     * @param mappings Values of mapped parameters from the request.
     * @return  InputStream A result page returned by the server in response to HTTP form submitted.
     */
    protected InputStream doGET(FormParameters request, String encoding, Map mappings)
        throws IOException
    {
        this.connection = null;

        // construct the URL.
        StringBuffer queryString = new StringBuffer();
        Iterator     params      = request.getParametersIterator();
        while ( params.hasNext() )
        {
            Parameter param  = (Parameter) params.next();
            Object value     = param.getValue(mappings);

            // We shouldn't really support InputStreams or Readers with GET (queryString max length is
            // about 4k anyway).
            if (value instanceof InputStream)
            {
                value = new String( StreamUtils.readFullyAndCloseInput((InputStream) value));
            }
            if (value instanceof Reader)
            {
                value = StreamUtils.readFullyAndCloseInput((Reader) value);
            }

            if (queryString.length() > 0)
                queryString.append('&');
            else
                queryString.append('?');

            queryString.append(URLEncoding.encode(param.getName().toString(), encoding));
            queryString.append('=');
            queryString.append(URLEncoding.encode(value.toString(), encoding));
        }

        URLConnection connection;

        // Construct an URL with query string part attached and open a stream.
        connection = new URL(service.getServiceURL().toExternalForm() + queryString).openConnection();
        if (connection instanceof java.net.HttpURLConnection)
        {
            ((java.net.HttpURLConnection) connection).setRequestMethod("GET");
        }

        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.setUseCaches(false);

        // pass HTTP headers
        HashMap httpHeaders = service.getHttpHeaders();
        for (Iterator i = httpHeaders.keySet().iterator(); i.hasNext();)
        {
            String header = (String) i.next();
            connection.setRequestProperty(header, (String) httpHeaders.get(header));
        }

        // we always want to accept GZIP streams
        connection.setRequestProperty("Accept-Encoding", "gzip");

        // connect to the service.
        connection.connect();

        // Get response data.
        InputStream is = connection.getInputStream();

        if ("gzip".equals(connection.getHeaderField("Content-Encoding")))
            is = new GZIPInputStream(is);

        this.connection = connection;

        if (checkConnectionResponseOk(connection)==false)
            return null;

        return is;
    }


    /**
     * Submits parameters using POST request method. <tt>application/x-www-form-urlencoded</tt> body encoding
     * is used (support for other formats may be provided in the future).
     *
     * InputStream/ Reader parameters are supported and passed directly to the output stream (they are
     * not buffered).
     *
     * @param request  Request parameters to be submitted.
     * @param encoding Encoding to be used for converting parameters to URLEncoding. UTF-8 is recommended.
     * @param mappings Values of mapped parameters from the request.
     * @return  InputStream A result page returned by the server in response to HTTP form submitted.
     */
    protected InputStream doPOST(FormParameters request, String encoding, Map mappings)
        throws IOException
    {
        URLConnection      connection;
        OutputStream       outStream;

        // Open a connection to the HTTP service provider.
        connection = service.getServiceURL().openConnection();

        if (connection instanceof java.net.HttpURLConnection)
        {
            ((java.net.HttpURLConnection) connection).setRequestMethod("POST");
        }

        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);

        // pass HTTP headers
        HashMap httpHeaders = service.getHttpHeaders();
        for (Iterator i = httpHeaders.keySet().iterator(); i.hasNext(); )
        {
            String header = (String) i.next();
            connection.setRequestProperty(header, (String) httpHeaders.get(header));
        }

        // These two we will always want to override
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Accept-Encoding", "gzip");

        outStream = connection.getOutputStream();

        // Send parameters to the service.
        Iterator params = request.getParametersIterator();
        byte [] tmp = new byte[3*8000];
        byte [] bbuffer = new byte [8000];
        char [] cbuffer = new char [8000];
        boolean firstRound = true;
        while ( params.hasNext() )
        {
            Parameter param  = (Parameter) params.next();
            Object value     = param.getValue(mappings);
            
            if (firstRound==false)
            {
                outStream.write( '&' );
            }
            else
            {
                firstRound = false;
            }
            
            outStream.write( URLEncoding.encode(param.getName().toString(), encoding).getBytes());
            outStream.write( '=' );
            if (value instanceof InputStream)
            {
                int i;
                do
                {
                    i = ((InputStream) value).read(bbuffer);
                    if (i>0)
                    {
                        int maxIndex = URLEncoding.encodeToArray(bbuffer, 0, i, tmp);
                        outStream.write(tmp, 0, maxIndex);
                    }
                } while (i!=-1);
            }
            else
            if (value instanceof Reader)
            {
                int i;
                do
                {
                    i = ((Reader) value).read(cbuffer);
                    if (i>0)
                    {
                        outStream.write(URLEncoding.encode(new String(cbuffer,0,i).getBytes(encoding)));
                    }
                } while (i!=-1);
            }
            else
            {
                outStream.write( URLEncoding.encode(value.toString().getBytes(encoding)));
            }
        }

        outStream.flush();
        outStream.close();

        this.connection = connection;

        if (checkConnectionResponseOk(connection)==false)
            return null;

        // Get response data.
        InputStream is = connection.getInputStream();

        if ("gzip".equals(connection.getHeaderField("Content-Encoding")))
        {
            is = new GZIPInputStream(is);
        }

        return is;
    }

    protected boolean checkConnectionResponseOk(URLConnection connection)
    {
        if (connection instanceof java.net.HttpURLConnection)
        {
            try
            {
                if (((HttpURLConnection) connection).getResponseCode() != HttpURLConnection.HTTP_OK)
                    return false;
            }
            catch (IOException fof)
            {
                // This is a BUG in JDK. The connection should return the response code,
                // but instead it throws an exception.
                String header = 
                    ((HttpURLConnection) connection).getHeaderField(0);
                if (header == null)
                    return false;

                // copied from jdk1.4
                if (header.startsWith("HTTP/1."))
                {
                    int codePos = header.indexOf(' ');
                    if (codePos > 0)
                    {        
                        int phrasePos = header.indexOf(' ', codePos+1);
            
                        // deviation from RFC 2616 - don't reject status line
                        // if SP Reason-Phrase is not included.
                        if (phrasePos < 0) 
                            phrasePos = header.length();
            
                        try {
                            int responseCode = Integer.parseInt(
                                header.substring(codePos+1, phrasePos));     
                            return responseCode == HttpURLConnection.HTTP_OK;
                        } catch (NumberFormatException e) { }
                    }
                }                     
                return false;
            }
        }
        return true;
    }
}



