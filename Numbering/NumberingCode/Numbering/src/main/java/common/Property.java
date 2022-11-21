package common;

/**
 * CommonCache.properties�� ���� ����ڰ� �ý��� �Ӽ��� �ִ´�.
 * ����ڰ� ������ �Ӽ��� ���� ��� ������ �� �� �ֵ��� �Ѵ�.
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
	
	//CommonCache.properties���� ���
	public static final String PROPERTIES_PATH = System.getProperty("catalina.home","") + File.separator + "CommonCache.properties";
	
	//�α׷��� ����
	public static final String KEY__LOG4j_LOG_LEVEL = "key_log4j.level";

	//�α� ���ϸ� ����
	public static final String KEY__LOG4j_LOG_FILENAME = "key_log4j.log_file_name";

	//���꿣�� ȣ�� Ÿ�Ӿƿ� �ð� ����
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
