package com.otc.otc;

//import lombok.extern.java.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


class ConnectInfo {

    private final Socket socket;
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;
    ConnectInfo(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        this.socket = socket;
        this.bufferedReader = bufferedReader;
        this.bufferedWriter = bufferedWriter;
    }
}



//@Log
public class TcpWasServer {
    private int SERVER_PORT = 8080;
    private ServerSocket serverSocket;

    private Map<String,ConnectInfo> connectedUserMap;


    public void openSocket(){
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
//            log.info("서버 연결에 실패하였습니다");
        }

    }

    public void init(){
        this.connectedUserMap = new HashMap<>();
    }

    public void start() {
        init();

        //연결부분
        try{
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            Socket socket = serverSocket.accept();

            System.out.println(socket.getInetAddress() + " 에서 접속시도");
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String uuid = UUID.randomUUID().toString();
            ConnectInfo connectInfo = new ConnectInfo(socket,input,output);

            RequestThread serverThread = new RequestThread(uuid,connectInfo);
            serverThread.run();

        }catch (IOException e){
//            log.info("서버 연결에 실패하였습니다");
        }
    }
}
