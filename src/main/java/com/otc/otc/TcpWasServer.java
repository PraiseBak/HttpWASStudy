package com.otc.otc;


import com.otc.otc.dto.ConnectInfo;
import lombok.extern.java.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;


public class TcpWasServer {
    private int SERVER_PORT = 8080;
    private ServerSocket serverSocket;

    private static final Logger log = Logger.getLogger(TcpWasServer.class.getName());

    private Map<String, ConnectInfo> connectedUserMap;

    public void init(){
        this.connectedUserMap = new HashMap<>();
    }

    public void start() {
        init();

        //연결부분
        try{
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            while (true){
                Socket socket = serverSocket.accept();
                //request 단위로 쓰레드 생성
                log.info(socket.getInetAddress() + " 에서 접속시도");

                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                String uuid = UUID.randomUUID().toString();
                ConnectInfo connectInfo = new ConnectInfo(socket,input,output);

                RequestThread serverThread = new RequestThread(connectInfo);
                serverThread.run();
            }

        }catch (IOException e){
//            e.printStackTrace();
            log.info("서버 연결에 실패하였습니다");
            log.warning(e.getMessage());
        }
    }
}
