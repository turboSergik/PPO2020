package com.company;

import java.net.ServerSocket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class Server {
    private final int SERVER_PORT = 6100;

    private ServerSocket serverSocket = null;
    private final CallableServer callable2;
    private final FutureTask<String> futureTask;
    private final ExecutorService executor;

    class CallableServer implements Callable<String> {
        private int port;

        public CallableServer(int port) {
            this.port = port;
        }

        @Override
        public String call() throws Exception {
            serverSocket = new ServerSocket(port);
            System.out.println("Server start on port: " + port);

            while (true) {
                ConnectionWorker worker;
                try {
                    System.out.println("TRYING TO ACCEPT");
                    worker = new ConnectionWorker(serverSocket.accept());
                    System.out.println("TRYING TO ACCEPT");
                    Thread t = new Thread(worker);
                    t.start();
                } catch (Exception e) {
                    System.err.println("Connection error : " + e.getMessage());

                    if (serverSocket.isClosed())
                        break;
                }
            }

            return null;
        }
    }

    public Server() {
        callable2  = new CallableServer(SERVER_PORT);

        futureTask = new FutureTask<>(callable2);

        executor = Executors.newFixedThreadPool(2);
        executor.execute(futureTask);
    }

    public static void main(String[] args) {
        new Server();
    }
}
