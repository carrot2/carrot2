package org.apache.lucene.misc;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Lucene" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Lucene", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
 
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * A specialized collection for efficient handling of a
 * collection of trigrams. Allow the collection to be save/retrieved
 * from a file as well
 * 
 * @author Jean-Francois Halleux
 * @version $version$
 */
public class Trigrams implements TrigramListener {

	private Map map;
	
	private TrigramWithFrequency[] twfa;
	
	public Trigrams() {
		map=new HashMap();	
	}
	
	// A private constructor used when load trigrams file
	// to prevent rehashing
	private Trigrams(int size) {
		map=new HashMap(size,0.7f); 
	}

	// Add a trigram in the trigram map
	// If the trigram existed, its frequency is increased by 1 
	public void addTrigram(Trigram t) {
		Image image = (Image) map.get(t);
		if (image == null) {
			map.put(t, new Image(1, -1));
		} else {
			Image newImage = new Image(image.frequency + 1, -1);
			map.put(t, newImage);
		}
	}

	// Return the frequency of this trigram in the map
	public int getFrequency(Trigram t) {
		Image image = (Image) map.get(t);
		if (image == null)
			return 0;
		return image.frequency;
	}

	//Return the rank of this trigram in the map
	//-1 if trigram is not in the list
	public int getRank(Trigram t) {
		Image image = (Image) map.get(t);
		if (image == null)
			return -1;
		return image.rank;
	}

	public int size() {
		return map.size();
	}

	public void clear() {
		map.clear();
	}

	/*
	 * Fill in rank in the image/value for each key
	 */
	public void finishBuildUp() {
		twfa = new TrigramWithFrequency[size()];
		Iterator it = map.keySet().iterator();
		int c = 0;
		while (it.hasNext()) {
			Trigram t = (Trigram) it.next();
			Image i = (Image) map.get(t);
			twfa[c++] = new TrigramWithFrequency(t, i.frequency);
		}
		Arrays.sort(twfa);
		clear();
		for (int i = 0; i < twfa.length; i++) {
			TrigramWithFrequency twf = twfa[i];
			map.put(twf.getTrigram(), new Image(twf.getFrequency(), i));
		}
	}

	/**
	 * Computes and returns the average trigram frequency
	 */
	public int getAverageFrequency() {
		if (size() == 0)
			return -1;
		Collection c = map.values();
		Iterator it = c.iterator();
		int i = 0;
		int total = 0;
		while (it.hasNext()) {
			total += ((Image) it.next()).frequency;
			i++;
		}
		return total / i;
	}

	/**
	* Computes and returns the median trigram frequency
	*/
	public int getMedianFrequency() {
		if (size()==0) return -1;
		Iterator it=map.values().iterator();
		int median=size()/2;
		for (int i=0;i<median;i++) it.next();
		return ((Image)it.next()).frequency;
	}

	/**
	* Save the sorted set in the reference xx.tri file
	* File format is length averageFreq medianFreq (char1 char2 char3 frequency)*
	* Every data item is an integer
	* Sorted in decreasing order of frequency
	* File is GZipped
	*/
	public void saveToFile(String fileName) throws IOException {
		finishBuildUp();
		
		DataOutputStream dos =
			new DataOutputStream(
				new BufferedOutputStream(
					new GZIPOutputStream(
						new FileOutputStream(fileName))));

		dos.writeInt(size());
		dos.writeInt(getAverageFrequency());
		dos.writeInt(getMedianFrequency());

		for (int i=0;i<twfa.length;i++) {
			TrigramWithFrequency twf=twfa[i];
			Trigram t = twf.getTrigram();
			dos.writeChar(t.getFirstChar());
			dos.writeChar(t.getSecondChar());
			dos.writeChar(t.getThirdChar());
			dos.writeInt(twf.getFrequency());
		}

		dos.close();
	}
	
	/**
	 * Save the sorted set in a zip entry
	 * File format is length averageFreq medianFreq (char1 char2 char3 frequency)*
	 * Every data item is an integer
	 * Sorted in decreasing order of frequency
	 * File is GZipped
	 */
	public void saveToOutputStream(DataOutputStream dos) throws IOException {
		finishBuildUp();
				
		dos.writeInt(twfa.length);
		dos.writeInt(getAverageFrequency());
		dos.writeInt(getMedianFrequency());

		for (int i=0;i<twfa.length;i++) {
			TrigramWithFrequency twf=twfa[i];
			Trigram t = twf.getTrigram();
			dos.writeChar(t.getFirstChar());
			dos.writeChar(t.getSecondChar());
			dos.writeChar(t.getThirdChar());
			dos.writeInt(twf.getFrequency());
		}		
	}

	/**
	 * Load a reference file.
	 */
	public static Trigrams loadFromFile(String fileName) throws IOException {
		DataInputStream dis =
			new DataInputStream(
				new BufferedInputStream(
					new GZIPInputStream(
						new FileInputStream(fileName))));

		int size = dis.readInt();
		//avg freq
		dis.readInt();
		//median freq
		dis.readInt();

		Trigrams trigrams = new Trigrams(size);
		
		for (int i = 0; i < size; i++) {
			char[] buf = new char[3];
			buf[0] = dis.readChar();
			buf[1] = dis.readChar();
			buf[2] = dis.readChar();
				
			int freq = dis.readInt();
			Trigram t = new Trigram(buf,0);
			trigrams.map.put(t, new Image(freq, -1));
		}
		trigrams.finishBuildUp();
		dis.close();
		return trigrams;
	}

	/**
	 * Load a reference file.
	 */
	public static Trigrams loadFromInputStream(DataInputStream is) throws IOException {
		int size = is.readInt();
		//avg freq
		is.readInt();
		//median freq
		is.readInt();

		Trigrams trigrams = new Trigrams(size);
		
		for (int i = 0; i < size; i++) {
			char[] buf = new char[3];
			buf[0] = is.readChar();
			buf[1] = is.readChar();
			buf[2] = is.readChar();
			int freq = is.readInt();
			Trigram t = new Trigram(buf,0);
			trigrams.map.put(t, new Image(freq, -1));
		}
		
		trigrams.finishBuildUp();
		return trigrams;
	}
	
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Size : ");
		sb.append(size());
		sb.append("\n");
		sb.append("Average Frequency : ");
		sb.append(getAverageFrequency());
		sb.append("\n");
		sb.append("Median Frequency : ");
		sb.append(getMedianFrequency());
		sb.append("\n");

		for (int i=0;i<twfa.length;i++) {
			sb.append(twfa[i]);
			sb.append('\n');
		}
		return sb.toString();
	}

	// returns the "distance" between two trigrams
	// Assume other is a "reference" trigrams, which is fairly
	// complete and that the number of different trigrams for each 
	// language is roughly the same
	// other must have been "finished"
	public long distance(Trigrams other) {
		Iterator it = map.keySet().iterator();
		long distance = 0;
		int otherSize = other.size();

		while (it.hasNext()) {
			Trigram t = (Trigram) it.next();
			int rank = other.getRank(t);
			if (rank == -1)
				rank = otherSize;
			distance += rank;
		}

		return distance;
	}
	
	public void hashTest() {
		int[] hashtable=new int[size()];
		int best=0;
		int x=0,y=0;
		
		while (true) {
				for (int i=0;i<hashtable.length;i++) {
					hashtable[i]=0;
				}
				Iterator it = map.keySet().iterator();
				while (it.hasNext()) {
					Trigram t = (Trigram) it.next();
					x=(int)(Math.random()*100)+250;
					y=(int)(Math.random()*200)+370;
														
					int h=t.getFirstChar()+x*t.getSecondChar()+y*t.getThirdChar();
					
					h=h%hashtable.length;				
										
					//original (toString) : 6131
					//best: 292, 471 , 7217
					hashtable[h]=hashtable[h]+1;
				}
				int t=0;
				for (int i=0;i<hashtable.length;i++) {
					if (hashtable[i]==1) t++;
				}
				if (t>best) {
					best=t;
					System.out.println(x+":"+y+":"+t+","+hashtable.length);
				}
				
		}
	}

}

/**
 * A trigram with frequency class, used to keep track of the
 * trigrams in a specialized collection
 */
class TrigramWithFrequency implements Comparable {

	private final Trigram t;
	private final int frequency;

	public TrigramWithFrequency(Trigram t, int frequency) {
		this.t = t;
		this.frequency = frequency;
	}

	public int getFrequency() {
		return frequency;
	}

	public Trigram getTrigram() {
		return t;
	}
	
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append(t);
		sb.append(':');
		sb.append(frequency);
		return sb.toString();
	}

	/*
	 * Force a total order on trigrams by:
	 * 1. Sorting them by decreasing frequency
	 * 2. For the same frequency, by alphabetic order
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		TrigramWithFrequency twf = (TrigramWithFrequency) o;
		if (frequency > twf.frequency)
			return -1;
		if (frequency < twf.frequency)
			return 1;
		return t.compareTo(twf.t);
	}

}

/*
 * The image or value of a key in the trigrams collection
 */
class Image {

	int frequency;
	int rank;

	Image(int frequency, int rank) {
		this.frequency = frequency;
		this.rank = rank;
	}

}
