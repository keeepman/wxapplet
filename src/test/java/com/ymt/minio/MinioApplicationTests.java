package com.ymt.minio;

import com.sun.jmx.remote.internal.ArrayQueue;
import com.ymt.base.EncryptionClassLoader;
import com.ymt.bean.LoginBean;
import com.ymt.domain.User;
import com.ymt.service.UserService;
import jdk.jfr.internal.tool.Main;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.transaction.TransactionManager;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootTest
@EnableCaching
class MinioApplicationTests {

    @Resource
    private UserService userService;

    @Test
    void contextLoads() {
        User user = new User();
        user.setId(11121L);
        user.setNickName("dkfew");
        userService.insertSelective(user);
    }

    @Test
    void getUsers() {
        User u = new User();
        u.setId(1111L);
        u.setNickName("fwef22222e22w");
        User user1 = userService.selectByPrimaryKey(1111L);
        User user5 = userService.selectByPrimaryKey(2222L);
        User user2 = userService.selectByPrimaryKey(1111L);
        int i = userService.updateByPrimaryKeySelective(u);
        User user = userService.selectByPrimaryKey(1111L);
        userService.deleteByPrimaryKey(1111L);
        User user4 = userService.selectByPrimaryKey(1111L);
        User user6 = userService.selectByPrimaryKey(2222L);
        System.out.println();
    }

    @SneakyThrows
    @Test
    public void arrayQueue() {
        Object lock1 = new Object();
        Object lock2 = new Object();
        Lock r1 = new ReentrantLock();
        Lock r2 = new ReentrantLock();

        Thread t1 = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                //synchronized (lock1) {
                //    System.out.println("线程1lock1");
                //    // 处理业务逻辑
                //    // 线程1尝试获取lock2锁
                //    synchronized (lock2) {
                //        Thread.sleep(1000);
                //        // 处理业务逻辑
                //        System.out.println("线程1lock2");
                //    }
                //}
                r1.lock();
                System.out.println("线程1lock1");
                Thread.sleep(1000);
                r1.unlock();
                r2.lock();
                System.out.println("线程1lock2");
                //r1.unlock();
                r2.unlock();
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                r2.lock();
                System.out.println("线程2lock2");
                r1.lock();
                System.out.println("线程2lock1");
                r2.unlock();
                r1.unlock();
            }
        });
        t1.start();
        t2.start();
        LockSupport.park();

    }


    @Test
    public void casDemo() {
        User u = new User();
        u.setId(1010L);
        AtomicReference<User> ref = new AtomicReference<>(u);
        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep((long) (Math.random() * 1000L));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            User u1 = new User();
            u1.setBindId(1111L);
            ref.compareAndSet(u, u1);
            User result = ref.get();
            System.out.println(result);
        });
        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep((long) (Math.random() * 1000L));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            User u1 = new User();
            u1.setTotalScore(2222L);
            ref.compareAndSet(u, u1);
            User result = ref.get();
            System.out.println(result);
        });
        t1.start();
        t2.start();
        LockSupport.park();
    }

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private TransactionManager transactionManager;
    @Test
    public void getBean(){

    }


}

