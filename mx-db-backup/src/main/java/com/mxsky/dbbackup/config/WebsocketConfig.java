package com.mxsky.dbbackup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/*
 * @Description: websocket
 * @Author: Jevon.Li
 * @Date: 2020-11-14 11:51
 */
@Configuration
@Import(cn.hutool.extra.spring.SpringUtil.class)
public class WebsocketConfig  {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}