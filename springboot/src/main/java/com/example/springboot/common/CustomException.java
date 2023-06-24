package com.example.springboot.common;


//自定义的业务异常类
public class CustomException extends RuntimeException{    //自定义一下没有直接用是为了自定义业务异常信息
    public CustomException(String message){          //这是java里面的啊，啥意思呢
        super(message);
    }
}
