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

import cn.hutool.extra.spring.SpringUtil;
import com.mxsky.dbbackup.constants.CommonConstant;
import com.mxsky.dbbackup.controller.LogsWS;
import com.mxsky.dbbackup.dao.DsConfigDao;
import com.mxsky.dbbackup.dao.TaskRecordDao;
import com.mxsky.dbbackup.entity.DsConfigEntity;
import com.mxsky.dbbackup.entity.TaskRecordEntity;
import com.mxsky.dbbackup.enums.ScheduleStatusEnum;
import com.mxsky.dbbackup.utils.ShellExecutorUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/*
 * @Description: 数据还原服务
 * @Author: Jevon.Li
 * @Date: 2020-11-14 11:53
 */
public class DBRestoreService implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(DBRestoreService.class);


    private Long rId;

    private Boolean backup;

    public DBRestoreService(Long rId, Boolean backup) {
        this.rId = rId;
        this.backup = backup;
    }

    public void run() {
        log.info("=================后台执行数据还原=========================");
        //先暂停备份
        ScheduleService.stopSchedule();
        doRestore(this.rId, this.backup);
    }


    public static void doRestore(Long rId, Boolean backup) {
        final LogsWS logsWS = SpringUtil.getBean("logsWS");

        try {
            //如果需要备份
            if (backup) {
                logsWS.sendMessage("===先备份再还原数据库===");
                DBBackupService.doDBBackup();
            }
            if (rId == null) {
                throw new RuntimeException("rId不能为空");
            }
            System.setProperty(CommonConstant.SCHEDULE_STATUS_ID, ScheduleStatusEnum.DO_RESTORE.getCode() + "");
            DsConfigEntity dsConfig = null;
            List<DsConfigEntity> dslist = DsConfigDao.list();
            if (dslist.size() > 0) {
                dsConfig = dslist.get(0);
            } else {
                System.setProperty(CommonConstant.SCHEDULE_STATUS_ID, ScheduleStatusEnum.RESTORE_FAIL.getCode() + "");
                throw new RuntimeException("请设置好数据库");
            }

            //查询数据
            TaskRecordEntity taskRecord = TaskRecordDao.info(rId);
            if (taskRecord == null) {
                throw new RuntimeException("备份数据已经不存在");
            }
            String dataFile = taskRecord.getDataFile();

            //执行命令
            DBCmdService.getDBRestoreCmd(dsConfig, dataFile);
            String[] cmd = DBCmdService.getDBRestoreCmd(dsConfig, dataFile);
            String workPath = DBCmdService.getWorkpath();
            logsWS.sendMessage("===开始还原数据库===");

            int exitValue = ShellExecutorUtil.execute(
                    cmd,
                    workPath,
                    null,
                    (message, process) -> {
                        log.info(message);
                        logsWS.sendMessage(message);
                    }
            );
            log.info("exitValue: " + exitValue);
            System.setProperty(CommonConstant.SCHEDULE_STATUS_ID, ScheduleStatusEnum.RESTORE_SUCCESS.getCode() + "");
        } catch (ShellExecutorUtil.CommandTimeoutException e) {
            try {
                logsWS.sendMessage(e.getMessage());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            log.info(e.getMessage());
        } catch (Exception e) {
            try {
                logsWS.sendMessage(e.getMessage());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.setProperty(CommonConstant.SCHEDULE_STATUS_ID, ScheduleStatusEnum.RESTORE_FAIL.getCode() + "");
        } finally {
            ScheduleService.startSchedule();
            System.setProperty(CommonConstant.TASK_RUNNING_FALG, "N");
        }
    }


}
