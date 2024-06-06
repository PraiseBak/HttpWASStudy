package com.otc.otc.dto;


public class Tmp {
    private MethodEnum method;
    private String uri;

    public Tmp(MethodEnum method, String data){
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


