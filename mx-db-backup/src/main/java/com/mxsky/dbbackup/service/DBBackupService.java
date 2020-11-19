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

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileAppender;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.extra.spring.SpringUtil;
import com.mxsky.dbbackup.constants.CommonConstant;
import com.mxsky.dbbackup.controller.LogsWS;
import com.mxsky.dbbackup.dao.DsConfigDao;
import com.mxsky.dbbackup.dao.TaskRecordDao;
import com.mxsky.dbbackup.entity.DsConfigEntity;
import com.mxsky.dbbackup.entity.TaskRecordEntity;
import com.mxsky.dbbackup.enums.ScheduleStatusEnum;
import com.mxsky.dbbackup.enums.TaskStatusEnum;
import com.mxsky.dbbackup.utils.ShellExecutorUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static java.lang.System.out;

/*
 * @Description: 备份服务
 * @Author: Jevon.Li
 * @Date: 2020-11-14 11:52
 */
public class DBBackupService implements Task, Runnable {
    private static final Logger log = LoggerFactory.getLogger(DBBackupService.class);


    public void execute() {
        log.info("=================执行定时数据备份=========================");
        doDBBackup();
    }


    public void run() {
        log.info("=================后台执行数据备份=========================");
        doDBBackup();
    }


    public static void doDBBackup() {
        final LogsWS logsWS = SpringUtil.getBean("logsWS");
        StringBuilder exclogs = new StringBuilder();
        System.setProperty(CommonConstant.SCHEDULE_STATUS_ID, ScheduleStatusEnum.RUNNING.getCode() + "");
        TimeInterval timer = DateUtil.timer();
        TaskRecordEntity entity = new TaskRecordEntity();
        entity.setrId(RandomUtil.randomLong(50000));
        entity.setBackupTime(new Date());
        String publicFileName = DateUtil.format(new Date(), "yyyy-MM-dd_HH-mm-ss");
        String dataFile = CommonConstant.DB_DATA_PATH + publicFileName + ".sql";
        String logFile = CommonConstant.DB_DATA_PATH + publicFileName + ".log";

        try {
            logsWS.sendMessage("=================后台执行数据备份=========================");
            DsConfigEntity dsConfig = null;
            List<DsConfigEntity> dslist = DsConfigDao.list();
            if (dslist.size() > 0) {
                dsConfig = dslist.get(0);
            } else {
                System.setProperty(CommonConstant.SCHEDULE_STATUS_ID, ScheduleStatusEnum.FAIL.getCode() + "");
                throw new RuntimeException("请设置好数据库");
            }


            //执行命令
            String[] cmd = DBCmdService.getDBBackupCmd(dsConfig, dataFile);
            String workPath = DBCmdService.getWorkpath();

            int exitValue = ShellExecutorUtil.execute(
                    cmd,
                    workPath,
                    null,
                    (message, process) -> {
                        try {
                            exclogs.append(message);
                            logsWS.sendMessage(message);
                            log.info(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
            );
            log.info("exitValue: " + exitValue);
            exclogs.append("exitValue: " + exitValue);
            entity.setDataFileSize(new File(dataFile).length());
            entity.setDataFile(dataFile);
            entity.setLogFile(logFile);
            entity.setBackupDuration(timer.intervalSecond());
            entity.setStatus(TaskStatusEnum.SUCCESS.getCode());
            System.setProperty(CommonConstant.SCHEDULE_STATUS_ID, ScheduleStatusEnum.SUCCESS.getCode() + "");
            logsWS.sendMessage("数据库备份成功！");
        } catch (ShellExecutorUtil.CommandTimeoutException e) {
            log.info(e.getMessage());
            exclogs.append(e.getMessage());
        } catch (Exception e) {
            try {
                logsWS.sendMessage("数据库备份失败！");
                logsWS.sendMessage(e.getMessage());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            exclogs.append(e.getMessage());
            System.setProperty(CommonConstant.SCHEDULE_STATUS_ID, ScheduleStatusEnum.FAIL.getCode() + "");
            entity.setBackupDuration(timer.intervalSecond());
            entity.setStatus(TaskStatusEnum.FAIL.getCode());
        } finally {
            System.setProperty(CommonConstant.TASK_RUNNING_FALG, "N");
            BufferedOutputStream output = FileUtil.getOutputStream(logFile);
            IoUtil.write(output,"UTF-8",true,exclogs.toString());
            try {
                TaskRecordDao.saveOrUpdate(entity);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
