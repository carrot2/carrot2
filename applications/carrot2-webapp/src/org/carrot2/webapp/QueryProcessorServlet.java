package org.carrot2.webapp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.attribute.AttributeBinder;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.simplexml.NoClassAttributePersistenceStrategy;
import org.carrot2.webapp.attribute.Request;
import org.carrot2.webapp.jawr.JawrUrlGenerator;
import org.carrot2.webapp.model.*;
import org.carrot2.webapp.util.RequestParameterUtils;
import org.carrot2.webapp.util.UserAgentUtils;
import org.simpleframework.xml.load.Persister;
import org.simpleframework.xml.stream.Format;

/**
 * 
 */
@SuppressWarnings("serial")
public class QueryProcessorServlet extends HttpServlet 
{
    public final static String MIME_XML = "text/xml";
    public final static String ENCODING_UTF = "utf-8";
    public final static String MIME_XML_CHARSET_UTF = MIME_XML + "; charset="
        + ENCODING_UTF;

    private CachingController controller;

    private JawrUrlGenerator jawrUrlGenerator;

    @Override
    @SuppressWarnings("unchecked")
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        controller = new CachingController(DocumentSource.class);
        controller.init(new HashMap<String, Object>(), WebappConfig.INSTANCE.components);

        jawrUrlGenerator = new JawrUrlGenerator(config.getServletContext());
    }

    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        // Unpack parameters from string arrays
        final Map<String, Object> requestParameters = RequestParameterUtils
            .unpack(request);
        requestParameters.put("modern", UserAgentUtils.isModernBrowser(request
            .getHeader("User-Agent")));

        try
        {
            // Build model for this request
            final RequestModel requestModel = new RequestModel();
            final AttributeBinder.AttributeBinderActionBind attributeBinderActionBind = new AttributeBinder.AttributeBinderActionBind(
                Input.class, requestParameters, true,
                AttributeBinder.AttributeTransformerFromString.INSTANCE);
            AttributeBinder.bind(requestModel,
                new AttributeBinder.AttributeBinderAction []
                {
                    attributeBinderActionBind
                }, Input.class, Request.class);
            requestModel.afterParametersBound(attributeBinderActionBind.remainingValues);

            // Add some values in case there was no query parameters -- we want to use the
            // web application defaults and not component defaults.
            requestParameters.put(AttributeNames.RESULTS, requestModel.results);

            // Perform processing
            ProcessingResult processingResult = null;
            ProcessingException processingException = null;
            try
            {
                if (requestModel.type.requiresProcessing)
                {
                    if (RequestType.CLUSTERS.equals(requestModel.type)
                        || RequestType.FULL.equals(requestModel.type)
                        || RequestType.CARROT2.equals(requestModel.type))
                    {
                        processingResult = controller.process(requestParameters,
                            requestModel.source, requestModel.algorithm);
                    }
                    else if (RequestType.DOCUMENTS.equals(requestModel.type))
                    {
                        processingResult = controller.process(requestParameters,
                            requestModel.source);
                    }
                }
            }
            catch (ProcessingException e)
            {
                processingException = e;
            }

            // Send response
            response.setContentType(MIME_XML_CHARSET_UTF);
            final ServletOutputStream outputStream = response.getOutputStream();
            final PageModel pageModel = new PageModel(request, requestModel,
                jawrUrlGenerator, processingResult, processingException);

            final Persister persister = new Persister(
                NoClassAttributePersistenceStrategy.INSTANCE,
                getPersisterFormat(pageModel));

            if (RequestType.CARROT2.equals(requestModel.type))
            {
                persister.write(processingResult, outputStream);
            }
            else
            {
                persister.write(pageModel, outputStream);
            }
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }

    private Format getPersisterFormat(PageModel pageModel)
    {
        return new Format(2, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<?xml-stylesheet type=\"text/xsl\" href=\"@"
            + WebappConfig.getContextRelativeSkinStylesheet(pageModel.requestModel.skin)
            + "\" ?>");
    }
}