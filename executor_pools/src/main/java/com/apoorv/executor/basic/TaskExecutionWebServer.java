package com.apoorv.executor.basic;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by apoorv on 6/26/17.
 */
public class TaskExecutionWebServer {
    private static final int NTHREADS=100;
    private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);

    private static void handleRequest(Socket connection) throws IOException {
        BufferedReader in =  new BufferedReader(new InputStreamReader(connection.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

        String line = in.readLine();
        while (!line.isEmpty()) {
            System.out.println(line);
            line = in.readLine();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Date today = new Date();
        out.write("HTTP/1.0 200 OK\r\n\r\n" + today);
        out.close();
        connection.close();
    }

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(9191);
        while(true){
            final Socket connection = socket.accept();
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        handleRequest(connection);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            exec.execute(r);
        }
    }
}
