package engine.service;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.logging.Logger;
import engine.GuidSeqId;

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
		Logger.debug("<=================Stream Process=================>");
		
		//req������ ���ڿ��� ���
		int contentLength = req.getContentLength();
		byte[] recvByte = new byte[contentLength];
		req.getInputStream().read(recvByte);
		String recvStr = new String(recvByte);
		
		Logger.debug("Receive Data : " + recvStr);
		
		int andIdx = recvStr.indexOf('&');
		String Guid = null;
		String SeqId = null;
		String sendStr = null;
		
		//name-value ���� �б�
		if(andIdx == -1) {	
			if(recvStr.equalsIgnoreCase("guidseqid") == true) {
				Guid = GuidSeqId.getGuid();
				SeqId = GuidSeqId.getSeqId();
				sendStr = Guid + SeqId;
				Logger.debug("sendDocument : [" + sendStr + "]");
			} 
			//���� seq id���� ���� ���
			else if(recvStr.equalsIgnoreCase("seqid") == true) {
				sendStr = String.valueOf(GuidSeqId.SEQ_ID);
			}			
			else {
				Logger.warn("Request message should be \"guidseqid\"");
				sendStr = "";
			}
		}
		else {			
			if(recvStr.equalsIgnoreCase("guid&seqid") == true) {
				Guid = "guid=" + GuidSeqId.getGuid();
				SeqId = "seqid=" + GuidSeqId.getSeqId();
				sendStr = Guid + "&" + SeqId;
				Logger.debug("sendDocument : [" + sendStr + "]");
			} 
			//���� seq id���� ���� ���
			else if(recvStr.equalsIgnoreCase("seqid&") == true) {
				sendStr = "seqid=" + String.valueOf(GuidSeqId.SEQ_ID);
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