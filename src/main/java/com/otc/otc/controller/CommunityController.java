package com.otc.otc.controller;


import com.otc.otc.TcpWasServer;
import com.otc.otc.annotations.Controller;
import com.otc.otc.annotations.MethodMapping;
import com.otc.otc.annotations.RequestMapping;
import com.otc.otc.dto.MethodEnum;

import java.util.logging.Logger;

@Controller
@RequestMapping(url = "/test")
public class CommunityController {

    public CommunityController(){

    }

    @MethodMapping(url = "/a",method = MethodEnum.GET)
    public void hello(){
        System.out.println("hello");

    }

    /**
     * nullable에 관한 처리는 따로하지 않았음
     * @param arg1
     * @param arg2
     */
    @MethodMapping(url = "/b",method = MethodEnum.GET)
    public void helloWithParameter(String arg1,String arg2){
        System.out.println("helloWithParamater");
        System.out.println(arg1 + "," + arg2);
    }
}
