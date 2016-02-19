
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.httpclient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.*;

import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * 
 */
@SuppressWarnings("deprecation")
public class HttpClientFactoryTest extends CarrotTestCase
{
    private static Thread pseudoServer;

    private static ServerSocket serverSocket;

    private static ArrayList<Socket> sockets = Lists.newArrayList();

    @BeforeClass
    public static void setup() throws Exception
    {
        serverSocket = new ServerSocket(/* any */0);

        pseudoServer = new Thread()
        {
            public void run()
            {
                try
                {
                    Socket socket;
                    while ((socket = serverSocket.accept()) != null)
                    {
                        sockets.add(socket);
                    }
                }
                catch (SocketException e)
                {
                    // Ignore, socket closed.
                }
                catch (IOException e)
                {
                    throw new RuntimeException();
                }
            }
        };
    }

    @AfterClass
    public static void cleanup() throws Exception
    {
        serverSocket.close();
        pseudoServer.interrupt();

        while (!sockets.isEmpty())
        {
            CloseableUtils.close(sockets.remove(sockets.size() - 1));
        }
    }

    /**
     * Verify that the connection timeout is working.
     */
    @Test
    public void testTimeOut() throws Exception
    {
        DefaultHttpClient client = HttpClientFactory.getTimeoutingClient(500);
        HttpGet request = new HttpGet("http://localhost:" + serverSocket.getLocalPort());

        long start = System.currentTimeMillis();
        try
        {
            client.execute(request);
            Assert.fail();
        }
        catch (ConnectTimeoutException e)
        {
            // Expected. This is thrown if TCP connection fails into a 
            // firewall deadhole, for example.
        }
        catch (SocketTimeoutException e)
        {
            // Expected. This is thrown if no data appears on input within
            // the given timeout range.
        }
        finally
        {
            client.getConnectionManager().shutdown();
        }
        long end = System.currentTimeMillis();
        
        assertThat(end - start).as("Timeout").isGreaterThanOrEqualTo(500);
    }
}
