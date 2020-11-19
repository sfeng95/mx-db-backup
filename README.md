# mx-db-backup

#### 介绍
木星数据库备份还原助手 
只需要引入一个pom依赖就可以在程序中实现数据库定时备份还原功能， 简单，方面，实用，大大降低开发和运维人员的工作量

#### 软件架构
使用了springBoot vue.js mysqldump rman


#### 安装教程

1.从下载原代码或者编译后的jar依赖导入到本地maven仓库
2.在程序的pom文件新增一个依赖
```xml
        <!--引入 沐星数据库备份还原助手-->
        <dependency>
            <groupId>com.mx-sky</groupId>
            <artifactId>mx-db-backup</artifactId>
            <version>1.0.0</version> 
        </dependency>
```
3.编辑配置文件application.yml

```
server:
    port: 7080
    
spring:
  h2:
    console:
      enabled: true

#设置木星备份助手     
mxdbBackup:
  enableLogin: true #开启登录验证
  password: 123456 #登录密码
```

4.在启动类加入@EnableDBBackup注解
```java
@SpringBootApplication
@EnableDBBackup  //在启动类使用该注解 开启数据库备份还原助手 功能
public class BackupApplication  extends SpringBootServletInitializer {
    public static void main(String[] args) throws Exception {
        System.out.println("请访问地址： http://localhost:7080/mx-ui/index.html");
     SpringApplication.run(BackupApplication.class, args);
    }
}
```

#### 使用说明

在浏览器打开地址：http://ip:端口:项目路径/mx-ui/index.html

例如： http://localhost:7080/mx-ui/index.html

#### QQ讨论群
<img  src="https://gitee.com/mx-sky/mx-db-backup/raw/master/images/mx-db-backup.jpg"  width="300px" >

#### 功能截图

<img  src="https://gitee.com/mx-sky/mx-db-backup/raw/master/images/1.png"  width="100%" >
<img  src="https://gitee.com/mx-sky/mx-db-backup/raw/master/images/2.jpg"  width="100%" >
<img  src="https://gitee.com/mx-sky/mx-db-backup/raw/master/images/3.jpg"  width="100%" >
<img  src="https://gitee.com/mx-sky/mx-db-backup/raw/master/images/4.jpg"  width="100%" >
<img  src="https://gitee.com/mx-sky/mx-db-backup/raw/master/images/5.jpg"  width="100%" >
<img  src="https://gitee.com/mx-sky/mx-db-backup/raw/master/images/6.jpg"  width="100%" >

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request



#### 开源协议和免责声明

1. 这个项目可以商业使用，商业不需要授权
2. 这个项目二次开源需要作者本人同意
3. 请保留源码和相关描述文件的项目出处，作者声明等。
4. 分发源码时候，请注明软件出处
5. 在修改包名，模块名称，项目代码等时，请注明软件出处
6. 使用本项目前先备份好数据，请认真严格测试程序，如果使用本程序导致数据丢失，一切责任与本人无关


#### 捐赠
<img  align="left" src="https://gitee.com/mx-sky/file-store/raw/master/images/payment/wx.png" height="462" width="297" >
<br/>
<img align="left" src="https://gitee.com/mx-sky/file-store/raw/master/images/payment/zfb.png" height="462" width="297" >




