
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.googleapi;

import java.io.*;
import java.util.*;

/**
 * A pool for Google API keys.
 * 
 * @author Dawid Weiss
 */
public class GoogleKeysPool {
	/**
	 * Milliseconds per day + 5 minutes.
	 */
	private final static long DEAD_KEY_DELAY = (24 * 60 * 60 * 1000) + (5 * 60 * 1000);

	private LinkedList availableKeys = new LinkedList();
	private HashSet borrowedKeys = new HashSet();

	private TreeSet inactiveKeys = new TreeSet();

	static class WaitingKey implements Comparable {
		final long availableAt;
		final GoogleApiKey key;
		
		WaitingKey(long availableAt, GoogleApiKey key) {
			this.availableAt = availableAt;
			this.key = key;
		}

		public int compareTo(Object o) {
			final WaitingKey other = (WaitingKey) o;
			if (availableAt < other.availableAt) {
				return -1;
			} else if (availableAt > other.availableAt) {
				return 1;
			} else {
				// We don't define equivalence because we couldn't use a set then.
				return this.hashCode() - other.hashCode();
			}
		}
	}
	
	public GoogleApiKey borrowKey() throws Exception {
		synchronized (this) {
			if (false == hasActiveKeys()) {
				throw new Exception("No active Google API keys available.");
			}
			while (availableKeys.size() == 0) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					// Ignore interrupts.
				}
			}
			GoogleApiKey key = (GoogleApiKey) availableKeys.remove(0);
			borrowedKeys.add(key);
			return key;
		}
	}

	public void returnKey(GoogleApiKey key) {
		synchronized (this) {
			if (false == borrowedKeys.contains(key)) {
				throw new Error("Assertion failure: returned key not in the borrowed list.");
			}

			borrowedKeys.remove(key);

			if (key.isInvalid()) {
				inactiveKeys.add(new WaitingKey(System.currentTimeMillis() + DEAD_KEY_DELAY, key));
			} else {
				availableKeys.add(key);
			}
			notifyAll();
		}
	}

	public boolean hasActiveKeys() {
		synchronized (this) {
			checkInactiveKeys();
			if (availableKeys.size() + borrowedKeys.size() > 0) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Check if any of the invalid keys can be reclaimed.
	 * <b>Synchronize on 'this' before invoking this method.</b>
	 */
	private void checkInactiveKeys() {
		final long now = System.currentTimeMillis();
		for (Iterator i = inactiveKeys.iterator(); i.hasNext();) {
			WaitingKey wkey = (WaitingKey) i.next();
			if (wkey.availableAt < now) {
				// Reclaim this key.
				i.remove();
				wkey.key.setInvalid(false);
				availableKeys.add(wkey.key);
			} else {
				// Keys are sorted, exit.
				break;
			}
		}
	}

	/**
	 * Adds a single key to the pool.
	 *
	 * @param key The key.
	 */
	public void addKey(final String key) {
		addKey(key, null);
	}

	/**
	 * Adds a single named key to the pool.
	 *
	 * @param key The key.
	 */
	public void addKey(final String key, final String name) {
		GoogleApiKey newKey;
		if (name == null) {
			newKey = new GoogleApiKey(key);
		} else {
			newKey = new GoogleApiKey(key, name);
		}
		synchronized (this) {
			availableKeys.add(newKey);
		}
	}

	/**
	 * Adds all keys from files in the given folder. A key file
	 * is a plain text file. The key must be the only entry on the
	 * first line.
	 *
	 * @param dir The directory to scan.
	 * @param extension File extension. Only files ending with this
	 * 			extension will be considered keys.
	 */
	public void addKeys(File dir, final String extension) throws IOException {
		File [] keys = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String fileName) {
				return fileName.endsWith(extension);
			}
		});
        if (keys != null) {
    		for (int i = 0; i < keys.length; i++) {
    			BufferedReader reader = new BufferedReader(
    					new InputStreamReader(
    							new FileInputStream(keys[i]), "UTF-8"));
    			try {
    				String line = reader.readLine();
    				if (line == null || "".equals(line.trim())) {
    					throw new IOException("Key file is incorrect: first line is empty: "
    							+ keys[i].getAbsolutePath());
    				}
    				addKey(line.trim(), keys[i].getName());
    			} finally {
    				reader.close();
    			}
    		}
        }
	}
    
    public int getKeysTotal() {
        synchronized (this) {
            return inactiveKeys.size() + availableKeys.size();
        }
    }

    public final static String POOL_SYSPROPERTY = "googleapi.keypool";
    private static GoogleKeysPool defaultPool;

    /**
     * Returns the default instance of a google keys pool, instantiated
     * from a location given in the system property <code>googleapi.keypool</code>.
     */
    public static synchronized GoogleKeysPool getDefault() {
        if (defaultPool == null) {
            final String poolLocation = System.getProperty(POOL_SYSPROPERTY);
            if (poolLocation != null) {
                final GoogleKeysPool pool = new GoogleKeysPool();
                final File keyPool = new File(poolLocation);
                if (keyPool.exists() && keyPool.isDirectory()) {
                    try {
                        pool.addKeys(keyPool, ".key");
                    } catch (IOException e) {
                        throw new RuntimeException("Could not read google keys from:"
                                + keyPool.getAbsolutePath(), e);
                    }
                }
                if (pool.getKeysTotal() == 0) {
                    throw new RuntimeException(
                            "No available keys in: " 
                            + keyPool.getAbsolutePath()
                            + " (remember about *.key extension)");
                }        
                defaultPool = pool;
            } else {
                throw new RuntimeException("System property: "
                        + POOL_SYSPROPERTY + " undefined.");
            }
        }
        return defaultPool;
    }
}
