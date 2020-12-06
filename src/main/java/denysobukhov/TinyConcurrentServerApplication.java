package denysobukhov;

import denysobukhov.server.WebServer;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * @author Denis Obukhov  / created on 02 Dec 2020
 */
public class TinyConcurrentServerApplication {

    private static final String SERVER_PORT = "server.port";
    private static final String HOST = "0.0.0.0";

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        final InetAddress host = Inet4Address.getByName(TinyConcurrentServerApplication.HOST);
        final int port = getServerPortEnvVariable();
        ServerSocket serverSocket = new ServerSocket(port, 1024, host);
        WebServer webServer = new WebServer(serverSocket);
        webServer.waitTermination();
        System.out.println("main thread exiting");
    }

    public static int getServerPortEnvVariable() {
        return Integer.parseInt(Optional.ofNullable(System.getProperty(SERVER_PORT)).orElseThrow(() -> new IllegalArgumentException("server.port variable is not specified")));
    }
}
