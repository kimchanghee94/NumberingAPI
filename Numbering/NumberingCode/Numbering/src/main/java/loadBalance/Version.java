package loadBalance;

/**
 * loadBalance 모듈에 대한 version 출력 클래스
 * 
 * @version 1.00
 * @since 2022/11/13
 * @author Changhee Kim
 */

public class Version {
	public final static String VERSION_NUMBER = "1";

	public final static String VERSION_SUB_NUMBER = "00";

	public final static String NAME = "loadB";

	public final static String BUILD_ID = "0001";

	public final static String BUILD_DATE = "2022.11.13";

	public final static String VERSION = "Numbering_" + VERSION_NUMBER + "." + VERSION_SUB_NUMBER + "-" + NAME + "[" + BUILD_ID + "](" + BUILD_DATE + ") "
			+ VERSION_SUB_NUMBER;

	public final static String REQUIRES = "";

	public final static String CHANGE = "채번 API 서버 구축";

	public final static String FULL_NUMBER = "[" + BUILD_ID + "](" + BUILD_DATE + ")";

	public static void main(String[] args) {
		System.out.println(VERSION + "\nrequires : " + REQUIRES);	
	}
}