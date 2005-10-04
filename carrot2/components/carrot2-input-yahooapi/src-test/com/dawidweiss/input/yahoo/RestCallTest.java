package com.dawidweiss.input.yahoo;

import java.io.InputStream;
import java.util.HashMap;

import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.dawidweiss.carrot.util.common.StreamUtils;
import com.dawidweiss.carrot.util.jdom.JDOMHelper;
import com.dawidweiss.carrot.util.net.http.FormActionInfo;
import com.dawidweiss.carrot.util.net.http.FormParameters;
import com.dawidweiss.carrot.util.net.http.HTTPFormSubmitter;

/**
 * A REST-type call to Yahoo search service.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class RestCallTest extends TestCase {

	public RestCallTest(String s) {
		super(s);
	}

	public void testRestCallUsingPost() throws Exception {
		SAXBuilder builder = new SAXBuilder(false);
		Document doc = builder.build(this.getClass().getResourceAsStream("yahoo.xml"));
		Element configuration = doc.getRootElement();

	    FormActionInfo formActionInfo = new FormActionInfo(JDOMHelper.getElement(
	                "/request", configuration));
	    FormParameters formParameters = new FormParameters(JDOMHelper.getElement(
	                "/request/parameters", configuration));
	    HTTPFormSubmitter submitter = new HTTPFormSubmitter(formActionInfo);		

	    HashMap mappedParameters = new HashMap();
	    mappedParameters.put("query.string", "dawid weiss");
	    mappedParameters.put("query.startFrom", "1");
		InputStream is = submitter.submit(formParameters, mappedParameters, "UTF-8");
		System.out.println(new String(StreamUtils.readFullyAndCloseInput(is)));
	}
}
