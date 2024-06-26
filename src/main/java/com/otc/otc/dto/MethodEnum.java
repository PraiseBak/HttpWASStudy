package com.otc.otc.dto;

//import lombok.extern.java.Log;
//import org.apache.coyote.BadRequestException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;


public enum MethodEnum {
    GET("GET"),
    POST("POST"),
    PATCH("PATCH"),
    PULL("PULL");

    private final String method;

    MethodEnum(String method) {

        this.method = method;
    }

    public String getMethod() {
        return method;
    }




}