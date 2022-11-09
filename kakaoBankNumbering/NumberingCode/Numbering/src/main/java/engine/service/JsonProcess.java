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
import engine.GuidSeqId;

/**
 * JsonŸ�Կ� ���� ���� ó��
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
		Logger.debug("<=================Json Process=================>");

		//req������ ���ڿ��� ���
		int contentLength = req.getContentLength();
		byte[] recvByte = new byte[contentLength];
		req.getInputStream().read(recvByte);
		String recvStr = new String(recvByte);
		
		//���ڿ� jsonObject�� ��ȯ
		Logger.debug("Receive Data : " + recvStr);
		int seqIdChk = 0;
		JSONParser parser = new JSONParser();
		try {
			JSONObject jo = (JSONObject) parser.parse(recvStr);
			
			Iterator it = jo.keySet().iterator();
			int chkCnt=0;
			while(it.hasNext()) {
				String key = (String)it.next();
				
				if((key.equalsIgnoreCase("guid") || key.equalsIgnoreCase("seqid")) 
						&& jo.get(key).toString().equalsIgnoreCase("")) {
					if(key.equalsIgnoreCase("seqid")) {
						seqIdChk++;
					}
					chkCnt++;
				}
			}

			String Guid = null;
			String SeqId = null;
			String sendStr = null;
			
			if(chkCnt==2){
				Guid = GuidSeqId.getGuid();
				SeqId = GuidSeqId.getSeqId();
				
				jo = new JSONObject();
				jo.put("guid", Guid);
				jo.put("seqid",SeqId);
				sendStr = jo.toString();	
				Logger.debug("sendDocument : [" + sendStr + "]");
			} else if(chkCnt==1 && seqIdChk==1) {
				SeqId = String.valueOf(GuidSeqId.SEQ_ID);
				 
				jo = new JSONObject();
				jo.put("seqid", SeqId);
				sendStr = jo.toString();
				Logger.debug("sendDocument : [" + sendStr + "]");
			} else {
				Logger.warn("Request message should be \"guid\":\"\",\"seqid\":\"\"");
				sendStr = "";
			}
			
			//��� ���� ó��
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