package com.mxsky.dbbackup.service;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MySQLBackup
{
	/**
	 * 定义用于备份数据库的静态变量
	 */
	private static String user = "root";		//连接数据库的用户名
	private static String password = "root";	//连接数据库的密码
	private static String database;				//备份数据库的名字
	private static String host = "localhost";	//连接的主机名
	private static String port = "3306";		//数据库的接口
	private static String charsetName = "utf8"; //数据库的字符集，默认为utf8
	private static String mysqlPath = "";		//mysql的安装目录，配置环境变量的情况下可以不设置值
	
	private static String EXPORT_BASE_PATH = "/home/"+System.getProperty("user.name");	
	private static String BAKUP_FOLDER = "/backup/mysql/";	
	
	private static String exportPath = EXPORT_BASE_PATH+BAKUP_FOLDER;			//数据库备份的输出路径
	
	/**
	 * 程序中用到的其它变量
	 */
	private static String osName = System.getProperty("os.name");
	private static String fileName;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
	
	private static Logger logger = Logger.getLogger(MySQLBackup.class);
	
	/**
	 * 程序入口：采用ApplicationContext中获取的值进行备份
	 */
	public static void backupAndSave(String springResourcePath){
		try {
			initByApplication(springResourcePath);
			doBackUpAndSave();
		} catch (ParserConfigurationException e) {
			logger.error("MySQL数据库进行备份，XML解析器创建失败！");
		} catch (SAXException e) {
			logger.error("MySQL数据库进行备份，XML解析错误！");
		} catch (IOException e) {
			logger.error("MySQL数据库进行备份，XML文件不存在！");
		}
	}
	/**
	 * 程序入口重载，采用用户自定义的参数进行数据库备份
	 * @param user 			用户名(为null时采用root)
	 * @param password 		密码(为null时采用root)
	 * @param database 		数据库名,多个数据库时，用空格分开
	 * @param host 			主机名(为null是采用localhost)
	 * @param port 			端口号(为null时采用默认端口号3306)
	 * @param mysqlPath 	MySQL的安装路径(配置MySQL的环境变量时，参数可为null)
	 * @param exportPath 	输出备份的路径(为null时采用CommonDef定义的常量值)
	 */
	public static void backupAndSave(String user , String password , String database , String host , String port , String charsetName , String mysqlPath , String exportPath){
		initByCustomer(user, password, database, host, port, charsetName , mysqlPath, exportPath);
		doBackUpAndSave();
	}
	
	
	
	/**
	 * 默认初始化
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	private static void initByApplication(String springResourcePath) throws ParserConfigurationException, SAXException, IOException{
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		InputStream resourceAsStream = null;
		Document document = null;
		resourceAsStream = MySQLBackup.class.getClassLoader().getResourceAsStream(springResourcePath);
		document = documentBuilder.parse(resourceAsStream);
		 
		
		NodeList beanNodeList = document.getElementsByTagName("bean");

		Map<String , String> propertiesMap = new HashMap<String, String>();
		for (int i = 0; i < beanNodeList.getLength(); i++) {
			Node beanNode = beanNodeList.item(i);
			Element bean = (Element) beanNode;
			if("dataSource".equals(bean.getAttribute("id"))){
				NodeList nodeList = bean.getElementsByTagName("property");
				for (int j = 0; j < nodeList.getLength(); j++) {
					Element propertyNode = (Element) nodeList.item(j);
					String name = propertyNode.getAttribute("name");
					String value = propertyNode.getAttribute("value");
					propertiesMap.put(name, value);
				}
				break;
			}
		}
		
		user = propertiesMap.get("user");
		password = propertiesMap.get("password");
		String jdbcUrl = propertiesMap.get("jdbcUrl");
		
		//解析jdbcUrl中的 host port database  charsetName
		//jdbc:mysql://127.0.0.1:3306/xiaodai?useUnicode=true&amp;characterEncoding=UTF8&amp;zeroDateTimeBehavior=convertToNull
		//jdbc:mysql:///message
		String hostAndPort = jdbcUrl.substring(jdbcUrl.indexOf("//")+2,jdbcUrl.lastIndexOf("/"));
		if(hostAndPort.length()!=0&&hostAndPort.contains(":")){
			String connectHost = hostAndPort.substring(0,hostAndPort.lastIndexOf(":"));
			if(connectHost!=""){
				host = connectHost;
			}
			String connectPort = hostAndPort.substring(hostAndPort.lastIndexOf(":")+1);
			if(connectPort!=""){
				port = connectPort;
			}
		}
		
		int databaseLastIndex = -1;
		if((databaseLastIndex = jdbcUrl.indexOf("?"))!=-1){
			database = jdbcUrl.substring(jdbcUrl.lastIndexOf("/")+1,databaseLastIndex);
		}else{
			database = jdbcUrl.substring(jdbcUrl.lastIndexOf("/")+1);
		}
		
		int charsetNameFromIndex = -1;
		if((charsetNameFromIndex = jdbcUrl.indexOf("characterEncoding"))!=-1){
			charsetNameFromIndex = charsetNameFromIndex + "characterEncoding".length()+1;
			
			int charsetNameLastIndex = jdbcUrl.indexOf("&", charsetNameFromIndex);
			if(charsetNameLastIndex!=-1){
				charsetName = jdbcUrl.substring(charsetNameFromIndex,charsetNameLastIndex);
			}else if(jdbcUrl.substring(charsetNameFromIndex)!=""){
				charsetName = jdbcUrl.substring(charsetNameFromIndex);
			}
		}
	}
	/**
	 * 自定义初始化
	 * @param user
	 * @param password
	 * @param database
	 * @param host
	 * @param port
	 * @param mysqlPath
	 * @param exportPath
	 */
	private static void initByCustomer(String user , String password , String database , String host , String port , String charsetName , String mysqlPath , String exportPath){
		if(user!=null&&user.length()!=0)
			MySQLBackup.user = user;
		if(password!=null&&password.length()!=0)
			MySQLBackup.password = password;
		if(database!=null&&database.length()!=0)
			MySQLBackup.database = database;
		if(host!=null&&host.length()!=0)
			MySQLBackup.host = host;
		if(port!=null&&port.length()!=0)
			MySQLBackup.port = port;
		if(charsetName!=null&&charsetName.length()!=0)
			MySQLBackup.charsetName = charsetName;
		if(mysqlPath!=null&&mysqlPath.length()!=0)
			MySQLBackup.mysqlPath = mysqlPath + File.separator + "bin" + File.separator;
		if(exportPath!=null&&exportPath.length()!=0){
			MySQLBackup.exportPath = exportPath;
		}
	}
	/**
	 * 执行备份和保存
	 * @throws IOException 
	 */
	private static void doBackUpAndSave(){
		fileName = dateFormat.format(new Date())+".sql";
		File file = new java.io.File(exportPath);
		if(!file.exists()){
			file.mkdirs();
		}else{
			if(!file.isDirectory()){
				file.delete();
				file.mkdirs();
			}
		}
		File exportFile = new File(exportPath+fileName);
		if(exportFile.exists()){
			exportFile.delete();
		}
		try {
			exportFile.createNewFile();
		} catch (IOException e1) {
			logger.error("MySQL数据库进行备份，"+exportPath+fileName+"文件创建失败！");
		}
		/**
		 * 判断平台是因为在linux和window下的命令不同，window下存在mysqlPath时，不加cmd /c会出错
		 */
		String mysqldump = "";
		if(osName.startsWith("Windows")){
			mysqldump = "cmd /c \""+mysqlPath + "mysqldump\"";
		}else{
			mysqldump = mysqlPath + "mysqldump";
		}
		String command = new StringBuffer(mysqldump) 									//mysql安装路径  \"作用为：防止路径中包含空格,单在linux中不能出现"作用的命令名，否则将不承认是个命令
								.append(" -u").append(user)								//用户名
								.append(" -p").append(password)							//密码
								.append(" -h").append(host)								//主机
								.append(" -P").append(port)								//端口号
								.append(" --default-character-set=").append(charsetName)//字符集
								.append(" -B ").append(database)						//数据库名称
								.append(" -r").append(exportPath+fileName)				//导出路径
								.toString();
		logger.info("\nMySQL数据库进行备份："
				+ "\n\tuser:"+user
				+ "\n\tpassword:"+password
				+ "\n\thost:"+host
				+ "\n\tport:"+port
				+ "\n\tdatabase:"+database
				+ "\n\texportPath:"+exportPath);
		try {
			doBackup(command);
			doSave();
		} catch (IOException e) {
			logger.error("MySQL数据库进行备份，备份命令执行错误！"+e.getMessage());
		}
	}
	
	/**
	 * 执行命令行命令
	 * @param command
	 * @return
	 * @throws IOException 
	 */
	private static void doBackup(String command) throws IOException{
		logger.info("MySQL数据库进行备份，命令为："+command);
		Runtime runtime = Runtime.getRuntime();
		Process exec = runtime.exec(command);
		try {
			exec.waitFor();
		} catch (InterruptedException e) {
			logger.error("MySQL数据库进行备份，命令没有正常退出！");
		}
	}
	/**
	 * 将数据库输出的文件，保存为压缩文件，并将sql文件删除
	 */
	private static void doSave() throws IOException{
		
		File exportFile = new File(exportPath+fileName);
		FileInputStream inputStream = new FileInputStream(exportFile);
		String fileStr = exportPath+fileName.substring(0,fileName.lastIndexOf("."));
		
		if(osName.startsWith("Windows")){
			fileStr = fileStr + ".zip";
			logger.info("MySQL数据库进行备份，备份路径为："+fileStr);
			try {
				saveAsZip(inputStream,fileStr);
			} catch (Exception e) {
				logger.error("MySQL数据库进行备份，生成备份文件失败！");
			}
		}else{
			fileStr = fileStr + ".gz";
			logger.info("MySQL数据库进行备份，备份路径为："+fileStr);
			try {
				saveAsGZip(inputStream, fileStr);
			} catch (Exception e) {
				logger.error("MySQL数据库进行备份，生成备份文件失败！");
			}
		}
		inputStream.close();
		exportFile.delete();
	}
	/**
	 * 将输入流保存为Zip文件格式，包含sql文件
	 * @param inputStream
	 * @param fileStr
	 * @throws IOException
	 */
	private static void saveAsZip(FileInputStream inputStream , String fileStr) throws IOException{
		FileOutputStream fileOutputStream = new FileOutputStream(fileStr);
		ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
		byte[] b = new byte[1024];
		int length = 0;
		zipOutputStream.putNextEntry(new ZipEntry(fileName));
		while((length = inputStream.read(b))!=-1){
			zipOutputStream.write(b, 0, length);
		}
		zipOutputStream.closeEntry();
		zipOutputStream.finish();
		zipOutputStream.flush();
		zipOutputStream.close();
		
		fileOutputStream.close();
	}
	/**
	 * 将输入流保存为GZip文件
	 * @param inputStream
	 * @param fileStr
	 * @throws IOException
	 */
	private static void saveAsGZip(FileInputStream inputStream , String fileStr) throws IOException{
		FileOutputStream fileOutputStream = new FileOutputStream(fileStr);
		GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
		byte[] b = new byte[1024];
		int length = 0;
		while((length = inputStream.read(b))!=-1){
			gzipOutputStream.write(b, 0, length);
		}
		gzipOutputStream.finish();
		gzipOutputStream.flush();
		gzipOutputStream.close();
		
		fileOutputStream.close();
	}
}