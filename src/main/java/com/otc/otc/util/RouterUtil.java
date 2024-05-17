package com.otc.otc.util;


import com.otc.otc.dto.MethodEnum;
import com.otc.otc.dto.RequestData;

import java.util.Arrays;

public class RouterUtil {

    /**
     * router 기능 만들어서 리플렉션으로 캐치한다음에
     * 해당 필드나 메서드 등 확인해서 해당 메서드를 실행해야함
     * @param requestStr
     * @return
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


}
