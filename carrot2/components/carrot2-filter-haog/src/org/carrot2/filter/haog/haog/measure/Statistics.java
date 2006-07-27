
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

package org.carrot2.filter.haog.haog.measure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class enables easy time measuring and important values saving during 
 * processing. One statistics file via running application is created. 
 * 
 * @author Karol Gołembniak
 */
public class Statistics {

	private static Statistics statistics = new Statistics();
	private Date statsInitTime;
	private List headers;
	private Set headersSet;
	private List memory;
	private Map results;
	private long result;
	private ArrayList timerStack;
	private int depthCounter;
	private boolean enabled;
	
	/**
	 * This class is a singleton. This method returns it's instance.
	 * @return Instance of this class
	 */
	public static Statistics getInstance(){
		return statistics;
	}
	
	/**
	 * Default constructor.
	 */
	private Statistics(){
		this.headers = new ArrayList();
		this.memory =  new ArrayList();
		this.timerStack =  new ArrayList();
		this.headersSet = new HashSet();
		this.statsInitTime = new Date();
		this.depthCounter = -1;
		this.enabled = false;
	}
	
	/**
	 * This method enables functionality of this class and should be called 
	 * before taking measures. Call this method before calling startMeasure()
	 * method.
	 */
	public void enable(){
		this.enabled = true;
	}
	
	/**
	 * This method disables taking statistics.
	 */
	public void diasble(){
		this.enabled = false;
	}
	
	/**
	 * Starts time measuring.
	 */
	public void startTimer(){
		if (!this.enabled) {return;}
		this.timerStack.add(new Long(System.currentTimeMillis()));
		this.depthCounter ++;
	}
	
	/**
	 * Ends last started time measuring and saves result under given header. 
	 * @param header - Header for this value.
	 */
	public void endTimer(String header){
		if (!this.enabled) {return;}
		Long startTime = (Long) this.timerStack.get(this.depthCounter); 
		this.result = System.currentTimeMillis() - startTime.longValue();
		this.timerStack.remove(this.depthCounter);
		this.depthCounter --;
		if (!this.headersSet.contains(header)){
			this.headers.add(header);
			this.headersSet.add(header);
		}
		results.put(header, new Long(this.result));
	}
	
	/**
	 * Saves given value under given header.
	 * @param header - Header for given value.
	 * @param value - Value to save.
	 */
	public void setValue(String header, Object value){
		if (!this.enabled) {return;}
		if (!this.headersSet.contains(header)){
			this.headers.add(header);
			this.headersSet.add(header);
		}
		this.results.put(header, value);
	}
	
	/**
	 * Initializes single measure - should be used once per processing.
	 */
	public void startMeasure(){
		if (!this.enabled) {return;}
		this.results = new HashMap();
	}
	
	/**
	 * Ends measure and saves values to file.
	 */
	public void endMeasure(){
		if (!this.enabled) {return;}
		this.memory.add(this.results);
		saveResults();
	}
	
	/**
	 * Saves reasult to file. Shouldn't be called. Use rather endMeasure(). 
	 */
	public void saveResults() {
		if (!this.enabled) {return;}
		try {
			File file = new File("stats_" + 
					this.statsInitTime.toString().replaceAll("[.:?/\\ ]+", "-") + ".csv");
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			try {
				String header;
				for (Iterator it1 = this.headers.listIterator(); it1.hasNext(); ){
					header = (String) it1.next();
					writer.write(header + ";");
				}
				
				writer.newLine();
				
				Object value = null;
				Map res;
				for (Iterator it1 = this.memory.listIterator(); it1.hasNext(); ){
					res = (Map) it1.next();
					
					for (Iterator it2 = this.headers.listIterator(); it2.hasNext(); ){
						header = (String) it2.next();
						value = res.get(header);
						if (value == null) {
							writer.write("-;");
						} else {
							writer.write(value + ";");
						}
					}
					writer.newLine();
				}
			} finally {
				writer.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("Save statistics error:", e);
		}
	}
}
