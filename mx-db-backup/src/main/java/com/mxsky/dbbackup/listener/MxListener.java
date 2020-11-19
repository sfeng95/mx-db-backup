/*
 *
 * Copyright [2020] [https://www.mxsky.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * mx-db-backup采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改源码头部的版权声明。
 * 3.请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://gitee.com/mx-sky/mx-db-backup
 * 5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/mx-sky/mx-db-backup
 * 6.这个项目可以商业使用，不需要授权联系
 * 7.使用本项目前先备份好数据，请认真严格测试程序，如果使用本程序导致数据丢失，一切责任与本人无关
 * 8.若您的项目无法满足以上几点，可申请商业授权，获取商业授权许可请联系邮箱 sfeng95@qq.com
 *
 */

package com.mxsky.dbbackup.listener;


import com.mxsky.dbbackup.constants.CommonConstant;
import com.mxsky.dbbackup.service.DBLibUnZipService;
import com.mxsky.dbbackup.service.H2DataBaseService;
import com.mxsky.dbbackup.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;

/*
 * @Description: 监听器类
 * @Author: Jevon.Li
 * @Date: 2020-11-14 11:51
 */
@WebListener
public class MxListener implements ServletContextListener {

	private static final Logger log = LoggerFactory.getLogger(MxListener.class);

	public void contextInitialized(ServletContextEvent sce) {

		try {
			//解压数据库依赖程序
			DBLibUnZipService.init();
			//初始化数据库
			H2DataBaseService.init();
			//初始化定时器
			ScheduleService.init();
			File dataFile=new File(CommonConstant.DB_DATA_PATH);
			if(!dataFile.exists()){
				dataFile.mkdirs();
			}
			log.info("=====================数据库备份还原助手 初始化成功！================================");
		} catch (Exception e) {
			log.error("====================数据库备份还原助手 初始化失败！================================");
			e.printStackTrace();
		}
	}

	public void contextDestroyed(ServletContextEvent servletContextEvent) {

	}

}