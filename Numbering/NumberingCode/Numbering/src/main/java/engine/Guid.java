package engine;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import common.logging.Logger;

/**
 * GUID, Seq_ID ó�� Ŭ����
 * 
 * @version 1.00
 * @since 2022/11/13
 * @author Changhee Kim
 */

public class Guid {
	private static final DateFormat GUID_yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	private static final String PROCESSID_URL = "http://localhost:9090/processID/reply";

	private static final String PROCESSID_DATA = "getProcessID";

	private static final String SERVERNAME_URL = "http://localhost:9191/serverName/reply";

	private static final String SERVERNAME_DATA = "getServerName";

	public static String getGuid(HttpServletRequest req) throws IOException {
		// GUID Date���� 17�ڸ�
		Logger.info("get Guid");
		String GUIDDate = GUID_yyyyMMddHHmmssSSS.format(new Date(System.currentTimeMillis()));
		Logger.debug("GUID Date : [" + GUIDDate + "]");

		// subEngine ���ؽ�Ʈ ���� �ִ´�.
		String ContextPath = req.getContextPath();
		if (ContextPath.length() > 3) {
			ContextPath = ContextPath.substring(ContextPath.length() - 4);
		} else if(ContextPath.length() == 3) {
			ContextPath = "0" + ContextPath;
		} else if(ContextPath.length() == 2) {
			ContextPath = "00" + ContextPath;
		} else if(ContextPath.length() == 1) {
			ContextPath = "000" + ContextPath;
		} else {
			ContextPath = "0000";
		}
		Logger.debug("Context Path : [" + ContextPath + "]");

		// ���μ��� id�� ���Ѵ�
		String processID = doPostServer(PROCESSID_URL, PROCESSID_DATA);
		Logger.debug("process ID : [" + processID + "]");

		// �������� ���Ѵ�.
		String serverName = doPostServer(SERVERNAME_URL, SERVERNAME_DATA);
		Logger.debug("server name : [" + serverName + "]");

		return GUIDDate + ContextPath + processID + serverName;
	}

	public static String doPostServer(String URL, String DATA) throws IOException {
		Logger.debug("doPost " + DATA + " Server");

		// url��ü, ���� ��ü ����
		URL url = new URL(URL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// Post����
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);

		// ������ ������ ����
		DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
		outputStream.writeBytes(DATA);
		outputStream.flush();
		outputStream.close();

		// ���䵥���� ����
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuffer stringBuffer = new StringBuffer();
		String inputLine;

		while ((inputLine = bufferedReader.readLine()) != null) {
			stringBuffer.append(inputLine);
		}
		bufferedReader.close();

		String response = stringBuffer.toString();
		String hashCode = String.valueOf(response.hashCode());

		// ���� ���� �ؽ��ڵ�� ��ȯ �� ���� ���� Flag ���� �߰�
		// process id 4�ڸ��� ��ȯ
		if (DATA.equals(PROCESSID_DATA)) {
			Logger.debug("process ID : [" + response + "], hashCode : [" + hashCode + "]");
			if (hashCode.length() > 3) {
				hashCode = hashCode.substring(hashCode.length() - 4);
			}

			int iHashCode = Integer.parseInt(hashCode);
			hashCode = String.format("%04d", iHashCode);
			hashCode = "P" + hashCode;
		}
		// server name 3�ڸ��� ��ȯ
		else if (DATA.equals(SERVERNAME_DATA)) {
			Logger.debug("server name : [" + response + "], hashCode : [" + hashCode + "]");
			if (hashCode.length() > 2) {
				hashCode = hashCode.substring(hashCode.length() - 3);
			}

			int iHashCode = Integer.parseInt(hashCode);
			hashCode = String.format("%03d", iHashCode);
			hashCode = "S" + hashCode;
		}

		return hashCode;
	}
}
