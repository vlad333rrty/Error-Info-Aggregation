package com.rest;

import com.data.Error;
import com.data.IError;
import com.modules.IErrorReader;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;

public class ErrorReader implements IErrorReader {
    public static final int PORT=4040;
    private final LinkedBlockingDeque<Connection> connections =new LinkedBlockingDeque<>();
    private final LinkedBlockingDeque<IError> errors=new LinkedBlockingDeque<>();
    private final LinkedBlockingDeque<String> rawError =new LinkedBlockingDeque<>();

    public ErrorReader(){

    }

    public void start(){
        new Thread(this::run).start();

        new Thread(()->{
            while (true){
                if (Thread.interrupted()) return;
                String list=rawError.poll();
                if (list!=null){
                    Error error=new Error();
                    error.set("body", list);
                    errors.add(error);
                }
            }
        }).start();
    }

    private void run(){
        try{
            ServerSocket server=new ServerSocket(PORT);
            while (true){
                Socket client=server.accept();
                Connection connection=new Connection(client);
                connections.add(connection);
                connection.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public IError getError() {
        return errors.poll();
    }

    public class Connection extends Thread{
        private final Socket socket;
        private final ObjectInputStream in;
        Connection(Socket socket) throws IOException {
            this.socket=socket;
            in = new ObjectInputStream(socket.getInputStream());
        }

        @Override
        public void run() {
            while (true) {
                if (Thread.interrupted()) return;
                try{
                    int n=in.available();
                    if (n>0){
                        StringBuilder builder=new StringBuilder();
                        for (int i=0;i<n;i++){
                            builder.append((char)in.readByte());
                        }
                        rawError.add(builder.toString());
                    }
                } catch (IOException e) {
                    //TODO
                }
            }
        }
    }
}
