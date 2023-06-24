package com.example.springboot.filter;

import com.alibaba.fastjson.JSON;
import com.example.springboot.common.BaseContext;
import com.example.springboot.common.R;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
//路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

//        1.获取本次请求的URI
        String requestURI = request.getRequestURI();
//只拦截针对controller交互的请求，静态页面请求可以不拦截
        //定义不需要处理的请求路径
        log.info("拦截到请求：{}",requestURI);
        String[] urls = new String[]{
//                "/common/login",
//                "/common/logout",
                "/backend/**",
                "/common/**",
        };
//        2.判断本次请求是否需要处理
        boolean check = check(urls,requestURI);
//        3.如果不需要处理，则直接放行
        if(check == true){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
//        4-1.判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户ID为{}",request.getSession().getAttribute("user"));

            Long userID = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentID(userID);

            filterChain.doFilter(request,response);
            return;
        }

//        5.如果未登录则返回未登录结果,通过输出流方式向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url,requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
