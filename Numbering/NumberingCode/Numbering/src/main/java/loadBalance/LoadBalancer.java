package loadBalance;

/**
 * 채번 서버구동
 * RR 로드밸런싱을 통해 각 서브엔진 서버로 데이터 전달 
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
		
		//content-type 구분
		String reqContentType = request.getContentType();
		Logger.debug("request ContentType : " + reqContentType);
		
		//req데이터 문자열로 출력
		int contentLength = request.getContentLength();
		byte[] recvByte = new byte[contentLength];
		request.getInputStream().read(recvByte);
		String recvStr = new String(recvByte);
		Logger.debug("Receive Data : " + recvStr);

		//타임아웃 발생 시 다른 서브 서버로 태운다.
		int insureNextSub = Load_Balance_Idx%Server_Num;
		
		//Seq ID값 확인 용도의 경우 subServer를 탈 필요가 없어진다.
		String responseStr = chkSeqId(recvStr);
		
		//로드밸런싱 Round-Robin형태로 구현
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
			
			//Round Robin 로직
			Load_Balance_Idx++;
			if(Load_Balance_Idx == Server_Num) {
				Load_Balance_Idx = 0;
			}
			
			//타임아웃 발생한 거래에 대해 sub 서버를 바꾼다.
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
			
			//받아온 guid값 뒤에 seqId를 붙여준다.
			responseStr = addSeqId(responseStr, reqContentType);
		}
		
		Logger.debug("Response Document : [" + responseStr + "]");
		
		OutputStream os = response.getOutputStream();
		response.setContentType(reqContentType);
		byte[] responseByte = responseStr.getBytes("EUC-KR");
		os.write(responseByte);
		os.flush();
	}
	
	//sub Server로 guid값을 받아오기 위해 http do post 송신
	public String doPostSubServer(String URL, String ContentType, String DATA) throws IOException {		
	    //url객체, 연결 객체 생성
		int TIMEOUT_VALUE = Property.getKEY__RESP_TIMEOUT_SEC(); //시스템 속성으로 설정
		Logger.debug("Time out set : " + TIMEOUT_VALUE/1000 + "s");
		
		URL url = new URL(URL);
		String response = "";
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		try {
			//Post설정
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", ContentType);
			connection.setDoOutput(true);
						
			//타임아웃 제한을 걸어 시간 초과시 다음 sub서버로 보내도록 한다.
			connection.setConnectTimeout(TIMEOUT_VALUE);
			connection.setReadTimeout(TIMEOUT_VALUE);

			//전송할 데이터 설정
			DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(DATA);
			outputStream.flush();
			outputStream.close();

			//응답데이터 세팅
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
	
	//받아온 guid값 뒤에 seqId를 붙여준다.
	public String addSeqId (String responseStr, String contentType) {
		Logger.info("add seq id");
		String type = contentType.substring(contentType.indexOf('/') + 1);
		
		//Stream타입에 대한 처리
		if(type.equalsIgnoreCase("plain") && responseStr.length() != 0) {
			responseStr = responseStr + getSeqId(false);
		}
		//Json 타입에 대한 처리
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
	
	//seqId 값을 반환한다.
	public String getSeqId(boolean chkFlag) {
		String nextSEQDate = SEQ_yyyyMMdd.format(new Date(System.currentTimeMillis()));

		// seq id 확인 용일 경우 일자가 바뀌었을 땐 0번 seq번호를 알려준다.
		if(chkFlag == true) {
			if (curSEQDate.compareTo(nextSEQDate) < 0) {
				Seq_ID = 0;
			}
		}
		//증번 seq no 로직
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
	
	//Seq ID값을 확인한다.
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