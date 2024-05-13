package com.otc.otc;

//import lombok.extern.java.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class TcpWasServerMain {
    public static void main(String[] args) {
        TcpWasServer tcpWasServer = new TcpWasServer();
        tcpWasServer.start();
    }
}
