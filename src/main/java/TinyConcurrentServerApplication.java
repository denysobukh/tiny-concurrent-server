import server.WebServer;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutionException;

/**
 * @author Denis Obukhov  / created on 02 Dec 2020
 */
public class TinyConcurrentServerApplication {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        final InetAddress host = Inet4Address.getByName("0.0.0.0");
	String serverPort = System.getProperty("PORT","8080");
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(serverPort), 1024, host);
        WebServer webServer = new WebServer(serverSocket);
        webServer.waitTermination();
        System.out.println("main thread exiting");
    }
}
