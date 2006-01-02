
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.input.localcache;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dawidweiss.carrot.util.net.URLEncoding;

/**
 * A trimmed version of a zipped cached query class present in the
 * remote controller. This class here is used mainly to access remote
 * queries stored in local cache (for debugging purposes).
 * 
 * @author Dawid Weiss
 */
class ZIPCachedQuery {
	private static Logger log = Logger.getLogger(ZIPCachedQuery.class);

	private long dataOffset;

	private File dataFile;

	private String componentId;

	private String query;

	private Map optionalParams;

	/**
	 * Creates a new representation of a ZIPped cached query that is ready to
	 * retrieve the contents of the query.
	 */
	public ZIPCachedQuery(File cacheFile) throws IOException {
		this.dataFile = cacheFile;
		loadDataFromFile();
	}

	/**
	 * Loads the cached query from the file and saves an index
	 * to the query's result.
	 * 
	 * @throws IOException
	 */
	protected void loadDataFromFile() throws IOException {
		InputStream is = null;

		try {
			is = new BufferedInputStream(new GZIPInputStream(
					new FileInputStream(this.dataFile)));

			int dataOffset = 0;
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			while (true) {
				int b = is.read();

				if (b == 0) {
					// field separator reached.
					if (this.componentId == null) {
						byte[] encoded = os.toByteArray();
						dataOffset += encoded.length;
						dataOffset += 1; // include the separator field
						componentId = new String(URLEncoding.decode(encoded),
								"UTF-8");
					} else if (this.query == null) {
						byte[] encoded = os.toByteArray();
						dataOffset += encoded.length;
						dataOffset += 1; // include the separator field

						try {
							String queryXml = new String(URLEncoding
									.decode(encoded), "UTF-8");

							// parse the query.
							SAXReader reader = new SAXReader();
							Element root = reader.read(
									new StringReader(queryXml))
									.getRootElement();
							this.query = root.getText();
						} catch (Exception e) {
							throw new IOException(
									"Cannot unmarshall cached query.");
						}
					} else if (this.optionalParams == null) {
						byte[] encoded = os.toByteArray();
						dataOffset += encoded.length;
						dataOffset += 1; // include the separator field

						if (encoded.length > 0) {
							Map optionalParams = new HashMap();
							String urlEncoded = new String(URLEncoding
									.decode(encoded), "UTF-8");
							StringTokenizer tokenizer = new StringTokenizer(
									urlEncoded, "&", false);

							while (tokenizer.hasMoreTokens()) {
                                String param = tokenizer.nextToken();
                                if (param.indexOf('=') < 0) {
	                                // If no '=', consider the parameter empty.
	                                optionalParams.put(param, "");
                                } else {
									String key = param.substring(0, param.indexOf('='));
									String value = param.substring(param.indexOf('=')+1);
									optionalParams.put(key, value);
								}
							}
							this.optionalParams = optionalParams;
						} else {
							this.optionalParams = null;
						}

						this.dataOffset = dataOffset;
						return;
					}

					os.reset();
				} else if (b == -1) {
					throw new IOException("Premature end of cached file.");
				} else {
					os.write(b);
				}
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error("Cannot close input cache file: "
							+ this.dataFile.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * @return Returns the componentId.
	 */
	public String getComponentId() {
		return componentId;
	}

	/**
	 * @return Returns the optionalParams.
	 */
	public Map getOptionalParams() {
		return optionalParams;
	}

	/**
	 * @return Returns the query.
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Return the stream to input cache data
	 */
	public InputStream getData() throws java.io.IOException {
		InputStream is;
		is = new BufferedInputStream(new GZIPInputStream(new FileInputStream(
				dataFile)));
		is.skip(this.dataOffset);

		return is;
	}
}