/*
 * Carrot2 Project Copyright (C) 2002-2005, Dawid Weiss Portions (C)
 * Contributors listen in carrot2.CONTRIBUTORS file. All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder of CVS
 * checkout or at: http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */
package com.dawidweiss.carrot.input.localcache;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A cached queries store stores cached queries from one or
 * more directories. A store can be reused by many
 * {@link RemoteCacheAccessLocalInputComponent} objects. 
 * 
 * @author Dawid Weiss
 */
public class CachedQueriesStore {
	private static Logger log = Logger.getLogger(CachedQueriesStore.class);

	/** 
	 *  A map of lists -- every query (String) links to a list of
	 *  component id's and those link to actual ZIPCachedQuery objects. 
	 */
	private HashMap cachedByQuery = new HashMap();


	/**
	 * Creates a new store using files in the provided directory.
	 * 
	 * <p>The directory should contain only cached queries.</p>
	 */
	public CachedQueriesStore(File directory) {
		this( new File [] {directory});
	}

	/**
	 * Creates a new store using files in the provided list of directories
	 * 
	 * <p>The directories should contain only cached queries.</p>
	 */
	public CachedQueriesStore(File [] directories) {
		initialize( directories );
	}
	
	private void initialize(File [] lookupDirs) {
    	// scan through cached queries, unpacks them.
    	for (int i=0;i<lookupDirs.length;i++) {
    		if (!lookupDirs[i].isDirectory())
    			throw new IllegalArgumentException("Not a directory: " + lookupDirs[i].getAbsolutePath());

    		File [] files = lookupDirs[i].listFiles();
    		for (int f = 0; f<files.length; f++) {
    			try {
    				ZIPCachedQuery zcq = new ZIPCachedQuery(files[f]);
    				String query = zcq.getQuery();
    				if (!cachedByQuery.containsKey(query)) {
    					ArrayList list = new ArrayList();
    					cachedByQuery.put(query, list);
    				}

    				ArrayList list = (ArrayList) cachedByQuery.get(query);
    				list.add(zcq);
    			} catch (Exception e) {
    				log.warn("Could not read cached file: "
    					+ files[f].getAbsolutePath());
    			}
    		}
    	}
    }

	public List getCachedQueries() {
		List result = new ArrayList(cachedByQuery.size());
		synchronized (cachedByQuery) {
			for (Iterator i = cachedByQuery.values().iterator(); i.hasNext(); ) {
				List queries = (List) i.next();
				result.addAll( queries );
			}
		}
		return result;
	}

	public ZIPCachedQuery getQuery(String query, String component) {
		synchronized (cachedByQuery) {
			List queries = (List) cachedByQuery.get(query);
			if (queries == null) return null;
			
			if (component != null) {
				int max = queries.size();
				for (int i=0;i<max;i++) {
					ZIPCachedQuery q = (ZIPCachedQuery) queries.get(i);
					if (component.equals(q.getComponentId()))
						return q;
				}
				// no such combination. return null.
				return null;
			} else {
				// return the first one there.
				return (ZIPCachedQuery) queries.get(0);
			}
		}
	}
}
