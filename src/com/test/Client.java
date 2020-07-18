package com.test;

import com.rest.ConnectionHandler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Random;

public class Client extends Thread {
    public static final String address = "localhost";
    private final ResponseHandler responseHandler=new ResponseHandler();
    private final String name;
    public Client(String name){
        this.name=name;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(address, ConnectionHandler.PORT);

            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
            InputStreamReader in = new InputStreamReader(socket.getInputStream());
            Random r = new Random();
            int i = r.nextInt(6) + 1;

            File f = new File(String.format("Tests/Test%d.txt", i));
            FileReader reader = new FileReader(f);

            StringBuilder builder = new StringBuilder();
            while (reader.ready()) builder.append((char) reader.read());

            out.write(builder.toString());
            out.flush();
            while (!responseHandler.hasVerdict()) {
                builder = new StringBuilder();
                while (in.ready()) {
                    builder.append((char) in.read());
                }

                String s=builder.toString().trim();

                if (s.length() > 0) {
                    responseHandler.check(s);
                    System.out.format("Response to client %s: %s\n",name,s);
                }
            }

            if (responseHandler.success()){
                System.out.format("Client %s: success\n",name);
            }else {
                System.err.format("Client %s: failure\n",name);
            }

            in.close();
            out.close();
            socket.close();
            System.out.format("Client %s closed connection\n",name);
        } catch (IOException ioe) {
            System.err.format("Server is no longer available: %s\n",ioe);
        }
    }
}