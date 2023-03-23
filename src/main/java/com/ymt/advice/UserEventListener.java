package com.ymt.advice;

import com.ymt.bean.LoginBean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    @EventListener
    public void handleUserCreatedEvent(LoginBean event) {
        String rawData = event.getRawData();
        System.out.println("rawData:" + rawData);
        // 处理用户创建事件，例如发送欢迎邮件等
    }
}