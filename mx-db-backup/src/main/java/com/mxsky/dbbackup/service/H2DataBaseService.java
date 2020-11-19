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

import cn.hutool.core.io.IoUtil;
import com.mxsky.dbbackup.constants.H2DataBaseConstant;
import com.mxsky.dbbackup.utils.JarUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.SQLException;


/*
 * @Description: H2DB 服务
 * @Author: Jevon.Li
 * @Date: 2020-11-14 11:53
 */
public class H2DataBaseService {

    private static final Logger log = LoggerFactory.getLogger(H2DataBaseService.class);

    public static  void init() throws Exception {
        DataSource ds=getDataSource();
        if(isFristStart(ds)){
            createDatabase(ds);
        }
    }


    public static JdbcDataSource getDataSource(){
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(H2DataBaseConstant.URL);
        ds.setUser(H2DataBaseConstant.USERNAME);
        ds.setPassword(H2DataBaseConstant.PASSWORD);
        return ds;
    }


    public static void createDatabase(DataSource ds) throws Exception {
        InputStream in = JarUtil.getInputStream(H2DataBaseService.class, "db/V1.0__init.sql");
        String sqltext =IoUtil.read(in,"UTF-8");
        String[] sqls = sqltext.split(";");
        QueryRunner run = new QueryRunner(ds);
        for (String sql : sqls) {
            run.update(sql);
        }
    }

    /**
     * 是否第一次启动使用
     * @param ds
     * @return
     * @throws Exception
     */
    public static boolean isFristStart(DataSource ds) throws Exception {
        try {
            String sql="SELECT * FROM DB_TYPE DB_TYPE";
            QueryRunner qr = new QueryRunner(ds);
            qr.query(sql, new ArrayHandler());
        } catch (SQLException e) {
            return true;
        }
        return false;
    }

}
