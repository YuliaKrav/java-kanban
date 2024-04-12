package server;

import java.io.IOException;

public class MainKVServer {

    public static void main(String[] args) throws IOException {
        new KVServer().start();
    }
}
