package com.dawidweiss.carrot.input.yahoo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.dawidweiss.carrot.util.jdom.JDOMHelper;
import com.dawidweiss.carrot.util.net.http.FormActionInfo;
import com.dawidweiss.carrot.util.net.http.FormParameters;
import com.dawidweiss.carrot.util.net.http.HTTPFormSubmitter;

/**
 * A Yahoo search service descriptor.
 * 
 * @author Dawid Weiss
 */
public class YahooSearchServiceDescriptor {
    private FormActionInfo formActionInfo;
    private FormParameters formParameters;

    /**
     * Creates an empty descriptor. Initialize it using
     * {@link #initializeFromXML(InputStream)}.
     */
    public YahooSearchServiceDescriptor() {
    }

    /**
     * Creates a descriptor based on the provided specification.
     */
    public YahooSearchServiceDescriptor(FormActionInfo formActionInfo, 
            FormParameters formParameters) {
        this.formActionInfo = formActionInfo;
        this.formParameters = formParameters;
    }

    /**
     * Initializes the descriptor from an XML file. See the unit tests
     * for examples.
     * 
     * Any previous content is erased.
     *  
     * @throws IOException  
     */
    public void initializeFromXML(InputStream xmlStream) throws IOException {
        Document doc;
        try {
            final SAXBuilder builder = new SAXBuilder(false);
            doc = builder.build(xmlStream);
        } catch (JDOMException e) {
            throw new IOException("Can't parse input stream: " + e.toString());
        }
        final Element configuration = doc.getRootElement();

        FormActionInfo formActionInfo = new FormActionInfo(JDOMHelper.getElement(
                    "/request", configuration));
        FormParameters formParameters = new FormParameters(JDOMHelper.getElement(
                    "/request/parameters", configuration));

        this.formActionInfo = formActionInfo;
        this.formParameters = formParameters;
    }

    /**
     * Returns form action info associated with this service.
     */
    public FormActionInfo getFormActionInfo() {
        return formActionInfo;
    }
    
    /**
     * Returns form parameters used in this service. Certain form
     * parameters may require mappings to real values 
     * (see {@link HTTPFormSubmitter#submit(FormParameters, Map, String)}).
     */
    public FormParameters getFormParameters() {
        return formParameters;
    }

    /**
     * Returns maximum results per query. Currently hardcoded at 50.
     */
    public int getMaxResultsPerQuery() {
        return 50;
    }
}
