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

package com.mxsky.dbbackup.service;

import com.mxsky.dbbackup.constants.CommonConstant;
import com.mxsky.dbbackup.entity.DsConfigEntity;
import com.mxsky.dbbackup.enums.SystemOSNameEnum;
import com.mxsky.dbbackup.utils.JarUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * @Description: 命令服务
 * @Author: Jevon.Li
 * @Date: 2020-11-14 11:52
 */
public class DBCmdService {

    /**
     * 基于线程上下文的工作目录
     */
    private static final ThreadLocal<String> workpath = new ThreadLocal<String>();

    public static String getWorkpath() {
        return workpath.get();
    }

    public static void setWorkpath(String path) {
        workpath.set(path);
    }

    public static void remove() {
        workpath.remove();
    }


    private static final Logger log = LoggerFactory.getLogger(DBCmdService.class);


    public static  String[] getDBRestoreCmd(DsConfigEntity dsConfig,String dataFile){
        if(dsConfig.getDbType().contains("mysql")){
            return getMysqlDBdbRestoreCmd(dsConfig,dataFile);
        }
        return null;
    }


    public static  String[] getDBBackupCmd(DsConfigEntity dsConfig,String dataFile){
        if(dsConfig.getDbType().contains("mysql")){
            return getMysqlDBBackupCmd(dsConfig,dataFile);
        }
        return null;
    }


    public static  String[] getMysqlDBBackupCmd(DsConfigEntity dsConfig,String dataFile){
        List<String> commands = new ArrayList<String>();
        String dbLibPath="";
        if(JarUtil.getSystemOsName().equals(SystemOSNameEnum.windows.getCode())){
            dbLibPath=CommonConstant.DB_LIB_PATH+"dbProgram"+File.separator+"windows"+File.separator+dsConfig.getDbType();
        }else {
            dbLibPath=CommonConstant.DB_LIB_PATH+"dbProgram"+File.separator+"linux"+File.separator+dsConfig.getDbType();
        }
        log.info( dbLibPath);
        DBCmdService.setWorkpath(dbLibPath);

        if(JarUtil.getSystemOsName().equals(SystemOSNameEnum.windows.getCode())){
            commands.add("cmd.exe");
            commands.add("/c");
        }

        if(JarUtil.getSystemOsName().equals(SystemOSNameEnum.linux.getCode())){
            commands.add("sh");
            commands.add("/c");
        }

        if(JarUtil.getSystemOsName().equals(SystemOSNameEnum.windows.getCode())){
            commands.add("mysqldump");
        }
        if(JarUtil.getSystemOsName().equals(SystemOSNameEnum.linux.getCode())){
            commands.add("./mysqldump");
        }
        commands.add("-h"+dsConfig.getAdress());
        commands.add("-P"+dsConfig.getPort());
        commands.add("-u"+dsConfig.getUsername());
        commands.add("-p"+dsConfig.getPassword());
        if(StringUtils.isEmpty(dsConfig.getDatabase())){
            commands.add("--all-databases");
        }else{
            commands.add("--databases "+dsConfig.getDatabase());
        }
        commands.add("-e");
        commands.add("--max_allowed_packet=4194304");
        commands.add("--net_buffer_length=16384");
        commands.add("--result-file="+dataFile);

//        log.info( StringUtils.join(commands.toArray()," "));
        return commands.toArray(new String[commands.size()]);

    }


    public static  String[] getMysqlDBdbRestoreCmd(DsConfigEntity dsConfig,String dataFile){
        String dbLibPath="";
        if(JarUtil.getSystemOsName().equals(SystemOSNameEnum.windows.getCode())){
            dbLibPath=CommonConstant.DB_LIB_PATH+"dbProgram"+File.separator+"windows"+File.separator+dsConfig.getDbType();
        }else {
            dbLibPath=CommonConstant.DB_LIB_PATH+"dbProgram"+File.separator+"linux"+File.separator+dsConfig.getDbType();
        }
        log.info( dbLibPath);
        setWorkpath(dbLibPath);

        List<String> commands = new ArrayList<String>();
        if(JarUtil.getSystemOsName().equals(SystemOSNameEnum.windows.getCode())){
            commands.add("cmd.exe");
            commands.add("/c");
        }

        if(JarUtil.getSystemOsName().equals(SystemOSNameEnum.linux.getCode())){
            commands.add("sh");
            commands.add("/c");
        }

        if(JarUtil.getSystemOsName().equals(SystemOSNameEnum.windows.getCode())){
            commands.add("mysql");
        }
        if(JarUtil.getSystemOsName().equals(SystemOSNameEnum.linux.getCode())){
            commands.add("./mysql");
        }
        commands.add("-h"+dsConfig.getAdress());
        commands.add("-P"+dsConfig.getPort());
        commands.add("-u"+dsConfig.getUsername());
        commands.add("-p"+dsConfig.getPassword());
        if(StringUtils.isNotEmpty(dsConfig.getDatabase())){
            commands.add(dsConfig.getDatabase());
        }
        commands.add("<"+dataFile);
        //不打印log防止密码泄露
        //log.info( StringUtils.join(commands.toArray()," "));
        return commands.toArray(new String[commands.size()]);
    }





}
