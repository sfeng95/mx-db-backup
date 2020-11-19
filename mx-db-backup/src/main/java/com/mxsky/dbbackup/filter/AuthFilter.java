package com.mxsky.dbbackup.filter;

import com.alibaba.fastjson.JSON;
import com.mxsky.dbbackup.pojo.R;
import com.mxsky.dbbackup.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthFilter  implements Filter {

    @Autowired
    private AuthService authService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if(request.getRequestURI().contains("login")){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String token = request.getHeader("MX-TOKEN");
        if(StringUtils.isEmpty(token)) {
            response(servletResponse,R.fail(401,"请登录"));
            return;
        }
        if(!authService.checkToken(token)){
            response(servletResponse,R.fail(401,"请重新登录！"));
            return;
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }


    public void response( ServletResponse servletResponse ,R r) throws IOException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setStatus(r.getCode());
        response.setHeader("Content-type", "textml;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JSON.toJSONString(r));
    }
}