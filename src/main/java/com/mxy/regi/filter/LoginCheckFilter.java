package com.mxy.regi.filter;

import com.alibaba.fastjson.JSON;
import com.mxy.regi.common.BaseContext;
import com.mxy.regi.common.JsonResult;
import com.sun.deploy.net.HttpResponse;
import jdk.nashorn.internal.objects.NativeString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户权限过滤器
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter" , urlPatterns = "/*")
@Component
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request  = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取本次请求的URI
        String requestURI =  request.getRequestURI();

        log.info("拦截到请求：{}",requestURI);
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**"
        };
        //判断此次请求是否需要处理
        boolean check = check(urls,requestURI);

        //检测不需要处理之后则放行
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //如果已经是登录状态则放行
        if (request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录，登陆ID为：{}",request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }

        //如果是未登录状态则拦截
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(JsonResult.error("NOTLOGIN")));
        return;

    }

    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }


}
