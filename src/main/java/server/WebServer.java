package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Denis Obukhov  / created on 02 Dec 2020
 */
public class WebServer {
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final Future<?> listenerTask;
    private AtomicInteger hits = new AtomicInteger(0);

    public WebServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.executorService = Executors.newCachedThreadPool();
        executorService.submit(new MeterTask(hits));

        System.out.println("bound to " + serverSocket.getInetAddress() + " : " + serverSocket.getLocalPort());

        listenerTask = executorService.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    final Socket accept = serverSocket.accept();
                    hits.incrementAndGet();
                    executorService.submit(new ResponseHandler(accept));
                }
            } catch (Exception e) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("listenerTask interrupted");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void shutdown() throws InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        if (!executorService.isTerminated()) {
            List<Runnable> orfans = executorService.shutdownNow();
            System.out.println("orfans: " + orfans.size());
        }
        if (!listenerTask.isDone()) {
            listenerTask.cancel(true);
        }
    }

    public void waitTermination() throws ExecutionException, InterruptedException {
        listenerTask.get();
        shutdown();
    }

    private static class ResponseHandler implements Runnable {

        private static final String response = "HTTP/1.1 200 OK\r\n\r\n<html>\n" +
                "<body>\n" +
                "<h1>Hello, World!</h1>\n" +
                "</body>\n" +
                "</html>\r\n";

        private final Socket socket;

        public ResponseHandler(Socket socket) throws IOException {
            this.socket = socket;
//            System.out.println("connection from " + socket.getInetAddress() + ":" + socket.getPort());
//            System.out.print(".");
        }

        @Override
        public void run() {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                while (!socket.isClosed()) {
                    String line = bufferedReader.readLine();
//                    System.out.println("=>" + line);
//                    System.out.println(Arrays.toString(line.getBytes(StandardCharsets.UTF_8)));
                    if (!line.isEmpty()) {
                        stringBuilder.append(line);
                    } else {
//                        System.out.println("<===");
                        if (stringBuilder.indexOf("GET /") != -1) {
                            printWriter.print(response);
                            printWriter.flush();
                        }
                        if (!socket.getKeepAlive()) {
                            break;
                        }
                    }
                }
                bufferedReader.close();
                printWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (!socket.isClosed()) {
                    try {
                        socket.close();
                    } catch (IOException suppressed) {
                        suppressed.printStackTrace();
                    }
                }
            }
        }
    }

    private static class MeterTask implements Runnable {
        private final AtomicInteger counter;
        private long lastT;
        private int lastC;

        public MeterTask(AtomicInteger counter) {
            this.counter = counter;
            lastT = System.currentTimeMillis();
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                final long t = System.currentTimeMillis();
                final int c = counter.get();
                long deltaT = t - lastT;
                int deltaC = c - lastC;
                lastC = c;
                lastT = t;
                if (deltaC != 0) {
                    final double throughput = deltaC * 1000.0 / deltaT;
                    System.out.printf("%-6.2f requests/sec %n", throughput);
                }
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    System.out.println("MeterTask interrupted");
                    break;
                }
            }
        }
    }
}

