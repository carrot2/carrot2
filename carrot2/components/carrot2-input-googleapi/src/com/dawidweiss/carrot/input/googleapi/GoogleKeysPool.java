package com.dawidweiss.carrot.input.googleapi;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

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

	public void addKey(final String key, int maxResults) {
		final GoogleApiKey newKey = new GoogleApiKey(key, maxResults);
		synchronized (this) {
			availableKeys.add(newKey);
		}
	}
}
