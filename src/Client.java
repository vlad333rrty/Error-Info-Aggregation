import com.rest.ErrorReader;

import java.io.*;
import java.net.Socket;

public class Client {
    private static Socket clientSocket; //сокет для общения
    private static ObjectOutputStream out; // поток записи в сокет

    public static void main(String[] args) {
        try {
            try {
                clientSocket = new Socket("localhost", ErrorReader.PORT);

                out = new ObjectOutputStream(clientSocket.getOutputStream());
                int c=0;

                while (true){
                    c++;
                    if (c%100==0){
                        out.write("Hello".getBytes());
                        out.flush();
                    }
                }
            } finally {
                clientSocket.close();
                out.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }
}
