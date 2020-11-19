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

package com.mxsky.dbbackup.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import com.mxsky.dbbackup.constants.CommonConstant;
import com.mxsky.dbbackup.enums.SystemOSNameEnum;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/*
 * @Description: jar包工具类
 * @Author: Jevon.Li
 * @Date: 2020-11-14 11:54
 */
public class JarUtil {


    /**
     * 根据class从lib文件夹获取jar包路径
     * @param cls
     * @return
     */
    public static String  getJarPathFromLib(Class cls){
        ///E:/workspace/xxxxPoject/src/back-end/xxxPJC/db-backup/target/lib/xj-db-backup-1.0.0.jar
        URL url = cls.getProtectionDomain().getCodeSource().getLocation();
        String path = url.getFile();
        if (System.getProperty("os.name").contains("dows")) {
            if (path.indexOf("/") == 0) {
                path = path.substring(path.indexOf("/") + 1, path.length());
            }
        }
        return path;
    }


    /**
     * 获取运行环境，lib打包方式
     *
     * @param cls
     * @return
     */
    public static int getRunEnviron(Class cls) {
        String path = cls.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (path.contains(".jar")) {
            if (path.contains("!/")) {
                //jar模式运行,lib放在jar里面
                return CommonConstant.ON_JAR_LIB_INSIDE;
            } else {
                //jar模式运行,lib放在jar外面
                return CommonConstant.ON_JAR_LIB_OUTSIDE;
            }
        } else {
            //idea模式运行
            return CommonConstant.NOT_JAR;
        }
    }


    /**
     * 获取该类在运行jar包里的所依赖的jar包名称
     *
     * @param cls
     * @return
     */
    public static String getDPJarName(Class cls) {
        String path = cls.getProtectionDomain().getCodeSource().getLocation().getPath();
       if (path.contains("!/")) {
            String[] jars = path.split("!/");
            if (jars.length == 2) {
                path = jars[1];
                path = path.substring(path.lastIndexOf("/") + 1, path.length());
            } else {
                throw new RuntimeException("不符合 jar模式运行,lib放在jar里面");
            }
        }
        return path;
    }


    /**
     * 获取类所在的jar的系统路径
     *
     * @param cls
     * @return
     */
    public static String getJarFilePath(Class cls) {
        URL url = cls.getProtectionDomain().getCodeSource().getLocation();
        String path = url.getFile();
        if (path.contains("!/")) {
            path = path.substring(path.indexOf("/"), path.length());
        }
        if (path.indexOf("!") > -1) {
            path = path.substring(0, path.indexOf("!"));
        }
        if (System.getProperty("os.name").contains("dows")) {
            if (path.indexOf("/") == 0) {
                path = path.substring(path.indexOf("/") + 1, path.length());
            }
        }
        return path;
    }


    /**
     * 获取Jar包里面的文件流
     *
     * @param jarFilePath jar路径
     * @param name        xx结尾的文件名
     * @return
     * @throws Exception
     */
    public static InputStream getJarInputStream(String jarFilePath, String name) throws Exception {
        JarFile jarFile = new JarFile(new File(jarFilePath));
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (entryName.endsWith(name)) {
                return jarFile.getInputStream(jarFile.getEntry(entryName));
            }
        }
        throw new Exception(jarFilePath + "里找不到" + name + "文件");
    }



    public static InputStream getInputStream(Class cls,String fileName) {
        try {
            //获取运行目录
            String workSpace = System.getProperty("user.dir") + File.separator + CommonConstant.DB_SUPPORT_PATH + File.separator;
            File runPathFile = new File(workSpace);
            if (!runPathFile.exists()) {
                runPathFile.mkdirs();
            }
            //判断打包方式
            switch (JarUtil.getRunEnviron(cls)){
                case CommonConstant.NOT_JAR:
                    //idea模式
                    InputStream in = ResourceUtil.getStream(fileName);
                    return in;
                case CommonConstant.ON_JAR_LIB_OUTSIDE:
                    //lib依赖放在jar外面
                    //获取zip所在的jar的位置
                    String libJarFile=JarUtil.getJarPathFromLib(cls);
                    InputStream libJarInputSt = JarUtil.getJarInputStream(libJarFile, fileName);
                    return libJarInputSt;
                case CommonConstant.ON_JAR_LIB_INSIDE:
                    //lib依赖放在jar里面
                    //获取启动类所在的jar文件路径
                    String jarFilePath=JarUtil.getJarFilePath(cls);
                    //依赖的jar名称
                    String dependentJarName=JarUtil.getDPJarName(cls);
                    //准备复制出来的依赖的jar目录
                    String dependentJarFile=workSpace+dependentJarName;
                    InputStream inputSt = JarUtil.getJarInputStream(jarFilePath, dependentJarName);
                    FileUtils.copyInputStreamToFile(inputSt,new File(dependentJarFile));
                    inputSt.close();
                    //再从依赖的jar里解压zip文件
                    InputStream jarInputSt = JarUtil.getJarInputStream(dependentJarFile,fileName);
                    return jarInputSt;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    public static String getSystemOsName(){
        if (System.getProperty("os.name").contains("dows")) {
           return SystemOSNameEnum.windows.getCode();
        }
        return SystemOSNameEnum.linux.getCode();
    }









//    public static void main(String[] args) throws Exception {
//        String path="file:/E:/workspace/xxxxPoject/src/back-end/xxxPJC/db-backup/target/db-backup-1.0.0.jar!/BOOT-INF/lib/xj-db-backup-1.0.0.jar!/";
//        idea模式   /E:/workspace/xxxxPoject/src/back-end/xxxPJC/xj-db-backup/target/classes/
//        jar模式运行,lib放在jar外面 /E:/workspace/xxxxPoject/src/back-end/xxxPJC/db-backup/target/lib/xj-db-backup-1.0.0.jar
//        jar模式运行,lib放在jar里面  file:/E:/workspace/xxxxPoject/src/back-end/xxxPJC/db-backup/target/db-backup-1.0.0.jar!/BOOT-INF/lib/xj-db-backup-1.0.0.jar!/

//        System.out.println(getRunEnviron(JarUtil.class));
//        System.out.println(getJarFilePath(JarUtil.class));
//        System.out.println(getDPJarName(JarUtil.class));
//    }


}
