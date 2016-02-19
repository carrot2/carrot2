
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

package org.carrot2.dcs;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.HttpAuthHub;
import org.carrot2.core.ProcessingResult;
import org.carrot2.source.xml.XmlDocumentSource;
import org.carrot2.source.xml.XmlDocumentSourceDescriptor;
import org.carrot2.util.resource.URLResourceWithParams;
import org.carrot2.util.tests.CarrotTestCase;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.security.Password;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakLingering;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope.Scope;
import com.carrotsearch.randomizedtesting.rules.SystemPropertiesRestoreRule;

/**
 * Test cases for the {@link DcsApp}.
 */
@ThreadLeakLingering(linger = 3000)
@ThreadLeakScope(Scope.SUITE)
public class AuthConnectionTest extends CarrotTestCase
{
    private static class ListenerAdapter implements LifeCycle.Listener
    {
        public void lifeCycleFailure(LifeCycle lc, Throwable t)
        {
        }

        public void lifeCycleStarted(LifeCycle lc)
        {
        }

        public void lifeCycleStarting(LifeCycle lc)
        {
        }

        public void lifeCycleStopped(LifeCycle lc)
        {
        }

        public void lifeCycleStopping(LifeCycle lc)
        {
        }
    }
    
    @Rule
    public RuleChain rules = RuleChain.outerRule(new SystemPropertiesRestoreRule());

    @Test
    public void checkBasicAuthAccess() throws Throwable
    {
        final Server server = new Server();
        final SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(/* any */ 0);
        connector.setReuseAddress(false);
        connector.setSoLingerTime(0);
        server.addConnector(connector);

        HashLoginService loginService = new HashLoginService();
        loginService.putUser("username", new Password("userpass"), new String []
        {
            "role1", "role2"
        });

        final CountDownLatch latch = new CountDownLatch(1);

        WebAppContext wac = new WebAppContext();
        wac.getSecurityHandler().setLoginService(loginService);
        wac.setContextPath("/");

        connector.addLifeCycleListener(new ListenerAdapter()
        {
            public void lifeCycleStarted(LifeCycle lc)
            {
                System.out.println("Started on port: " + connector.getLocalPort());
                latch.countDown();
            }
            
            public void lifeCycleFailure(LifeCycle lc, Throwable t)
            {
                System.out.println("Failure: " + t);
                latch.countDown();
            }
        });
        wac.setParentLoaderPriority(true);

        URL resource = getClass().getResource("/auth/basic/kaczynski.xml");
        assertThat(resource.toURI().getScheme()).isEqualTo("file");
        File webapp = new File(resource.toURI());
        webapp = webapp.getParentFile(); // /auth/basic
        webapp = webapp.getParentFile(); // /auth
        wac.setWar(webapp.getAbsolutePath());
        wac.setClassLoader(Thread.currentThread().getContextClassLoader());

        server.setHandler(wac);
        server.setStopAtShutdown(true);
        try
        {
            server.start();
            latch.await();

            System.setProperty(HttpAuthHub.USERNAME_PROPERTY, "username");
            System.setProperty(HttpAuthHub.PASSWORD_PROPERTY, "userpass");
            Controller c = ControllerFactory.createSimple();
            try
            {
                Map<String,Object> attrs = new HashMap<String,Object>();
                XmlDocumentSourceDescriptor.attributeBuilder(attrs)
                    .xml(new URLResourceWithParams(new URL(
                        "http://localhost:"
                        + connector.getLocalPort()
                        + "/basic/kaczynski.xml")));
                ProcessingResult r = c.process(attrs, XmlDocumentSource.class);

                assertThat(r.getDocuments()).hasSize(50);
            }
            finally
            {
                c.dispose();
            }
        }
        finally
        {
            server.stop();
        }
    }
}
