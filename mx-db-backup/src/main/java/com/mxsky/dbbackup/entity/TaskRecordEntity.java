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

package com.mxsky.dbbackup.entity;

import org.apache.commons.dbutils.BaseResultSetHandler;

import java.sql.SQLException;
import java.util.Date;

/*
 * @Description: 执行记录实体
 * @Author: Jevon.Li
 * @Date: 2020-11-14 11:50
 */
public class TaskRecordEntity extends BaseResultSetHandler {

    /**
     * 主键
     */
    private Long rId;

    /**
     * 定时任务id
     */
    private Long sId;


    /**
     * 备份时间
     */
    private Date backupTime;


    /**
     * 状态
     */
    private int status;


    /**
     * 备份耗时
     */
    private Long backupDuration;


    /**
     * 日志文件
     */
    private String logFile;

    /**
     * 文件大小
     */
    private Long dataFileSize;


    /**
     * 数据文件
     */
    private String dataFile;


    protected Object handle() throws SQLException {
        return null;
    }


    public Long getrId() {
        return rId;
    }

    public void setrId(Long rId) {
        this.rId = rId;
    }

    public Long getsId() {
        return sId;
    }

    public void setsId(Long sId) {
        this.sId = sId;
    }

    public Date getBackupTime() {
        return backupTime;
    }

    public void setBackupTime(Date backupTime) {
        this.backupTime = backupTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getBackupDuration() {
        return backupDuration;
    }

    public void setBackupDuration(Long backupDuration) {
        this.backupDuration = backupDuration;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public Long getDataFileSize() {
        return dataFileSize;
    }

    public void setDataFileSize(Long dataFileSize) {
        this.dataFileSize = dataFileSize;
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }
}
