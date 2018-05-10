package com.huangzhenzhen.houseFund.vo;

public enum LoginMethodEnum {

    ACCOUNT_AND_PASSWORD("账号密码登录"),
    MULTIPLE_PARMAS("多参数登录");

    private String title;

    LoginMethodEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


}
