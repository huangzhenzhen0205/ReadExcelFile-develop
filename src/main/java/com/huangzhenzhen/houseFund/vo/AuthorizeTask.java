package com.huangzhenzhen.houseFund.vo;

import lombok.Data;

import java.util.List;


@Data
public class AuthorizeTask {
    private String user_id;
    private String website;
    private LoginMethodEnum login_method;
    private String login_method_code;
    private List<RequiredParam> requiredParams;

}
