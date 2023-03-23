package com.ymt.service;

import com.ymt.domain.User;
import org.springframework.stereotype.Service;
public class UserContext {
    private static ThreadLocal<User> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }

    public static User getCurrentUser() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}
