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

package com.mxsky.dbbackup.dao;

import com.mxsky.dbbackup.entity.DsConfigEntity;
import com.mxsky.dbbackup.service.H2DataBaseService;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.List;

/*
 * @Description: 数据源Dao
 * @Author: Jevon.Li
 * @Date: 2020-11-14 11:48
 */
public class DsConfigDao {



    public static List<DsConfigEntity> list() throws SQLException {
        String sql="SELECT * FROM `db_config` ";
        QueryRunner qr = new QueryRunner(H2DataBaseService.getDataSource());
        return qr.query(sql, new BeanListHandler<DsConfigEntity>(DsConfigEntity.class));
    }




    public static DsConfigEntity info(Long dsId) throws SQLException {
        if(dsId==null){
            throw new RuntimeException("dsId不能为空");
        }
        String sql="SELECT * FROM `db_config` WHERE dsId=?";
        QueryRunner qr = new QueryRunner(H2DataBaseService.getDataSource());
        DsConfigEntity vo=qr.query(sql, new BeanHandler<DsConfigEntity>(DsConfigEntity.class),dsId);
        return vo;
    }





    public static void saveOrUpdate(DsConfigEntity dto) throws SQLException {
        if(StringUtils.isEmpty(dto.getAdress())){
            throw new RuntimeException("数据库adress不能为空");
        }
        if(StringUtils.isEmpty(dto.getPort())){
            throw new RuntimeException("数据库port不能为空");
        }
        if(StringUtils.isEmpty(dto.getDbType())){
            throw new RuntimeException("数据库dbType不能为空");
        }
        if(StringUtils.isEmpty(dto.getUsername())){
            throw new RuntimeException("数据库username不能为空");
        }
        DsConfigEntity entity = info(dto.getDsId());
        if(entity!=null){
            if(StringUtils.isEmpty(dto.getPassword())){
                dto.setPassword(entity.getPassword());
            }
            String sql="UPDATE db_config set dbType=?,adress=?,port=?,username=?,password=?,database=? WHERE dsId=?";
            QueryRunner qr = new QueryRunner(H2DataBaseService.getDataSource());
            qr.update(sql,dto.getDbType(),dto.getAdress(),dto.getPort(),dto.getUsername(),dto.getPassword(),dto.getDatabase(),dto.getDsId());
        }else{
            if(StringUtils.isEmpty(dto.getPassword())){
                throw new RuntimeException("数据库password不能为空");
            }
            dto.setName("默认数据库");
            String sql="INSERT INTO `db_config` (`dsId`, `name`, `dbType`, `adress`, `port`,  `username`,`password`, `database`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            QueryRunner qr = new QueryRunner(H2DataBaseService.getDataSource());
            qr.update(sql,dto.getDsId(),dto.getName(),dto.getDbType(),dto.getAdress(),dto.getPort(),dto.getUsername(),dto.getPassword(),dto.getDatabase());
        }
    }

}
