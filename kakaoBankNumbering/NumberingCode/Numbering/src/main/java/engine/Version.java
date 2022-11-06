package engine;

/**
 * numbering engine 모듈에 대한 version 출력 클래스
 * 
 * @version 1.00
 * @since 2022/11/13
 * @author Changhee Kim
 */

public class Version {
	private final static String VERSION_NUMBER = "1";

	private final static String VERSION_SUB_NUMBER = "00";

	private final static String NAME = "engine";

	private final static String BUILD_ID = "0001";

	private final static String BUILD_DATE = "2022.11.13";

	private final static String VERSION = "Numbering_" + VERSION_NUMBER + "." + VERSION_SUB_NUMBER + "-" + NAME + "[" + BUILD_ID + "](" + BUILD_DATE + ") "
			+ VERSION_SUB_NUMBER;

	private final static String REQUIRES = "";

	private final static String CHANGE = "채번 API 서버 구축";

	private final static String FULL_NUMBER = "[" + BUILD_ID + "](" + BUILD_DATE + ")";

	public static String getVersion() {
		return VERSION + "\nrequires : " + REQUIRES;
	}
}