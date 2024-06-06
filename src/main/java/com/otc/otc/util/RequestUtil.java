package com.otc.otc.util;


import com.otc.otc.dto.MethodEnum;
import com.otc.otc.dto.RequestData;
import com.otc.otc.dto.RequestInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestUtil {



    /**
     *
     * @param requestStr
     * @return

        String requestStr -> RequestData로 변환
     *
     */
    public static RequestData getConvertedRequestData(String requestStr) {
        String[] split  = requestStr.split(" ");
        String method = split[0];
        String data = split[1];

        MethodEnum methodEnum = Arrays.stream(MethodEnum.values()).filter((str) -> {
                    return str.getMethod().equals(method);
                }).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 메소드를 찾을 수 없습니다: " + method));

        RequestData requestData = new RequestData(methodEnum,data);
        return requestData;
    }

    public static boolean isContentType(String line) {
        if (line.toLowerCase().startsWith("content-type:")) {
            return true;
        }
        return false;
    }

    public static boolean isContentLength(String line) {
        if (line.toLowerCase().startsWith("content-length:")) {
            return true;
        }
        return false;
    }



    public static void setRequestInfo(String line, RequestInfo requestInfo,boolean isBody) {
        if(isContentType(line)){
            requestInfo.setContentType(line.substring("Content-Type:".length(),line.length()));
        }

        if(isContentLength(line)){
            requestInfo.setIsContentExists(true);
            String contentLineStr = line.replace("Content-Length:", "").trim();
            int contentLineNum = Integer.parseInt(contentLineStr);
            requestInfo.setContentLength(contentLineNum);
        }

        if(isBody){
//            System.out.println(requestInfo.getBody()== null ? 0 : requestInfo.getBody().length());
            requestInfo.setBody(requestInfo.getBody() + line);
        }
    }


    /**
     * TODO 개선
     * @param requestStr
     * @return
     */
    public boolean isValidRequest(String requestStr){
        if(requestStr.split(" ").length != 3){
            return false;
        }
        return true;
    }


    // 쿼리 스트링을 파싱하여 Map<String, String> 형태로 반환하는 메서드
    public static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString != null && queryString.length() > 1) {
            String[] parts = queryString.substring(1).split("&");
            for (String part : parts) {
                String[] keyValue = part.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }

    // 쿼리 스트링에서 가져온 값들을 메서드의 파라미터 타입에 맞게 변환하는 메서드
    public static Object[] convertArguments(Map<String, String> params, Class<?>[] parameterTypes) {
        Object[] arguments = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];

            String paramName = "arg" + (i + 1);
            String paramValue = params.get(paramName);

            arguments[i] = convertArgument(paramValue, parameterType);
        }
        return arguments;
    }

    // 인수를 파라미터 타입에 맞게 변환하는 메서드
    public static Object convertArgument(String argument, Class<?> parameterType) {
        if(argument == null) return null;
        // 파라미터 타입이 Long일 때
        if (parameterType.equals(Long.class)) {
            // Long으로 변환
            return Long.valueOf(argument);
        }

        // 파라미터 타입이 String일 때
        if (parameterType.equals(String.class)) {
            // 그대로 반환
            return argument;
        }


        // 기본적으로는 null 반환
        return null;
    }
}
