package engine.service;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.logging.Logger;
import engine.Guid;

/**
 * StreamŸ�Կ� ���� ���� ó��
 * 
 * @version 1.00
 * @since 2022/11/13
 * @author Changhee Kim
 */

public class StreamProcess {
	HttpServletRequest req;
	HttpServletResponse res;

	public StreamProcess(HttpServletRequest req, HttpServletResponse res){
		this.req = req;
		this.res = res;
	}
	
	//ä�� ó��
	public void method() throws ServletException, IOException, InterruptedException{
		Logger.info("<=================Stream Process=================>");

		//req������ ���ڿ��� ���
		int contentLength = req.getContentLength();
		byte[] recvByte = new byte[contentLength];
		req.getInputStream().read(recvByte);
		String recvStr = new String(recvByte);
		
		
		int andIdx = recvStr.indexOf('&');
		String guid = null;
		String seqId = null;
		String sendStr = null;
		
		//name-value ���� �б�
		if(andIdx == -1) {	
			if(recvStr.equalsIgnoreCase("guidseqid") == true) {
				guid = Guid.getGuid(req);
				seqId = "";
				sendStr = guid + seqId;
			} 	
			else {
				Logger.warn("Request message should be \"guidseqid\"");
				sendStr = "";
			}
		}
		else {			
			if(recvStr.equalsIgnoreCase("guid&seqid") == true) {
				guid = "guid=" + Guid.getGuid(req);
				seqId = "seqid=" + "";
				sendStr = guid + "&" + seqId;
			} 
			else {
				Logger.warn("Request message should be \"guid&seqid\"");
				sendStr = "";
			}
		}

		//��� ���� ó��
		OutputStream os = res.getOutputStream();
		res.setContentType("text/plain");
		byte[] sendByte = sendStr.getBytes("EUC-KR");
		os.write(sendByte);
		os.flush();
	}
}