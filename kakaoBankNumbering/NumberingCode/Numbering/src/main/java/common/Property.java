package common;

import java.io.File;

/**
 * CommonCache.properties를 통해 사용자가 시스템 속성을 넣는다.
 * 사용자가 설정한 속성에 대해 기능 동작을 할 수 있도록 한다.
 *  
 * @version 1.00
 * @since 2022/11/13
 * @author Changhee Kim
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import common.logging.Logger;

public class Property {
	public static Properties prop = new Properties();
	
	public static final String PROPERTIES_PATH = System.getProperty("catalina.home","") + File.separator + "CommonCache.properties";
	
	public static final String KEY__LOG4j_LOG_LEVEL = "key_log4j.level";

	public static final String KEY__LOG4j_LOG_FILENAME = "key_log4j.log_file_name";


	static {
		try {
			prop.load(new FileInputStream(PROPERTIES_PATH));
		} catch (IOException e) {
			Logger.error(PROPERTIES_PATH + "file not found");
		}
	}

	public static String getKEY__LOG4j_LOG_LEVEL() {
		return prop.getProperty(KEY__LOG4j_LOG_LEVEL, "DEBUG");
	}
	
	public static String getKEY__LOG4j_LOG_FILENAME() {
		return prop.getProperty(KEY__LOG4j_LOG_FILENAME, Logger.DEFAULT_LOGGER_NAME);
	}
}
