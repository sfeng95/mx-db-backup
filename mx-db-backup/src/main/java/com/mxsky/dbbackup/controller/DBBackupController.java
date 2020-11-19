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

package com.mxsky.dbbackup.controller;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import com.mxsky.dbbackup.constants.CommonConstant;
import com.mxsky.dbbackup.dao.DsConfigDao;
import com.mxsky.dbbackup.dao.ScheduleDao;
import com.mxsky.dbbackup.dao.TaskRecordDao;
import com.mxsky.dbbackup.dto.ScheduleDTO;
import com.mxsky.dbbackup.entity.DsConfigEntity;
import com.mxsky.dbbackup.entity.ScheduleEntity;
import com.mxsky.dbbackup.entity.TaskRecordEntity;
import com.mxsky.dbbackup.pojo.R;
import com.mxsky.dbbackup.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DBBackupController
 * @Description: 控制器
 * @Author Jevon.Li
 * @Date 2020/11/14 11:37
 * @Version V1.0
 **/
@Controller
@RequestMapping("/xjdb-backup")
public class DBBackupController {


    @Autowired
    private AuthService authService;

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class,SQLException.class,RuntimeException.class})
    @ResponseBody
    public R handleError(Exception e) {
        return R.fail(e.getMessage());
    }


    @PostMapping("/login")
    @ResponseBody
    public R<String> login(String password) throws SQLException {
        return R.ok(authService.login(password));
    }


    @GetMapping("/schedule/status")
    @ResponseBody
    public R<Map<String,Object>> scheduleStatus() throws SQLException {
        return R.ok(ScheduleService.scheduleStatusEnum());
    }

    @PostMapping("/schedule/upStatus")
    @ResponseBody
    public R upStatus(){
        ScheduleService.updateStatus();
        return R.ok();
    }


    @PostMapping("/schedule/execute")
    @ResponseBody
    public R executeSchedule(){
        System.setProperty(CommonConstant.TASK_RUNNING_FALG,"Y");
        ThreadUtil.execAsync(new DBBackupService());
        return R.ok("执行成功，请稍后查看结果");
    }




    @PostMapping("/schedule/cancelExecute")
    @ResponseBody
    public R cancelExecuteSchedule(){
        System.setProperty(CommonConstant.SCHEDULE_STATUS_ID, "");
        System.setProperty(CommonConstant.TASK_RUNNING_FALG,"N");
        return R.ok();
    }


    @PostMapping("/schedule/stop")
    @ResponseBody
    public R stopSchedule(){
        ScheduleService.stopSchedule();
        return R.ok();
    }


    @PostMapping("/schedule/start")
    @ResponseBody
    public R startSchedule(){
        ScheduleService.startSchedule();
        return R.ok();
    }



    @GetMapping("/dbConfig/info")
    @ResponseBody
    public R<DsConfigEntity> dbConfigInfo(Long dsId) throws SQLException {
        DsConfigEntity vo = DsConfigDao.info(dsId);
        vo.setPassword(null);
        return R.ok(vo);
    }



    @PostMapping("/dbConfig/saveOrUpdate")
    @ResponseBody
    public R dbConfigSaveOrUpdate(@RequestBody DsConfigEntity dto) throws SQLException {
        DsConfigDao.saveOrUpdate(dto);
        return R.ok();
    }




    @GetMapping("/schedule/info")
    @ResponseBody
    public R<ScheduleEntity> scheduleInfo(Long sId) throws SQLException {
        return R.ok(ScheduleDao.info(sId));
    }


    @PostMapping("/schedule/saveOrUpdate")
    @ResponseBody
    public R scheduleSaveOrUpdate(@RequestBody ScheduleDTO dto) throws SQLException, ParseException {
        ScheduleService.update(dto);
        return R.ok();
    }


    @GetMapping("/taskRecord/list")
    @ResponseBody
    public R<List<TaskRecordEntity>> taskRecordList() throws SQLException {
        return R.ok(TaskRecordDao.list());
    }


    @PostMapping("/taskRecord/delete")
    @ResponseBody
    public R taskRecordList(Long rId) throws SQLException {
        TaskService.deleteBackup(rId);
        return R.ok();
    }


    @RequestMapping(value = "/taskRecord/dowloadData", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void dowloadData(Long rId,HttpServletResponse response) throws SQLException, IOException {
        TaskRecordEntity entity = TaskRecordDao.info(rId);
        if(entity==null){
            throw new RuntimeException("备份数据已经删除了");
        }
       String filename= FileNameUtil.getName(new File(entity.getDataFile()));
        String type = new MimetypesFileTypeMap().getContentType(filename);
        response.setHeader("Content-type",type);
        String hehe = new String(filename.getBytes("utf-8"), "iso-8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + hehe);
        FileInputStream in=new  FileInputStream(entity.getDataFile());
        IoUtil.copy(in, response.getOutputStream(), IoUtil.DEFAULT_BUFFER_SIZE);
        in.close();
    }


    @RequestMapping(value = "/taskRecord/dowloadLog", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void dowloadLog(Long rId,HttpServletResponse response) throws SQLException, IOException {
        TaskRecordEntity entity = TaskRecordDao.info(rId);
        if(entity==null){
            throw new RuntimeException("备份数据已经删除了");
        }
        String filename= FileNameUtil.getName(new File(entity.getLogFile()));
        String type = new MimetypesFileTypeMap().getContentType(filename);
        response.setHeader("Content-type",type);
        String hehe = new String(filename.getBytes("utf-8"), "iso-8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + hehe);
        FileInputStream in=new  FileInputStream(entity.getLogFile());
        IoUtil.copy(in, response.getOutputStream(), IoUtil.DEFAULT_BUFFER_SIZE);
        in.close();
    }




    @PostMapping("/dbReStore")
    @ResponseBody
    public R dbReStore(Long rId,Boolean backup){
        System.setProperty(CommonConstant.TASK_RUNNING_FALG,"Y");
        ThreadUtil.execAsync(new DBRestoreService(rId,backup));
        return R.ok("执行成功，请稍等片刻查看结果");
    }



}
