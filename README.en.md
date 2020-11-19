# mx-db-backup

####Introduction
Jupiter database backup and restore assistant
Only need to introduce a POM dependency can realize the function of database regular backup and restore in the program. It is simple, practical, and can greatly reduce the workload of development and operation and maintenance personnel

####Software architecture
Spring boot is used vue.js  mysqldump rman
####Installation tutorial
1. Import the downloaded source code or compiled jar dependency into the local Maven repository
2. Add a dependency in the POM file of the program
```xml
<!-- introduces muxing database backup and restore assistant -->
<dependency>
<groupId> com.mx -sky</groupId>
<artifactId>mx-db-backup</artifactId>
<version>1.0.0</version>
</dependency>
```
3. Edit the configuration file application.yml
```yaml
server:
port: 7080
spring:
H2:
console:
enabled: true
#Set up Jupiter backup assistant
mxdbBackup:
Enable login: true # enable login authentication
Password: 123456 #login password
```
4. Add @ enabledbbackup annotation to the startup class
```java
@SpringBootApplication
@EnableDBBackup  //use this annotation in the startup class to enable the database backup and restore assistant function
public class BackupApplication  extends SpringBootServletInitializer {
    public static void main(String[] args) throws Exception {
        System.out.println("please visit address： http://localhost:7080/mx-ui/index.html");
     SpringApplication.run(BackupApplication.class, args);
    }
}
```
####Instructions for use
Open address in browser: http://ip : Port: Project path / MX UI/ index.html

For example: http://localhost :7080/mx-ui/ index.html

####QQ discussion group
<img  src="https://gitee.com/mx-sky/mx-db-backup/raw/master/images/mx-db-backup.jpg"  width="300px" >

####Function screenshot
<img  src="https://gitee.com/mx-sky/mx-db-backup/raw/master/images/1.png"  width="100%" >
<img  src="https://gitee.com/mx-sky/mx-db-backup/raw/master/images/2.jpg"  width="100%" >
<img  src="https://gitee.com/mx-sky/mx-db-backup/raw/master/images/3.jpg"  width="100%" >
<img  src="https://gitee.com/mx-sky/mx-db-backup/raw/master/images/4.jpg"  width="100%" >
<img  src="https://gitee.com/mx-sky/mx-db-backup/raw/master/images/5.jpg"  width="100%" >
<img  src="https://gitee.com/mx-sky/mx-db-backup/raw/master/images/6.jpg"  width="100%" >

####Participation contribution

1. Fork warehouse
2. New feat_ XXX branch
3. Submit code
4. Create a new pull request

####Open source agreement and Disclaimer

1. This project can be used for commercial use without authorization
2. The second open source of this project needs the author's approval
3. Please keep the source code and related description files of project source, author statement, etc.
4. When distributing the source code, please indicate the source of the software
5. When modifying package name, module name and project code, please indicate the software source
6. Backup the data before using this project. Please test the program carefully and strictly. If the data is lost due to the use of this program, all responsibilities are irrelevant to me

####Donation
<img  align="left" src="https://gitee.com/mx-sky/file-store/raw/master/images/payment/wx.png" height="462" width="297" >
<br/>
<img align="left" src="https://gitee.com/mx-sky/file-store/raw/master/images/payment/zfb.png" height="462" width="297" >

