package loadBalance;

/**
 * ä�� ��������
 * RR �ε�뷱���� ���� �� ���꿣�� ������ ������ ���� 
 * 
 * @version 1.00
 * @since 2022/11/13
 * @author Changhee Kim
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import common.Property;
import common.logging.Logger;

@WebServlet("/LoadBalancer")
public class LoadBalancer extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static int Load_Balance_Idx = 0;
	private static int Server_Num = 3;
	
	private static long Seq_ID = 0L;
	private static final DateFormat SEQ_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	private static String curSEQDate = SEQ_yyyyMMdd.format(new Date(System.currentTimeMillis()));
	
	private static final String NUMBERING_ENGINE_SUB1_URL = "http://localhost:7171/numberingEngineSub1/reply";
	private static final String NUMBERING_ENGINE_SUB2_URL = "http://localhost:7272/numberingEngineSub2/reply";
	private static final String NUMBERING_ENGINE_SUB3_URL = "http://localhost:7373/numberingEngineSub3/reply";
	
    public LoadBalancer() {
    	Logger.initLogger();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logger.info("<=================LoadBalancer Server Started=================>");
		Logger.debug("********************" + Thread.currentThread().getName() + "********************");
		
		//content-type ����
		String reqContentType = request.getContentType();
		Logger.debug("request ContentType : " + reqContentType);
		
		//req������ ���ڿ��� ���
		int contentLength = request.getContentLength();
		byte[] recvByte = new byte[contentLength];
		request.getInputStream().read(recvByte);
		String recvStr = new String(recvByte);
		Logger.debug("Receive Data : " + recvStr);

		//Ÿ�Ӿƿ� �߻� �� �ٸ� ���� ������ �¿��.
		int insureNextSub = Load_Balance_Idx%Server_Num;
		
		//Seq ID�� Ȯ�� �뵵�� ��� subServer�� Ż �ʿ䰡 ��������.
		String responseStr = chkSeqId(recvStr);
		
		//�ε�뷱�� Round-Robin���·� ����
		if(responseStr.length() == 0) {
			if(Load_Balance_Idx%Server_Num == 0) {
				Logger.info("<=================SUB1 Server On=================>");
				responseStr = doPostSubServer(NUMBERING_ENGINE_SUB1_URL, reqContentType, recvStr);			
			} else if(Load_Balance_Idx%Server_Num == 1) {
				Logger.info("<=================SUB2 Server On=================>");
				responseStr = doPostSubServer(NUMBERING_ENGINE_SUB2_URL, reqContentType, recvStr);			
			} else if(Load_Balance_Idx%Server_Num == 2) {
				Logger.info("<=================SUB3 Server On=================>");
				responseStr = doPostSubServer(NUMBERING_ENGINE_SUB3_URL, reqContentType, recvStr);			
			}
			
			//Round Robin ����
			Load_Balance_Idx++;
			if(Load_Balance_Idx == Server_Num) {
				Load_Balance_Idx = 0;
			}
			
			//Ÿ�Ӿƿ� �߻��� �ŷ��� ���� sub ������ �ٲ۴�.
			while(responseStr.equals("error")) {
				insureNextSub = (++insureNextSub)%Server_Num;
				Logger.info("<=================doPost Next SUB Server=================>");
				if(insureNextSub == 0) {
					Logger.info("<=================SUB1 Server On=================>");
					responseStr = doPostSubServer(NUMBERING_ENGINE_SUB1_URL, reqContentType, recvStr);			
				} else if(insureNextSub == 1) {
					Logger.info("<=================SUB2 Server On=================>");
					responseStr = doPostSubServer(NUMBERING_ENGINE_SUB2_URL, reqContentType, recvStr);			
				} else if(insureNextSub == 2) {
					Logger.info("<=================SUB3 Server On=================>");
					responseStr = doPostSubServer(NUMBERING_ENGINE_SUB3_URL, reqContentType, recvStr);			
				}
			}
			
			//�޾ƿ� guid�� �ڿ� seqId�� �ٿ��ش�.
			responseStr = addSeqId(responseStr, reqContentType);
		}
		
		Logger.debug("Response Document : [" + responseStr + "]");
		
		OutputStream os = response.getOutputStream();
		response.setContentType(reqContentType);
		byte[] responseByte = responseStr.getBytes("EUC-KR");
		os.write(responseByte);
		os.flush();
	}
	
	//sub Server�� guid���� �޾ƿ��� ���� http do post �۽�
	public String doPostSubServer(String URL, String ContentType, String DATA) throws IOException {		
	    //url��ü, ���� ��ü ����
		int TIMEOUT_VALUE = Property.getKEY__RESP_TIMEOUT_SEC(); //�ý��� �Ӽ����� ����
		Logger.debug("Time out set : " + TIMEOUT_VALUE/1000 + "s");
		
		URL url = new URL(URL);
		String response = "";
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		try {
			//Post����
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", ContentType);
			connection.setDoOutput(true);
						
			//Ÿ�Ӿƿ� ������ �ɾ� �ð� �ʰ��� ���� sub������ �������� �Ѵ�.
			connection.setConnectTimeout(TIMEOUT_VALUE);
			connection.setReadTimeout(TIMEOUT_VALUE);

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
			response = stringBuffer.toString();
		}catch(SocketTimeoutException ste) {
			Logger.error(ste);
			response = "error";
		} finally {
			connection.disconnect();
			return response;
		}

	}
	
	//�޾ƿ� guid�� �ڿ� seqId�� �ٿ��ش�.
	public String addSeqId (String responseStr, String contentType) {
		Logger.info("add seq id");
		String type = contentType.substring(contentType.indexOf('/') + 1);
		
		//StreamŸ�Կ� ���� ó��
		if(type.equalsIgnoreCase("plain") && responseStr.length() != 0) {
			responseStr = responseStr + getSeqId(false);
		}
		//Json Ÿ�Կ� ���� ó��
		else if(type.equalsIgnoreCase("json") && responseStr.length() != 0) {
			try {
				JSONObject jsonObject = (JSONObject) new JSONParser().parse(responseStr);
				jsonObject.replace("seqid", getSeqId(false));
				responseStr = jsonObject.toString();
			} catch(ParseException pe) {
				Logger.error(pe);
			}
		}
		return responseStr;
	}
	
	//seqId ���� ��ȯ�Ѵ�.
	public String getSeqId(boolean chkFlag) {
		String nextSEQDate = SEQ_yyyyMMdd.format(new Date(System.currentTimeMillis()));

		// seq id Ȯ�� ���� ��� ���ڰ� �ٲ���� �� 0�� seq��ȣ�� �˷��ش�.
		if(chkFlag == true) {
			if (curSEQDate.compareTo(nextSEQDate) < 0) {
				Seq_ID = 0;
			}
		}
		//���� seq no ����
		else {
			if (curSEQDate.compareTo(nextSEQDate) < 0 ) {
				Seq_ID = 1;
				curSEQDate = nextSEQDate;
			} else if(Seq_ID == 9999999999L) {
				Seq_ID = 1;
			}
			else {
				Seq_ID++;
			}
		}
		
		Logger.debug("Seq ID : [" + Seq_ID + "]");
		return String.valueOf(Seq_ID);
	}
	
	//Seq ID���� Ȯ���Ѵ�.
	public String chkSeqId(String recvStr) {
		Logger.info("check seq id");
		
		if(recvStr.equalsIgnoreCase("seqid")) {
			return getSeqId(true);
		}else if(recvStr.equalsIgnoreCase("seqid&")) {
			return "seqid=" + getSeqId(true);
		}else if(recvStr.equalsIgnoreCase("{\"seqid\":\"\"}")){
			return "{\"seqid\":\"" + getSeqId(true) + "\"}";
		}
		
		return "";
	}
}