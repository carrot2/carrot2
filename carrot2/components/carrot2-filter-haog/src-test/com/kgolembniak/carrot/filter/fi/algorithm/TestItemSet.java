package com.kgolembniak.carrot.filter.fi.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class TestItemSet extends TestCase {

	private ItemSet itemset;
	private BufferedReader resource;

	protected void setUp() throws Exception {
		super.setUp();
		this.itemset = new ItemSet(); 
	}

	protected void tearDown() throws Exception {
		this.itemset = null;
		super.tearDown();
	}
	
	public void testIncSupport(){
		final int prevSupport = this.itemset.getSupport();
		this.itemset.incSupport();
		final int nextSupport = this.itemset.getSupport();

		assertEquals(prevSupport + 1, nextSupport);
	}
	
	public void testItemsOrder(){
		try {
			loadResource("ItemSetOrderTest.res");
		} catch (IOException e){
			fail("Couldn't load resources file :" + e.getMessage());
		}
		
		try {
			String line = resource.readLine();
			while (line != null) {
				if ((!line.startsWith("#")) && (line.trim()!="")){
					final String [] questAnsw = line.split("=");
					final String question = questAnsw [0];
					final String answer = questAnsw [1];
					
					final String [] quest = question.split(",");
					final String [] answ = answer.split(",");
					
					this.itemset.clear();
					for (int i1=0; i1<quest.length; i1++){
						this.itemset.add(quest[i1].trim());
					}
					
					int i1=0;
					for (final Iterator it=this.itemset.iterator(); it.hasNext();){
						assertEquals(answ[i1].trim(), ((String)it.next()).trim());
						i1++;
					}
				}
				line = resource.readLine();
			}
		} catch (IOException e) {
			fail("Reading resources error :" + e.getMessage());
		} catch (Exception ex) {
			fail("Error in resources format :" + ex.getMessage());
		}
	}
	
	private void loadResource(String fileName) throws IOException{
		InputStream stream = this.getClass().getResourceAsStream(fileName);
		this.resource = new BufferedReader(new InputStreamReader(stream));
	}
	
	public void testGetWithoutLast(){
		final String[] items = {"people", "ability", "course", 
				"solvent", "application",  "usually"};
		final String lastItem = "usually";
		
		HashSet set = new HashSet();
		for (int i1=0; i1<items.length; i1++){
			set.add(items[i1]);
			this.itemset.add(items[i1]);
		}
		set.remove(lastItem);
		
		assertEquals(set, this.itemset.getWithoutLast());
	}
	
	public void testGetSubSets(){
		final String[] items = {"people", "ability", "course", 
				"solvent", "application",  "usually"};

		HashSet set = new HashSet();
		for (int i1=0; i1<items.length; i1++){
			set.add(items[i1]);
			this.itemset.add(items[i1]);
		}
		
		for (int i1=0; i1<items.length; i1++){
			HashSet newSet = new HashSet(set);
			newSet.remove(items[i1]);
			
			List subSets = this.itemset.getSubSets();
			boolean containsSet = false;
			for (int i2=0; i2<subSets.size(); i2++){
				if (newSet.equals(subSets.get(i2))){
					containsSet = true;
				}
			}

			assertTrue(containsSet);
		}
	}
}
