package com.ymt.domain;

import lombok.Data;

@Data
public class LoginResult {
    private String code;
    private String token;

    public LoginResult() {
    }

    public LoginResult(String code, String token) {
        this.code = code;
        this.token = token;
    }
}
