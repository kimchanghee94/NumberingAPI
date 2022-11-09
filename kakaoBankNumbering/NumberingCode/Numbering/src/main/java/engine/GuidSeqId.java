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

import common.logging.Logger;

/**
 * GUID, Seq_ID ó�� Ŭ����
 * 
 * @version 1.00
 * @since 2022/11/13
 * @author Changhee Kim
 */

public class GuidSeqId {
	private static final DateFormat GUID_yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	private static final DateFormat SEQ_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	
	private static String curSEQDate = SEQ_yyyyMMdd.format(new Date(System.currentTimeMillis()));
	
	public static long SEQ_ID;
	
	private static final String PROCESSID_URL = "http://localhost:8383/processID/reply";
	
	private static final String PROCESSID_DATA = "getProcessID";
	
	private static final String SERVERNAME_URL = "http://localhost:8484/serverName/reply";
	
	private static final String SERVERNAME_DATA = "getServerName";
	
	public static String getGuid() throws IOException{
		//GUID Date���� 17�ڸ�
	    Logger.debug("get Guid");
		String GUIDDate = GUID_yyyyMMddHHmmssSSS.format(new Date(System.currentTimeMillis()));
		Logger.debug("GUID Date : [" + GUIDDate + "]");

		//���μ��� id�� ���Ѵ�
		String processID = doPostServer(PROCESSID_URL, PROCESSID_DATA);
		Logger.debug("process ID : [" +processID + "]");

		//�������� ���Ѵ�.
		String serverName = doPostServer(SERVERNAME_URL, SERVERNAME_DATA);
		Logger.debug("server name : [" + serverName + "]");

		return GUIDDate + processID + serverName;
	}
	
	public static String getSeqId() {
		Logger.debug("get Seq ID");
		String nextSEQDate = SEQ_yyyyMMdd.format(new Date(System.currentTimeMillis()));
		
		//Seq id ��¥ ������ �� �ʱ�ȭ ��ų ��		
		if(curSEQDate.compareTo(nextSEQDate) < 0	 || SEQ_ID == 9999999999L) {
			SEQ_ID = 1;
		}else {
			SEQ_ID++;
		}

		Logger.debug("Seq ID : [" + SEQ_ID + "]");
		return String.valueOf(SEQ_ID);
	}
	
	public static String doPostServer(String URL, String DATA) throws IOException {
		Logger.debug("doPost " + DATA + " Server");
		
	    //url��ü, ���� ��ü ����
		URL url = new URL(URL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		//Post����
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);

		//������ ������ ����
		DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
		outputStream.writeBytes(DATA);
		outputStream.flush();
		outputStream.close();

		//���䵥���� ����
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuffer stringBuffer = new StringBuffer();
		String inputLine;

		while ((inputLine = bufferedReader.readLine()) != null) {
			stringBuffer.append(inputLine);
		}
		bufferedReader.close();

		String response = stringBuffer.toString();
		String hashCode = String.valueOf(response.hashCode());
		
		//���� ���� �ؽ��ڵ�� ��ȯ �� ���� ���� Flag ���� �߰�
		//process id 6�ڸ��� ��ȯ
		if(DATA.equals(PROCESSID_DATA)) {
			Logger.debug("process ID : [" + response + "], hashCode : [" + hashCode + "]");
			if(hashCode.length() > 5) {
				hashCode = hashCode.substring(hashCode.length()-6);				
			}
			
			int iHashCode = Integer.parseInt(hashCode);
			hashCode = String.format("%06d", iHashCode);
			hashCode = "P" + hashCode;
		}
		//server name 5�ڸ��� ��ȯ
		else if(DATA.equals(SERVERNAME_DATA)) {
			Logger.debug("server name : [" + response + "], hashCode : [" + hashCode + "]");
			if(hashCode.length() > 4) {
				hashCode = hashCode.substring(hashCode.length()-5);				
			}
			
			int iHashCode = Integer.parseInt(hashCode);	
			hashCode = String.format("%05d", iHashCode);
			hashCode = "S" + hashCode;			
		}
		
		return hashCode;
	}
}
