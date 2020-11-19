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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.cron.CronUtil;
import com.mxsky.dbbackup.constants.CommonConstant;
import com.mxsky.dbbackup.dao.ScheduleDao;
import com.mxsky.dbbackup.dto.ScheduleDTO;
import com.mxsky.dbbackup.entity.ScheduleEntity;
import com.mxsky.dbbackup.enums.ScheduleStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.TriggerBuilder;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * @Description: 定时器服务
 * @Author: Jevon.Li
 * @Date: 2020-11-14 11:53
 */
public class ScheduleService {


    public static void init(){
        stopSchedule();
        try {
            //查询所有的定时任务
            List<ScheduleEntity> taskList = ScheduleDao.list();
            for(ScheduleEntity task:taskList){
                CronUtil.getScheduler().clear();
                System.setProperty(CommonConstant.SCHEDULE_CRON,task.getCron());
                String id = CronUtil.schedule(task.getsId()+"",task.getCron(),new DBBackupService());
                System.setProperty(CommonConstant.SCHEDULE_ID,id);
                CronUtil.setMatchSecond(true);
                CronUtil.start(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("定时任务设置失败！");
        }

    }


    public static boolean status(){
        return CronUtil.getScheduler().isStarted();
    }

    public static Map<String,Object> scheduleStatusEnum() throws SQLException {
        Map<String,Object> res=new HashMap<String, Object>();
        boolean status=status();

        if(status){
            res=ScheduleStatusEnum.getMap(ScheduleStatusEnum.ENABLE.getCode());
            String msg= (String) res.get("msg");
            Date date=getNextTriggerTime(System.getProperty(CommonConstant.SCHEDULE_CRON));
            msg=msg+"<br>下次执行时间："+DateUtil.formatDateTime(date);
            res.put("msg",msg);

        }else{
            res=ScheduleStatusEnum.getMap(ScheduleStatusEnum.DISABLE.getCode());
        }

        List<ScheduleEntity> dsList = ScheduleDao.list();
        if(dsList.size()==0){
            res=ScheduleStatusEnum.getMap(ScheduleStatusEnum.NOREADY.getCode());
        }
        String statusId=System.getProperty(CommonConstant.SCHEDULE_STATUS_ID);
        if(StringUtils.isNotEmpty(statusId)){
            int statusID=Integer.parseInt(statusId);
            res=ScheduleStatusEnum.getMap(statusID);
        }

        return res;
    }


    public static void stopSchedule(){
        if(CronUtil.getScheduler().isStarted()){
            CronUtil.stop();
            CronUtil.getScheduler().clear();
        }
    }


    public static void startSchedule(){
        init();
    }

    public static void updateStatus(){
        System.setProperty(CommonConstant.SCHEDULE_STATUS_ID, "");
    }


    public static void update(ScheduleDTO dto) throws SQLException, ParseException {
        if(StringUtils.isEmpty(dto.getCron())){
            throw new RuntimeException("cron表达式不能为空");
        }
        if(dto.getDsId()==null){
            throw new RuntimeException("dsId不能为空");
        }
        ScheduleEntity entity=new ScheduleEntity();
        BeanUtil.copyProperties(dto,entity);
        ScheduleDao.saveOrUpdate(entity);
        init();
    }


    //获取下次执行时间（getFireTimeAfter，也可以下下次...）
    public static Date getNextTriggerTime(String cron){
        if(!CronExpression.isValidExpression(cron)){
            throw new RuntimeException("cron表达式不正确");
        }
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("Caclulate Date").withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        Date time0 = trigger.getStartTime();
        Date time1 = trigger.getFireTimeAfter(time0);
        return time1;
    }



}
