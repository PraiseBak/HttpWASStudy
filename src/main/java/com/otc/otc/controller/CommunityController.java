package com.otc.otc.controller;


import com.otc.otc.annotations.*;
import com.otc.otc.dto.MethodEnum;
import com.otc.otc.dto.Person;
import com.otc.otc.dto.RequestData;

@Controller
@RequestMapping(url = "/test")
public class CommunityController {

    public CommunityController(){

    }

    @MethodMapping(url = "/a",method = MethodEnum.POST)
    public void hello(@RequestBody(required = false) Person requestData){
        System.out.println("hello");
        System.out.println(requestData);
    }

    /**
     * nullable에 관한 처리는 따로하지 않았음
     * @param arg1
     * @param arg2
     */
    @MethodMapping(url = "/b",method = MethodEnum.GET)
    public void helloWithParameter(@RequestParam(required=false,defaultValue = "hello") String arg3, @RequestParam(required=false,defaultValue = "hello2") String arg4
            , @RequestBody(required = false) RequestData requestData){
        System.out.println("helloWithParamater");
        System.out.println(arg3 + "," + arg4);
    }
}
