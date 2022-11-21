package engine.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import common.logging.Logger;
import engine.Guid;

/**
 * Json타입에 대한 서비스 처리
 * 
 * @version 1.00
 * @since 2022/11/13
 * @author Changhee Kim
 */

public class JsonProcess {
	HttpServletRequest req;
	HttpServletResponse res;
	
	public JsonProcess(HttpServletRequest req, HttpServletResponse res){
		this.req = req;
		this.res = res;
	}
	
	public void method() throws ServletException, IOException, InterruptedException{
		Logger.info("<=================Json Process=================>");

		//req데이터 문자열로 출력
		int contentLength = req.getContentLength();
		byte[] recvByte = new byte[contentLength];
		req.getInputStream().read(recvByte);
		String recvStr = new String(recvByte);
		
		//문자열 jsonObject로 변환

		JSONParser parser = new JSONParser();
		try {
			JSONObject jo = (JSONObject) parser.parse(recvStr);
			
			Iterator it = jo.keySet().iterator();
			int chkCnt=0;
			while(it.hasNext()) {
				String key = (String)it.next();
				
				if((key.equalsIgnoreCase("guid") || key.equalsIgnoreCase("seqid")) 
						&& jo.get(key).toString().equalsIgnoreCase("")) {
					chkCnt++;
				}
			}

			String guid = null;
			String seqId = null;
			String sendStr = null;
			
			if(chkCnt==2){
				guid = Guid.getGuid(req);
				seqId = "";
				jo = new JSONObject();
				jo.put("guid", guid);
				jo.put("seqid",seqId);
				sendStr = jo.toString();	
			} else {
				Logger.warn("Request message should be \"guid\":\"\",\"seqid\":\"\"");
				sendStr = "";
			}
			
			//출력 전문 처리
			OutputStream os = res.getOutputStream();
			res.setContentType("application/json");
			byte[] sendByte = sendStr.getBytes("EUC-KR");
			os.write(sendByte);
			os.flush();
		} catch(ParseException pe) {
			pe.printStackTrace();
		}		
	}
}