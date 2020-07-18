package com.rest;

import com.data.IError;
import com.modules.IConnectionHandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingDeque;


public class ConnectionHandler implements IConnectionHandler {
    public static final int PORT=9000;
    private final Queue<Connection> connections =new LinkedBlockingDeque<>();
    private final Queue<IError> errors=new LinkedBlockingDeque<>();
    private final Map<Connection,Queue<Pair<ServerException,IError>>> connectionToExceptions=new ConcurrentHashMap<>();
    private final Map<IError,Connection> errorToConnection=new ConcurrentHashMap<>();
    private final ForkJoinPool receiveFJP=new ForkJoinPool(50);
    private final ForkJoinPool sendFJP=new ForkJoinPool(50);
    private Thread runThread;
    private ServerSocket server;

    @Override
    public void start(){
        try{
            server=new ServerSocket(PORT);
        }catch (IOException e){
            e.printStackTrace();
        }

        runThread=new Thread(this::run);
        runThread.start();
        LoggingManager.logger.info("Server started");
    }

    private void run() {
        try{
            while (!Thread.currentThread().isInterrupted()){
                Socket client=server.accept();
                Connection connection=new Connection(client);
                connections.add(connection);
                connectionToExceptions.put(connection,new ArrayDeque<>());
                sendFJP.execute(connection.messenger);
                receiveFJP.execute(connection);
            }
        }catch (IOException e){
            LoggingManager.logger.info("Server closed");
        }
    }

    @Override
    public void close(){
        runThread.interrupt();
        try {
            server.close();
            runThread.join();
            for (Connection c:connections){
                c.interrupt();
                c.join();
            }
            receiveFJP.shutdown();
            sendFJP.shutdown();
        }catch (InterruptedException | IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public IError getError() {
        return errors.poll();
    }

    /**
     * Sets the result of the error processing
     * @param error processed error
     * @param serverException contains an exception if it occurred, otherwise it's null
     */
    @Override
    public void setResult(IError error, ServerException serverException) {
        if (serverException==null) serverException=new ServerException(null);
        Connection c=errorToConnection.get(error);
        connectionToExceptions.get(c).add(new Pair<>(serverException,error));
    }

    private class Connection extends Thread{
        private final Socket socket;
        private InputStreamReader in;
        private OutputStreamWriter out;
        private final Thread messenger =new Messenger();
        private boolean isClosed;
        private final Timer timer=new Timer();
        Connection(Socket socket) {
            this.socket=socket;

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isClosed=isClosed();
                }
            },5000,5000);
        }

        @Override
        public void run() {
            try {
                in = new InputStreamReader(socket.getInputStream());
                out=new OutputStreamWriter(socket.getOutputStream());
                while (!Thread.currentThread().isInterrupted() && !isClosed) {
                    StringBuilder builder = new StringBuilder();
                    while(in.ready()){
                        builder.append((char)in.read());
                    }
                    IError e;
                    try{
                        e=ErrorParser.parse(builder.toString());
                    }catch (ServerException exception){
                        out.write(exception.getMessage());
                        out.flush();
                        continue;
                    }
                    if (e==null) continue;
                    out.write("Successfully received\n");
                    out.flush();
                    errorToConnection.put(e,this);
                    errors.add(e);
                }
            }catch (IOException e){
                try {
                    out.write("An error occurred on the server:\t"+e);
                    out.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }finally {
                try {
                    out.close();
                    in.close();
                    socket.close();
                    this.interrupt();
                    connections.remove(this);
                }catch (IOException exception){
                    exception.printStackTrace();
                }
            }
        }

        @Override
        public void interrupt() {
            super.interrupt();
            messenger.interrupt();
            timer.cancel();
        }

        private boolean isClosed(){
            try{
                out.write("\n");
                out.flush();
            }catch (IOException e){
                return true;
            }catch (NullPointerException nle){
                return false;
            }
            return false;
        }

        private class Messenger extends Thread{
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !isClosed){
                    Queue<Pair<ServerException,IError>> qse= connectionToExceptions.get(Connection.this);
                    while (true) {
                        try{
                            Pair<ServerException,IError> pair = qse.poll();
                            if (pair != null) {
                                if (pair.first.getException()==null) {
                                    out.write(String.format("The error \n\"%s\"\n was successfully stored\n",pair.second));
                                } else {
                                    out.write(
                                            String.format("Exception %s occurred while storing the following error \n\"%s\"\n",
                                                    pair.first.getException(),pair.second));
                                }
                                out.flush();
                            } else break;
                        }catch (IOException e){
                            Connection.this.interrupt();
                            connections.remove(Connection.this);
                        }
                    }
                }
            }
        }
    }

    private static class Pair<F,S>{
        F first;
        S second;
        public Pair(F f,S s){
            first=f;
            second=s;
        }
    }
}
