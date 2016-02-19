
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

package org.carrot2.core;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * HTTP authentication hub. It's not the best fix for CARROT-1072 (a better one would somehow
 * consolidate all URI resolution), but at least it should work for now.
 */
public final class HttpAuthHub
{
    public static final String USERNAME_PROPERTY = "http.auth.username";
    public static final String PASSWORD_PROPERTY = "http.auth.password";
    
    public static final AtomicBoolean once = new AtomicBoolean(true);

    static void setupAuthenticator()
    {
        if (System.getProperty(USERNAME_PROPERTY) != null ||
            System.getProperty(PASSWORD_PROPERTY) != null) {

            synchronized (HttpAuthHub.class) {
                if (once.getAndSet(false)) {
                    Authenticator.setDefault(new Authenticator()
                    {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication()
                        {
                            return new PasswordAuthentication(getUser(), getPassword());
                        }
                    });
                }
            }
        }
    }

    public static String getUser()
    {
        return System.getProperty(USERNAME_PROPERTY, null);
    }

    public static char [] getPassword()
    {
        String p = System.getProperty(PASSWORD_PROPERTY, null);
        return p == null ? null : p.toCharArray();
    }
}
