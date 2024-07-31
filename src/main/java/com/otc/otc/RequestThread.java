package com.otc.otc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otc.otc.annotations.RequestBody;
import com.otc.otc.annotations.RequestParam;
import com.otc.otc.dto.ConnectInfo;
import com.otc.otc.dto.HttpResponse;
import com.otc.otc.dto.RequestData;
import com.otc.otc.dto.RequestInfo;
import com.otc.otc.util.RequestUtil;
import lombok.extern.java.Log;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.InvalidParameterException;
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

    private final BufferedWriter bufferedWriter;
    private final BufferedReader bufferedReader;

    private static final Logger log = Logger.getLogger(RequestThread.class.getName());
    private final RequestMappingReflection routerReflection;
    private final RequestUtil requestUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public RequestThread(ConnectInfo connectInfo) {
        this.bufferedReader = connectInfo.bufferedReader;
        this.bufferedWriter = connectInfo.bufferedWriter;
        this.routerReflection = new RequestMappingReflection();
        this.requestUtil = new RequestUtil();
    }


    /**
     *     POST /api/users HTTP/1.1
     *             Host: example.com
     *             Content-Type: application/json
     *             Accept: application/json
     *             User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36
     *             Connection: keep-alive
     *             Content-Length: 49
     *             {
     *                 "name": "John Doe",
     *                     "email": "john.doe@example.com"
     *             }
     */

    @Override
    public void run() {
        try {
            //입력받고
            String requestStr = bufferedReader.readLine();
            String line;

            RequestInfo requestInfo = new RequestInfo();
            boolean isBody = false;
            StringBuilder bodyStringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                //처음에 비면
                if (line.trim().isEmpty()) {
                    if (isBody) {
                        RequestUtil.setRequestInfo(bodyStringBuilder.toString(), requestInfo, isBody);
                        break;
                    } else if (requestInfo.getIsContentExists()) {
                        isBody = true;
                        continue;
                    }
                }

                if (isBody) {
                    bodyStringBuilder.append(line + "\n");
                    if (bodyStringBuilder.length() + 1 >= requestInfo.getContentLength()) {
                        break;
                    }
                    continue;
                }
                //유효한 정보면 정보를 저장
                RequestUtil.setRequestInfo(line, requestInfo, isBody);
            }


            requestInfo.setBody(bodyStringBuilder.toString() + "}");

            //유효성 체크
            if(!requestUtil.isValidRequest(requestStr)){
                log.info("유효하지 않은 요청입니다.");
                return;
            }

//            데이터 변환
            RequestData requestData = RequestUtil.getConvertedRequestData(requestStr);

            //라우팅 된 메서드 가져옴
            Method method = routerReflection.getRoutedMethod(requestData.getUri(),requestData.getMethod());

            String paramsStr = requestData.getUri().indexOf("?") != -1 ? requestData.getUri().substring(requestData.getUri().indexOf("?")) : requestData.getUri();

            log.info("paramStr"  + paramsStr);
            //request로부터 파라미터를 parse하여 맵에 넣음
            Map<String, String> params = requestUtil.parseQueryString(paramsStr);

            log.info("paramStr"  + params.size());

            Object[] arguments = getConvertedParameter(method, params,requestInfo);

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
        } catch (RuntimeException e){
            log.info(e.getMessage());
        }
    }


    private Object[] getConvertedParameter(Method method, Map<String, String> params, RequestInfo requestInfo) {
        Object[] objects = new Object[method.getParameterCount()];
        int idx = 0;
        //해당하는 컨트롤러의 메서드의 파라미터를 체크
        for(Parameter parameter : method.getParameters()){
            String parameterName = parameter.getName();
            //이름 일치하지않음
            String parameterVal = params.get(parameterName);

            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
            RequestBody requestBody = parameter.getAnnotation(RequestBody.class);


            if(requestParam != null){
                parameterVal = requestParamProcessing(requestParam, parameterVal);
                objects[idx] = RequestUtil.convertArgument(parameterVal,parameter.getType());
            }else if(requestBody != null){
                Object object = requestBodyProcessing(requestInfo.getBody(),parameter.getType());
                objects[idx] = object;
            }

            log.info("object = " + objects[idx]);
            idx++;
        }
        return objects;
    }

    private Object requestBodyProcessing(String requestBodyStr, Class<?> parameterClass) {
        try {
            return objectMapper.readValue(requestBodyStr,parameterClass);
        } catch (JsonProcessingException e) {

            return null;
        }
    }

    private static String requestParamProcessing(RequestParam requestParam, String parameterVal) {
        if(requestParam != null && parameterVal == null) {
            //필요한 값인데 없으면
            if (requestParam.required()) throw new InvalidParameterException("해당하는 메서드가 존재하지 않습니다");
            //기본값이 있으면 그 값을 사용
            if (!requestParam.defaultValue().isBlank()) {
                parameterVal = requestParam.defaultValue();
            }
        }
        return parameterVal;
    }

}

