package com.dawidweiss.carrot.input.googleapi;

import java.util.Iterator;
import java.util.TreeSet;

import junit.framework.TestCase;


public class GoogleKeysPoolTest extends TestCase {

	public GoogleKeysPoolTest(String arg0) {
		super(arg0);
	}

	public void testWaitingKeyComparator() {
		GoogleKeysPool.WaitingKey wk1 = new GoogleKeysPool.WaitingKey(30, new GoogleApiKey("abc"));
		GoogleKeysPool.WaitingKey wk2 = new GoogleKeysPool.WaitingKey(20, new GoogleApiKey("abc"));
		GoogleKeysPool.WaitingKey wk3 = new GoogleKeysPool.WaitingKey(20, new GoogleApiKey("def"));
		GoogleKeysPool.WaitingKey wk4 = new GoogleKeysPool.WaitingKey(10, new GoogleApiKey("def"));
		TreeSet ts = new TreeSet();
		ts.add(wk1);
		ts.add(wk2);
		ts.add(wk3);
		ts.add(wk4);
		Iterator i = ts.iterator();
		assertEquals(wk4, i.next());
		i.next();
		i.next();
		assertEquals(wk1, i.next());
	}

	public void testBorrowKey() throws Exception {
		GoogleKeysPool gkp = new GoogleKeysPool();
		gkp.addKey("abc");

		GoogleApiKey key = gkp.borrowKey();
		assertNotNull(key);
		gkp.returnKey(key);
		key = gkp.borrowKey();
		assertNotNull(key);
	}
	
	public void testBorrowKey2() throws Exception {
		GoogleKeysPool gkp = new GoogleKeysPool();
		gkp.addKey("abc");
		gkp.addKey("def");

		GoogleApiKey key1 = gkp.borrowKey();
		assertNotNull(key1);
		GoogleApiKey key2 = gkp.borrowKey();
		assertNotNull(key2);

		assertTrue(key1 != key2);
	}

	public void testReturnKeyLock() throws Exception {
		final GoogleKeysPool gkp = new GoogleKeysPool();
		gkp.addKey("abc");

		final GoogleApiKey key1 = gkp.borrowKey();
		assertNotNull(key1);

		new Thread() {
			public void run() {
				try {
					sleep(2000);
					gkp.returnKey(key1);
				} catch (InterruptedException e) {
				}
			}
		}.start();
		
		// Attempt to get the key.
		final long start = System.currentTimeMillis();
		GoogleApiKey key2 = gkp.borrowKey();
		final long after = System.currentTimeMillis();
		assertEquals(key1, key2);

		// At least a second should pass.
		assertTrue(after - start > 1000);
	}

	public void testHasActiveKeys() throws Exception {
		GoogleKeysPool gkp = new GoogleKeysPool();
		gkp.addKey("abc");
		gkp.addKey("def");

		assertTrue(gkp.hasActiveKeys());

		GoogleApiKey key1 = gkp.borrowKey();
		assertNotNull(key1);
		GoogleApiKey key2 = gkp.borrowKey();
		assertNotNull(key2);
		
		key1.setInvalid(true);
		key2.setInvalid(true);
		
		gkp.returnKey(key1);
		gkp.returnKey(key2);
		
		assertTrue(false == gkp.hasActiveKeys());
		
		try {
			gkp.borrowKey();
			fail();
		} catch (Throwable t) {
			// expected.
		}
	}
}
