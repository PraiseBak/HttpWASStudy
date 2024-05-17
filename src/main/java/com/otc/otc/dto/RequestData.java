package com.otc.otc.dto;


public class RequestData {
    private MethodEnum method;
    private String uri;

    public RequestData(MethodEnum method, String data){
        this.uri = data;
        this.method = method;
    }

    public MethodEnum getMethod() {
        return method;
    }

    public String getUri(){
        return uri;

    }
}

