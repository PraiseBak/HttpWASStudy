package com.otc.otc.dto;

//import lombok.extern.java.Log;
//import org.apache.coyote.BadRequestException;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Data
public class RequestInfo {
    private String contentType = null;
    private Boolean isContentExists = false;
    private Integer contentLength = 0;
    private String body = "";


    public boolean isJson() {
        if(contentType != null){
            return contentType.startsWith("application/json");
        }
        return false;
    }


}