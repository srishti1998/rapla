package org.rapla.rest.client;

import junit.framework.TestCase;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.rapla.ServletTestBase;
import org.rapla.framework.logger.Logger;
import org.rapla.jsonrpc.client.swing.HTTPConnector;
import org.rapla.server.ServerServiceContainer;
import org.rapla.test.util.RaplaTestCase;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@RunWith(JUnit4.class)
public class RestAPITest  {


    int port = 8052;
    Server server;
    @Before
    public void setUp() throws Exception
    {
        Logger logger = RaplaTestCase.initLoger();
        final ServerServiceContainer servlet = RaplaTestCase.createServer(logger, "testdefault.xml");
        server = ServletTestBase.createServer(servlet, port);
    }

    @After
    public void tearDown() throws Exception
    {
        server.stop();
    }

    @Test
    public void testRestApi() throws Exception
    {
        RestAPIExample example = new RestAPIExample()
        {
            protected void assertTrue( boolean condition)
            {
                TestCase.assertTrue(condition);
            }
            
            protected void assertEquals( Object o1, Object o2)
            {
                TestCase.assertEquals(o1, o2);
            }
        };
        URL baseUrl = new URL("http://localhost:"+port+"/rapla/");
        example.testRestApi(baseUrl,"homer","duffs");
    }


    @Test
    public void testWebpages() throws Exception
    {
        HTTPConnector connector = new HTTPConnector();
        String body = null;
        String authenticationToken = null;
        Map<String, String> additionalHeaders = new HashMap<>();
        {
            URL baseUrl = new URL("http://localhost:"+port+"/rapla/server");
            final String result = connector.sendCallWithString("GET", baseUrl, body, authenticationToken, "text/html", additionalHeaders);
            TestCase.assertNotNull(result);
            TestCase.assertTrue( result.contains("Server running"));
        }
        {
            URL baseUrl = new URL("http://localhost:"+port+"/rapla/calendar");
            final String result = connector.sendCallWithString("GET", baseUrl, body, authenticationToken, "text/html", additionalHeaders);
            TestCase.assertNotNull(result);
            TestCase.assertTrue( result.toLowerCase().contains("<title>rapla"));
        }
        {
            URL baseUrl = new URL("http://localhost:"+port+"/rapla/auth");
            final String result = connector.sendCallWithString("GET", baseUrl, body, authenticationToken, "text/html", additionalHeaders);

            TestCase.assertNotNull(result);
        }
        {
            URL baseUrl = new URL("http://localhost:"+port+"/rapla/auth?username=homer&password=duffs");
            final String result = connector.sendCallWithString("POST", baseUrl, body, authenticationToken, "application/json", additionalHeaders);
            TestCase.assertNotNull(result);
        }

    }





}
