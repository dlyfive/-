package com.example.springboot.common;

//基于ThredLocal封装的工具类，用于保存和获取当前登录的用户id
public class BaseContext{   //以线程为作用域
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentID(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
