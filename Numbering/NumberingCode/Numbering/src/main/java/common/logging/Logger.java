package common.logging;

/**
 * log4j 기능에 대해 초기화를 한다.
 * 사용자 정의에 선언 되있을 경우(log_level, file_path, file_name등) 해당 값으로 세팅한다.
 *
 * 
 * @version log4j_1.2.8
 * @since 2022/11/13
 * @author Changhee Kim
 */

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

import common.Property;

public class Logger {
	
	public static final int OUTPUT_TARGET__BOTH = 1;

	public static final int OUTPUT_TARGET__CONSOLE_ONLY = 2;

	public static final int OUTPUT_TARGET__FILE_ONLY = 3;

	public static final String DEFAULT_LOGGER_NAME = "Numbering";

	public static final String DEFAULT_LOGGER_PATTERN = "[%d{yyyy-MM-dd HH:mm:ss}] [%-5p] %m%n";

	private static org.apache.log4j.Logger _logger;

	private static Appender _consoleAppender;

	private static Layout _layout = (Layout) new PatternLayout(DEFAULT_LOGGER_PATTERN);

	static {
		_consoleAppender = (Appender) new ConsoleAppender(_layout);
		_logger = org.apache.log4j.Logger.getLogger(DEFAULT_LOGGER_NAME);
		_logger.setLevel(Level.toLevel("DEBUG"));
		_logger.addAppender(_consoleAppender);
	}

	public static void setLevel(String level) {
		_logger.setLevel(Level.toLevel(level));
	}
	
	public static void initLogger() {
		Logger.info("<=================Log4j init=================>");
		String level = Property.getKEY__LOG4j_LOG_LEVEL();
		Logger.setLevel(level);
		Logger.info("Logger level set by [" + level + "]");
		String file_path = System.getProperty("catalina.home", "") + File.separator + Logger.DEFAULT_LOGGER_NAME + "_log";
		Logger.info("file_path = " + file_path);
		String logFile = file_path + File.separator + Property.getKEY__LOG4j_LOG_FILENAME() + ".log";
		Logger.initOutputTarget(OUTPUT_TARGET__BOTH, logFile);
		Logger.info("Logger output will be appended to the file [" + logFile + "]");
	}

	public static synchronized void initOutputTarget(int outputTarget, String fileName) {
		switch (outputTarget) {
		case OUTPUT_TARGET__BOTH:
			addFileAppender(fileName);
			break;
		case OUTPUT_TARGET__FILE_ONLY:
			addFileAppender(fileName);
			break;
		}
	}

	public static Layout getLayout() {
		return _layout;
	}

	public static ConsoleAppender getDefaultConsoleAppender() {
		return (ConsoleAppender) _consoleAppender;
	}

	public static void addAppender(Appender appender) {
		_logger.addAppender(appender);
	}

	private static void addFileAppender(String fileName) {
		Appender appender = _logger.getAppender(fileName);
		if (appender != null)
			return;
		File dir = new File(fileName.substring(0, fileName.lastIndexOf(File.separator)));
		if (!dir.exists())
			dir.mkdirs();
		try {
			DailyRollingFileAppender dailyRollingFileAppender = new DailyRollingFileAppender(_layout, fileName,"'.'yyyy-MM-dd");
			dailyRollingFileAppender.setName(fileName);
			_logger.addAppender((Appender) dailyRollingFileAppender);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private static String findClassName() {
		if (_logger.getLevel().toInt() == 10000)
			try {
				/**
				 * doPost메서드(NumberingServlet클래스)가 Logger클래스의 debug메서드를 호출
				 * 최종적으로 findClassName()을 호출
				 * 최종클래스가 0번 idx, debug메서드 호출이 1번 idx, doPost메서드 호출이 2번 idx로 들어간다.
				 */
				StackTraceElement element = (new Exception()).getStackTrace()[2];
				String className = element.getClassName();
				int i = className.lastIndexOf('.');
				return "[" + className.substring(i + 1) + "] ";
			} catch (NoSuchMethodError nsme) {
				return "";
			}
		return "";
	}

	public static void fatal(Object message) {
		_logger.fatal(message);
	}

	public static void fatal(Object message, Throwable t) {
		_logger.fatal(message, t);
	}

	public static void fatal(Throwable t) {
		_logger.fatal(t.getMessage(), t);
	}

	public static void error(Object message) {
		_logger.error(message);
	}

	public static void error(Object message, Throwable t) {
		_logger.error(message, t);
	}

	public static void error(Throwable t) {
		_logger.error(t.getMessage(), t);
	}

	public static void warn(Object message) {
		_logger.warn(message);
	}

	public static void warn(Object message, Throwable t) {
		_logger.warn(message, t);
	}

	public static void warn(Throwable t) {
		_logger.warn(t);
	}

	public static void info(Object message) {
		_logger.info(message);
	}

	public static void info(Object message, Throwable t) {
		_logger.info(message, t);
	}

	public static void info(Throwable t) {
		_logger.info(t);
	}

	public static void debug(Object message) {
		_logger.debug(findClassName() + message);
	}

	public static void debug(Object message, Throwable t) {
		_logger.debug(findClassName() + message, t);
	}

	public static void debug(Throwable t) {
		_logger.debug(findClassName(), t);
	}

	public static boolean isDebugEnabled() {
		return _logger.isDebugEnabled();
	}
}