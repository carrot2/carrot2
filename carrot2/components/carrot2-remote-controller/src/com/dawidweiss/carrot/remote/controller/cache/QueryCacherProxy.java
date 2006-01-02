
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.remote.controller.cache;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query;


class QueryCacherProxy
{
    private ByteArrayOutputStream data = new ByteArrayOutputStream(8000);
    private InputStream is;
    private boolean streamReadFailed = false;
    private static final Logger log = Logger.getLogger(QueryCacherProxy.class);

    public QueryCacherProxy(
        final Cache controller, final InputStream is, final Query q, final String componentId,
        final Map optionalParams
    )
    {
        log.debug("New cache redirector.");
        this.is = new InputStream()
                {
                    public int available()
                        throws IOException
                    {
                        return is.available();
                    }


                    public void close()
                        throws IOException
                    {
                        log.debug("Closing cached stream.");

                        try
                        {
                            is.close();
                        }
                        catch (IOException e)
                        {
                            throw e;
                        }
                    }


                    public int read()
                        throws IOException
                    {
                        try
                        {
                            int i = is.read();

                            if (i == -1)
                            {
                                streamEnd();

                                return -1;
                            }

                            data.write(i);

                            return i;
                        }
                        catch (IOException e)
                        {
                            streamReadFailed = true;
                            throw e;
                        }
                    }


                    public int read(byte [] b, int off, int len)
                        throws IOException
                    {
                        try
                        {
                            int i = is.read(b, off, len);

                            if (i == -1)
                            {
                                streamEnd();

                                return -1;
                            }

                            data.write(b, off, i);

                            return i;
                        }
                        catch (IOException e)
                        {
                            streamReadFailed = true;
                            throw e;
                        }
                    }


                    public long skip(long n)
                        throws IOException
                    {
                        try
                        {
                            return is.skip(n);
                        }
                        catch (IOException e)
                        {
                            streamReadFailed = true;
                            throw e;
                        }
                    }


                    public void mark(int readlimit)
                    {
                        throw new RuntimeException("Not on cache redirected strams.");
                    }


                    public boolean markSupported()
                    {
                        return false;
                    }


                    public void reset()
                        throws IOException
                    {
                        throw new IOException("Not on cache redirected strams.");
                    }


                    private void streamEnd()
                    {
                        log.debug("EOF of cached stream");

                        byte [] b = data.toByteArray();

                        if (b.length == 0)
                        {
                            log.debug("Empty cached stream - ignoring.");

                            return;
                        }

                        try
                        {
                            controller.addToCache(
                                new MemoryCachedQuery(q, componentId, optionalParams, b)
                            );
                        }
                        catch (Throwable e)
                        {
                            log.debug("Could not create cached query", e);
                        }
                    }
                };
    }

    public InputStream getInputStream()
    {
        return is;
    }
}
