package server;

import denysobukhov.TinyConcurrentServerApplication;
import denysobukhov.server.WebServer;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.fail;


/**
 * @author Denis Obukhov  / created on 04 Dec 2020
 */
class WebServerTest {

    private static int port;
    private static WebServer webServer;
    private int TIMEOUT_MS = 3000;
    private final RequestConfig requestConfig = RequestConfig
            .custom()
            .setConnectTimeout(TIMEOUT_MS).build();
    private final CloseableHttpClient httpRequestBuilder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
    private CloseableHttpClient httpclient = HttpClients.createDefault();

    @BeforeAll
    static void beforeAll() throws IOException {
        System.setProperty("server.port", "9999");
        port = TinyConcurrentServerApplication.getServerPortEnvVariable();
        final InetAddress host = Inet4Address.getByName("0.0.0.0");
        ServerSocket serverSocket = new ServerSocket(port, 1024, host);
        webServer = new WebServer(serverSocket);
    }

    @AfterAll
    static void afterAll() throws InterruptedException {
        System.out.println("after all");
        webServer.shutdown();
    }

    @Test
    void testGet200() {
        HttpUriRequest request = new HttpGet("http://localhost:" + port + "/");
        HttpContext httpContext;

        // When
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpRequestBuilder.execute(request);
        } catch (IOException e) {
            fail(e);
        }

        System.out.println(httpResponse);

        // Then
        Assertions.assertEquals(
                httpResponse.getStatusLine().getStatusCode(),
                HttpStatus.SC_OK);
    }
}