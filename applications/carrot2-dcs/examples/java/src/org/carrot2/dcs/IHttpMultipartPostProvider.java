package org.carrot2.dcs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

/**
 * Anything capable of formatting, sending and parsing a multipart HTTP POST. We provide
 * several possible implementations.
 */
public interface IHttpMultipartPostProvider
{
    /**
     * @param dcsURI Target DCS address.
     * @param attributes Attributes that should be passed to the DCS.
     * @return Returns HTTP response body. 
     */
    InputStream post(URI dcsURI, Map<String, String> attributes) throws IOException;
}
