package com.otc.otc;

import com.otc.otc.dto.ConnectInfo;
import com.otc.otc.dto.HttpResponse;
import com.otc.otc.dto.RequestData;
import com.otc.otc.util.RequestUtil;
import lombok.extern.java.Log;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Logger;

/*
request 참고
POST /login HTTP/1.1
Host: www.example.com
Content-Type: application/x-www-form-urlencoded
Content-Length: 27

username=john&password=123456
 */
@Log
public class RequestThread implements Runnable {
    private final int SERVER_PORT = 8080;
    private final String uuid;
    private final ConnectInfo connectInfo;
    private final BufferedWriter bufferedWriter;
    private final BufferedReader bufferedReader;

    private static final Logger log = Logger.getLogger(RequestThread.class.getName());
    private final RequestMappingReflection routerReflection;
    private final RequestUtil requestUtil;


    public RequestThread(String uuid, ConnectInfo connectInfo) {
        this.uuid = uuid;
        this.connectInfo = connectInfo;
        this.bufferedReader = connectInfo.bufferedReader;
        this.bufferedWriter = connectInfo.bufferedWriter;
        this.routerReflection = new RequestMappingReflection();
        this.requestUtil = new RequestUtil();
    }

    @Override
    public void run() {
        try {
            //입력받고
            String requestStr = bufferedReader.readLine();

            //유효성 체크
            if(!requestUtil.isValidRequest(requestStr)){
                log.info("유효하지 않은 요청입니다.");
                return;
            }

//            데이터 변환
            RequestData requestData = RequestUtil.getConvertedRequestData(requestStr);

            //라우팅 된 메서드 가져옴
            Method method = routerReflection.getRoutedMethod(requestData.getUri(),requestData.getMethod());

            String paramsStr = requestData.getUri().substring(requestData.getUri().indexOf("?"));
            Map<String, String> params = requestUtil.parseQueryString(paramsStr);

            Object[] arguments = requestUtil.convertArguments(params, method.getParameterTypes());


            Object inst = method.getDeclaringClass().getDeclaredConstructor().newInstance();
            Object result = method.invoke(inst, arguments);


            bufferedWriter.write(HttpResponse.ok().body(result == null ? "" : result.toString()).toString());
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e){
            log.info("연결에 실패하였습니다.");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            log.info("404 error");
            try {
                bufferedWriter.write(HttpResponse.notFound().body("404").toString());
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException ex) {

            }
        } catch (InvocationTargetException e) {
            log.info("접근불가한 클래스");
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            log.info("접근불가한 클래스");
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            log.info("접근불가한 클래스");
            throw new RuntimeException(e);
        }
    }

}




