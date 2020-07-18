package com.test;

import java.util.ArrayList;

public class Test {
    public static final int CLIENTS=250;

    public static void main(String[] args) {
        ArrayList<Client> clients=new ArrayList<>();
        for (int i=0;i<CLIENTS;i++){
            Client client =new Client(String.format("Client%d",i));
            client.start();
            clients.add(client);
        }
        for (Client c:clients) {
            try {
                c.join();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
