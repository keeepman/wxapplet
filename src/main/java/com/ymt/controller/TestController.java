package com.ymt.controller;

import com.ymt.advice.LimitType;
import com.ymt.annotation.Limit;
import com.ymt.domain.User;
import com.ymt.service.UserContext;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/limit")
public class TestController {


    private static final AtomicInteger ATOMIC_INTEGER_1 = new AtomicInteger();
    private static final AtomicInteger ATOMIC_INTEGER_2 = new AtomicInteger();
    private static final AtomicInteger ATOMIC_INTEGER_3 = new AtomicInteger();

    /**
     * @author fu
     * @description
     * @date 2020/4/8 13:42
     */
    @Limit(key = "limitTest", period = "10", count = "3")
    @GetMapping("/limitTest1")
    public int testLimiter1() {
        return ATOMIC_INTEGER_1.incrementAndGet();
    }

    /**
     * @author fu
     * @description
     * @date 2020/4/8 13:42
     */
    @Limit(key = "customer_limit_test", period = "10", count = "3", limitType = LimitType.CUSTOMER)
    @GetMapping("/limitTest2")
    public int testLimiter2() {

        return ATOMIC_INTEGER_2.incrementAndGet();
    }

    /**
     * @author fu
     * @description
     * @date 2020/4/8 13:42
     */
    @Limit(key = "ip_limit_test", period = "10", count = "3", limitType = LimitType.IP)
    @GetMapping("/limitTest3")
    public int testLimiter3() {

        return ATOMIC_INTEGER_3.incrementAndGet();
    }

    @GetMapping("/setUser")
    public void setUser() {
        User user = new User();
        user.setId(11L);
        user.setNickName("nick");
        UserContext.setCurrentUser(user);
        System.out.println();
    }

    @GetMapping("/getUser")
    public void getUser() {
        String s = null;
        Assert.notNull(s, "SQL must not be null");
    }
}
