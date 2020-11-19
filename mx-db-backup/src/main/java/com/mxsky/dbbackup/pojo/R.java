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

package com.mxsky.dbbackup.pojo;

import com.fasterxml.jackson.databind.JsonSerializer.None;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/*
 * @Description: 结果包装类
 * @Author: Jevon.Li
 * @Date: 2020-11-14 11:52
 */
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    @JsonSerialize(
        nullsUsing = None.class
    )
    private T data;
    private String msg;

    private R() {
    }

    private R(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private R(int code, String msg, T data) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static <T> R ok() {
        return new R(200, "ok");
    }

    public static <T> R<T> ok(T data) {
        return new R(200, "ok", data);
    }

    public static <T> R<T> ok(int code, String msg, T data) {
        return new R(code, msg, data);
    }

    public static <T> R<T> fail() {
        return new R(500, "error");
    }



    public static <T> R<T> fail(String msg) {
        return new R(500, msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return new R(code, msg);
    }

    public static <T> R<T> fail(int code, String msg, T data) {
        return new R(code, msg, data);
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
}
