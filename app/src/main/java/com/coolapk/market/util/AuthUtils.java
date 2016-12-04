package com.coolapk.market.util;

/**
 * Created by Hesh on 2016/11/22.
 */

public class AuthUtils {

    public static native String getAS(String str);

    static {
        System.loadLibrary("a");
    }
}
