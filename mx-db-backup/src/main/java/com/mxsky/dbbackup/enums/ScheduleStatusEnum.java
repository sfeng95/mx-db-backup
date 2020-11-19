
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

package com.mxsky.dbbackup.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/*
 * @Description: 运行状态枚举
 * @Author: Jevon.Li
 * @Date: 2020-11-14 11:50
 */
@Getter
public enum ScheduleStatusEnum {


    NOREADY(0, "请设置数据源，设置定时任务"),


    ENABLE(1, "已激活"),


    DISABLE(2, "定时器已暂停"),


    RUNNING(3, "备份任务正在进行中..."),


    SUCCESS(4, "备份任务执行成功，请查看备份文件列表"),


    FAIL(5, "备份任务执行失败，请查看备份文件列表"),

    DO_RESTORE(6, "数据还原正在进行中..."),

    RESTORE_SUCCESS(7, "数据还原执行成功"),

    RESTORE_FAIL(8, "数据还原执行失败");


    private final Integer code;

    private final String message;

    ScheduleStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Map<String, Object> getMap(int status) {
        Map<String,Object> res=new HashMap<String, Object>();
        for (ScheduleStatusEnum i : ScheduleStatusEnum.values()) {
            if (i.getCode() == status) {
                res.put("code",i.getCode());
                res.put("msg", i.getMessage());
                return res;
            }
        }
        return null;
    }

}
