
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * A class to help generating trigrams
 * 
 * @author Jean-Francois Halleux
 * @version $version$
 */
class TrigramGenerator {
	
	//BUFFERSIZE must be at least 2
	private static final int BUFFERSIZE = 1024;
	private static String fileSeparator;
	
	public TrigramGenerator() {
		fileSeparator=System.getProperty("file.separator");
		if (fileSeparator==null) fileSeparator="/";
	}
	
	/*
	 * Process the reader and notifies regitered listeners
	 * Reader is closed when processing is finished
	 */
	public static void start(Reader r,TrigramListener tl) throws IOException {
		start(r,tl,Integer.MAX_VALUE);
	}
	
	/*
	 * Process the reader and notifies regitered listeners
	 * Reader is closed when processing is finished
	 */
	public static void start(Reader r, TrigramListener tl, int maxGrams) throws IOException {
		char[] buf = new char[BUFFERSIZE];
		char[] trigram = new char[3];
		
		char charmin2 = 0;
		char charmin1 = 0;
		
		int nbread = r.read(buf, 0, BUFFERSIZE);
		
		boolean firstbuf = true;
		int totalGrams = 0;
		
		while (nbread != -1) {
			if (!firstbuf) {
				trigram[0] = charmin2;
				trigram[1] = charmin1;
				trigram[2] = buf[0];
				totalGrams =
					addTrigram(tl,trigram,0, totalGrams, maxGrams);
				if (totalGrams == -1)
					break;
				if (nbread >= 2) {
					trigram[0] = charmin1;
					trigram[1] = buf[0];
					trigram[2] = buf[1];
					totalGrams =
						addTrigram(tl,trigram,0, totalGrams, maxGrams);
					if (totalGrams == -1)
						break;
				}
			}
			
			for (int i = 2; i < nbread; i++) {
				totalGrams = addTrigram(tl,buf,i-2, totalGrams, maxGrams);
				if (totalGrams == -1)
					break;
			}
			
			if (totalGrams == -1)
				break;
			
			if (nbread == BUFFERSIZE) {
				charmin2 = buf[nbread - 2];
				charmin1 = buf[nbread - 1];
			}
			firstbuf = false;
			nbread = r.read(buf, 0, BUFFERSIZE);
		}
		
		r.close();
	}
	
	/*
	 * A new trigram has been found : notify listener
	 */
	private static int addTrigram(TrigramListener tl,char[] buf, int posinbuf, int totalGrams, int maxGrams) {
		if (totalGrams < maxGrams) {
			tl.addTrigram(new Trigram(buf,posinbuf));
			return ++totalGrams;
		} else {
			return -1;
		}
	}
	
	/**
	 * Generates .tri files from a directory location
	 * Each subDirectory of fileLocation should be named xx where
	 * xx is the ISO Language code of the files contained in xx
	 * Language files should be plain text
	 * xx.tri files are generated at the same directory level as
	 * fileLocation
	 */
	public void generateTriFilesJar(String fileLocation, int maxSize) throws IOException {
		JarOutputStream jos=new JarOutputStream(
				new FileOutputStream(
						fileLocation+"/Trigrams.jar"));
		
		DataOutputStream dos=new DataOutputStream(
				new BufferedOutputStream(jos));
		
		jos.setComment("Trigrams reference file");
		StringBuffer manifestBuffer=new StringBuffer();
		manifestBuffer.append("Manifest-Version: 1.0\n");
		manifestBuffer.append("Created-By: " + 
                System.getProperty("user.name") + "\n\n");
		
		File f = new File(fileLocation);
		
		//TODO:cleanup this
		if (f.isDirectory()) {
			
			String[] files = f.list();
			for (int i = 0; i < files.length; i++) {
				File fd = new File(f.getAbsolutePath() + fileSeparator +files[i]);
				if (fd.isDirectory()) {
					manifestBuffer.append("Name: "+fd.getName()+".tri\n");
					manifestBuffer.append("Language: "+fd.getName()+"\n\n");
				}
			}
		}
		
		JarEntry je=new JarEntry("META-INF/MANIFEST.MF");
		jos.putNextEntry(je);
		jos.write(new String(manifestBuffer).getBytes());
		dos.flush();
		
		f = new File(fileLocation);
		
		if (f.isDirectory()) {
			
			String[] files = f.list();
			for (int i = 0; i < files.length; i++) {
				File fd = new File(f.getAbsolutePath() + fileSeparator +files[i]);
				if (fd.isDirectory()) {
					System.err.println("Language: " + files[i]);
					processLanguageDirJar(dos,jos, fd, maxSize);
				}
			}
		}
		
		dos.close();
	}
	
	/**
	 * Process a single language directory by processing all
	 * its language files and storing a xx.tri file at fileLocation
	 */
	private void processLanguageDirJar(DataOutputStream dos,JarOutputStream jos, File f, int maxSize)
	throws IOException {
		Trigrams ts = new Trigrams();
		
		String[] files = f.list();
		for (int i = 0; i < files.length; i++) {
			System.err.println(" - " + files[i]);
			Reader r =
				new BufferedReader(
						new FileReader(f.getAbsolutePath() + fileSeparator + files[i]));
			TrigramGenerator.start(r,ts);
		}
		JarEntry je=new JarEntry(f.getName()+".tri");
		je.setComment("Reference trigram file for language ["+f.getName()+"]");
		jos.putNextEntry(je);
		
		ts.saveToOutputStream(dos);
		
		dos.flush();
	}
	
	
	public static void main(String[] args) throws IOException {
		int defSize = Integer.MAX_VALUE;
		try {
			defSize = Integer.parseInt(args[1]);
		} catch (Exception e) {
			System.err.println("Trigram files with max entries: " + defSize);
		}
		new TrigramGenerator().generateTriFilesJar(args[0], defSize);
	}
	
}