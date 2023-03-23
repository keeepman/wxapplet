package com.ymt.bean;

import lombok.Data;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Data
public class LoginBean {
    private String code;
    private String encryptedData;
    private String iv;
    private String rawData;
    private String signature;
}
