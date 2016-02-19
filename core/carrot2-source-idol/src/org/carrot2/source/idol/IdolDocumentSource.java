
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.idol;

import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Callable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.carrot2.core.Document;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.MultipageSearchEngine;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.source.opensearch.RomeFetcherUtils;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Level;
import org.carrot2.util.attribute.Required;
import org.carrot2.util.attribute.constraint.IntRange;
import org.slf4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;

/**
 * A {@link IDocumentSource} fetching {@link Document}s (search results) from an IDOL
 * Search Engine. Please note that you will need to install an XSLT stylesheet in your 
 * IDOL instance that transforms the search results into the OpenSearch format. The XSLT 
 * stylesheet is available under the <tt>org.carrot2.source.idol</tt> package, next to
 * the binaries of this class.
 * 
 * <p>
 * Based on code donated by Julien Nioche. Autonomy IDOL support contributed by James
 * Sealey.
 * </p>
 * 
 * @see "http://www.autonomy.com/content/Products/products-idol-server/index.en.html"
 */
@Bindable(prefix = "IdolDocumentSource")
public class IdolDocumentSource extends MultipageSearchEngine
{
    /** Logger for this class. */
    final static Logger logger = org.slf4j.LoggerFactory
        .getLogger(IdolDocumentSource.class);

    /**
     * Maximum concurrent threads from all instances of this component.
     */
    private static final int MAX_CONCURRENT_THREADS = 10;

    /**
     * URL of the IDOL Server.
     */
    @Input
    @Processing
    @Init
    @Attribute
    @Required
    @Label("IDOL server address")
    @Level(AttributeLevel.BASIC)
    @Group(SERVICE)    
    public String idolServerName;

    /**
     * IDOL Server Port.
     */
    @Input
    @Processing
    @Init
    @Attribute
    @Required
    @Label("IDOL server port")
    @Level(AttributeLevel.BASIC)
    @Group(SERVICE)
    public int idolServerPort;

    /**
     * IDOL XSL Template Name. The Reference of an IDOL XSL template that outputs the
     * results in OpenSearch format.
     */
    @Input
    @Processing
    @Init
    @Attribute
    @Required
    @Label("IDOL XSL template name")
    @Level(AttributeLevel.ADVANCED)
    @Group(SERVICE)    
    public String xslTemplateName;

    /**
     * Any other search attributes (separated by &amp;) from the Autonomy Query Search
     * API's Ensure all the attributes are entered to satisfy XSL that will be applied.
     */
    @Input
    @Processing
    @Init
    @Attribute
    @Label("Other IDOLSearch attributes")
    @Level(AttributeLevel.ADVANCED)
    @Group(SERVICE)
    public String otherSearchAttributes;

    /**
     * Results per page. The number of results per page the document source will expect
     * the feed to return.
     */
    @Input
    @Processing
    @Init
    @Attribute
    @Required
    @IntRange(min = 1)
    @Label("Results per page")
    @Level(AttributeLevel.ADVANCED)
    @Group(SERVICE)
    public int resultsPerPage = 50;

    /**
     * Minimum IDOL Score. The minimum score of the results returned by IDOL.
     */
    @Input
    @Processing
    @Init
    @Attribute
    @IntRange(min = 1)
    @Label("Minimum score")
    @Level(AttributeLevel.BASIC)
    @Group(SERVICE)
    public int minScore = 50;

    /**
     * Maximum number of results. The maximum number of results the document source can
     * deliver.
     */
    @Input
    @Processing
    @Init
    @Attribute
    @IntRange(min = 1)
    @Label("Maximum results")
    @Level(AttributeLevel.BASIC)
    @Group(SERVICE)
    public int maximumResults = 100;

    /**
     * User agent header. The contents of the User-Agent HTTP header to use when making
     * requests to the feed URL. If empty or <code>null</code> value is provided, the
     * following User-Agent will be sent:
     * <code>Rome Client (http://tinyurl.com/64t5n) Ver: UNKNOWN</code>.
     */
    @Input
    @Init
    @Processing
    @Attribute
    @Label("User agent")
    @Level(AttributeLevel.ADVANCED)
    @Group(SERVICE)
    public String userAgent = null;
    
    /**
     * User name to use for authentication.
     */
    @Input
    @Processing
    @Attribute
    @Label("User name")
    @Level(AttributeLevel.MEDIUM)
    @Group(SERVICE)
    public String userName;

    /**
     * Search engine metadata create upon initialization.
     */
    private MultipageSearchEngineMetadata metadata;

    /** Fetcher for OpenSearch feed. */
    private FeedFetcher feedFetcher;

    @Override
    public void beforeProcessing()
    {
        this.metadata = new MultipageSearchEngineMetadata(resultsPerPage, maximumResults,
            false);
        this.feedFetcher = new HttpURLFeedFetcher();
        if (org.apache.commons.lang.StringUtils.isNotBlank(this.userAgent))
        {
            this.feedFetcher.setUserAgent(this.userAgent);
        }
    }

    @Override
    public void process() throws ProcessingException
    {
        super.process(metadata,
            getSharedExecutor(MAX_CONCURRENT_THREADS, this.getClass()));
    }

    @Override
    protected Callable<SearchEngineResponse> createFetcher(final SearchRange bucket)
    {
        return new SearchEngineResponseCallable()
        {
            public SearchEngineResponse search() throws Exception
            {
                final String url = getURL();
                logger.debug("Fetching URL: " + url);
                return RomeFetcherUtils.fetchUrl(url, feedFetcher);
            }
        };
    }

    // Set the URL string using the Autonomy connection
    private String getURL()
    {
        // Set stringbuilder to create the url string
        StringBuilder url = new StringBuilder();

        // Append Server Address
        url.append("http://");
        url.append(this.idolServerName);
        url.append(":");
        url.append(this.idolServerPort);

        // build query parameters
        url.append("/action=Query&");
        url.append("Text=" + query + "&");
        url.append("MinScore=" + this.minScore + "&");
        url.append("maxresults=" + this.maximumResults + "&");
        url.append("template=" + this.xslTemplateName + "&");
        url.append(this.otherSearchAttributes);

        // Add the security token if the username has been set
        if (userName != null)
        {
            String securityToken = getSecurityToken();
            if (securityToken != "")
            {
                url.append("&SecurityInfo=" + securityToken);
            }
        }

        // return the URL to an IDOL OPEN Search results page
        return url.toString();

    }

    // get the security token using the username
    private String getSecurityToken()
    {
        String rtn = "";
        try
        {
            String url = "http://" + this.idolServerName + ":" + this.idolServerPort
                + "/" + "action=userread&username=" + userName + "&securityinfo=true";

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new URL(url).openStream());
            rtn = URLEncoder.encode(getSecurityInfo(doc), "UTF-8");
        }
        catch (Exception e)
        {
            logger.error("Could not get security token", e);
        }
        return rtn;
    }

    // extract the token from the XML document
    private String getSecurityInfo(org.w3c.dom.Document document)
    {
        String rtn = "";
        Element e = document.getDocumentElement();
        NodeList nodeList = e.getElementsByTagName("responsedata");
        for (int temp = 0; temp < nodeList.getLength(); temp++)
        {
            Node nNode = nodeList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element eElement = (Element) nNode;
                rtn = getTagValue("autn:securityinfo", eElement);
            }
        }
        return rtn;
    }

    // get the tag value
    private static String getTagValue(String sTag, Element eElement)
    {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);

        return nValue.getNodeValue();
    }
}
