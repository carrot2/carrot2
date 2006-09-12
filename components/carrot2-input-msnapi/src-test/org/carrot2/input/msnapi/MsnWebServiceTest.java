package org.carrot2.input.msnapi;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import com.microsoft.msnsearch.*;

/**
 * Tests plain WebService using Axis calls.
 * 
 * @author Dawid Weiss
 */
public class MsnWebServiceTest extends TestCase {
    public void testWebService() throws ServiceException, RemoteException {
        final String query = "Dawid Weiss";
        final int offset = 0;
        final int count = 50;
        
        final MSNSearchPortType search = new MSNSearchServiceLocator().getMSNSearchPort();
        final SearchRequest request = new SearchRequest();

        // This is Carrot-Search's open source application identifier
        request.setAppID("DE531D8A42139F590B253CADFAD7A86172F93B96");

        request.setSafeSearch(SafeSearchOptions.Off);
        request.setFlags(new String [] {SearchFlagsNull._None});
        request.setCultureInfo("en-US");
        request.setQuery(query);
        request.setRequests(new SourceRequest [] {
                new SourceRequest(SourceType.Web,
                        offset, count, 
                        new String [] {
                        ResultFieldMaskNull._Url,
                        ResultFieldMaskNull._Title,
                        ResultFieldMaskNull._Description,
                })
        });

        final SearchResponse response = search.search(request);
        final SourceResponse [] responses = response.getResponses();
        for (int i = 0; i < responses.length; i++) {
            final Result [] results = responses[i].getResults();
            System.out.println("TOTAL: " + responses[i].getTotal());
            System.out.println("range: " + responses[i].getOffset()
                    + " - " + results.length);
            for (int j = 0; j < results.length; j++) {
                System.out.println(results[j].getUrl());
                System.out.println(results[j].getTitle());                
                System.out.println(results[j].getDescription());
                System.out.println();
            }
        }
    }
}