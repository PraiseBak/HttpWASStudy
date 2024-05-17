package com.otc.otc.dto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

public class ConnectInfo {

    private final Socket socket;
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;

    public ConnectInfo(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        this.socket = socket;
        this.bufferedReader = bufferedReader;
        this.bufferedWriter = bufferedWriter;
    }
}
