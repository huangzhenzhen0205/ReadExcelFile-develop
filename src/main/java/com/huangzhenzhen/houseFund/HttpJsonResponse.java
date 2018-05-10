package com.huangzhenzhen.houseFund;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class HttpJsonResponse<T>{
    protected T data;
    protected String code;
    protected String message;

    public HttpJsonResponse() {
    }



}
