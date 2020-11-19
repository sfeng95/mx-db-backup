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

package com.mxsky.dbbackup.constants;

import java.io.File;

/**
 * @ClassName CommonConstant
 * @Description: 常量
 * @Author Jevon.Li
 * @Date 2020/11/14 11:37
 * @Version V1.0
 **/
public class CommonConstant {

    /**
     * 定时任务id
     */
    public static final String SCHEDULE_ID = "SCHEDULE_ID";


    /**
     * 定时表达式
     */
    public static final String SCHEDULE_CRON = "SCHEDULE_CRON";


    /**
     * 任务状态id
     */
    public static final String SCHEDULE_STATUS_ID = "SCHEDULE_STATUS";


    /**
     * 任务是否运行中标识
     */
    public static final String TASK_RUNNING_FALG = "TASK_RUNNING_FALG";

    /**
     * 工作文件夹
     */
    public static final String DB_SUPPORT_PATH = "dbSupport";


    /**
     * 备份数据文件夹
     */
    public static final String DB_DATA_PATH = System.getProperty("user.dir") + File.separator + "dbBaseBackupFile" + File.separator;


    /**
     * 类库文件路径
     */
    public static final String DB_LIB_PATH = System.getProperty("user.dir") + File.separator + DB_SUPPORT_PATH + File.separator;


    /**
     * 数据库依赖的程序
     */
    public static final String DB_SUPPORT_FILE = "dbProgram.zip";

    /**
     * 打包方式 idea模式
     */
    public static final int NOT_JAR=0;


    /**
     * 打包方式 jar模式运行,lib放在jar外面
     */
    public static final int ON_JAR_LIB_OUTSIDE=1;


    /**
     * 打包方式 jar模式运行,lib放在jar里面
     */
    public static final int ON_JAR_LIB_INSIDE=2;

}
