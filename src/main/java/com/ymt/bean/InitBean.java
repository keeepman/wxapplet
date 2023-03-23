package com.ymt.bean;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Data
@Component
public class InitBean {

    private String name;
    private String age;

    @PostConstruct
    public void init() {
    }
}
