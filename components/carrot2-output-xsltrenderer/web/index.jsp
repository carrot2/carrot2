<%@ page session="false" contentType="text/html; charset=utf-8"%>
<html>
        <body>
            <h1>Carrot-output XSLT Renderer</h1>
            <h2>An output processing service for Carrot2</h2>

            <p>
                <b>Refer to project documentation on how to use and deploy this service.</b>
            </p>
            
            <h2>Various notes and implementation details</h2>
            <p>
                <ul>
                    <li>Parameters can be passed to stylesheet if they are passed in the POST stream before carrot data.
                    Parameter name cannot be set to any of the reserved keywords: 'stylesheet'.
                    </li>
                </ul>
            </p>

            <hr />

            <p>
                [ <a href="test.jsp">Manual form for POST test</a> ]
            </p>
        </body>
</html>

