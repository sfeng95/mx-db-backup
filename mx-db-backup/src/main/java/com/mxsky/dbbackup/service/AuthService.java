package com.mxsky.dbbackup.service;


import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/*
 * @Description: 认证服务
 * @Author: Jevon.Li
 * @Date: 2020-11-14 11:52
 */
@Service
public class AuthService {

    private static final long tokenTimeOut=1000*1800;

    private TimedCache<String,String> tokenCache = CacheUtil.newTimedCache(tokenTimeOut);

    @Autowired
    private Environment env;


    public  String login(String password){
        String correctPassword = env.getProperty("mxdbBackup.password");
        if(StringUtils.isEmpty(password)){
            throw new RuntimeException("登录密码不能为空");
        }
        if(!password.equals(correctPassword)){
            throw new RuntimeException("登录密码不正确");
        }
        String token=IdUtil.simpleUUID();
        tokenCache.put(token,token,tokenTimeOut);
        tokenCache.schedulePrune(30000);
        return token;
    }


    public boolean checkToken(String token){
        String oldtoken=tokenCache.get(token);
        if(StringUtils.isNotEmpty(oldtoken)){
            return true;
        }
        return false;
    }
}
