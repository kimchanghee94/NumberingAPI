package common;

/**
 * CommonCache.properties를 통해 사용자가 시스템 속성을 넣는다.
 * 사용자가 설정한 속성에 대해 기능 동작을 할 수 있도록 한다.
 *  
 * @version 1.00
 * @since 2022/11/13
 * @author Changhee Kim
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import common.logging.Logger;

public class Property {
	public static Properties prop = new Properties();
	
	//CommonCache.properties파일 경로
	public static final String PROPERTIES_PATH = System.getProperty("catalina.home","") + File.separator + "CommonCache.properties";
	
	//로그레벨 설정
	public static final String KEY__LOG4j_LOG_LEVEL = "key_log4j.level";

	//로그 파일명 설정
	public static final String KEY__LOG4j_LOG_FILENAME = "key_log4j.log_file_name";

	//서브엔진 호출 타임아웃 시간 설정
	public static final String KEY__RESP_TIMEOUT_SEC = "key_resp.timeout.sec";

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
	
	public static int getKEY__RESP_TIMEOUT_SEC() {
		return Integer.parseInt(prop.getProperty(KEY__RESP_TIMEOUT_SEC, "10")) * 1000;
	}
}
