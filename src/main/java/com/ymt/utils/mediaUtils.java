package com.ymt.utils;

import java.util.List;

public class mediaUtils {
    public static String[] imgTypeList = new String[]{
            "png", "jpg", "jpeg", "gif"
    };

    public static String[] videoTypeList = new String[]{
            "mp4", "flv", "m4v", "mov", "rmvb", "wmv", "avi", "mkv"
    };

    public static boolean checkType(String[] types, String typeName) {
        for (String type : types) {
            boolean b = type.equalsIgnoreCase(typeName);
            if (b) {
                return true;
            }
        }
        return false;
    }

    public static Integer judgeType(String typeName) {
        if (checkType(imgTypeList, typeName)) {
            return 0;
        } else if (checkType(videoTypeList, typeName)) {
            return 1;
        }
        return null;
    }
}
