package com.otc.otc;

//import lombok.extern.java.Log;
//import org.apache.coyote.BadRequestException;

import java.io.*;
import java.util.Arrays;
import java.util.Optional;



/*
request 참고

POST /login HTTP/1.1
Host: www.example.com
Content-Type: application/x-www-form-urlencoded
Content-Length: 27

username=john&password=123456
 */
//@Log
public class RequestThread implements Runnable {
    private final int SERVER_PORT = 8080;
    private final String uuid;
    private final ConnectInfo connectInfo;
    private final BufferedWriter bufferedWriter;
    private final BufferedReader bufferedReader;

    public RequestThread(String uuid, ConnectInfo connectInfo) {
        this.uuid = uuid;
        this.connectInfo = connectInfo;
        this.bufferedReader = connectInfo.bufferedReader;
        this.bufferedWriter = connectInfo.bufferedWriter;
    }

    @Override
    public void run() {
        try {
            //입력받고
            String requestStr = bufferedReader.readLine();

            //유효성 체크
            if(!isValidRequest(requestStr)){
                return;
            }
            //데이터 변환

            RequestData requestData = getConvertedRequestData(requestStr);
            if(requestData ==null ) return;
            //html을 리턴하는 등 responseData 리턴해주기
//            bufferedWriter.write();

//        } catch (BadRequestException exception){
//            log.info("유효하지 않은 요청입니다");
        } catch (IOException e){
//            log.info("연결에 실패하였습니다");
        }

    }



    //String requestStr -> RequestData
//    private RequestData getConvertedRequestData(String requestStr) throws BadRequestException {
    private RequestData getConvertedRequestData(String requestStr) {
        String[] split  = requestStr.split(" ");
        String data = split[0];
        String method = split[1];

        MethodEnum methodEnum = Arrays.stream(MethodEnum.values()).filter((str) -> {
            return str.getMethod().equals(method);
        }).findFirst().orElseGet(null);

        if(methodEnum == null) return null;

        RequestData requestData = new RequestData(methodEnum,data);
        return requestData;
    }

    private boolean isValidRequest(String requestStr){
        if(requestStr.split(" ").length != 2){
            return false;
        }
        return true;
    }
}
class RequestData {
    private MethodEnum method;
    private String data;

    RequestData(MethodEnum method,String data){
        this.data = data;
        this.method = method;
    }
}

